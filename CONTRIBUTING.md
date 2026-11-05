# Contributing to HY320 Pro Launcher

This project maintains Basic and Ultimate editions for the specifically verified HY320 Pro firmware.
The general CleanYourTV reference launcher is maintained [upstream](https://github.com/Gathin23/cleanyourtv).

Describe the edition, firmware/build, SDK and observed behavior. Omit private network/device/account
identifiers. A matching retail name alone does not establish compatibility. Do not remove device,
firmware, caller/signature or transaction/recovery checks to claim support for a different unit.

Run `python scripts/test_release.py` and the build described in [BUILD.md](docs/hy320pro/BUILD.md).
The build runs the selected edition's JVM tests and lint before assembling. Explain physical tests
separately from software/static checks. UI/control changes need actual device screenshots and owner
confirmation where optics, remote keys, audio or HDMI cannot be proved by screenshots.

Preserve Basic's stock-settings route and minimal authority. Ultimate changes must retain typed
operations, exact-original-state recovery and clear permission scope. Never add root, factory-menu,
system-partition/firmware changes, authentication bypasses or silent package disabling. Keep the stock
launcher enabled. Do not disrupt the sole Bluetooth remote or network during unattended checks.

Keep private keys, credentials, pairing records, calibration, dumps and personal screenshots out of
source and release assets. New releases must keep the publisher's signing identity and retain all
required notices. Contributions are under the project's MIT licence, subject to existing third-party licences.
