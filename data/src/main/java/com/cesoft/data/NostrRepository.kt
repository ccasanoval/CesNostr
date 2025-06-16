package com.cesoft.data

import android.util.Log
import com.cesoft.domain.entity.NostrEvent
import com.cesoft.domain.entity.NostrKeys
import com.cesoft.domain.entity.NostrKindStandard
import com.cesoft.domain.entity.NostrMetadata
import com.cesoft.domain.entity.NostrPrivateKey
import com.cesoft.domain.entity.NostrPublicKey
import com.cesoft.domain.repo.NostrRepositoryContract
import rust.nostr.sdk.Client
import rust.nostr.sdk.Event
import rust.nostr.sdk.Filter
import rust.nostr.sdk.Keys
import rust.nostr.sdk.Kind
import rust.nostr.sdk.KindStandard
import rust.nostr.sdk.Metadata
import rust.nostr.sdk.NostrSigner
import rust.nostr.sdk.PublicKey
import rust.nostr.sdk.Tags
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset
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

    private fun KindStandard?.toEntity(): NostrKindStandard = when(this) {
        KindStandard.TEXT_NOTE -> NostrKindStandard.TEXT_NOTE
        KindStandard.REPOST -> NostrKindStandard.REPOST
        else -> NostrKindStandard.UNKNOWN
    }

    private fun Tags.toEntity(): List<String> {
        val tags = mutableListOf<String>()
        for(t in this.toVec()) {
            tags.add(t.toString())
        }
        return tags
    }

    private fun Event.toEntity(authMetaList: List<NostrMetadata>): NostrEvent {
        var authMeta: NostrMetadata? = null
        for(meta in authMetaList) {
            if(meta.npub == author().toBech32()) {
                authMeta = meta
                break
            }
        }
        return toEntity(authMeta)
    }

    private fun Event.toEntity(authMeta: NostrMetadata?): NostrEvent {
        val createdAt = LocalDateTime.ofEpochSecond(createdAt().asSecs().toLong(), 0, ZoneOffset.UTC)
        return NostrEvent(
            kind = kind().asStd().toEntity(),
            tags = tags().toEntity(),
            authKey = author().toBech32(),
            authMeta = authMeta ?: NostrMetadata.Empty,
            createdAt = createdAt,
            content = content(),
            json = asJson()
        )
    }

    private fun Event.toMetadata(npub: String): NostrMetadata {
        val metadata = Metadata.fromJson(content()).asRecord()
        return NostrMetadata(
            npub = npub,
            about = metadata.about ?: "",
            name = metadata.name ?: "",
            displayName = metadata.displayName ?: "",
            website = metadata.website ?: "",
            picture = metadata.picture ?: "",
            banner = metadata.banner ?: "",
            lud06 = metadata.lud06 ?: "",
            lud16 = metadata.lud16 ?: "",
            nip05 = metadata.nip05 ?: "",
        )
    }

    private fun NostrMetadata.toDto(): Metadata {
        val metadata = Metadata()
        if(name.isNotBlank()) metadata.setName(name)
        if(displayName.isNotBlank()) metadata.setDisplayName(displayName)
        if(about.isNotBlank()) metadata.setAbout(about)
        if(website.isNotBlank()) metadata.setWebsite(website)
        if(picture.isNotBlank()) metadata.setPicture(picture)
        if(banner.isNotBlank()) metadata.setBanner(banner)
        if(lud16.isNotBlank()) metadata.setLud16(lud16)
        if(lud06.isNotBlank()) metadata.setLud06(lud06)
        if(nip05.isNotBlank()) metadata.setNip05(nip05)
        return metadata
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
            val filterMD = Filter()
                .kind(Kind.fromStd(KindStandard.METADATA))
                .author(publicKey)
                .limit(1u)
            val event = client.fetchEvents(filterMD, Duration.ofSeconds(5L)).toVec().first()
            // Return metadata
            Log.e(TAG, "getUserMetadata:e:----------------- ${event.toMetadata(npub).displayName}, ${event.toMetadata(npub).name}")
            return Result.success(event.toMetadata(npub))
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

    fun NostrKindStandard?.toDto(): KindStandard = when(this) {
        NostrKindStandard.TEXT_NOTE -> KindStandard.TEXT_NOTE
        NostrKindStandard.NOSTR_CONNECT -> KindStandard.NOSTR_CONNECT
        NostrKindStandard.REPOST -> KindStandard.REPOST
        NostrKindStandard.COMMENT -> KindStandard.COMMENT
        NostrKindStandard.METADATA -> KindStandard.METADATA
        else -> KindStandard.TEXT_NOTE
    }

    override suspend fun getEvents(
        kind: NostrKindStandard?,
        authList: List<String>
    ): Result<List<NostrEvent>> {
        try {
            val client = Client()
//            var client = prefsRepository.readPrivateKey()?.let {
//                val keys = Keys.parse(it)
//                val signer = NostrSigner.keys(keys)
//                Client(signer = signer)
//            } ?: run {
//                Client()
//            }
            addRelays(client)
            client.connect()

            val authListKeys = mutableListOf<PublicKey>()
            for (auth in authList) {
                authListKeys.add(PublicKey.parse(auth))
            }

            val filter = Filter()
                .kind(Kind.fromStd(kind?.toDto()!!))
                .authors(authListKeys)
                .limit(15u)
            val events = client.fetchEvents(filter, Duration.ofSeconds(5L)).toVec()
            //for(e in events)android.util.Log.e(TAG, "getEvents:--------------- $e")

            val filterMeta = Filter()
                .kind(Kind.fromStd(KindStandard.METADATA))
                .authors(authListKeys)
            val metas: List<Event> = client.fetchEvents(filterMeta, Duration.ofSeconds(5L)).toVec()
            val auths: List<NostrMetadata> = metas.map { it.toMetadata(it.author().toBech32()) }

            return Result.success(events.map { it.toEntity(auths) })
        }
        catch (e: Exception) {
            Log.e(TAG, "getEvents:e:--------------- $e")
            return Result.failure(e)
        }
    }

    companion object {
        const val TAG = "NostrRepo"
    }
}
