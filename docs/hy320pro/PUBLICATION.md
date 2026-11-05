# Standalone publication validation

Version 1.0.1 extracts only the launcher, Basic/Ultimate flavors, companion, relevant tests/scripts, configuration, device documentation and required licences. No upstream device catalog, theme library, add-ons or maintainer workflows are copied. No parent checkout is required.

The source is derived from the prior published HY320 Pro implementation; see NOTICE.md. Package IDs and signing identity are retained; versionCode advances from 100 to 101. This is a packaging/publication change, not a projector performance or hardware-function change. No device connection or installation is performed during publication.

Original Ultimate controls and helper boot behavior were verified on the owner's unit. Exact public release artifacts have not been physically retested; this release remains a prerelease. Build/static checks are recorded below after the standalone build.

Standalone validation: Basic 226 JVM tests, Ultimate 425 JVM tests, zero failures and zero lint errors in both editions. Seven release/installer boundary tests pass. Both launchers and the companion build; all three APKs have versionCode 101 and the same preserved signing certificate. Local documentation links are self-contained. Basic APK checks confirm no Ultimate secure/system settings write permissions, Bluetooth-control permission, native settings library or shell-helper class. See release-validation.json for artifact sizes.
