package com.example.mongk.directprinterkotlin

import android.app.Activity
import android.bluetooth.BluetoothAdapter
import android.graphics.BitmapFactory
import android.net.wifi.WifiManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.print.PrintHelper

class DeviceListActivity : AppCompatActivity() {

    val TAG = "DeviceListActivity"
    val D = true
    val PERMISSIONS_REQUEST_LOCATION = "100"
    val CURRENT_ANDROID_VERSION = android.os.Build.VERSION.BASE_OS



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_list)

        setResult(Activity.RESULT_CANCELED)
    }

    private fun doPrint(){
        val photoPrinter = PrintHelper(this)
        photoPrinter.scaleMode = PrintHelper.SCALE_MODE_FIT

        val bitmap = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher)

        photoPrinter.printBitmap("test_print", bitmap)
    }
}
