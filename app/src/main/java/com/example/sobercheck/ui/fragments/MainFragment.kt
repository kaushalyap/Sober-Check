package com.example.sobercheck.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.preference.PreferenceManager
import com.example.sobercheck.R
import com.example.sobercheck.databinding.FragmentMainBinding
import com.example.sobercheck.ui.activities.MainActivity


class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!
    private lateinit var mainActivity: MainActivity


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)
        mainActivity = activity as MainActivity
        showFabBottomAppBar()
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        val address = sharedPreferences.getString(getString(R.string.pref_drop_off_location), "")
        binding.textView.text = address
        return binding.root
    }

    private fun showFabBottomAppBar() {
        mainActivity.showFabBottomAppBar()
    }

    private fun hideFabBottomAppBar() {
        mainActivity.hideFabBottomAppBar()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        hideFabBottomAppBar()
    }
}