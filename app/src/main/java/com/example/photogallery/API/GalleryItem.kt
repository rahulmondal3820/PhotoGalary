package com.example.photogallery.API

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GalleryItem(
  @Json(name = "alt_description")  val title:String?,
    val id:String,
   @Json(name = "urls") val url:Url,
@Json(name = "links") val  photoPageUri:Uri
    )

@JsonClass(generateAdapter = true)
data class  Url(
    val small:String,
    val regular:String,
    val full:String
)
@JsonClass(generateAdapter = true)
data class Uri(
        val html:String
        )