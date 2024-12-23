package com.example.photogallery.API

import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
private  const val API_KEY= "59GjyFRMUygl7Gk2OwxWIBHBvQ1fHsAOfYVtQmliqYI"
class PhotoInterceptor : Interceptor{
    override fun intercept(chain: Interceptor.Chain): Response {
      val  originalRequest :Request = chain.request()

        val newUrl:HttpUrl = originalRequest.url.newBuilder()
            .addQueryParameter("client_id", API_KEY)
            .addQueryParameter("per_page", "30")
            .addQueryParameter("order_by","popular")
            .build()
         val newRequest:Request = originalRequest.newBuilder()
             .url(newUrl)
             .build()
      return   chain.proceed(newRequest)
    }

}