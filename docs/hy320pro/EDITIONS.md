# Editions and switching

| | Basic (main) | Advanced (main) | Ultimate (separate branch) |
|---|---|---|---|
| Custom Home | Yes | Yes | Yes |
| Settings design | Stock | Eight styled sections | Eight styled sections |
| Public/API-supported local controls | Launcher preferences | Volume, mute, on-demand scan, readouts and optional time/preferences access | Same plus full implementation |
| Restricted controls | Stock | Stock fallback | Firmware-specific controls and optional shell helper |
| Pairing companion / persistent settings helper | Absent | Absent | Optional |
| Debugging must stay reachable for normal use | No | No | See that branch's lifecycle documentation |

The Ultimate branch preserves the previous full settings implementation; it does not become part of
Advanced by changing a config switch. Main contains no helper, pairing transport, settings provider,
native projection writer or companion build. Ultimate uses a distinct build flavor and APK filename.
All editions retain the same Android package ID and publisher signer for deliberate updates.

## Moving from the old Advanced or Ultimate to a helper-free edition

Do this on your own unit with a recovery copy of the previous same-signed APK and private backups.
This release has not performed the migration on the original owner's projector.

1. Finish every optical operation and restore/resolve pending geometry journals first. Switch
   automatic startup off in Settings Starter and Stop helper. The install script refuses a live
   helper, incomplete operation or enabled starter; never bypass it with an unguarded install.
2. Revoke/remove the companion's own Wireless debugging pairing through Android's paired-computers
   page. Remove the ordinary Settings Starter app with Android's user confirmation. Keep your
   computer paired only for the installation interval if needed.
3. Read the old branch's permission/settings recovery guide. An APK update is not a restoration of
   changed calibration, Wi-Fi, system locale or other system settings. Restore desired original
   values/access from your own records before switching; never import someone else's values.
4. Back up and install the chosen new same-signed APK. Confirm Home, stock settings fallbacks,
   remote, HDMI, audio, normal reboot and standby/wake with the physical device.
5. Disable Wireless, vendor ADB and USB debugging enabled for setup. Remove unused authorisations
   and verify closure as the installation guide describes. Normal Basic/Advanced use does not turn
   debugging back on. The companion must be removed/disabled separately: updating the launcher does
   not remove an independently installed app.

The build version is 1.3.0 / code 130. Do not silently overwrite older release assets; source tags,
edition labels, APK hashes and signing identity must agree. An APK from another signer cannot update
your installation. Never clear launcher data merely to switch editions.
