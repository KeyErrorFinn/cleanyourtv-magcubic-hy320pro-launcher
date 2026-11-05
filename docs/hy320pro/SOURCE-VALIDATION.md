# Edition split validation

5 November 2026. Version 1.3.0, code 130; source prepared locally for owner review.

- Basic: 246 JVM tests, no failures; zero lint errors; signed release build passed.
- Helper-free Advanced: 256 JVM tests, no failures; zero lint errors; signed release build passed.
- Seven installer/edition boundary tests pass.
- `python scripts/check-apks.py` verifies both final APK hashes, matching publisher signatures,
  no INTERNET/CHANGE_CONFIGURATION, no settings-session/starter/recovery providers, no native
  libraries, no pairing service names and no debugging-key writer strings in DEX.
- Basic APK: 650,400 bytes. Advanced APK: 684,412 bytes.
- Original publisher certificate retained; no key, pairing identity or private state included.
- No projector connection, installation, calibration or debugging changes were performed.

The helper-enabled implementation is preserved as Ultimate on a separate local branch. No new
GitHub release or upstream update is claimed here. Existing v1.0.1 assets are historical and do not
represent helper-free Advanced. Build outputs are isolated in `dist/helper-free/`.

Physical verification of the new split remains pending. Check normal boot/Home, physical D-pad and
Back, the eight sections and stock fallbacks, volume/mute/held repeat, Bluetooth/Wi-Fi scan and
permissions, file picker, HDMI/streaming audio, standby/wake with debugging off. Source/package
checks do not prove those behaviors. See COMPATIBILITY.md and EDITIONS.md for limits and migration.
