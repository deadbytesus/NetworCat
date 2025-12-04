// Copyright (c) 2025, DeadBytes(Luka Maidanov). ALL RIGHTS RESERVED.
//
// SPDX-License-Identifier: Apache-2.0

package xxx.deadbytes.networcat

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Handler
import android.os.IBinder
import android.os.Message
import android.os.Messenger

class ConfigService : Service() {
    companion object {
        const val MSG_GET_CONFIG = 1
        const val MSG_CONFIG_RESULT = 2
    }

    private lateinit var prefs: SharedPreferences
    private val messenger = Messenger(ConfigHandler())

    inner class ConfigHandler : Handler() {
        override fun handleMessage(msg: Message) {
            when (msg.what) {
                MSG_GET_CONFIG -> {
                    val iso = prefs.getString("pref_country_iso", "") ?: ""
                    val numeric = prefs.getString("pref_numeric", "") ?: ""
                    val operator = prefs.getString("pref_operator", "") ?: ""

                    val bundle = android.os.Bundle().apply {
                        putString("country_iso", iso)
                        putString("numeric", numeric)
                        putString("operator", operator)
                    }

                    val reply = Message.obtain(null, MSG_CONFIG_RESULT)
                    reply.data = bundle
                    try {
                        msg.replyTo.send(reply)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        prefs = getSharedPreferences("preferences", Context.MODE_PRIVATE)
    }

    override fun onBind(intent: Intent?): IBinder = messenger.binder
}
