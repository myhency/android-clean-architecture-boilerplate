package io.play.clean_architecture_boilerplate.core.di

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import io.play.clean_architecture_boilerplate.core.data.local.AffirmationDao
import io.play.clean_architecture_boilerplate.core.data.local.AppDatabase
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "app_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideAffirmationDao(appDatabase: AppDatabase): AffirmationDao {
        return appDatabase.affirmationDao()
    }
}