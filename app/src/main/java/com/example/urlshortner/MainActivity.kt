package com.example.urlshortner

import android.app.Dialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.webkit.URLUtil.isValidUrl
import android.widget.TextView
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
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
import java.lang.Exception
import java.net.URL

class MainActivity : AppCompatActivity() {
    var binding: ActivityMainBinding? = null
    lateinit var dialog: Dialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        binding?.btnShortUrl?.setOnClickListener {
            validateLink()
        }
        binding?.openToChromeIcon?.setOnClickListener {
            openChrome()
        }
        binding?.copyToClipboardBtn?.setOnClickListener {
            copyToClipBoard()
        }
        binding?.editText?.doOnTextChanged { text, start, before, count ->
            binding?.tilEditText?.error = null
        }
    }

    private fun openChrome() {
        val openURL = Intent(Intent.ACTION_VIEW)
        val url = binding?.textView?.text.toString()
        openURL.data = Uri.parse(url)
        startActivity(openURL)
    }

    private fun copyToClipBoard() {
        var clipBardManager: ClipboardManager = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        clipBardManager.setPrimaryClip(ClipData.newPlainText("Shorted Link",binding?.textView?.text.toString()))
        binding?.copyToClipboardBtn?.text = "Copied!"
    }

    fun validateLink(){
        val long_link: String = binding?.editText?.text.toString()

        if(long_link.isEmpty()){
            Toast.makeText(this, "Please Enter Your URL!", Toast.LENGTH_SHORT).show()
        }else if(!isValidUrl(long_link)){
            Toast.makeText(this, "URL is not Valid!", Toast.LENGTH_SHORT).show()
            binding?.tilEditText?.error = "Enter A Valid URL!"
        }else{
            lauchDialog()
            CoroutineScope(Dispatchers.IO).launch {
                callapi(long_link)
            }
        }
    }

    private fun lauchDialog() {
        dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_loading)
        dialog.setCancelable(false)
        if(dialog.window != null){
            dialog!!.window!!.setBackgroundDrawable(ColorDrawable(0))
        }
        dialog.show()
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
                dialog.dismiss()
                val response: RecievedLink? = response.body()
                val json_from_response = Gson().toJson(response)
                val short_link_object = Gson().fromJson(json_from_response, RecievedLink::class.java)
                //Log.e("skhfakhdksahfaksdhfk", short_link_object.result_url)
                binding?.llForLink?.visibility = View.VISIBLE
                binding?.textView?.text = short_link_object.result_url
            }

            override fun onFailure(call: Call<RecievedLink>, t: Throwable) {
                dialog.dismiss()
                binding?.llForLink?.visibility = View.VISIBLE
                binding?.textView?.text = "Server Down!"
            }
        })
    }
}