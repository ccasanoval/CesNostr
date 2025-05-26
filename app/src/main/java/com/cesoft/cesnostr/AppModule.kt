package com.cesoft.cesnostr

import android.content.Context
import com.cesoft.cesnostr.view.Util
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideApp(@ApplicationContext appContext: Context) = appContext

    @Singleton
    @Provides
    fun provideUtil(@ApplicationContext appContext: Context) = Util(appContext)

//    @OptIn(DelicateCoroutinesApi::class)
//    @Singleton
//    @Provides
//    fun provideSharedLocationManager(
//        @ApplicationContext appContext: Context
//    ): SharedLocationManager {
//        val scope = (appContext as App).applicationScope
//        return SharedLocationManager(appContext, scope)
//    }
}
