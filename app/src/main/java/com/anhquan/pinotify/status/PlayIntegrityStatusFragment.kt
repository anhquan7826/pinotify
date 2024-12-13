package com.anhquan.pinotify.status

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.anhquan.pinotify.databinding.FragmentPlayintegrityStatusBinding

class PlayIntegrityStatusFragment : Fragment() {
    private lateinit var binding: FragmentPlayintegrityStatusBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPlayintegrityStatusBinding.inflate(inflater, container, false)
        return binding.root
    }
}