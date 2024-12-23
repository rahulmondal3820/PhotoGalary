package com.example.photogallery

import android.util.Log
import com.example.photogallery.API.UnsplashApi
import com.example.photogallery.API.GalleryItem
import com.example.photogallery.API.PhotoInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create

private const val TAG = " photoRepository"
class PhotoRepository   {

    private var unsplashApi:UnsplashApi

init {
    val okHttpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(PhotoInterceptor())
        .build()

    try {


        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://api.unsplash.com/")
            .addConverterFactory(MoshiConverterFactory.create())
            .client(okHttpClient)
            .build()
       unsplashApi = retrofit.create<UnsplashApi>()



    } catch (ex: Exception) {
        Log.e(TAG,"Network Request fail",ex)
        throw ex
    }
}

    suspend fun fetchPhotos() :List<GalleryItem> = unsplashApi.fetchPhoto()
    suspend fun searchPhoto(query: String):List<GalleryItem> = unsplashApi.searchPhoto(query).photos
}