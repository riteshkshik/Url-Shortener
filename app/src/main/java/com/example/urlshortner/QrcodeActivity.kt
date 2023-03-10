package com.example.urlshortner

import android.R.attr.bitmap
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.urlshortner.databinding.ActivityQrcodeBinding
import com.google.zxing.BarcodeFormat
import com.google.zxing.WriterException
import com.google.zxing.qrcode.QRCodeWriter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream


class QrcodeActivity : AppCompatActivity() {
    var binding: ActivityQrcodeBinding? = null
    var short_url: String? = null
    var cbitmap: Bitmap? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQrcodeBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        if (intent.hasExtra("short_url")) {
            short_url = intent.getStringExtra("short_url")
            CoroutineScope(Dispatchers.Main).launch {
                generate_qr()
            }
        } else {
            Toast.makeText(this, "Something Went Wrong!", Toast.LENGTH_SHORT).show()
        }

        binding?.shareQrCode?.setOnClickListener {
            if (cbitmap != null) {
                shareQrToOtherApps()
            } else {
                Toast.makeText(this, "Qr Code is Generating!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun shareQrToOtherApps() {
        val bitmapPath: String =
            MediaStore.Images.Media.insertImage(contentResolver, cbitmap, short_url, null)
        val bitMapUri: Uri = Uri.parse(bitmapPath)
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, bitMapUri)
            type = "image/*"
        }
        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }

    suspend fun generate_qr() {
        short_url = short_url!!.trim()
        binding?.shortUrlTextView?.text = short_url
        val writer = QRCodeWriter()
        try {
            val bitMatrix = writer.encode(short_url, BarcodeFormat.QR_CODE, 512, 512)
            val width = bitMatrix.width
            val height = bitMatrix.height
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            for (x in 0 until width) {
                delay(1)
                for (y in 0 until height) {
                    if (bitMatrix[x, y]) {
                        bitmap.setPixel(x, y, Color.parseColor("#2AA4F4"))
                    } else {
                        bitmap.setPixel(x, y, Color.WHITE)
                    }
                }
                binding?.qrCode?.setImageBitmap(bitmap)
            }
            cbitmap = bitmap

        } catch (e: WriterException) {
            e.printStackTrace()
        }
    }
}