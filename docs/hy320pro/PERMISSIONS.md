# Helper-free Advanced permissions

Basic has no Advanced permission additions. Advanced declares no INTERNET, settings-session provider,
pairing permission, native bridge or CHANGE_CONFIGURATION. It cannot enable debugging or start a
shell helper. Normal use works after debugging is switched off.

| Access | Purpose and boundary |
|---|---|
| MODIFY_AUDIO_SETTINGS | Standard media volume/mute and optional local quiet-hours volume cap |
| BLUETOOTH_CONNECT | Paired-device names, adapter status and Android-confirmed pairing |
| BLUETOOTH_SCAN; legacy BLUETOOTH/ADMIN | Explicit foreground search only, bounded to 12 seconds |
| CHANGE_WIFI_STATE | Explicit foreground Wi-Fi scan; power/configuration remains stock |
| WRITE_SETTINGS special access | Only `sound_effects_enabled`, `screen_off_timeout`, `time_12_24` |
| WRITE_SECURE_SETTINGS (optional one-time grant) | Only `auto_time` and `auto_time_zone`; original values journalled |
| PACKAGE_USAGE_STATS special access | Optional app storage breakdown; absent access shows unavailable |
| REQUEST_DELETE_PACKAGES | Android confirmation for removal of a user-installed app; system/self protected |
| Existing shared location/network-state access | Optional Wi-Fi name/scan results; generic Network works without it |

WRITE_SECURE_SETTINGS is broader than those two keys. The app code restricts itself to those keys;
without the grant, selecting automatic time opens system date/time settings. No debugging, keyboard,
locale, vendor property, network credential or arbitrary secure/global setting writes are present.
The three WRITE_SETTINGS controls request normal app-specific access where available. If the firmware
has no approval activity, this explicit optional setup records access first and uses ordinary ADB:

```powershell
python scripts/device.py --device <projector-endpoint> --apply grant-advanced
```

Read the script before running it: it grants the listed optional runtime/settings access, records
original permission/app-op states in `.local/`, and does not select Home or modify a setting value.
You can instead leave access ungranted and use stock controls. Actual permission dialogs remain
Android's own. At the end of setup disable debugging and revoke unused pairing/authorisation.

[Recovery](RESTORE.md) restores exact original access from your private record. No permission makes
every manufacturer API available; Advanced deliberately uses stock fallbacks for those controls.
