package com.cesoft.data

import android.util.Log
import com.cesoft.domain.AppError
import com.cesoft.domain.entity.NostrEvent
import com.cesoft.domain.entity.NostrKeys
import com.cesoft.domain.entity.NostrKindStandard
import com.cesoft.domain.entity.NostrMetadata
import com.cesoft.domain.entity.NostrPrivateKey
import com.cesoft.domain.entity.NostrPublicKey
import com.cesoft.domain.repo.NostrRepositoryContract
import rust.nostr.sdk.Client
import rust.nostr.sdk.Event
import rust.nostr.sdk.EventBuilder
import rust.nostr.sdk.Events
import rust.nostr.sdk.Filter
import rust.nostr.sdk.Keys
import rust.nostr.sdk.Kind
import rust.nostr.sdk.KindStandard
import rust.nostr.sdk.NostrSigner
import rust.nostr.sdk.PublicKey
import java.time.Duration
import javax.inject.Inject

class NostrRepository @Inject constructor(
    val prefsRepository: PrefsRepository
): NostrRepositoryContract {

    private suspend fun addRelays(client: Client) {
        client.addRelay("wss://nos.lol")
        client.addRelay("wss://nostr.bitcoiner.social")
        client.addRelay("wss://nostr.mom")
        client.addRelay("wss://nostr.oxtr.dev")
        client.addRelay("wss://relay.nostr.band")
        client.addRelay("wss://relay.damus.io")
        client.addRelay("wss://nostr.swiss-enigma.ch")
    }

    override suspend fun getUserMetadata(npub: String): Result<NostrMetadata> {
        try {
            val publicKey = PublicKey.parse(npub)
            // Create Nostr client
            val client = Client()
            // Add relays
            addRelays(client)
            // Connect
            client.connect()
            // Filter metadata
//            val filterMD = Filter()
//                .kind(Kind.fromStd(KindStandard.METADATA))
//                .author(publicKey)
//                .limit(1u)
//            val event = client.fetchEvents(filterMD, Duration.ofSeconds(5L)).toVec().first()
            val meta = client.fetchMetadata(publicKey, Duration.ofSeconds(5L))?.toEntity(npub)
            // Return metadata
            Log.e(TAG, "getUserMetadata:----------------- ${meta?.displayName}, ${meta?.name}")
            return if(meta != null) return Result.success(meta)
            else Result.failure(AppError.NotKnownError)
        }
        catch(e: Exception) {
            Log.e(TAG, "getUserMetadata:e:----------------- $e : $npub")
            return Result.failure(e)
        }
    }

    override suspend fun getKeys(nsec: String): Result<NostrKeys> {
        try {
            val keys = Keys.parse(nsec)
            val publicKey = NostrPublicKey(keys.publicKey().toBech32())
            val privateKey = NostrPrivateKey(keys.secretKey().toBech32())
            val nostrKeys = NostrKeys(publicKey, privateKey)
            return Result.success(nostrKeys)
        }
        catch (e: Exception) {
            Log.e(TAG, "getKeys------------ $e")
            return Result.failure(e)
        }
    }

    override suspend fun createUser(metadata: NostrMetadata): Result<NostrKeys> {
        try {
            val keys = Keys.generate()
            val signer = NostrSigner.keys(keys)
            val client = Client(signer = signer)
            addRelays(client)
            //
            client.setMetadata(metadata.toDto())
            val publicKey = NostrPublicKey(keys.publicKey().toBech32())
            Log.e(TAG, "createUser------------ npub = ${publicKey.npub}")
            val privateKey = NostrPrivateKey(keys.secretKey().toBech32())
            Log.e(TAG, "createUser------------ nsec = ${privateKey.nsec}")
            return Result.success(NostrKeys(publicKey, privateKey))
        }
        catch (e: Exception) {
            Log.e(TAG, "createUser------------ $e")
            return Result.failure(e)
        }
    }

    override suspend fun fetchEvents(
        kind: NostrKindStandard?,
        authList: List<String>,
        limit: ULong
    ): Result<List<NostrEvent>> {
        try {
            //val client = Client()
            var client = prefsRepository.readPrivateKey()?.let {
                val keys = Keys.parse(it)
                val signer = NostrSigner.keys(keys)
                Client(signer = signer)
            } ?: run {
                Client()
            }
            addRelays(client)
            client.connect()

            val authListKeys = mutableListOf<PublicKey>()
            for (auth in authList) {
                authListKeys.add(PublicKey.parse(auth))
            }

            val filter = Filter()
                .kind(Kind.fromStd(kind?.toDto()!!))
                .authors(authListKeys)
                .limit(limit)
            val events = client.fetchEvents(filter, Duration.ofSeconds(5L)).toVec()

            val filterMeta = Filter()
                .kind(Kind.fromStd(KindStandard.METADATA))
                .authors(authListKeys)
            val metas: List<Event> = client.fetchEvents(filterMeta, Duration.ofSeconds(5L)).toVec()
            val auths: List<NostrMetadata> = metas.map { it.toMetadata(it.author().toBech32()) }

            return Result.success(events.map { it.toEntity(auths) })
        }
        catch (e: Exception) {
            Log.e(TAG, "fetchEvents:e:--------------------- $e")
            return Result.failure(e)
        }
    }

    override suspend fun sendEvent(event: NostrEvent): Result<Unit> {
        try {
            var keys: Keys? = null
            var client = prefsRepository.readPrivateKey()?.let {
                keys = Keys.parse(it)
                val signer = NostrSigner.keys(keys)
                Client(signer = signer)
            } ?: run {
                Client()
            }
            addRelays(client)
            client.connect()

            when (event.kind) {
                NostrKindStandard.TEXT_NOTE -> {
                    //val tags = event.tags.map { it.toDto() }
                    val eb = EventBuilder
                        .textNote(event.content)
                        .allowSelfTagging()
                        .pow(10u)
                    keys?.let { eb.signWithKeys(keys) }
                    //eb.tags(event.tags)
                    client.sendEventBuilder(eb)
                }
                else -> return Result.failure(AppError.NotKnownError)
            }
            return Result.success(Unit)
        }
        catch(e: Exception) {
            return Result.failure(e)
        }
    }

    //https://github.com/nostr-protocol/nips/blob/master/02.md
    override suspend fun sendFollowList(followList: List<String>): Result<Unit> {
        try {
            var keys: Keys? = null
            var client = prefsRepository.readPrivateKey()?.let {
                keys = Keys.parse(it)
                val signer = NostrSigner.keys(keys)
                Client(signer = signer)
            } ?: run {
                Client()
            }
            addRelays(client)
            client.connect()

            /// Sending follow list deletes previous, so you must always append previous list
            val newFollowList = mutableListOf<String>()
            newFollowList.addAll(followList)

            //FOLLOW_SET : https://github.com/nostr-protocol/nips/blob/master/02.md
            //CONTACT_LIST : idem?
            val filter = Filter()
                .kind(Kind.fromStd(KindStandard.CONTACT_LIST))//
                //.limit(15u)//TODO: TEST
            val events: Events = client.fetchEvents(filter, Duration.ofSeconds(2L))
            for (e in events.toVec()) {
                android.util.Log.e(TAG, "sendFollowList--------------- ${e.asJson()}")
            }

            val publicKeyList = newFollowList.map { PublicKey.parse(it) }
            val eb = EventBuilder.followSet("yourCesNstrContactList", publicKeyList)
            keys?.let { eb.signWithKeys(keys) }
            client.sendEventBuilder(eb)
            return Result.success(Unit)
        }
        catch(e: Exception) {
            return Result.failure(e)
        }
    }

    companion object {
        const val TAG = "NostrRepo"
    }
}
