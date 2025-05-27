package com.cesoft.data

import com.cesoft.domain.entity.NostrKeys
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
import java.time.Duration
import kotlin.String

class NostrRepository(
    //client: Client
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
            val event = client.fetchEvents(filterMD, Duration.ofSeconds(1L)).toVec().first()
            // Return metadata
            return Result.success(event.toMetadata())
        }
        catch(e: Exception) {
            android.util.Log.e(TAG, "getUserMetadata:e:----------------- $e : $npub")
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
            android.util.Log.e(TAG, "getKeys------------ $e")
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
            android.util.Log.e(TAG, "createUser------------ npub = ${publicKey.npub}")
            val privateKey = NostrPrivateKey(keys.secretKey().toBech32())
            android.util.Log.e(TAG, "createUser------------ nsec = ${privateKey.nsec}")
            return Result.success(NostrKeys(publicKey, privateKey))
        }
        catch (e: Exception) {
            android.util.Log.e(TAG, "createUser------------ $e")
            return Result.failure(e)
        }
    }

    companion object {
        const val TAG = "NostrRepo"
    }
}