# Ultimate branch validation

5 November 2026. Ultimate is the renamed full-control edition on the separate `ultimate` branch.

- 445 JVM tests passed; zero release lint errors; signed release build succeeded.
- Seven installer/signature boundary tests passed.
- All 73 original full-settings source/native/manifest files compare byte-for-byte with their
  pre-split `app/src/advanced` counterparts; only the source directory/flavor, label and delivery names
  changed. The companion additionally has an Ultimate label and version 1.3.0 / code 130.
- Ultimate APK: 844,297 bytes. Optional Ultimate Settings Starter APK: 1,590,111 bytes.
- Delivery hashes verified; the original publisher certificate is retained.
- Outputs are isolated in `dist/ultimate/`; these do not belong to the helper-free main branch.

No projector was connected or modified, and these renamed build artifacts have not received a
fresh physical run. Existing source tags/releases are preserved. No repository push, release or
maintainer message has occurred for this split; it is prepared locally for the owner's approval.
