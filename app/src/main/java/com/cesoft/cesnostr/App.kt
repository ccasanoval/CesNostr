package com.cesoft.cesnostr

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class App: Application()


//TODO: Let user sign a new nostr key...
    //-> make a new notr user account

//TODO: Zaps : https://github.com/nostr-protocol/nips/blob/master/57.md
//TODO: Let user publish events
//TODO: Let user add/delete follow authors
//TODO: Let user add/delete relays
//TODO: Let user search authors by name
//TODO: Let user search tags
//TODO: Let user see author detailed info
//TODO: Let user see his own keys: private and public... -> create password or biometrics