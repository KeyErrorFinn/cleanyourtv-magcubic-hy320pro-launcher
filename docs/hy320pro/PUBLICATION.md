# Publication checklist

The main branch publishes Basic and helper-free Advanced. Ultimate's helper-enabled implementation,
documentation, builds and downloads belong to its separate branch. Upstream contribution text must
refer only to helper-free builds and require debugging off after setup.

Before publishing an APK, run `python scripts/test_release.py` and `python scripts/build.py` with
the desired editions and the publisher's private signing key. Inspect final merged manifests and
DEX: no INTERNET, settings-session/starter providers, native projector bridge or pairing client in
either main-branch APK. Verify hashes, certificate and matching version/code, and distinguish build
checks from physical projector tests. Retain all relevant licences.

Version 1.3.0 outputs are in `dist/helper-free/`; never mix old companion APKs or old Advanced assets
into them. Existing v1.0.1 release assets predate this split and must not be silently replaced or
relabelled. Do not move existing source tags. Public releases need accurate physical verification
limits and instructions for upgrading from old helper-enabled Advanced.

Never publish private backups, calibration, device/network/account identifiers, pairing identities
or signing keys. Screenshot descriptions must say which actual build was captured. Main source and
release packaging are independent of the upstream reference APK-size gate.
