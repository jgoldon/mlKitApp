package com.example.mlkitapp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    private val pickPhotoRequestCode: Int = 101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        fab.setOnClickListener {
                view -> pickImage()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                pickPhotoRequestCode -> {
                    val bitmap:Bitmap? = getImageFromData(data)
                    bitmap?.apply {
                        contentIV.setImageBitmap(bitmap)
                        processImageTagging(bitmap)
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun processImageTagging(bitmap: Bitmap) {
        val visionImg = FirebaseVisionImage.fromBitmap(bitmap)
        val labeler = FirebaseVision.getInstance().onDeviceImageLabeler
        labeler.processImage(visionImg)
            .addOnSuccessListener { tags ->
                tagsTV.text = tags.joinToString(", ") { it.text + ": " + it.confidence }
            }
            .addOnFailureListener { ex ->
                Log.wtf("LAB", ex)
            }
    }

    private fun getImageFromData(data: Intent?) : Bitmap? {
        val selectedImage : Uri? = data?.data
        return MediaStore.Images.Media.getBitmap(this.contentResolver, selectedImage)
    }

    private fun pickImage() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "image/*"
        startActivityForResult(intent, pickPhotoRequestCode)
    }
}
