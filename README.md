# CPU Governor — TB-X606F
### Lenovo Tab M10 HD 2nd Gen · Mediatek Helio P22T

A native Android app for controlling CPU governor and frequency on the TB-X606F.
Requires a **rooted device** (Magisk recommended).

---

## Requirements
- Android Studio Hedgehog (2023.1.1) or newer
- JDK 17 (bundled with Android Studio)
- Android SDK 34
- A rooted TB-X606F with Magisk

---

## How to build

1. Open Android Studio → **File → Open** → select this `CpuGovernor` folder
2. Wait for Gradle sync to complete (it will download dependencies automatically)
3. Connect your TB-X606F via USB with USB debugging enabled, OR
4. **Build → Build Bundle(s) / APK(s) → Build APK(s)**
5. The APK will appear at:
   `app/build/outputs/apk/debug/app-debug.apk`

---

## How to install (sideload)

### Option A — Android Studio (easiest)
With the tablet connected via USB:
- Click **Run ▶** in Android Studio

### Option B — ADB
```bash
adb install app/build/outputs/apk/debug/app-debug.apk
```

### Option C — Copy APK to tablet
Copy `app-debug.apk` to the tablet, open it with a file manager,
and allow "Install from unknown sources" when prompted.

---

## How it works

The app executes root shell commands via `su -c` to write to:
```
/sys/devices/system/cpu/cpuN/cpufreq/scaling_governor
/sys/devices/system/cpu/cpuN/cpufreq/scaling_max_freq
```
across all 8 cores of the Helio P22T.

**Profiles:**
| Profile       | Governor      | Max Freq  | Est. drain  |
|---------------|---------------|-----------|-------------|
| Battery saver | powersave     | 600 MHz   | 6–10 %/hr  |
| Eco           | conservative  | 1196 MHz  | 10–16 %/hr |
| Balanced      | schedutil     | 1690 MHz  | 16–24 %/hr |
| Performance   | performance   | 2000 MHz  | 25–38 %/hr |

**Persists across reboots** via `BootReceiver` — the last applied profile
is saved to SharedPreferences and reapplied on `BOOT_COMPLETED`.

---

## First launch
When you open the app for the first time, your root manager (Magisk)
will show a permission prompt — tap **Grant**. Without this the app
cannot modify governor settings.

---

## File structure
```
CpuGovernor/
├── app/src/main/java/com/tbx606f/cpugovernor/
│   ├── App.kt                  — Application class, initializes prefs
│   ├── MainActivity.kt         — UI controller
│   ├── MainViewModel.kt        — State management, coroutines
│   ├── CpuGovernorManager.kt   — All root shell / sysfs logic
│   ├── PowerProfile.kt         — Profile data + presets
│   ├── ProfilePrefs.kt         — SharedPreferences persistence
│   ├── CoreAdapter.kt          — RecyclerView adapter for core list
│   └── BootReceiver.kt         — Restores governor after reboot
├── app/src/main/res/
│   ├── layout/activity_main.xml
│   ├── layout/item_core.xml
│   ├── values/colors.xml
│   ├── values/strings.xml
│   └── values/themes.xml
└── README.md
```
