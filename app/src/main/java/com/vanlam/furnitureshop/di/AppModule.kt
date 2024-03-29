package com.vanlam.furnitureshop.di

import android.app.Application
import android.content.Context.MODE_PRIVATE
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.vanlam.furnitureshop.firebase.FirebaseCommon
import com.vanlam.furnitureshop.utils.Constants.INTRODUCTION_SP
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideFirebaseAuth() = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirestoreDatabase() = Firebase.firestore

    @Provides
    @Singleton
    fun provideStorageRef() = FirebaseStorage.getInstance().reference

    @Provides
    fun provideIntroductionSP(application: Application) = application.getSharedPreferences(INTRODUCTION_SP, MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideFirebaseCommon(
        firestore: FirebaseFirestore,
        auth: FirebaseAuth
    ) = FirebaseCommon(firestore, auth)
}