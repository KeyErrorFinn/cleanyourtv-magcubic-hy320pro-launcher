# Restore and remove

Keep the private backup folder and previous same-signed APK off the device. Backups are not a full
firmware image and cannot guarantee recovery after boot failure. No system package is disabled by
this project's install/update scripts.

## Restore stock Home

Use Android's Home chooser or, with ordinary authorised ADB and the confirmed endpoint:

```powershell
python scripts/device.py --device <projector-endpoint> --apply home --stock
```

The recorded stock component is `com.htc.magcubicos/com.htc.magcubicos.activity.MainActivity`.
The script selects the exact device, backs up current state, sets Home and checks resolution.
Press the physical Home button to verify the result. Stock MagcubicOS remains installed and enabled.

## Restore optional Advanced settings and access

Advanced journals originals before direct changes: `standard_settings_undo` for button sound,
screen timeout and clock format, and `automatic_time_undo` for automatic time/time zone. Restore
those values using normal system pages and your private original records before removing the app.
For a setting originally absent, use the recorded namespace/key and ordinary explicit-device ADB
`settings delete` rather than inventing a default. Do not replay a full settings dump blindly.
Runtime access and WRITE_SETTINGS app-op can be restored from the script's original record:

```powershell
python scripts/device.py --device <projector-endpoint> --apply restore-access --record <your-original.json>
```

The script preserves each recorded grant state and app-op value. This restores access, not the
settings values changed while access was available. No package is blindly enabled, data cleared
or projection calibration reset. Appearance backup restores launcher choices only.

## Previously installed helper-enabled edition

If moving from old Advanced/Ultimate, follow [edition migration](EDITIONS.md) first. Finish pending
optical transactions, disable companion boot startup, stop the helper, remove its pairing and remove
the companion with Android confirmation. A launcher update alone cannot remove a separate app.
The install script retains defensive checks for an existing old helper/companion and refuses an
unsafe update. Do not bypass those checks.

After stock Home works, remove the ordinary launcher app through Android if desired. Disable all
debugging enabled for setup, remove unused pairings/authorisations and verify used ports as in
[Install](INSTALL.md). Never interrupt normal shutdown/cooling. No root or firmware modification.
