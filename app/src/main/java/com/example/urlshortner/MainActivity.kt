package com.example.urlshortner

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import com.example.urlshortner.databinding.ActivityMainBinding
import com.example.urlshortner.model.sendLink
import com.example.urlshortner.model.short_link_api
import com.example.urlshortner.model.RecievedLink
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.yield
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {
    var binding: ActivityMainBinding? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.btnShortUrl?.setOnClickListener {
            validateLink()
        }
    }

    fun validateLink(){
        val long_link: String = binding?.editText?.text.toString()
        binding?.textView?.visibility = View.VISIBLE
        binding?.textView?.text = "https://goolnk.com/AqJYaW"
        if(long_link.isEmpty()){
            Toast.makeText(this, "Please Enter Your URL!", Toast.LENGTH_SHORT).show()
        }else{
            CoroutineScope(Dispatchers.IO).launch {
                //callapi(long_link)
            }
        }
    }

    suspend fun callapi(long_link: String) {
        val retrofitBuilder = Retrofit.Builder()
            .baseUrl("https://url-shortener-service.p.rapidapi.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val api_interface_object = retrofitBuilder.create(short_link_api::class.java)
        val user_link = sendLink(long_link)
        val call = api_interface_object.sendLink(user_link)
        yield()
        call.enqueue(object : Callback<RecievedLink>{
            override fun onResponse(call: Call<RecievedLink>, response: Response<RecievedLink>) {
                val response: RecievedLink? = response.body()
                val json_from_response = Gson().toJson(response)
                val short_link_object = Gson().fromJson(json_from_response, RecievedLink::class.java)
                //Log.e("skhfakhdksahfaksdhfk", short_link_object.result_url)
                binding?.textView?.visibility = View.VISIBLE
                binding?.textView?.text = short_link_object.result_url
            }

            override fun onFailure(call: Call<RecievedLink>, t: Throwable) {
                binding?.textView?.text = "Server Down!"
            }
        })
    }
}