package com.example.sobercheck.ui.activities

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.sobercheck.R
import com.example.sobercheck.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = findNavController(R.id.nav_host_fragment)
        binding.bottomAppBar.setupWithNavController(navController)
        setSupportActionBar(binding.bottomAppBar)

        binding.fabCheck.setOnClickListener {
            navController.navigate(R.id.action_main_to_selfie)
        }
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

    fun showFabBottomAppBar() {
        binding.fabCheck.show()
        binding.bottomAppBar.performShow()
    }

    fun hideFabBottomAppBar() {
        binding.fabCheck.hide()
        binding.bottomAppBar.performHide()
    }

    fun makeRequest(permission: String, requestCode: Int) {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
            AlertDialog.Builder(this).setTitle("Hello")
        } else {
            ActivityCompat.requestPermissions(this, arrayOf(permission), requestCode)
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            1 -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.d("PERMISSION", "Calling permission has been denied!")
                } else {
                    Log.d("PERMISSION", "Calling permission has been granted!")
                    makeCall()
                }
            }
            2 -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.d("PERMISSION", "Sending SMS permission has been denied!")

                } else {
                    Log.d("PERMISSION", "Sending SMS permission has been granted!")
                    sendSMS()
                }
            }
        }
    }

    fun makeCall() {
        val intent = Intent(Intent.ACTION_CALL, Uri.parse("tel:1234567890"))
        startActivity(intent)
    }

    fun sendSMS() {
        val userName = "John"
        val emergencyContactNo = "1234567890"
        val messageBody =
            "Drunk detected!, Please look after $userName to avoid accidents. Sent by Sober Check app"
        val smsManager: SmsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(emergencyContactNo, null, messageBody, null, null)
        Toast.makeText(this, "Message Sent", Toast.LENGTH_LONG).show()
    }
}