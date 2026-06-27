package com.example.skkniapp.security

import android.app.Activity
import android.app.Application
import android.os.Build
import android.os.Bundle
import android.os.Debug
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import com.scottyab.rootbeer.RootBeer
import androidx.appcompat.app.AlertDialog
import com.aheaditec.talsec_security.security.api.SuspiciousAppInfo
import com.aheaditec.talsec_security.security.api.Talsec
import com.aheaditec.talsec_security.security.api.TalsecConfig
import com.aheaditec.talsec_security.security.api.TalsecMode
import com.aheaditec.talsec_security.security.api.ThreatListener
import java.lang.ref.WeakReference
import kotlin.system.exitProcess

object RaspGuard {

    private const val TAG = "RaspGuard"

    private const val EXPECTED_PACKAGE_NAME = "com.example.skkniapp"
    private val EXPECTED_SIGNING_CERTIFICATE_HASHES = arrayOf(
        "M/jxW966OIazCDsz8PKGkwXzSJvl1GfSVesekBIJKVk="
    )
    private const val WATCHER_MAIL = "security@example.com"

    private val mainHandler = Handler(Looper.getMainLooper())

    private var currentActivity: WeakReference<Activity>? = null

    private val activeThreats = linkedSetOf<String>()

    private var dialog: AlertDialog? = null

    fun install(application: Application, isProductionBuild: Boolean) {
        application.registerActivityLifecycleCallbacks(activityTracker)

        preflight(application)

        val config = TalsecConfig.Builder(
            EXPECTED_PACKAGE_NAME,
            EXPECTED_SIGNING_CERTIFICATE_HASHES
        )
            .watcherMail(WATCHER_MAIL)
            .prod(isProductionBuild)
            .killOnBypass(isProductionBuild)
            .build()

        val threatDetectedListener = object : ThreatListener.ThreatDetected() {
            override fun onRootDetected() = warn("Root/jailbreak detected")
            override fun onDebuggerDetected() = warn("Debugger attached")
            override fun onEmulatorDetected() = warn("Running on an emulator")
            override fun onTamperDetected() = warn("App tampering/repackaging detected")
            override fun onUntrustedInstallationSourceDetected() =
                warn("Installed from an untrusted source")
            override fun onHookDetected() = warn("Hooking framework detected (Frida/Xposed)")
            override fun onDeviceBindingDetected() = warn("Device binding mismatch")
            override fun onObfuscationIssuesDetected() = warn("Obfuscation issue detected")
            override fun onMultiInstanceDetected() = warn("Multiple app instances detected")
            override fun onMalwareDetected(suspiciousApps: List<SuspiciousAppInfo>) =
                warn("Suspicious/malware apps detected: ${suspiciousApps.size}")
        }

        val deviceStateListener = object : ThreatListener.DeviceState() {
            override fun onUnlockedDeviceDetected() = warn("Bootloader is unlocked")
            override fun onDeveloperModeDetected() = warn("Developer mode is enabled")
            override fun onADBEnabledDetected() = warn("USB debugging (ADB) is enabled")
        }

        ThreatListener(threatDetectedListener, deviceStateListener)
            .registerListener(application)

        Talsec.start(application, config, TalsecMode.BACKGROUND)
    }

    private fun preflight(application: Application) {
        if (isEmulator()) warn("Running on an emulator")
        if (RootBeer(application).isRooted) warn("Root/jailbreak detected")
        if (Debug.isDebuggerConnected() || Debug.waitingForDebugger()) warn("Debugger attached")

        val resolver = application.contentResolver
        if (Settings.Global.getInt(resolver, Settings.Global.ADB_ENABLED, 0) == 1) {
            warn("USB debugging (ADB) is enabled")
        }
        if (Settings.Global.getInt(resolver, Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, 0) == 1) {
            warn("Developer mode is enabled")
        }
    }

    private fun isEmulator(): Boolean {
        return Build.FINGERPRINT.startsWith("generic")
            || Build.FINGERPRINT.startsWith("unknown")
            || Build.MODEL.contains("google_sdk")
            || Build.MODEL.contains("Emulator")
            || Build.MODEL.contains("Android SDK built for")
            || Build.MANUFACTURER.contains("Genymotion")
            || Build.PRODUCT == "google_sdk"
            || Build.PRODUCT.contains("sdk_gphone")
            || Build.HARDWARE.contains("goldfish")
            || Build.HARDWARE.contains("ranchu")
            || (Build.BRAND.startsWith("generic") && Build.DEVICE.startsWith("generic"))
    }

    private fun warn(message: String) {
        Log.w(TAG, message)
        mainHandler.post { reportThreat(message) }
    }

    private fun reportThreat(message: String) {
        if (!activeThreats.add(message)) return
        showOrUpdateDialog()
    }

    private fun showOrUpdateDialog() {
        val activity = currentActivity?.get() ?: return
        if (activity.isFinishing || activity.isDestroyed) return

        val body = activeThreats.joinToString(separator = "\n") { "•  $it" }
        val existing = dialog
        if (existing != null && existing.isShowing) {
            existing.setMessage(body)
            return
        }

        dialog = AlertDialog.Builder(activity)
            .setTitle("⚠️ Security threat detected")
            .setMessage(body)
            .setCancelable(false)
            .setPositiveButton("Tutup aplikasi") { _, _ -> closeApp() }
            .create()
            .apply {
                setCanceledOnTouchOutside(false)
                show()
            }
    }

    private fun closeApp() {
        dismissDialog()
        currentActivity?.get()?.finishAffinity()
        currentActivity = null
        exitProcess(0)
    }

    private fun dismissDialog() {
        dialog?.takeIf { it.isShowing }?.dismiss()
        dialog = null
    }

    private val activityTracker = object : Application.ActivityLifecycleCallbacks {
        override fun onActivityResumed(activity: Activity) {
            currentActivity = WeakReference(activity)
            if (activeThreats.isNotEmpty()) showOrUpdateDialog()
        }

        override fun onActivityPaused(activity: Activity) {
            if (currentActivity?.get() === activity) {
                dismissDialog()
                currentActivity = null
            }
        }

        override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit
        override fun onActivityStarted(activity: Activity) = Unit
        override fun onActivityStopped(activity: Activity) = Unit
        override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
        override fun onActivityDestroyed(activity: Activity) = Unit
    }
}
