package com.binus.paper

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.binus.paper.databinding.ActivityMainBinding
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var mBluetoothAdapter: BluetoothAdapter
    private lateinit var mBluetootheLeScanner: BluetoothLeScanner

    private lateinit var scanSetting: ScanSettings

    private var scanning = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.setDataBinding()
        Timber.plant(Timber.DebugTree())

        this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        this.mBluetootheLeScanner = mBluetoothAdapter.bluetoothLeScanner
    }

    private fun setDataBinding() {
        this.binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        this.binding.lifecycleOwner = this
        this.binding.activity = this
    }

    override fun onDestroy() {
        super.onDestroy()
        Timber.i("onDestroy")
        this.stopScan()
    }

    private val scanCallback = object : ScanCallback() {
        override fun onScanFailed(errorCode: Int) {
            super.onScanFailed(errorCode)
            Timber.e("Failed $errorCode")
        }

        override fun onScanResult(callbackType: Int, result: ScanResult?) {
            super.onScanResult(callbackType, result)
            Timber.e("Scan Result : ${result?.rssi} ${result?.device}")
        }
    }

    private fun reqPermission(): Boolean {
        val permissionList = mutableListOf<String>()

        if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionList.add(Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if (ContextCompat.checkSelfPermission(
                        this,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionList.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }

        if (permissionList.count() != 0) {
            requestPermissions(permissionList.toTypedArray(), 2)
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 2) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Timber.d("Permission Granted")
            } else {
                this.reqPermission()
            }
        }
    }

    private fun startScan() {
        Timber.e("Start Scanning")
        this.scanSetting =
                ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_POWER).build()
        // TODO: Add filter
        this.mBluetootheLeScanner.startScan(null, scanSetting, scanCallback)
        this.scanning = true
        // Stop scan after 5 seconds
        GlobalScope.launch {
            delay(15000)
            this@MainActivity.stopScan()
        }
    }

    private fun stopScan() {
        Timber.e("Stop Scanning")
        this.mBluetootheLeScanner.stopScan(scanCallback)
        this.scanning = false

//        this.startScan()
    }

    fun handleClick(view: View) {
        when (view) {
            this.binding.btnMap -> {
                startActivity(Intent(this, MapsActivity::class.java))
            }
            this.binding.btnStartScanning -> {
                Timber.e("Scan")
                if (this.reqPermission()) {
                    if (scanning) {
                        this.stopScan()
                    }
                    this.startScan()
                }
            }
            this.binding.btnStopScanning -> {
                mBluetootheLeScanner.stopScan(scanCallback)
                this.scanning = false
            }
        }
    }
}