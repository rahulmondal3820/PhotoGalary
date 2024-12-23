package com.example.photogallery.API

import retrofit2.http.GET
import retrofit2.http.Query

interface UnsplashApi {

    // @GET("photos/?"+"client_id=59GjyFRMUygl7Gk2OwxWIBHBvQ1fHsAOfYVtQmliqYI"+"&per_page=30"+"&order_by=populer")
    @GET("photos/")
    suspend fun fetchPhoto(): List<GalleryItem>

    @GET("search/photos")
    suspend fun searchPhoto(@Query("query") query: String):UnsplashSearchResponse

}