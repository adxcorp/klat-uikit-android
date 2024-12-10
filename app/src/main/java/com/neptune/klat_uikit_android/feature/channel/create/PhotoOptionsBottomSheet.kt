package com.neptune.klat_uikit_android.feature.channel.create

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
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
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    private val takePictureLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            currentPhotoUri?.let { uri ->
                photoActionListener.onPhotoCaptured(uri)
                dismiss()
            }
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
        requestPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(parentActivity, "카메라 권한이 허용되었습니다.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(parentActivity, "카메라 권한이 취소되었습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setClickListener() = with(binding) {
        binding.clPhotoPick.setOnClickListener {

        }

        binding.clTakePicture.setOnClickListener {
            checkCameraPermission()
        }
    }

    private fun checkCameraPermission() {
        val cameraPermission: String = Manifest.permission.CAMERA
        val cameraPermissionState = (ContextCompat.checkSelfPermission(
            parentActivity,
            Manifest.permission.CAMERA
        ))
        when {
            cameraPermissionState == PackageManager.PERMISSION_GRANTED -> openCamera()
            shouldShowRequestPermissionRationale(cameraPermission) -> showPermissionRationale()
            cameraPermissionState == PackageManager.PERMISSION_DENIED -> requestPermissionLauncher.launch(cameraPermission)
        }
    }

    private fun showPermissionRationale() {
        AlertDialog.Builder(requireContext())
            .setTitle("카메라 권한 요청")
            .setMessage("카메라 권한을 허용해야 사진 촬영이 가능합니다.")
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
                    takePictureLauncher.launch(uri)
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
}