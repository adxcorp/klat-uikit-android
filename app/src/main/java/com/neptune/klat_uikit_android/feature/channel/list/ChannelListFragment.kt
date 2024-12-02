package com.neptune.klat_uikit_android.feature.channel.list

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import com.neptune.klat_uikit_android.R
import com.neptune.klat_uikit_android.databinding.FragmentChannelListBinding

class ChannelListFragment : Fragment() {
    private var _binding: FragmentChannelListBinding? = null
    private val binding get() = _binding ?: error("FragmentChannelListBinding 초기화 에러")
    private val parentActivity = requireActivity()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentChannelListBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHeaderUI()
    }

    private fun setHeaderUI() = with(binding) {
        layoutHeader.ivLeftBtn.apply {
            visibility = View.VISIBLE
            setImageResource(R.drawable.ic_24_back)
            setOnClickListener { parentActivity.finish() }
        }

        layoutHeader.tvLeftText.apply {
            visibility = View.VISIBLE
            text = "채널"
        }

        layoutHeader.ivFirstRightBtn.apply {
            visibility = View.VISIBLE
            setImageResource(R.drawable.ic_24_search)
            setOnClickListener {
                setSearchUI()
            }
        }

        layoutHeader.ivSecondRightBtn.apply {
            visibility = View.VISIBLE
            setImageResource(R.drawable.ic_24_add_channel)
            setOnClickListener {
                // TODO 채널 추가
            }
        }
    }

    private fun setSearchUI() = with(binding) {
        layoutSearch.root.visibility = View.VISIBLE
        layoutSearch.etSearch.requestFocus()

        // 리팩토링
        val imm = parentActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(layoutSearch.etSearch, InputMethodManager.SHOW_IMPLICIT)

        layoutSearch.etSearch.addTextChangedListener { input ->
            layoutSearch.tvSearchCancel.isVisible = !input.isNullOrEmpty()
        }

        layoutSearch.tvSearchCancel.setOnClickListener {
            layoutSearch.etSearch.setText("")
            layoutSearch.root.visibility = View.GONE

            // 리팩토링
            val imm = parentActivity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(layoutSearch.etSearch.windowToken, 0)
        }
    }
}