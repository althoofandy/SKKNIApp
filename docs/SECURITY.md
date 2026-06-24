# Mobile Security

← [Back to README](../README.md)

## Platform-level hardening

`app/src/main/AndroidManifest.xml`:

```xml
<application
    android:name=".SkkniApp"
    android:allowBackup="false"
    ...>
    <activity
        android:name=".MainActivity"
        android:exported="true"
        android:launchMode="singleInstance"
        android:taskAffinity="">
        ...
    </activity>
</application>
```

| Setting | Why |
|---|---|
| `android:allowBackup="false"` | Prevents app data — including the favorites Room database — from being extracted via `adb backup`. |
| Runtime location permission | Requested via `ActivityResultContracts.RequestPermission` instead of relying on install-time grants; the app degrades gracefully (Snackbar) if denied. |
| HTTPS-only networking | All calls target `https://` endpoints on public, key-less weather/geocoding APIs — no credentials or secrets are stored in the app. |
| `singleInstance` + empty `taskAffinity` | Reduces the surface for task-hijacking / activity-injection attacks against `MainActivity`. |

## Runtime Application Self-Protection (RASP)

The app integrates [freeRASP](https://docs.talsec.app/freerasp) by Talsec (`TalsecSecurity-Community`) to detect device/runtime threats at startup and continuously in the background.

**Detected threats:**

| Category | Detected via |
|---|---|
| Root / jailbreak | `onRootDetected()` |
| Attached debugger | `onDebuggerDetected()` |
| Emulator | `onEmulatorDetected()` |
| App tampering / repackaging | `onTamperDetected()` |
| Untrusted installation source (sideload) | `onUntrustedInstallationSourceDetected()` |
| Hooking frameworks (Frida, Xposed) | `onHookDetected()` |
| Multiple running instances | `onMultiInstanceDetected()` |
| Known malware / suspicious apps installed | `onMalwareDetected(suspiciousApps)` |
| Unlocked bootloader | `onUnlockedDeviceDetected()` |
| Developer mode enabled | `onDeveloperModeDetected()` |
| USB debugging (ADB) enabled | `onADBEnabledDetected()` |

### Gradle setup

`settings.gradle.kts` — repositories (the Talsec repo **must** be declared last):

```kotlin
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        // freeRASP (Talsec) artifact repository — must stay last.
        maven { url = uri("https://europe-west3-maven.pkg.dev/talsec-artifact-repository/freerasp") }
    }
}
```

`gradle/libs.versions.toml`:

```toml
freerasp = "18.0.2"
talsec-freerasp = { group = "com.aheaditec.talsec.security", name = "TalsecSecurity-Community", version.ref = "freerasp" }
```

`app/build.gradle.kts`:

```kotlin
implementation(libs.talsec.freerasp)
```

> ⚠️ The published Talsec docs reference the package `com.aheaditec.talsec.security.*`, but the actual `18.0.2` artifact ships its public API under `com.aheaditec.talsec_security.security.api.*` (verified by decompiling the resolved AAR). Import from that package, not from the docs' sample code.

### Integration code

`app/src/main/java/com/example/skkniapp/security/RaspGuard.kt`:

```kotlin
object RaspGuard {

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
}
```

Wired up in `SkkniApp.kt`:

```kotlin
override fun onCreate() {
    super.onCreate()
    startKoin { ... }
    RaspGuard.install(this, isProductionBuild = !BuildConfig.DEBUG)
}
```

Detections are currently logged via `Log.w` only — plug in real handling (force logout, block sensitive screens, send telemetry) before shipping to production.

### Configuring freeRASP before a release build

`RaspGuard` ships with placeholder values that **must** be replaced before a signed release build, otherwise freeRASP will treat the app itself as tampered:

1. Get the release keystore's SHA-256 signing certificate hash, base64-encoded:
   ```bash
   keytool -list -v -keystore your-release.keystore -alias your-key-alias
   # or, against a built APK:
   apksigner verify --print-certs app-release.apk
   ```
2. Replace `EXPECTED_SIGNING_CERTIFICATE_HASHES` in `RaspGuard.kt` with that hash.
3. Replace `WATCHER_MAIL` with a real mailbox that should receive Talsec threat alerts.
4. `RaspGuard.install()` passes `isProductionBuild = !BuildConfig.DEBUG`, so debug builds run in non-`prod` mode (report-only) while release builds run in `prod` mode with `killOnBypass` enabled.
