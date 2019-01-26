package com.example.andre.userinterface

import android.content.Context
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.util.Log

class PentaxConnector(val context: MainActivity) {

    val TAG = "PentaxConnector"

    fun connectToWPAWiFi(ssid:String,pass:String){
        if(isConnectedTo(ssid)){ //see if we are already connected to the given ssid
            Log.d(TAG, "Connected to: $ssid")
            return
        }
        val wm: WifiManager = context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        var wifiConfig=getWiFiConfig(ssid)

        if(wifiConfig==null){
            Log.d(TAG, "Adding SSID to WiFiConfig")
            createWPAProfile(ssid,pass)
            wifiConfig=getWiFiConfig(ssid)
        }

        wm.disconnect()
        wm.enableNetwork(wifiConfig!!.networkId,true)
        val r = wm.reconnect()
        Log.d(TAG,"intiated connection to SSID: $ssid result: $r")
    }

    fun isConnectedTo(ssid: String):Boolean{
        val wm:WifiManager= context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        if(wm.connectionInfo.ssid == ssid){
            return true
        }
        return false
    }

    fun getWiFiConfig(ssid: String): WifiConfiguration? {
        val wm:WifiManager= context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiList=wm.configuredNetworks
        for (item in wifiList){
            if(item.SSID != null && item.SSID.equals(ssid)){
                return item
            }
        }
        return null
    }

    fun createWPAProfile(ssid: String,pass: String){
        Log.d(TAG,"Saving SSID: $ssid")
        val conf = WifiConfiguration()
        conf.SSID = ssid
        conf.preSharedKey = pass
        val wm:WifiManager= context.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wm.addNetwork(conf)
        Log.d(TAG,"saved SSID to WiFiManger")
    }

    /**
     * @brief checks the connection to the camera
     * @return true if connection is alive
     * @return false if connection is lost or not established
      */
    fun isConnectionAlive() : Int{

        val url = "http://192.168.0.1/v1/apis"
        var returnValue : Int = 0
        var r = khttp.async.get(url) {
            val status = statusCode
            val r = "$jsonObject"
            returnValue = status
        }
        return returnValue
    }

}