# Install on your own HY320 Pro

Keep normal power and ventilation. Do not use factory/engineering menus. A regular USB-A host port
does not establish support for a computer debugging cable; do not use a USB-A-to-A cable. USB debugging
may affect USB-host peripherals on some firmware; this is a possibility to check, not a proven fact
about every HY320 Pro. The paired Wireless debugging route does not require enabling USB debugging.

## Connect and verify

On the original tested firmware `Projector_20260713.153921`, the owner first installed **Activity
Launcher** (installed package `de.szalkowski.activitylauncher.oss`). In its activity list, open
**Android Settings** (`com.android.tv.settings`) →
**`com.android.tv.settings.system.development.DevelopmentActivity`**. The exact component is
`com.android.tv.settings/.system.development.DevelopmentActivity`; the saved baseline confirms it
was opened by Activity Launcher. This is normal Android Developer options, not the manufacturer's
USB debug / ADB debug page and not a factory menu. Installing Projectivy or Bare alone did not
expose this pairing page. Another activity-opening tool may work, but was not the recorded route.

Choose **Wireless debugging** → **Pair device with pairing code**. Run
`adb pair <projector-ip>:<pairing-port>` and enter the code at the terminal prompt, then
`adb connect <projector-ip>:<connection-port>` using the separate displayed connection endpoint.
Keep the code private. The launcher/helper also recognise `Projector_20261014.163241`, but this
does not establish identical physical verification of that firmware or its initial access route.
If the activity is absent or blocked, stop rather than trying factory/service activities.

Use current Android SDK Platform-Tools. Open the ordinary Android Developer options page exposed on
your unit. If there is paired Wireless debugging, pair with its pairing port, then connect using its
separate connection port. Keep the pairing code private. Never substitute a guessed port or switch
to plaintext legacy TCP ADB just to follow an example. Accept Android authorisation/Wi-Fi consent
yourself. If developer access is unavailable, stop the ADB setup.

In the commands below, replace `<projector endpoint>` with the confirmed IP:connection-port (or exact
ADB serial). Every device script requires it. No network scanning or automatic device selection occurs.

```powershell
python scripts/device.py --device <projector endpoint> inspect
python scripts/device.py --device <projector endpoint> backup
```

Backup records go in `.local/` and are private. Unsupported/unreadable items are recorded. These are
not firmware backups. They do not guarantee recovery from boot failure. Establish physical baseline:
Home/Back/Settings, HDMI picture/audio, focus and projection-control access, held-volume/mute,
streaming playback, your USB/Bluetooth/casting features and normal standby/wake. Do not change
calibration to test access. Preserve any pre-existing failures in your notes.

## Install, select Home and close debugging

Build the chosen edition and use its matching hash file. For an old helper-enabled installation,
complete [edition migration](EDITIONS.md) first. Keep a recovery copy of your previous same-signed APK.

```powershell
python scripts/device.py --device <projector-endpoint> --apply install --apk dist/helper-free/CleanYourTV-HY320Pro-Advanced.apk
```

Use `CleanYourTV-HY320Pro-Basic.apk` for Basic. Installation never silently sets Home, grants access,
disables packages or starts a helper. Open the app manually, test the physical remote and stock
fallbacks, then select Home through the normal chooser or the explicit separate command:

```powershell
python scripts/device.py --device <projector-endpoint> --apply home
```

Advanced's optional [permissions](PERMISSIONS.md) support only documented direct controls. They
are not required for stock fallbacks. Complete physical Home/Back, D-pad, volume/held repeat, settings,
HDMI/streaming audio and normal boot/standby/wake checks. Keep stock MagcubicOS enabled. Do not alter
focus, keystone or other calibration simply to check navigation.

After installation and verification, turn Wireless debugging off, remove unused computer pairings
and revoke debugging authorisations. Turn vendor ADB and USB debugging enabled for setup off too.
Use `python scripts/check-adb-port.py --host <projector-ip> --port <used-wireless-port>` before reboot, then check
5555 after normal reboot. A connection refusal establishes closure; timeout/routing errors are
inconclusive. `adb disconnect` only disconnects the computer. Neither main-branch edition enables
debugging on boot or requires a network ADB listener in normal use.

If USB peripherals stopped during debugging, reconnect them afterward and confirm actual operation.
Never interrupt normal cooling/shutdown. Backups are not firmware images; [Restore](RESTORE.md).
