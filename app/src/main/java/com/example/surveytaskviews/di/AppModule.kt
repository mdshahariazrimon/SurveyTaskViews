package com.example.surveytaskviews.di

import android.content.Context
import androidx.room.Room
import com.example.surveytaskviews.data.db.FormDao
import com.example.surveytaskviews.data.db.FormDatabase
import com.example.surveytaskviews.data.network.ApiService
import com.example.surveytaskviews.data.repository.FormRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit =
        Retrofit.Builder()
            .baseUrl("https://api.jsonbin.io/v3/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService =
        retrofit.create(ApiService::class.java)

    @Provides
    @Singleton
    fun provideFormRepository(apiService: ApiService): FormRepository =
        FormRepository(apiService)
    @Provides
    @Singleton
    fun provideFormDatabase(@ApplicationContext context: Context): FormDatabase {
        return Room.databaseBuilder(
            context,
            FormDatabase::class.java,
            "form_database"
        ).build()
    }
    // NEW: Provider for the Form DAO
    @Provides
    @Singleton
    fun provideFormDao(database: FormDatabase): FormDao {
        return database.formDao()
    }
}