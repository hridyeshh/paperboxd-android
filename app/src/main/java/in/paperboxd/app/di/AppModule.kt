package `in`.paperboxd.app.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * App-wide Hilt bindings. Network + storage providers are added in build order
 * step 2 (SecurePrefs, ApiClient, ApiService).
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule
