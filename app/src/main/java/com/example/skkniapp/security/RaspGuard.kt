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
import com.example.skkniapp.core.AppConstants
import com.aheaditec.talsec_security.security.api.SuspiciousAppInfo
import com.aheaditec.talsec_security.security.api.Talsec
import com.aheaditec.talsec_security.security.api.TalsecConfig
import com.aheaditec.talsec_security.security.api.TalsecMode
import com.aheaditec.talsec_security.security.api.ThreatListener
import java.lang.ref.WeakReference
import kotlin.system.exitProcess

object RaspGuard {

    private val mainHandler = Handler(Looper.getMainLooper())

    private var currentActivity: WeakReference<Activity>? = null

    private val activeThreats = linkedSetOf<String>()

    private var dialog: AlertDialog? = null

    fun install(application: Application, isProductionBuild: Boolean) {
        application.registerActivityLifecycleCallbacks(activityTracker)

        preflight(application)

        val config = TalsecConfig.Builder(
            AppConstants.RASP_EXPECTED_PACKAGE_NAME,
            AppConstants.RASP_EXPECTED_SIGNING_CERTIFICATE_HASHES
        )
            .watcherMail(AppConstants.RASP_WATCHER_MAIL)
            .prod(isProductionBuild)
            .killOnBypass(isProductionBuild)
            .build()

        val threatDetectedListener = object : ThreatListener.ThreatDetected() {
            override fun onRootDetected() = warn(AppConstants.RASP_THREAT_ROOT)
            override fun onDebuggerDetected() = warn(AppConstants.RASP_THREAT_DEBUGGER)
            override fun onEmulatorDetected() = warn(AppConstants.RASP_THREAT_EMULATOR)
            override fun onTamperDetected() = warn(AppConstants.RASP_THREAT_TAMPER)
            override fun onUntrustedInstallationSourceDetected() =
                warn(AppConstants.RASP_THREAT_UNTRUSTED_SOURCE)
            override fun onHookDetected() = warn(AppConstants.RASP_THREAT_HOOK)
            override fun onDeviceBindingDetected() = warn(AppConstants.RASP_THREAT_DEVICE_BINDING)
            override fun onObfuscationIssuesDetected() = warn(AppConstants.RASP_THREAT_OBFUSCATION)
            override fun onMultiInstanceDetected() = warn(AppConstants.RASP_THREAT_MULTI_INSTANCE)
            override fun onMalwareDetected(suspiciousApps: List<SuspiciousAppInfo>) =
                warn(AppConstants.RASP_THREAT_MALWARE_PREFIX + suspiciousApps.size)
        }

        val deviceStateListener = object : ThreatListener.DeviceState() {
            override fun onUnlockedDeviceDetected() = warn(AppConstants.RASP_THREAT_BOOTLOADER_UNLOCKED)
            override fun onDeveloperModeDetected() = warn(AppConstants.RASP_THREAT_DEVELOPER_MODE)
            override fun onADBEnabledDetected() = warn(AppConstants.RASP_THREAT_ADB_ENABLED)
        }

        ThreatListener(threatDetectedListener, deviceStateListener)
            .registerListener(application)

        Talsec.start(application, config, TalsecMode.BACKGROUND)
    }

    private fun preflight(application: Application) {
        if (isEmulator()) warn(AppConstants.RASP_THREAT_EMULATOR)
        if (RootBeer(application).isRooted) warn(AppConstants.RASP_THREAT_ROOT)
        if (Debug.isDebuggerConnected() || Debug.waitingForDebugger()) warn(AppConstants.RASP_THREAT_DEBUGGER)

        val resolver = application.contentResolver
        if (Settings.Global.getInt(resolver, Settings.Global.ADB_ENABLED, AppConstants.RASP_SETTING_DISABLED) == AppConstants.RASP_SETTING_ENABLED) {
            warn(AppConstants.RASP_THREAT_ADB_ENABLED)
        }
        if (Settings.Global.getInt(resolver, Settings.Global.DEVELOPMENT_SETTINGS_ENABLED, AppConstants.RASP_SETTING_DISABLED) == AppConstants.RASP_SETTING_ENABLED) {
            warn(AppConstants.RASP_THREAT_DEVELOPER_MODE)
        }
    }

    private fun isEmulator(): Boolean {
        return Build.FINGERPRINT.startsWith(AppConstants.RASP_EMULATOR_GENERIC)
            || Build.FINGERPRINT.startsWith(AppConstants.RASP_EMULATOR_UNKNOWN)
            || Build.MODEL.contains(AppConstants.RASP_EMULATOR_GOOGLE_SDK)
            || Build.MODEL.contains(AppConstants.RASP_EMULATOR_MODEL_EMULATOR)
            || Build.MODEL.contains(AppConstants.RASP_EMULATOR_MODEL_ANDROID_SDK_BUILT_FOR)
            || Build.MANUFACTURER.contains(AppConstants.RASP_EMULATOR_GENYMOTION)
            || Build.PRODUCT == AppConstants.RASP_EMULATOR_GOOGLE_SDK
            || Build.PRODUCT.contains(AppConstants.RASP_EMULATOR_PRODUCT_SDK_GPHONE)
            || Build.HARDWARE.contains(AppConstants.RASP_EMULATOR_HARDWARE_GOLDFISH)
            || Build.HARDWARE.contains(AppConstants.RASP_EMULATOR_HARDWARE_RANCHU)
            || (Build.BRAND.startsWith(AppConstants.RASP_EMULATOR_GENERIC) && Build.DEVICE.startsWith(AppConstants.RASP_EMULATOR_GENERIC))
    }

    private fun warn(message: String) {
        Log.w(AppConstants.RASP_LOG_TAG, message)
        mainHandler.post { reportThreat(message) }
    }

    private fun reportThreat(message: String) {
        if (!activeThreats.add(message)) return
        showOrUpdateDialog()
    }

    private fun showOrUpdateDialog() {
        val activity = currentActivity?.get() ?: return
        if (activity.isFinishing || activity.isDestroyed) return

        val body = activeThreats.joinToString(separator = AppConstants.RASP_DIALOG_SEPARATOR) {
            "${AppConstants.RASP_DIALOG_BULLET}$it"
        }
        val existing = dialog
        if (existing != null && existing.isShowing) {
            existing.setMessage(body)
            return
        }

        dialog = AlertDialog.Builder(activity)
            .setTitle(AppConstants.RASP_DIALOG_TITLE)
            .setMessage(body)
            .setCancelable(false)
            .setPositiveButton(AppConstants.RASP_DIALOG_POSITIVE_BUTTON) { _, _ -> closeApp() }
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
