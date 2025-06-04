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

//    @Provides
//    @Singleton
//    fun provideAppDatabase(
//        @ApplicationContext appContext: Context
//    ): AppDatabase {
//        return Room.databaseBuilder(
//            appContext,
//            AppDatabase::class.java,
//            "Sercom"
//        ).build()
//    }

//    @Singleton
//    @Provides
//    fun providePrefDataSource(
//        @ApplicationContext appContext: Context
//    ): PrefDataSource {
//        return PrefDataSource(appContext)
//    }
//
//    @Singleton
//    @Provides
//    fun provideLocalDataSource(
//        @ApplicationContext appContext: Context,
//        appDatabase: AppDatabase
//    ): LocalDataSource {
//        return LocalDataSource(appContext, appDatabase)
//    }
//
//    @Singleton
//    @Provides
//    fun provideRepository(
//        prefDataSource: PrefDataSource,
//        localDataSource: LocalDataSource,
//        remoteDataSource: RemoteDataSource
//    ): RepositoryContract {
//        return Repository(prefDataSource, localDataSource, remoteDataSource)
//    }

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
    ): NostrRepositoryContract {
        return NostrRepository()
    }

    @Singleton
    @Provides
    fun provideFilesRepository(
        @ApplicationContext appContext: Context,
    ): FilesRepositoryContract {
        return FilesRepository(appContext)
    }

}