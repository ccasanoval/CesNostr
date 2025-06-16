package com.cesoft.data.di

import android.content.Context
import com.cesoft.data.FilesRepository
import com.cesoft.data.NostrRepository
import com.cesoft.data.PrefsRepository
import com.cesoft.domain.repo.FilesRepositoryContract
import com.cesoft.domain.repo.NostrRepositoryContract
import com.cesoft.domain.repo.PrefsRepositoryContract
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DiModule {

    @Singleton
    @Provides
    fun providePrefsRepository(
        @ApplicationContext appContext: Context,
    ): PrefsRepositoryContract {
        return PrefsRepository(appContext)
    }

    @Singleton
    @Provides
    fun provideNostrRepository(
        prefsRepository: PrefsRepository
    ): NostrRepositoryContract {
        return NostrRepository(prefsRepository)
    }

    @Singleton
    @Provides
    fun provideFilesRepository(
        @ApplicationContext appContext: Context,
    ): FilesRepositoryContract {
        return FilesRepository(appContext)
    }

}