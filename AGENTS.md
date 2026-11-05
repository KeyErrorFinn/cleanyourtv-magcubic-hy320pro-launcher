# Instructions for coding agents

Read README.md and CONTRIBUTING.md. This is the standalone HY320 Pro launcher, not the upstream
CleanYourTV repository. Do not recreate upstream's unrelated device lists/tools or assume a parent checkout.

- `app/src/main` is shared; `app/src/basic` and `app/src/ultimate` are the two editions.
- `settings-starter` is the separately signed companion; `scripts` contains explicit-device setup,
  backup/access restoration, build and checks; `profiles` contains supported appearance defaults.
- Both editions intentionally use the same package ID and update signing identity. Companion and
  Ultimate launcher must share a signer. Never embed/export private signing keys.
- Basic must not gain the Ultimate permissions, providers, native settings bridge or helper.
- Retain exact firmware/user/device checks, typed shell operations, nonce/caller/signature checks,
  original-state journals and rollback behavior. No arbitrary command/settings interface.
- Real-device actions require the user's actual device context. Select its exact serial/endpoint
  on every ADB call. Never infer permission for optical/network/remote-disrupting writes from static tests.
- No root, bootloader unlock, flashing, system partitions, factory/engineering menus, verifier bypasses
  or disabling essential/projector components. Keep stock Home installed/enabled.
- For release work, run scripts/test_release.py and scripts/build.py. Report tests, lint/build,
  APK identity/hash checks and physical verification limits separately. Do not deploy to the projector
  merely because the user requests source/repository publication.
- No secrets, personal backups, keys, pairing/trust state, calibration or private identifiers in commits.
  Preserve LICENSE, FONT-LICENSE-OFL.txt, NOTICE.md and companion dependency licences.
