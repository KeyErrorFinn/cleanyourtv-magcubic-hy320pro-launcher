# Current source validation

Repository publication check, 3 November 2026, starting from source commit `07be073`.
No projector connection, installation, settings change or optical adjustment was performed.

- Basic: 246 JVM tests passed; release lint has no errors; signed release assembled.
- Ultimate: 445 JVM tests passed; release lint has no errors; signed release assembled.
- Seven public-edition/installer boundary tests passed.
- Settings Starter built and APK signature verification succeeded.
- Both editions and the companion used the existing publisher signing identity.
- Source/support files and newly added screenshots were reviewed for private identifiers.

These checks do not establish physical correctness of every current feature. Prior owner-confirmed
device behavior is recorded in [Compatibility](COMPATIBILITY.md); it must not be read as a fresh
end-to-end verification of this source snapshot or of the newer firmware allowlist entry.

Existing GitHub release tags and assets are preserved. The v1.0.1 prerelease is older than current
source (version 1.2.0); this source update does not replace its binaries or silently move its tag.
