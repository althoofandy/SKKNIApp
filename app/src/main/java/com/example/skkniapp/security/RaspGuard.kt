package com.example.skkniapp.security

import android.app.Application
import android.util.Log
import com.aheaditec.talsec_security.security.api.SuspiciousAppInfo
import com.aheaditec.talsec_security.security.api.Talsec
import com.aheaditec.talsec_security.security.api.TalsecConfig
import com.aheaditec.talsec_security.security.api.TalsecMode
import com.aheaditec.talsec_security.security.api.ThreatListener

/**
 * Wraps freeRASP (Talsec) runtime application self-protection: root/jailbreak,
 * emulator, debugger, hook framework (Frida/Xposed), tampering and untrusted
 * install-source detection. Threats are currently logged only — see
 * [ThreatListener.ThreatDetected] callbacks below to plug in real handling
 * (e.g. force-logout, block sensitive screens) before shipping to production.
 */
object RaspGuard {

    private const val TAG = "RaspGuard"

    // TODO: replace with the release keystore's SHA-256 signing certificate
    // hash(es) (base64) before shipping a signed build, otherwise freeRASP
    // will always report a tamper/signature mismatch.
    private const val EXPECTED_PACKAGE_NAME = "com.example.skkniapp"
    private val EXPECTED_SIGNING_CERTIFICATE_HASHES = arrayOf(
        "REPLACE_WITH_RELEASE_SIGNING_CERT_SHA256_BASE64"
    )
    private const val WATCHER_MAIL = "security@example.com"

    fun install(application: Application, isProductionBuild: Boolean) {
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

    private fun warn(message: String) {
        Log.w(TAG, message)
    }
}
