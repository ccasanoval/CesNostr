package com.cesoft.domain.entity

enum class NostrKindStandard {
    UNKNOWN,

    METADATA,
    TEXT_NOTE,
    CONTACT_LIST,
    OPEN_TIMESTAMPS,
    EVENT_DELETION,
    REPOST,
    GENERIC_REPOST,
    COMMENT,
    REACTION,
    BADGE_AWARD,
    CHANNEL_CREATION,
    CHANNEL_METADATA,
    CHANNEL_MESSAGE,
    CHANNEL_HIDE_MESSAGE,
    CHANNEL_MUTE_USER,
    GIT_PATCH,
    GIT_ISSUE,
    GIT_REPLY,
    GIT_STATUS_OPEN,
    GIT_STATUS_APPLIED,
    GIT_STATUS_CLOSED,
    GIT_STATUS_DRAFT,
    TORRENT,
    TORRENT_COMMENT,
    LABEL,
    WALLET_CONNECT_INFO,
    REPORTING,
    ZAP_PRIVATE_MESSAGE,
    ZAP_REQUEST,
    ZAP_RECEIPT,
    MUTE_LIST,
    PIN_LIST,
    BOOKMARKS,
    COMMUNITIES,
    PUBLIC_CHATS,
    BLOCKED_RELAYS,
    SEARCH_RELAYS,
    SIMPLE_GROUPS,
    INTERESTS,
    EMOJIS,
    FOLLOW_SET,
    RELAY_SET,
    BOOKMARK_SET,
    ARTICLES_CURATION_SET,
    VIDEOS_CURATION_SET,
    INTEREST_SET,
    EMOJI_SET,
    RELEASE_ARTIFACT_SET,
    RELAY_LIST,
    PEER_TO_PEER_ORDER,
    REQUEST_TO_VANISH,
    AUTHENTICATION,
    WALLET_CONNECT_REQUEST,
    WALLET_CONNECT_RESPONSE,
    NOSTR_CONNECT,
    LIVE_EVENT,
    LIVE_EVENT_MESSAGE,
    PROFILE_BADGES,
    BADGE_DEFINITION,
    SEAL,
    GIFT_WRAP,
    PRIVATE_DIRECT_MESSAGE,
    INBOX_RELAYS,
    MLS_KEY_PACKAGE_RELAYS,
    MLS_KEY_PACKAGE,
    MLS_WELCOME,
    MLS_GROUP_MESSAGE,
    LONG_FORM_TEXT_NOTE,
    GIT_REPO_ANNOUNCEMENT,
    APPLICATION_SPECIFIC_DATA,
    FILE_METADATA,
    HTTP_AUTH,
    SET_STALL,
    SET_PRODUCT,
    JOB_FEEDBACK,
    USER_STATUS,
    CASHU_WALLET,
    CASHU_WALLET_UNSPENT_PROOF,
    CASHU_WALLET_SPENDING_HISTORY,
    CODE_SNIPPET,
}

