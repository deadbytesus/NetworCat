// Copyright (c) 2025, DeadBytes(Luka Maidanov). ALL RIGHTS RESERVED.
//
// SPDX-License-Identifier: Apache-2.0

package xxx.deadbytes.networcat

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.os.Message
import android.os.Messenger
import de.robv.android.xposed.IXposedHookLoadPackage
import de.robv.android.xposed.XC_MethodHook
import de.robv.android.xposed.XposedBridge
import de.robv.android.xposed.XposedHelpers
import de.robv.android.xposed.callbacks.XC_LoadPackage

class Main : IXposedHookLoadPackage {

    private var config = mapOf(
        "pref_country_iso" to "",
        "pref_numeric" to "",
        "pref_operator" to ""
    )

    private var configMessenger: Messenger? = null
    private var clientMessenger: Messenger? = null

    override fun handleLoadPackage(lpparam: XC_LoadPackage.LoadPackageParam?) {
        if (lpparam == null) return

        XposedBridge.log("NetworCat: Module loaded for ${lpparam.packageName}")

        loadConfigViaService(lpparam)

        hookAllTelephony(lpparam)
    }

    private fun loadConfigViaService(lpparam: XC_LoadPackage.LoadPackageParam) {
        try {
            val contextClass = XposedHelpers.findClass("android.app.ActivityThread", lpparam.classLoader)
            val context = XposedHelpers.callStaticMethod(contextClass, "currentApplication") as Context

            val handler = object : Handler(Looper.getMainLooper()) {
                override fun handleMessage(msg: Message) {
                    if (msg.what == MSG_CONFIG_RESULT) {
                        val iso = msg.data.getString("country_iso", "") ?: ""
                        val numeric = msg.data.getString("numeric", "") ?: ""
                        val op = msg.data.getString("operator", "") ?: ""
                        config = mapOf(
                            "pref_country_iso" to iso,
                            "pref_numeric" to numeric,
                            "pref_operator" to op
                        )
                        XposedBridge.log("NetworCat: Config loaded: ISO=$iso NUM=$numeric OP=$op")
                    }
                }
            }

            clientMessenger = Messenger(handler)

            val serviceIntent = Intent().apply {
                setClassName("xxx.deadbytes.networcat", "xxx.deadbytes.networcat.ConfigService")
            }

            val conn = object : ServiceConnection {
                override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
                    configMessenger = Messenger(service)
                    val msg = Message.obtain(null, MSG_GET_CONFIG)
                    msg.replyTo = clientMessenger
                    configMessenger?.send(msg)
                }
                override fun onServiceDisconnected(name: ComponentName?) {
                    configMessenger = null
                }
            }

            context.bindService(serviceIntent, conn, Context.BIND_AUTO_CREATE)
        } catch (t: Throwable) {
            XposedBridge.log("NetworCat: Service error: ${t.message}")
        }
    }

    private fun hookAllTelephony(lpparam: XC_LoadPackage.LoadPackageParam?) {
        try {
            val telephonyClass = XposedHelpers.findClass("android.telephony.TelephonyManager", lpparam!!.classLoader)

            hookMethod(telephonyClass, "getNetworkOperator", "pref_numeric")
            hookMethod(telephonyClass, "getNetworkOperatorName", "pref_operator")
            hookMethod(telephonyClass, "getNetworkCountryIso", "pref_country_iso")
            hookMethod(telephonyClass, "getSimCountryIso", "pref_country_iso")
            hookMethod(telephonyClass, "getSimOperator", "pref_numeric")
            hookMethod(telephonyClass, "getSimOperatorName", "pref_operator")

            XposedBridge.log("NetworCat: All hooks installed")
        } catch (t: Throwable) {
            XposedBridge.log("NetworCat: Hook error: ${t.message}")
        }
    }

    private fun hookMethod(telephonyClass: Class<*>, methodName: String, configKey: String) {
        XposedHelpers.findAndHookMethod(telephonyClass, methodName, object : XC_MethodHook() {
            override fun afterHookedMethod(param: MethodHookParam) {
                val value = config[configKey]
                if (!value.isNullOrEmpty()) {
                    param.result = value
                    XposedBridge.log("NetworCat: $methodName -> '$value'")
                } else {
                    XposedBridge.log("NetworCat: $methodName -> original (no config '$configKey')")
                }
            }
        })
        XposedBridge.log("NetworCat: Hooked $methodName -> $configKey")
    }

    companion object {
        private const val MSG_GET_CONFIG = 1
        private const val MSG_CONFIG_RESULT = 2
    }
}
