package com.example.photogallery.API

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class UnsplashSearchResponse(

    @Json(name="results") val photos:List<GalleryItem>
)
