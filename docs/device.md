# Magcubic HY320 Pro

Device setup with two versions: **Basic** keeps manufacturer Settings; **Advanced** includes the integrated settings controls. This uses the device-note format for a unit without a tested debloat list. No system packages were selected for disabling.

## Device

| Item | Observed unit |
|---|---|
| Retail model | Magcubic HY320 Pro |
| Android | 14, SDK 34 |
| Firmware | `Projector_20260713.153921` |
| Device / model identifier | `rk3566_rgo` |
| Board | `exdroid` |
| CPU ABI | `armeabi-v7a`, `armeabi` |
| Memory | About 1 GB |
| Stock Home | `com.htc.magcubicos/com.htc.magcubicos.activity.MainActivity` |

Match the actual firmware/device/board, not only the retail name. HY320, HY320 Mini, HY300 and HY300 Pro contributions are not compatibility proof for this unit. Neither version requires root, firmware flashing, factory menus or system-partition changes. Keep stock MagcubicOS installed and enabled.

## Choose a version

| | [Basic](../profiles/basic/README.md) | [Advanced](../profiles/advanced/README.md) |
|---|---|---|
| Home design | Six-slot dock, black background, expanding shortcuts and app library | Same |
| App artwork | Fills each tile, with cropping | Same |
| Main Settings button | Stock manufacturer Settings | Integrated eight-section Settings |
| Projection controls | Stock pages | Integrated controls on verified firmware |
| Additional settings authority | None required for stock pages | Explicit documented permission/helper setup |
| Boot settings helper | Not used | Optional separately installed Settings Starter |
| APK | `CleanYourTV-HY320Pro-Basic.apk` | `CleanYourTV-HY320Pro-Advanced.apk` |
| Appearance profile | [config.json](../profiles/basic/config.json) | [config.json](../profiles/advanced/config.json) |

Both versions use `com.gathin.tvhome`, so choose one; they do not install side by side. Use the same signing identity for updates. Finish projection operations and disable/stop the Advanced helper before an edition switch. Configuration files control appearance; they cannot implement the custom interface, choose an APK edition or grant authority.

## Files

| File or directory | Purpose |
|---|---|
| This device note | Hardware, version choice, setup and recovery entry point |
| [Basic profile](../profiles/basic/README.md) | Basic setup links and appearance configuration |
| [Advanced profile](../profiles/advanced/README.md) | Advanced setup links and appearance configuration |
| [Implementation source](../README.md) | Both build flavors, tests and the optional companion |
| [Installation guide](hy320pro/INSTALL.md) | Explicit-device verification, private backup, installation and Home selection |
| [Build guide](hy320pro/BUILD.md) | Toolchain and per-builder signing |
| [Permissions/helper guide](hy320pro/PERMISSIONS.md) | Advanced access, pairing and helper lifetime |
| [Restore guide](hy320pro/RESTORE.md) | Stock Home, original access states, companion cleanup and removal |

This repository contains only the HY320 Pro implementation and supporting files. Upstream device lists and unrelated tools/themes are not copied. No debloat list is invented.

## Setup

1. Establish physical Home/Back, Settings, projection-control access, HDMI video/audio, held-volume/mute, streaming playback and used Bluetooth/USB/casting features. Record anything untested or already broken; do not change calibration to test access.
2. Enable only the debugging needed through ordinary settings on your unit. Prefer paired Wireless debugging and its actual pairing/connection ports. Accept normal Android authorisation yourself; do not use guessed hidden sequences or assume a legacy port.
3. Follow the installation guide to verify the exact device and save its own private baseline. Build from source or inspect the matching release source, APK hash and signing certificate.
4. Install and open the selected version manually. Installation does not silently choose Home, grant Advanced access or disable packages.
5. Test the app grid and physical remote, then select Home separately. Confirm return from apps, Settings and HDMI, then normal reboot. Keep stock MagcubicOS enabled.
6. Advanced only: choose the documented grants and optional helper setup. The optional boot starter briefly enables authenticated paired Wireless debugging, verifies the helper, then turns debugging off. Confirm its live status after reboot without pressing Start helper now.

Streaming apps, stores, casting, voice/update services and unidentified projector components were preserved. Missing dock applications are not installed automatically. AirScreen and Miracast remain separate receivers; the ChromeCast shortcut does not provide certified Chromecast built-in. No DNS filtering is silently applied.

## Restore

Use Android's Home chooser, or the supported explicit-device command in the restore guide, to select the recorded stock Home again. Undo Advanced changes/access using that owner's original-state records. Do not blindly enable packages or import another owner's settings. Disable automatic helper startup and stop it before removing the ordinary user apps. Close/revoke debugging access no longer needed and verify the previously used ports. Do not interrupt normal shutdown or cooling.

Backups and restore scripts are not firmware backups and do not guarantee recovery from boot failure. Never share personal backups, ADB/signing keys, accounts, Wi-Fi details or another unit's calibration.

## Verification

Original Advanced controls, physical navigation, HDMI/streaming audio and boot-helper operation were verified on one projector. The owner confirmed live helper connection with Wireless debugging off after normal reboot. Public Basic/Advanced packaging has build/static checks; those exact artifacts have not been physically retested on a second unit. See [compatibility and remaining limits](hy320pro/COMPATIBILITY.md).
