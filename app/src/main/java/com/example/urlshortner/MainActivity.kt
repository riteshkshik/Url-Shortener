package com.example.urlshortner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.example.urlshortner.model.response
import com.example.urlshortner.model.short_link_api
import com.example.urlshortner.model.testtest
import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val textView: TextView = findViewById(R.id.text_view);

        //val client = OkHttpClient.Builder().build()

        val retrofitBuilder = Retrofit.Builder()
            .baseUrl("https://url-shortener-service.p.rapidapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api_interface_object = retrofitBuilder.create(short_link_api::class.java)
        val user_link = response("https://medium.com/swlh/simplest-post-request-on-android-kotlin-using-retrofit-e0a9db81f11a")
        val call = api_interface_object.sendLink(user_link)

        call.enqueue(object : Callback<testtest>{
            override fun onResponse(call: Call<testtest>, response: Response<testtest>) {
                val check: testtest? = response.body()
                val checkUrl = Gson().toJson(check)
                val fromjson = Gson().fromJson(checkUrl, testtest::class.java)
//                Log.e("skhfakhdksahfaksdhfk", Gson().toJson(response.body().toString()))
                Log.e("skhfakhdksahfaksdhfk", fromjson.result_url)
            }

            override fun onFailure(call: Call<testtest>, t: Throwable) {
                textView.text = "failed"
                //TODO("Not yet implemented")
            }
        })




    }
}