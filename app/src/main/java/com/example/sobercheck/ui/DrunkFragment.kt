package com.example.sobercheck.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.sobercheck.databinding.FragmentDrunkBinding


class DrunkFragment : Fragment() {

    private var _binding: FragmentDrunkBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainActivity: MainActivity


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDrunkBinding.inflate(inflater, container, false)
        hideFabBottomAppBar()
        return binding.root
    }

    private fun hideFabBottomAppBar() {
        mainActivity = activity as MainActivity
        mainActivity.hideFabBottomAppBar()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        mainActivity.showFabBottomAppBar()
    }
}