package com.neptune.klat_uikit_android.feature.photo.select

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.neptune.klat_uikit_android.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PhotoCropActivity : AppCompatActivity() {
    private var currentPhotoUri: Uri? = null

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            findViewById<ImageView>(R.id.iv_image).setImageURI(currentPhotoUri)
        } else {

        }
    }

    private val selectPhotoLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { imageUri ->
        findViewById<ImageView>(R.id.iv_image).setImageURI(imageUri)
    }

    companion object {
        private val CAMERA_PERMISSION_REQUEST = 100
        private val GALLERY_PERMISSION_REQUEST = 101
        private val CAMERA_REQUEST = 102
        private val GALLERY_REQUEST = 103
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_select)

        findViewById<Button>(R.id.btn_photo).setOnClickListener {
            checkCameraPermission()
        }

        findViewById<Button>(R.id.btn_album).setOnClickListener {
//            checkGalleryPermission()
        }
    }

    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST
            )
        } else {
            openCamera()
        }
    }

//    private fun checkGalleryPermission() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), GALLERY_PERMISSION_REQUEST)
//        } else {
//            openGallery()
//        }
//    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                } else {
                    Toast.makeText(this, "카메라 권한이 필요합니다", Toast.LENGTH_SHORT).show()
                }
            }
            GALLERY_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    openGallery()
                } else {
                    Toast.makeText(this, "저장소 권한이 필요합니다", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, GALLERY_REQUEST)
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            try {
                createImageFile().let { photoFile ->
                    currentPhotoUri = FileProvider.getUriForFile(
                        this,
                        "${applicationContext.packageName}.provider",
                        photoFile
                    )
                    takePictureLauncher.launch(currentPhotoUri)
                }
            } catch (e: Exception) {
                Toast.makeText(this, "카메라 앱을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // 카메라로 찍은 사진을 저장할 파일 생성
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            Log.d("!! absolutePath : ", absolutePath.toString())
        }
    }
}