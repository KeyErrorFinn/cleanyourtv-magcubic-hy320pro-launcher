# Coding-agent instructions

Read README.md, CONTRIBUTING.md and docs/hy320pro/EDITIONS.md. Main contains simple Basic and
helper-free styled Advanced. The separate ultimate branch preserves full settings/helper source.

- Reuse the existing native Java launcher and styled settings; do not create an unrelated app.
- No helper, pairing client, native projector writer, session/starter provider or INTERNET in main.
  Advanced must work with debugging off; restricted operations open ordinary stock screens.
- WRITE_SECURE_SETTINGS is restricted to auto_time/auto_time_zone. WRITE_SETTINGS is restricted to
  sound_effects_enabled, screen_off_timeout, time_12_24. Journal exact originals before writes.
- Keep Basic's minimal permissions and stock settings route. Preserve original signer/package ID.
- No root, firmware/system-partition/factory changes, arbitrary shell or security bypasses.
- User-device actions require exact selector and actual context; do not deploy for publication work.
  Preserve stock launcher, sole remote, network, private state and pending optical recovery checks.
- Run scripts/test_release.py and scripts/build.py. Inspect final APK authority boundaries and report
  tests/build separately from physical-device validation. No personal keys or identifiers in commits.
- Do not message the upstream maintainer about the edition split until the owner explicitly approves.
