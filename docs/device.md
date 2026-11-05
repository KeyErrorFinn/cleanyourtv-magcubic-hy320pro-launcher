# Magcubic HY320 Pro

Original unit: Android 14 / SDK 34, firmware `Projector_20260713.153921`, device `rk3566_rgo`,
board `exdroid`, `armeabi-v7a`/`armeabi`, about 1 GB RAM. Source additionally recognises
`Projector_20261014.163241`; this is not equivalent physical verification.

Stock Home: `com.htc.magcubicos/com.htc.magcubicos.activity.MainActivity`. Keep it enabled.
No package-disable list is supplied; streaming, casting, stores, updates and unknown components stay.
Similarly named projector models do not establish compatibility.

Choose [Basic](../profiles/basic/README.md) for Home with stock settings or
[Advanced](../profiles/advanced/README.md) for styled settings with supported direct controls and
stock fallbacks. Neither includes a settings helper or leaves debugging available after setup.
The former helper-enabled implementation is preserved on the separate Ultimate branch.
[Edition comparison/migration](hy320pro/EDITIONS.md).

Follow [Build](hy320pro/BUILD.md), [Install](hy320pro/INSTALL.md), optional
[Permissions](hy320pro/PERMISSIONS.md) and [Restore](hy320pro/RESTORE.md).
Use only your own backups and calibration. No root, factory menu, flashing or system partition writes.
Physical verification of the new split remains pending; [limits](hy320pro/COMPATIBILITY.md).
