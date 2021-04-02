package com.example.sobercheck.ui.activities

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.annotation.StringRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.sobercheck.R
import com.example.sobercheck.databinding.ActivityMainBinding
import permissions.dispatcher.PermissionRequest

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()
    }

    private fun init() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initNavigation()
        binding.fabCheck.setOnClickListener {
            navController.navigate(R.id.action_main_to_selfie)
        }
    }

    private fun initNavigation() {
        navController = findNavController(R.id.nav_host_fragment)
        binding.bottomAppBar.setupWithNavController(navController)
        setSupportActionBar(binding.bottomAppBar)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.bottom_app_bar, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings -> navController.navigate(R.id.action_main_to_settings)
        }
        return true
    }

    internal fun showFabBottomAppBar() {
        binding.fabCheck.show()
        binding.bottomAppBar.performShow()
    }

    internal fun hideFabBottomAppBar() {
        binding.fabCheck.hide()
        binding.bottomAppBar.performHide()
    }

    internal fun showPermissionRationaleDialog(
        @StringRes messageResId: Int,
        request: PermissionRequest
    ) {
        AlertDialog.Builder(this)
            .setPositiveButton(R.string.allow) { _, _ -> request.proceed() }
            .setNegativeButton(R.string.deny) { _, _ -> request.cancel() }
            .setCancelable(false)
            .setMessage(messageResId)
            .show()
    }
}