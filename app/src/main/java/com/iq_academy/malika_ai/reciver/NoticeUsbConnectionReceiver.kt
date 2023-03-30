package com.iq_academy.malika_ai.reciver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.BatteryManager

class NoticeUsbConnectionReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        val plugged = intent!!.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1)
        if (plugged == BatteryManager.BATTERY_PLUGGED_USB) {
            //showDialog(context)
        }
    }


}