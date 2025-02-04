package com.neptune.klat_uikit_android.feature.chat.emoji

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.neptune.klat_uikit_android.databinding.LayoutEmojiBottomSheetBinding

class EmojiBottomSheet(
    private val messageType: MessageType,
    private val emojiSelectedListener: OnEmojiBottomSheetListener
) : BottomSheetDialogFragment() {
    private var _binding: LayoutEmojiBottomSheetBinding? = null
    private val binding get() = _binding ?: error("LayoutEmojiBottomSheetBinding 초기화 에러")
    private val adapter: EmojiAdapter by lazy { setAdapter() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = LayoutEmojiBottomSheetBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setListener()
    }

    private fun initView() = with(binding) {
        when (messageType) {
            MessageType.COPY -> layoutMessageCopy.root.visibility = View.VISIBLE
            MessageType.COPY_AND_DELETE -> layoutMessageCopyDelete.root.visibility = View.VISIBLE
            MessageType.DELETE -> layoutMessageDelete.root.visibility = View.VISIBLE
            MessageType.NONE -> Unit
        }

        rvEmojis.apply {
            layoutManager = GridLayoutManager(requireActivity(), 1, LinearLayoutManager.HORIZONTAL, false)
            adapter = this@EmojiBottomSheet.adapter
        }
    }

    private fun setAdapter(): EmojiAdapter {
        return EmojiAdapter { emoji ->
            emojiSelectedListener.selectedEmoji(emoji)
            dismiss()
        }
    }

    private fun setListener() = with(binding) {
        layoutMessageCopy.root.setOnClickListener {
            emojiSelectedListener.selectedCopyText()
            dismiss()
        }

        layoutMessageCopyDelete.clMessageCopy.setOnClickListener {
            emojiSelectedListener.selectedCopyText()
            dismiss()
        }

        layoutMessageCopyDelete.clMessageDelete.setOnClickListener {
            emojiSelectedListener.selectedDeleteText()
            dismiss()
        }

        layoutMessageDelete.root.setOnClickListener {
            emojiSelectedListener.selectedDeleteText()
            dismiss()
        }
    }

    enum class MessageType {
        COPY,
        COPY_AND_DELETE,
        DELETE,
        NONE
    }
}