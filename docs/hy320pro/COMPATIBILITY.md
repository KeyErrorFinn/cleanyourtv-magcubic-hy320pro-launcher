# Compatibility and physical verification

Original examined unit: Magcubic HY320 Pro, Android 14 SDK 34, `rk3566_rgo`, `exdroid`, firmware
`Projector_20260713.153921`, 32-bit ARM, about 1 GB RAM. Current source also recognises
`Projector_20261014.163241`; this is not proof of identical firmware interfaces or testing.

The preceding full implementation passed owner-confirmed Home/Back, settings navigation, volume,
held repeat/mute, HDMI/streaming audio and reboot checks. Those observations apply to that earlier
implementation; they do not physically validate these new helper-free APKs or the renamed Ultimate.

The new helper-free split is build/static tested only. Required real-device checks before claiming
it tested: install/update and Home; all eight sections/D-pad/Back; returning from each stock fallback;
volume/mute/held repeat; on-demand Bluetooth/Wi-Fi scan permissions; app-info and document picker;
HDMI/streaming audio; reboot/standby/wake with debugging off. Do not change calibration or disable
Bluetooth on a unit whose only remote depends on it merely to test page access.

Known boundaries:

- Advanced projection rows open manufacturer controls; no optical writes occur in Advanced itself.
- Exact exported stock subpages vary by firmware. A standard action or the normal manufacturer
  settings root is used; users may need to select the named option there.
- Bluetooth power/connection management, network configuration, manual clock/time zone, system
  language, keyboard management, app administration and reset/update workflows remain stock.
- Missing optional settings authority opens the appropriate stock page. Missing storage usage
  access shows a limited readout. No security setting is lowered to force a control.
- Hardware HDMI/protected video can be blank in screenshots; physical confirmation is required.
- No measured app-start performance improvement or universal retail-model support is claimed.
- AirScreen/Miracast are separate apps; no certified Chromecast receiver is added.

Backups are not firmware backups. Retain stock Home and Settings; recovery depends on accessible
Settings or ADB and does not guarantee recovery from a boot failure.
