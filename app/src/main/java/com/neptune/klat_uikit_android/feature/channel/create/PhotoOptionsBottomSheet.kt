package com.neptune.klat_uikit_android.feature.channel.create

import android.Manifest
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.neptune.klat_uikit_android.databinding.LayoutPhotoOptionsBottomSheetBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class PhotoOptionsBottomSheet(private val photoActionListener: PhotoActionListener) : BottomSheetDialogFragment() {
    private val parentActivity: FragmentActivity by lazy { requireActivity() }

    private var _binding: LayoutPhotoOptionsBottomSheetBinding? = null
    private val binding get() = _binding ?: error("LayoutPhotoOptionsBottomSheetBinding 초기화 에러")

    private var currentPhotoUri: Uri? = null

    private lateinit var requestPermissionCameraLauncher: ActivityResultLauncher<String>
    private lateinit var requestPermissionGalleryLauncher: ActivityResultLauncher<Array<String>>

    private val openCameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            currentPhotoUri?.let { uri ->
                photoActionListener.onPhotoCaptured(uri)
                dismiss()
            }
        }
    }

    private val openGalleryLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { photoUri ->
        photoUri?.let { uri ->
            photoActionListener.onPhotoSelected(uri)
            dismiss()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = LayoutPhotoOptionsBottomSheetBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setClickListener()
    }

    private fun init() {
        requestPermissionCameraLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            when (isGranted) {
                true -> openCamera()
                false -> showPermissionRationale(title = "카메라 권한 요청", message = "카메라 권한을 허용해야 사진 촬영이 가능합니다.")
            }
        }

        requestPermissionGalleryLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when (checkMediaPermissions(permissions)) {
                true -> openGalleryLauncher.launch("image/*")
                false -> showPermissionRationale(title = "갤러리 권한 요청", message = "권한을 허용해야 갤러리에 접근이 가능합니다.")
            }
        }
    }

    private fun setClickListener() = with(binding) {
        clPhotoPick.setOnClickListener {
            checkGalleryPermission()
        }

        clTakePicture.setOnClickListener {
            checkCameraPermission()
        }
    }

    /** shouldShowRequestPermissionRationale
     * 1. 사용자가 권한 요청을 한 번 거부한 경우 true 반환
     * 2. 사용자가 권한 요청을 아직 한 번도 본적 없는 경우 false 반환
     * 3. 사용자가 권한 요청을 다시 묻지 않기 옵션과 거부한 경우 false 반환
     * **/
    private fun checkCameraPermission() {
        val cameraPermission: String = Manifest.permission.CAMERA
        val cameraPermissionState = (ContextCompat.checkSelfPermission(
            parentActivity,
            Manifest.permission.CAMERA
        ))
        when (cameraPermissionState) {
            PackageManager.PERMISSION_GRANTED -> requestPermissionCameraLauncher.launch(cameraPermission)
            PackageManager.PERMISSION_DENIED -> requestPermissionCameraLauncher.launch(cameraPermission)
        }
    }

    private fun checkGalleryPermission() {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                requestPermissionGalleryLauncher.launch(arrayOf(
                    READ_MEDIA_VISUAL_USER_SELECTED,
                    READ_MEDIA_IMAGES,
                ))
            }

            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
                requestPermissionGalleryLauncher.launch(arrayOf(READ_MEDIA_IMAGES))
            }

            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU -> {
                requestPermissionGalleryLauncher.launch(arrayOf(READ_EXTERNAL_STORAGE))
            }
        }
    }

    private fun showPermissionRationale(
        title: String,
        message: String
    ) {
        AlertDialog.Builder(parentActivity)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("허용") { _, _ ->
                openAppSettings()
            }
            .setNegativeButton("취소", null)
            .show()
    }

    private fun openAppSettings() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", requireContext().packageName, null)
        }
        startActivity(intent)
    }


    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(parentActivity.packageManager) != null) {
            try {
                createImageFile().let { photoFile ->
                    currentPhotoUri = FileProvider.getUriForFile(
                        parentActivity,
                        "${parentActivity.packageName}.provider",
                        photoFile
                    )
                }
                currentPhotoUri?.let { uri ->
                    openCameraLauncher.launch(uri)
                }
            } catch (e: Exception) {
                Toast.makeText(parentActivity, "카메라 앱을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = parentActivity.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).also {
            Log.d("!! absolutePath : ", it.absolutePath.toString())
        }
    }

    private fun checkMediaPermissions(permissions: Map<String, Boolean>): Boolean {
        return when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
                permissions["android.permission.READ_MEDIA_VISUAL_USER_SELECTED"] == true ||
                        permissions["android.permission.READ_MEDIA_IMAGES"] == true
            }
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> permissions["android.permission.READ_MEDIA_IMAGES"] == true
            else -> permissions["android.permission.READ_EXTERNAL_STORAGE"] == true
        }
    }
}