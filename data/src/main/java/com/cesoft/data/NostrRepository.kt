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
import rust.nostr.sdk.Contact
import rust.nostr.sdk.Event
import rust.nostr.sdk.EventBuilder
import rust.nostr.sdk.Events
import rust.nostr.sdk.Filter
import rust.nostr.sdk.JsonValue
import rust.nostr.sdk.Keys
import rust.nostr.sdk.Kind
import rust.nostr.sdk.KindStandard
import rust.nostr.sdk.NostrSigner
import rust.nostr.sdk.PublicKey
import rust.nostr.sdk.SendEventOutput
import rust.nostr.sdk.TagKind
import java.time.Duration
import javax.inject.Inject
import kotlin.collections.map

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
            Log.e(TAG, "getKeys:e:------------ $e")
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
            Log.e(TAG, "createUser:e:------------ $e")
            return Result.failure(e)
        }
    }

    override suspend fun fetchEvents(
        kind: NostrKindStandard?,
        authList: List<String>,
        limit: ULong?
    ): Result<List<NostrEvent>> {
        try {
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

            var filter = Filter().kind(Kind.fromStd(kind.toDto()))
            if(authListKeys.isNotEmpty()) filter = filter.authors(authListKeys)
            if(limit != null) filter = filter.limit(limit)
            val events = client.fetchEvents(filter, Duration.ofSeconds(5L)).toVec()

            //TODO: Enhance for the case the user doesn't give authors
            //TODO: you must fetch the events, get all the different authors and then fetch their metadata..
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
            Log.e(TAG, "sendEvent:e:----------- $e")
            return Result.failure(e)
        }
    }

    override suspend fun fetchFollowList(): Result<List<NostrMetadata>> {
        try {
            var keys: Keys? = null
            var client = prefsRepository.readPrivateKey()?.let {
                keys = Keys.parse(it)
                val signer = NostrSigner.keys(keys)
                Client(signer = signer)
            } ?: run {
                //Client()
                return Result.failure(AppError.InvalidNostrKey)
            }
            addRelays(client)
            client.connect()

            /// Get follow author tags, and get the npub inside
            val npubs = mutableListOf<String>()
            val filter = Filter()
                .kind(Kind.fromStd(KindStandard.CONTACT_LIST))
                .author(keys!!.publicKey())
            var events: Events = client.fetchEvents(filter, Duration.ofSeconds(2L))
            for (e in events.toVec()) {
                val tags = e.tags().toVec()
                for (tag in tags) {
                    tag.content()?.let { npubs.add(it) }
                    //val meta = client.fetchMetadata(publicKey, Duration.ofSeconds(5L))?.toEntity(npub)//More calls... better all at once
                }
            }
            /// Now get the metadata from those npubs
            val filterMeta = Filter()
                .kind(Kind.fromStd(KindStandard.METADATA))
                .authors(npubs.map { PublicKey.parse(it) })
            val metas: List<Event> = client.fetchEvents(filterMeta, Duration.ofSeconds(5L)).toVec()
            val auths: List<NostrMetadata> = metas.map { it.toMetadata(it.author().toBech32()) }.distinctBy { it.npub }
            return Result.success(auths)
        }
        catch(e: Exception) {
            Log.e(TAG, "fetchFollowList:e:----------- $e")
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
                //Client()
                return Result.failure(AppError.InvalidNostrKey)
            }
            addRelays(client)
            client.connect()

            /// Sending follow list deletes previous, so you must always append previous list
            val newFollowList = mutableListOf<String>()
            newFollowList.addAll(followList)

            //FOLLOW_SET : https://github.com/nostr-protocol/nips/blob/master/02.md
            //CONTACT_LIST : idem?
            val filter = Filter()
                .kind(Kind.fromStd(KindStandard.CONTACT_LIST))
                .author(keys!!.publicKey())
            var events: Events = client.fetchEvents(filter, Duration.ofSeconds(2L))
            Log.e(TAG, "sendFollowList--------------- 0000")
            for (e in events.toVec()) {
                Log.e(TAG, "sendFollowList---------------json ${e.asJson()}")
                Log.e(TAG, "sendFollowList---------------npub ${e.author().toBech32()}")
                val tags = e.tags().toVec()
                for(tag in tags) {
                    if(tag.kind() == TagKind.SingleLetter && tag.kindStr() == "p")
                        Log.e(TAG, "sendFollowList---------------tag = OK")
                    Log.e(TAG, "sendFollowList---------------tag = ${tag.content()} / ${tag.kind()} / ${tag.kindStr()} / ${tag.asVec().first()}")
                    tag.content()?.let { newFollowList.add(it) }
                }
            }

            val newContacts = mutableListOf<Contact>()
            for(npub in newFollowList) {
                newContacts.add(
                    Contact(
                        publicKey = PublicKey.parse(npub),
                        relayUrl = null,
                        alias = null
                    )
                )
            }

            val eb = EventBuilder.contactList(newContacts)
            keys.let { eb.signWithKeys(keys) }
            val out: SendEventOutput = client.sendEventBuilder(eb)
            Log.e(TAG, "sendFollowList---------------out: ${out.success.size} / ${out.failed.size} / ${out.id}")
            for(s in out.success)
                Log.e(TAG, "sendFollowList---------------out S: $s ")
            for(f in out.failed)
                Log.e(TAG, "sendFollowList---------------out F: $f ")


            /// TODO: Check that the new ones have been added ? or is it a waste?
            events = client.fetchEvents(filter, Duration.ofSeconds(2L))
            Log.e(TAG, "sendFollowList 2--------------- 0000")
            for (e in events.toVec()) {
                val tags = e.tags().toVec()
                for(tag in tags) {
                    Log.e(TAG, "sendFollowList--2---tag = ${tag.content()} / ${tag.kind()} / ${tag.kindStr()}")
                    //TODO: Check that the new ones appears
                }
            }

            return if(out.success.isNotEmpty()) Result.success(Unit)
            else Result.failure(AppError.NotKnownError)
        }
        catch(e: Exception) {
            Log.e(TAG, "sendFollowList:e:----------- $e")
            return Result.failure(e)
        }
    }

    override suspend fun searchAuthors(searchText: String): Result<List<NostrEvent>> {
        try {
            var keys: Keys? = null
            var client = prefsRepository.readPrivateKey()?.let {
                keys = Keys.parse(it)
                val signer = NostrSigner.keys(keys)
                Client(signer = signer)
            } ?: run {
                //Client()
                return Result.failure(AppError.InvalidNostrKey)
            }
            addRelays(client)
            client.connect()

            val filter = Filter()
                .kinds(listOf(
                    Kind.fromStd(KindStandard.METADATA),
                    Kind.fromStd(KindStandard.TEXT_NOTE),
                    Kind.fromStd(KindStandard.LONG_FORM_TEXT_NOTE),
                    Kind.fromStd(KindStandard.COMMENT),
                    Kind.fromStd(KindStandard.CHANNEL_MESSAGE),
                    Kind.fromStd(KindStandard.REPOST),
                ))
                .search(searchText)
                //.author(keys!!.publicKey())
            var events: Events = client.fetchEvents(filter, Duration.ofSeconds(2L))

//            for(e in events.toVec()) {
//                android.util.Log.e(TAG, "searchAuthors------- $e")
//            }

            return Result.success(events.toVec().map { it.toEntity(NostrMetadata.Empty) })
        }
        catch (e: Exception) {
            Log.e(TAG, "searchAuthors:e:----------- $e")
            return Result.failure(e)
        }
    }

    companion object {
        const val TAG = "NostrRepo"
    }
}
