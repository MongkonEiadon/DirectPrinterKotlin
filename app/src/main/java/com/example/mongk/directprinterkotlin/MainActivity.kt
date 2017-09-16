package com.example.mongk.directprinterkotlin

import android.content.BroadcastReceiver
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import mu.KotlinLogging
import java.nio.channels.Channel

class MainActivity : AppCompatActivity() {

    val logger = KotlinLogging.logger{}
    val intentFilter =  IntentFilter()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val action = intent.action

        if(Intent.ACTION_SEND == action){
            logger.info{intent}
        }

        setContentView(R.layout.activity_main)


        //  Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);


        setContentView(R.layout.activity_main)
    }



    fun onPeersAvailable(peerList: WifiP2pDeviceList){
        val refresheedPeer = peerList.deviceList

    }
}
