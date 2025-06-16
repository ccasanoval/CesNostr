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


    private fun Event.toEntity(): NostrEvent {
        val createdAt = LocalDateTime.ofEpochSecond(createdAt().asSecs().toLong(), 0, ZoneOffset.UTC)
        return NostrEvent(
            kind = kind().asStd().toEntity(),
            tags = tags().toEntity(),
            auth = author().toBech32(),
            createdAt = createdAt,
            content = content(),
            json = asJson()
        )
    }

    private fun Event.toMetadata(): NostrMetadata {
        val metadata = Metadata.fromJson(content()).asRecord()
        return NostrMetadata(
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
            Log.e(TAG, "getUserMetadata:e:----------------- ${event.toMetadata().displayName}, ${event.toMetadata().name}")
            return Result.success(event.toMetadata())
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

            android.util.Log.e(TAG, "getEvents:--------------- "+kind?.toDto()+":"+KindStandard.TEXT_NOTE)
            val filter = Filter()
                .kind(Kind.fromStd(kind?.toDto()!!))
                .authors(authListKeys)
                .limit(15u)

            val events = client.fetchEvents(filter, Duration.ofSeconds(5L)).toVec()
            //for(e in events)android.util.Log.e(TAG, "getEvents:--------------- $e")
            return Result.success(events.map { it.toEntity() })
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

/*
try {
            initLogger(LogLevel.INFO)
            val pubKeyCes = PublicKey.parse("npub1e3grdtr7l8rfadmcpepee4gz8l00em7qdm8a732u5f5gphld3hcsnt0q7k")//CES
            val pubKeyBtc = PublicKey.parse("npub15tzcpmvkdlcn62264d20ype7ye67dch89k8qwyg9p6hjg0dk28qs353ywv")

            //TODO: Meter todo en repositorio -> dominio:caso_uso
            var client = readPrivateKey()?.let {
                val keys = Keys.parse(it)
                val signer = NostrSigner.keys(keys)
                Client(signer = signer)
            } ?: run {
                Client()
            }

            client.addRelay("wss://nos.lol")
            client.addRelay("wss://nostr.bitcoiner.social")
            client.addRelay("wss://nostr.mom")
            client.addRelay("wss://nostr.oxtr.dev")
            client.addRelay("wss://relay.nostr.band")
            client.addRelay("wss://relay.damus.io")
            client.addRelay("wss://nostr.swiss-enigma.ch")
            client.connect()

            //----------- Metadata
            val metadata = mutableMapOf<String, Metadata>()
            val filterMD = Filter()
                .kind(Kind.fromStd(KindStandard.METADATA))
                .authors(listOf(pubKeyCes, pubKeyBtc))
                .limit(5u)
            val metadataEvents = client.fetchEvents(filterMD, Duration.ofSeconds(1L)).toVec()
            metadataEvents.forEach { m ->
                metadata[m.author().toHex()] = Metadata.fromJson(m.content())
                android.util.Log.e("AA", "---------meta1: ${m.asJson()}")
                //      "id":"2ae631c0e256d373f87f47418fdc25fea168cd0b8c18949ad7923c18bd137ff9",
                //      "pubkey":"cc5036ac7ef9c69eb7780e439cd5023fdefcefc06ecfdf455ca26880dfed8df1",
                //      "created_at":1745489671,"kind":0,
                //      "tags":[["alt","User profile for Opus2501"]],
                //      "content":"{
                //          "name":"Opus2501",
                //          "display_name":"Opus2501",
                //          "picture":"https://cortados.freevar.com/web/front/images/scorpion_s.png",
                //          "website":"https://cortados.freevar.com",
                //          "lud16":"wordybritish81@walletofsatoshi.com",
                //          "banner":"https://cortados.freevar.com"}",
                //          "sig":"94c21c13ddf9a31f1d5571555a095b79271d3ff5b650fda70b3219bf572904d73b98953f8583cda79fd4c10e341dffe0d23768bbcee00b55cb59824f7d5d0a0c"
            }

            val filter = Filter()
                .kind(Kind.fromStd(KindStandard.TEXT_NOTE))
                .authors(listOf(pubKeyCes, pubKeyBtc))
                .limit(15u)
//            val events = client.fetchEventsFrom(
//                urls = listOf("wss://relay.damus.io"),
//                filter = filter,timeout = Duration.ofSeconds(10L)).toVec()
            val events = client.fetchEvents(filter, Duration.ofSeconds(10L)).toVec()
            Log.e("AAA", "************************* EVENTS = ${events.size}")
            client.close()

            //--------------------
            events.forEach { e ->
                Log.e("AAA", "*********************************************")
                //android.util.Log.e("AAA", "-------------------- id   = ${e.id()}")
                //android.util.Log.e("AAA", "-------------------- kind = ${e.identifier()}")
                Log.e("AAA", "-------------------- kind = ${e.kind()} ")
                //android.util.Log.e("AAA", "-------------------- #tag = ${e.tags().size}")
                var tags = ""
                for (tag in e.tags().toVec()) {
                    tags += tag
                }
                Log.e("AAA", "----------------------- tags = $tags")
                Log.e("AAA", "-------------------- auth = ${e.author().toNostrUri()}")
                //android.util.Log.e("AAA", "-------------------- auth = ${e.author().toBech32()}")
                Log.e("AAA", "-------------------- crat = ${e.createdAt().toHumanDatetime()}")
                Log.e("AAA", "-------------------- cont = ${e.content()}")
                //android.util.Log.e("AAA", "-------------------- vrfy = ${e.verify()}")
                //android.util.Log.e("AAA", "-------------------- sign = ${e.signature()}")
                Log.e("AAA", "-------------------- json = ${e.asJson()}")
            }
            //--------------------

            if (events.isNotEmpty()) {
                return HomeTransform.GoInit(events = events, metadata = metadata)
            }
            else {
                val e = AppError.NotFound
                Log.e(TAG, "fetch:e:---------------- $e")
                return HomeTransform.GoInit(error = e)
            }
        }
        catch (e: Exception) {
            Log.e(TAG, "fetch:failure:---------------- $e")
            return HomeTransform.GoInit(error = e)
        }
* */