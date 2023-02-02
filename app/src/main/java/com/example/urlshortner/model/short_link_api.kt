package com.example.urlshortner.model

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.FormUrlEncoded
import retrofit2.http.Headers
import retrofit2.http.Multipart
import retrofit2.http.POST

interface short_link_api {
    @Headers(
        "content-type: application/json",
        "X-RapidAPI-Key: 55551123e8msh477a5c9eadd7112p146c58jsn3242a5187be9",
        "X-RapidAPI-Host: url-shortener-service.p.rapidapi.com"
    )
    @POST("shorten")
    fun sendLink(@Body url : response) : Call<testtest>
}