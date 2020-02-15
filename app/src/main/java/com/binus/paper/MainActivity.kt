package com.binus.paper

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.*
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
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
    private var scannedBLE = mutableListOf<String>()
    private var scanning = false
    private var listBLE = mutableListOf<String>(BLEBeacon.beaconAddress1, BLEBeacon.beaconAddress2, BLEBeacon.beaconAddress3, BLEBeacon.beaconAddress4, BLEBeacon.beaconAddress5, BLEBeacon.beaconAddress6, BLEBeacon.beaconAddress7, BLEBeacon.beaconAddress8, BLEBeacon.beaconAddress9, BLEBeacon.beaconAddress10, BLEBeacon.beaconAddress11, BLEBeacon.beaconAddress12, BLEBeacon.beaconAddress13, BLEBeacon.beaconAddress14, BLEBeacon.beaconAddress15, BLEBeacon.beaconAddress16, BLEBeacon.beaconAddress17, BLEBeacon.beaconAddress18, BLEBeacon.beaconAddress19, BLEBeacon.beaconAddress20, BLEBeacon.beaconAddress21, BLEBeacon.beaconAddress22, BLEBeacon.beaconAddress23)

    private var filterBLE = mutableListOf<ScanFilter>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.setDataBinding()
        Timber.plant(Timber.DebugTree())

        if (BluetoothAdapter.getDefaultAdapter() != null) {
            this.setFilter()
            this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            this.mBluetootheLeScanner = mBluetoothAdapter.bluetoothLeScanner
        } else {
            Toast.makeText(this, "Device doesn't support bluetooth", Toast.LENGTH_SHORT).show()
        }

    }

    private fun setFilter() {
        this.listBLE.forEach {
            val scanFilter = ScanFilter.Builder().setDeviceAddress(it).build()
            filterBLE.add(scanFilter)
        }
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
            if (this@MainActivity.scannedBLE.indexOf(result?.device.toString()) != -1) {
                return Timber.e("Sudah pernah diinput")
            } else {
                this@MainActivity.scannedBLE.add(result?.device.toString())
            }
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
        this.mBluetootheLeScanner.startScan(this.filterBLE, this.scanSetting, this.scanCallback)
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
        this.scannedBLE.forEach {
            Timber.e("BLE Device : $it")
        }

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