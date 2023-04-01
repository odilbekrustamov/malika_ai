package com.iq_academy.malika_ai.ui.activity

import android.Manifest
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.NavGraph
import androidx.navigation.fragment.NavHostFragment
import com.iq_academy.malika_ai.R
import com.iq_academy.malika_ai.databinding.ActivityMainBinding
import com.iq_academy.malika_ai.reciver.NoticeUsbConnectionReceiver
import com.iq_academy.malika_ai.service.ListiningVoiceService
import com.iq_academy.malika_ai.utils.Extensions.isEmulator
import com.iq_academy.malika_ai.utils.KeyValue.LANGUAGE
import com.iq_academy.malika_ai.utils.KeyValue.LOG_IN
import com.iq_academy.malika_ai.utils.SharedPref
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

/**
 * Rustamov Odilbek, Android developer
 * 28/03/2023  +998-91-775-17-79
 */

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var navGraph: NavGraph
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var receiver: NoticeUsbConnectionReceiver
    private val viewModel: MainViewModel by viewModels<MainViewModelImp>()
    private val PERMISSION_REQUEST_RECORD_AUDIO = 101

    @Inject
    lateinit var sharedPref: SharedPref


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.statusBarColor = ContextCompat.getColor(this, R.color.black)

        navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as NavHostFragment
        navController = navHostFragment.navController
        navGraph = navController.navInflater.inflate(R.navigation.mobile_navigation)

        checkSaved(isRegistered())

        receiver = NoticeUsbConnectionReceiver()

        if (isEmulator()){
            finish()
        }

        checkPermissions()
    }

    private fun initViews() {
        controlClicks()
    }

    private fun checkPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            initViews()
        } else {
            requestSContactPermissiopn()
        }
    }

    private fun requestSContactPermissiopn() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.RECORD_AUDIO
            )
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                PERMISSION_REQUEST_RECORD_AUDIO
            )
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECORD_AUDIO),
                PERMISSION_REQUEST_RECORD_AUDIO
            )
        }
    }

    private fun controlClicks() {
        binding.llClearHistory.setOnClickListener {
            viewModel.deleteAllChats {
                it.onSuccess {
                    navController.popBackStack()
                    navController.navigate(R.id.homeFragment)
                }
                it.onFailure {
                    print(it.message)
                }
                closeDrawerLayout()
            }
        }

    }

    fun startService() {
        val intent = Intent(this, ListiningVoiceService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(intent)
        }
    }

    private fun isRegistered() = sharedPref.getLogIn(LOG_IN)

    private fun checkSaved(isMainFlow: Boolean) {
        if (isMainFlow) {
            navGraph.setStartDestination(R.id.homeFragment)
        } else {
            navGraph.setStartDestination(R.id.loginFragment)
        }
        navController.graph = navGraph
    }

    //Open DrawerLayout
    fun openDrawerLayout() {
        binding.drawerLayout.openDrawer(GravityCompat.START, true)
    }

    //Close DrawerLayout
    fun closeDrawerLayout() {
        binding.drawerLayout.closeDrawer(GravityCompat.START, true)
    }

    override fun onStart() {
        super.onStart()
        val filter = IntentFilter(Intent.ACTION_BATTERY_CHANGED)
        registerReceiver(receiver, filter)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(receiver)
    }

}