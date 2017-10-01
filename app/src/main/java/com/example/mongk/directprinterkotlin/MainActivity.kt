package com.example.mongk.directprinterkotlin


import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.net.wifi.p2p.WifiP2pManager
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import android.support.v7.app.AppCompatActivity
import com.RT_Printer.WIFI.WifiPrintDriver
import mu.KotlinLogging
import android.os.StrictMode
import android.provider.MediaStore
import android.view.View
import android.widget.EditText
import org.slf4j.helpers.Util
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.FileInputStream
import java.io.FileNotFoundException
import android.graphics.drawable.BitmapDrawable
import android.view.Gravity
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    val logger = KotlinLogging.logger{}
    val intentFilter =  IntentFilter()


    private var wifiSocket: WifiPrintDriver? = null

    private var mIpAddress: EditText? = null
    private var mConnect: Button? = null
    private var mPrintTest: Button? = null

    // The Handler that gets information back from the BluetoothChatService
    private val mHandler = object : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MESSAGE_STATE_CHANGE -> {
                    if (D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1)
                    when (msg.arg1) {

                    }
                }
                MESSAGE_WRITE -> {
                }
                MESSAGE_READ -> {
                    var Msg: String? = null
                    val readBuf = msg.obj as ByteArray
                    if (D) Log.i(TAG, "readBuf[0]:" + readBuf[0] + "  revBytes:" + revBytes)

                    Msg = ""
                    for (i in 0 until revBytes) {
                        Msg = Msg!! + " 0x"
                        Msg = Msg + Integer.toHexString(readBuf[i].toInt())
                    }
                    DisplayToast(Msg)
                }
                MESSAGE_TOAST -> {
                }
            }
        }
    }

    fun DisplayToast(str: String?) {
        val toast = Toast.makeText(this, str, Toast.LENGTH_SHORT)
        toast.setGravity(Gravity.TOP, 0, 100)
        toast.show()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val action = intent.action



        if (android.os.Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }

        //  Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);




        setContentView(R.layout.activity_main)
        InitialUIControl()


        if(wifiSocket == null){
            wifiSocket = WifiPrintDriver(this, mHandler)
        }

        // when intent was sent
        if(Intent.ACTION_SEND == action){
            logger.info{intent.type.toString()}

            doPrint(intent)

        }

    }


    @Override
    protected fun InitialWifiPrinter(): Boolean {
        if(wifiSocket == null) {
            wifiSocket = WifiPrintDriver(this, mHandler)
        }

        if(IPAddress == null) {    IPAddress =  mIpAddress!!.text.toString() }

        var port = 9100

        var result = wifiSocket!!.WIFISocket(IPAddress, port)

        if(result){

            Toast.makeText(this@MainActivity, "Connect to printer Success", Toast.LENGTH_SHORT).show()
            this@MainActivity.setTitle("Connect to printer Success")

        }
        else{
            Toast.makeText(this@MainActivity, "Failed to connect printer!", Toast.LENGTH_SHORT).show()
            this@MainActivity.setTitle("Failed to connect printer!")
        }

        return result
    }



    private fun doPrint(intent: Intent) {

        var uri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
        var context = applicationContext;
        var toast = Toast.makeText(context, "doing print!", Toast.LENGTH_SHORT)
        toast.show()

        if (uri != null) {


            toast = Toast.makeText(context, "Received Image!", Toast.LENGTH_SHORT)
            toast.show()

            var bmp = MediaStore.Images.Media.getBitmap(this.contentResolver, uri);

            val maxWidth = 300 //

            bmp = Bitmap.createScaledBitmap(bmp, maxWidth, ((bmp.height/bmp.width)* maxWidth), false)

            if(wifiSocket == null || wifiSocket!!.IsNoConnection()) {

                toast = Toast.makeText(context, "Setup Printer!", Toast.LENGTH_SHORT)
                toast.show()

                InitialWifiPrinter()

                wifiSocket!!.Begin()
            }


            if(!wifiSocket!!.IsNoConnection()) {
                toast = Toast.makeText(context, "Connected to printer!", Toast.LENGTH_SHORT)
                toast.show()
                val start = byteArrayOf(0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x1B, 0x40, 0x1B, 0x33, 0x00)
                wifiSocket!!.WIFI_Write(start, start.size)

                val bytes = Utils.getReadBitMapBytes(bmp)
                wifiSocket!!.WIFI_Write(bytes, bytes.size)

                val end = byteArrayOf(0x1d, 0x4c, 0x1f, 0x00)
                wifiSocket!!.WIFI_Write(end, end.size)


                toast = Toast.makeText(context, "done", Toast.LENGTH_SHORT)
                toast.show()
            }
        }
    }

    companion object {
        // Debugging
        private val TAG = "MainActivity"
        private val D = true

        // Message types sent from the BluetoothChatService Handler
        val MESSAGE_STATE_CHANGE = 1
        val MESSAGE_READ = 2
        val MESSAGE_WRITE = 3
        val MESSAGE_TOAST = 4

        val DEVICE_NAME = "device_name"
        val TOAST = "toast"
        var revBytes = 0

        var IPAddress: String? = ""


    }

    private fun InitialUIControl(){
        //initial ui
        mConnect = findViewById(R.id.btn_connect)
        mIpAddress = findViewById(R.id.ip_address)
        val _mPrintTest = findViewById<Button>(R.id.btn_test)

        //ui events
        _mPrintTest!!.setOnClickListener{
            if (wifiSocket == null || wifiSocket!!.IsNoConnection()) {
                Toast.makeText(this@MainActivity, "Initial Wifi Printer", Toast.LENGTH_SHORT).show()
                InitialWifiPrinter()
            }

            if(!wifiSocket!!.IsNoConnection()){
                wifiSocket!!.Begin()
                wifiSocket!!.printImage()

            }
            else{

                Toast.makeText(this@MainActivity, "No wifi connection!", Toast.LENGTH_SHORT).show()
            }

            finish()
        }
    }


}
