package com.anhquan.pinotify.view.status

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.anhquan.pinotify.databinding.FragmentPlayintegrityStatusBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class PlayIntegrityStatusFragment : Fragment() {
    private lateinit var binding: FragmentPlayintegrityStatusBinding
    private val viewModel: PlayIntegrityStatusViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayintegrityStatusBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onStart() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.initiate(this@PlayIntegrityStatusFragment.requireContext())
        }
        super.onStart()
    }
}