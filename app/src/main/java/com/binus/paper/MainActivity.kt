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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.binus.paper.databinding.ActivityMainBinding
import com.binus.paper.model.LocationRequest
import com.binus.paper.viewmodel.LocationViewModel
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
    private var listBLE = mutableListOf(
        BLEBeacon.beaconAddress1,
        BLEBeacon.beaconAddress2,
        BLEBeacon.beaconAddress3,
        BLEBeacon.beaconAddress4,
        BLEBeacon.beaconAddress5,
        BLEBeacon.beaconAddress6,
        BLEBeacon.beaconAddress7,
        BLEBeacon.beaconAddress8,
        BLEBeacon.beaconAddress9,
        BLEBeacon.beaconAddress10,
        BLEBeacon.beaconAddress11,
        BLEBeacon.beaconAddress12,
        BLEBeacon.beaconAddress13,
        BLEBeacon.beaconAddress14,
        BLEBeacon.beaconAddress15,
        BLEBeacon.beaconAddress16,
        BLEBeacon.beaconAddress17,
        BLEBeacon.beaconAddress18,
        BLEBeacon.beaconAddress19,
        BLEBeacon.beaconAddress20,
        BLEBeacon.beaconAddress21,
        BLEBeacon.beaconAddress22,
        BLEBeacon.beaconAddress23,
        BLEBeacon.testBeacon
    )
    private var filterBLE = mutableListOf<ScanFilter>()
    private var request = mutableListOf<LocationRequest>()

    private lateinit var viewModel: LocationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        this.setDataBinding()

        Timber.plant(Timber.DebugTree())

        this.viewModel = ViewModelProvider(
            this,
            ViewModelProvider.NewInstanceFactory()
        ).get(LocationViewModel::class.java)
        this.bindViewModel()
        if (BluetoothAdapter.getDefaultAdapter() != null) {
            this.setFilter()
            this.mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            this.mBluetootheLeScanner = mBluetoothAdapter.bluetoothLeScanner
        } else {
            Toast.makeText(this, "Device doesn't support bluetooth", Toast.LENGTH_SHORT).show()
        }

    }

    private fun setFilter() {
        this.listBLE.forEach { beacon ->
            val scanFilter = ScanFilter.Builder().setDeviceAddress(beacon).build()
            filterBLE.add(scanFilter)
        }
    }

    private fun bindViewModel() {
        this.viewModel.response.observe(this, Observer { data ->
            Timber.e("Response $data")
            this.binding.pbLocating.visibility = View.GONE
            val intent = Intent(this, MapsActivity::class.java)
            intent.putExtra(MapsActivity.latLong, data.data)
            startActivity(intent)
        })
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
                this@MainActivity.request.add(LocationRequest(result?.device.toString(), result?.rssi?.toDouble()
                        ?: -80.0))
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
        this.binding.pbLocating.visibility = View.VISIBLE
        this.scanSetting =
                ScanSettings.Builder().setScanMode(ScanSettings.SCAN_MODE_LOW_POWER).build()
        this.mBluetootheLeScanner.startScan(this.filterBLE, this.scanSetting, this.scanCallback)
        this.scanning = true
        // Stop scan after 15 seconds
        GlobalScope.launch {
            delay(10000)
            this@MainActivity.stopScan()
        }
    }

    private fun stopScan() {
        Timber.e("Stop Scanning")
        if (scannedBLE.size == 0) {
            runOnUiThread { Toast.makeText(this, "Tidak ada BLE di temukan", Toast.LENGTH_SHORT).show() }
        } else {
            runOnUiThread {
                this.request.forEach {
                    Timber.e("Request Data : ${it.id}, ${it.rssi}")
                }
                this.scannedBLE = mutableListOf()
                this.viewModel.setLocation(request)
            }
        }
        runOnUiThread {
            this.binding.pbLocating.visibility = View.GONE
            this.mBluetootheLeScanner.stopScan(scanCallback)
            this.scanning = false
        }

    }

    fun handleClick(view: View) {
        when (view) {
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
                this.stopScan()
            }
        }
    }
}