fun parseEventKind(kind: ULong): String {
    return when(kind) {
        0uL   -> "User Metadata	        01"
        1uL   -> "Short Text Note	    10"
        2uL   -> "Recommend Relay	    01 (deprecated)"
        3uL   -> "Follows	            02"
        4uL   -> "Encrypted Direct Messages	04"
        5uL   -> "Event Deletion Request	09"
        6uL   -> "Repost	            18"
        7uL   -> "Reaction	            25"
        8uL   -> "Badge Award	        58"
        9uL   -> "Chat Message	        C7"
        10uL  -> "Group Chat Threaded Reply	29 (deprecated)"
        11uL  -> "Thread	            7D"
        12uL  -> "Group Thread Reply	29 (deprecated)"
        13uL  -> "Seal	            59"
        14uL  -> "Direct Message  	17"
        15uL  -> "File Message	    17"
        16uL  -> "Generic Repost	    18"
        17uL  -> "Reaction to a website	25"
        20uL  -> "Picture	            68"
        21uL  -> "Video Event	        71"
        22uL  -> "Short-form Portrait Video Event	71"
        30uL  -> "internal reference	NKBIP-03"
        31uL  -> "external web reference	NKBIP-03"
        32uL  -> "hardcopy reference	NKBIP-03"
        33uL  -> "prompt reference	NKBIP-03"
        40uL  -> "Channel Creation	28"
        41uL  -> "Channel Metadata	28"
        42uL  -> "Channel Message	    28"
        43uL  -> "Channel Hide Message	28"
        44uL  -> "Channel Mute User	28"
        62uL  -> "Request to Vanish	62"
        64uL  -> "Chess (PGN)	        64"
        818uL -> "Merge Requests	    54"
        1018uL ->	"Poll Response	88"
        1021uL -> "Bid	15"
        1022uL -> "Bid confirmation	15"
        1040uL -> "OpenTimestamps	03"
        1059uL -> "Gift Wrap	59"
        1063uL -> "File Metadata	94"
        1068uL -> "Poll	88"
        1111uL -> "Comment	22"
        1311uL -> "Live Chat Message	53"
        1337uL -> "Code Snippet	C0"
        1617uL -> "Patches	34"
        1621uL -> "Issues	34"
        1622uL -> "Git Replies (deprecated)	34"
        in 1630uL..1633uL -> "Status    34"
        1971uL -> "Problem Tracker	nostrocket"
        1984uL -> "Reporting	56"
        1985uL -> "Label	32"
        1986uL -> "Relay reviews"
        1987uL -> "AI Embeddings / Vector lists	NKBIP-02"
        2003uL -> "Torrent	35"
        2004uL -> "Torrent Comment	35"
        2022uL -> "Coinjoin Pool	joinstr"
        4550uL -> "Community Post Approval	72"
        in 5000uL..5999uL -> "Job Request   90"
        in 6000uL..6999uL -> "Job Result    90"
        7000uL -> "Job Feedback	90"
        7374uL -> "Reserved Cashu Wallet Tokens	60"
        7375uL -> "Cashu Wallet Tokens	60"
        7376uL -> "Cashu Wallet History	60"
        in 9000uL..9030uL -> "Group Control Events  29"
        9041uL -> "Zap Goal	75"
        9321uL -> "Nutzap	61"
        9467uL -> "Tidal login	Tidal-nostr"
        9734uL -> "Zap Request	57"
        9735uL -> "Zap	57"
        9802uL -> "Highlights	84"
        10000uL -> "Mute list	51"
        10001uL -> "Pin list	51"
        10002uL -> "Relay List Metadata	65, 51"
        10003uL -> "Bookmark list	51"
        10004uL -> "Communities list	51"
        10005uL -> "Public chats list	51"
        10006uL -> "Blocked relays list	51"
        10007uL -> "Search relays list	51"
        10009uL -> "User groups	51, 29"
        10012uL -> "Favorite relays list	51"
        10013uL -> "Private event relay list	37"
        10015uL -> "Interests list	51"
        10019uL -> "Nutzap Mint Recommendation	61"
        10020uL -> "Media follows	51"
        10030uL -> "User emoji list	51"
        10050uL -> "Relay list to receive DMs	51, 17"
        10063uL -> "User server list	Blossom"
        10096uL -> "File storage server list	96"
        10166uL -> "Relay Monitor Announcement	66"
        13194uL -> "Wallet Info	47"
        17375uL -> "Cashu Wallet Event	60"
        21000uL -> "Lightning Pub RPC	Lightning.Pub"
        22242uL -> "Client Authentication	42"
        23194uL -> "Wallet Request	47"
        23195uL -> "Wallet Response	47"
        24133uL -> "Nostr Connect	46"
        24242uL -> "Blobs stored on mediaservers	Blossom"
        27235uL -> "HTTP Auth	98"
        30000uL -> "Follow sets	51"
        30001uL -> "Generic lists	51 (deprecated)"
        30002uL -> "Relay sets	51"
        30003uL -> "Bookmark sets	51"
        30004uL -> "Curation sets	51"
        30005uL -> "Video sets	51"
        30007uL -> "Kind mute sets	51"
        30008uL -> "Profile Badges	58"
        30009uL -> "Badge Definition	58"
        30015uL -> "Interest sets	51"
        30017uL -> "Create or update a stall	15"
        30018uL -> "Create or update a product	15"
        30019uL -> "Marketplace UI/UX	15"
        30020uL -> "Product sold as an auction	15"
        30023uL -> "Long-form Content	23"
        30024uL -> "Draft Long-form Content	23"
        30030uL -> "Emoji sets	51"
        30040uL -> "Curated Publication Index	NKBIP-01"
        30041uL -> "Curated Publication Content	NKBIP-01"
        30063uL -> "Release artifact sets	51"
        30078uL -> "Application-specific Data	78"
        30166uL -> "Relay Discovery	66"
        30267uL -> "App curation sets	51"
        30311uL -> "Live Event	53"
        30315uL -> "User Statuses	38"
        30388uL -> "Slide Set	Corny Chat"
        30402uL -> "Classified Listing	99"
        30403uL -> "Draft Classified Listing	99"
        30617uL -> "Repository announcements	34"
        30618uL -> "Repository state announcements	34"
        30818uL -> "Wiki article	54"
        30819uL -> "Redirects	54"
        31234uL -> "Draft Event	37"
        31388uL -> "Link Set	Corny Chat"
        31890uL -> "Feed	NUD: Custom Feeds"
        31922uL -> "Date-Based Calendar Event	52"
        31923uL -> "Time-Based Calendar Event	52"
        31924uL -> "Calendar	52"
        31925uL -> "Calendar Event RSVP	52"
        31989uL -> "Handler recommendation	89"
        31990uL -> "Handler information	89"
        32267uL -> "Software Application"
        34550uL -> "Community Definition	72"
        38383uL -> "Peer-to-peer Order events	69"
        in 39000uL..39009uL -> "Group metadata events   29"
        39089uL -> "Starter packs	51"
        39092uL -> "Media starter packs	51"
        39701uL -> "Web bookmarks	B0"
        else -> "$kind"
    }
}
