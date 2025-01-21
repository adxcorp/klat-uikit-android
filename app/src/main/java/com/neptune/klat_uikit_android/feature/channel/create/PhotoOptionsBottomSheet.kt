package com.neptune.klat_uikit_android.feature.channel.create

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.FragmentActivity
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.neptune.klat_uikit_android.core.base.ChannelObject
import com.neptune.klat_uikit_android.core.util.CameraUtils
import com.neptune.klat_uikit_android.core.util.PermissionUtils
import com.neptune.klat_uikit_android.databinding.LayoutPhotoOptionsBottomSheetBinding

class PhotoOptionsBottomSheet(private val photoActionListener: PhotoActionListener) : BottomSheetDialogFragment() {
    private val parentActivity: FragmentActivity by lazy { requireActivity() }

    private var _binding: LayoutPhotoOptionsBottomSheetBinding? = null
    private val binding get() = _binding ?: error("LayoutPhotoOptionsBottomSheetBinding 초기화 에러")

    private lateinit var requestPermissionCameraLauncher: ActivityResultLauncher<String>
    private lateinit var requestPermissionGalleryLauncher: ActivityResultLauncher<Array<String>>

    private val openCameraLauncher = registerForActivityResult(ActivityResultContracts.TakePicture()) { isSuccess ->
        if (isSuccess) {
            ChannelObject.photoUri?.let { uri ->
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
                true -> CameraUtils.openCamera(parentActivity, openCameraLauncher)
                false -> PermissionUtils.showPermissionRationale(
                    context = parentActivity,
                    title = "카메라 권한 요청",
                    message = "카메라 권한을 허용해야 사진 촬영이 가능합니다."
                )
            }
        }

        requestPermissionGalleryLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            when (PermissionUtils.checkMediaPermissions(permissions)) {
                true -> openGalleryLauncher.launch("image/*")
                false -> PermissionUtils.showPermissionRationale(
                    context = parentActivity,
                    title = "갤러리 권한 요청",
                    message = "권한을 허용해야 갤러리에 접근이 가능합니다."
                )
            }
        }
    }

    private fun setClickListener() = with(binding) {
        clPhotoPick.setOnClickListener {
            PermissionUtils.checkGalleryPermission(requestPermissionGalleryLauncher)
        }

        clTakePicture.setOnClickListener {
            PermissionUtils.checkCameraPermission(
                context = parentActivity,
                requestPermissionCameraLauncher = requestPermissionCameraLauncher
            )
        }
    }
}