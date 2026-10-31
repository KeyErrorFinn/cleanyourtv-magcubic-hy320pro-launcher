# Source and third-party notices

This launcher is derived from [Gathin23/CleanYourTV](https://github.com/Gathin23/cleanyourtv),
MIT licensed. The reference source snapshot was `eb0131a3`; device-specific development and
public Basic/Advanced packaging were subsequently published in
[the contribution fork](https://github.com/KeyErrorFinn/cleanyourtv-magcubic-hy320pro-launcher-contrib).
This standalone extraction starts from its commit `99192faa` (2026-10-31).
Original copyright/permission text is retained in [LICENSE](LICENSE).

- Outfit font: [SIL Open Font License](FONT-LICENSE-OFL.txt).
- Seasonal skin pictures: Microsoft's [Fluent Emoji](https://github.com/microsoft/fluentui-emoji) 3D, MIT;
  the list and licence are in [ICONS-LICENSE-FLUENT-EMOJI.txt](ICONS-LICENSE-FLUENT-EMOJI.txt).
- AndroidX RecyclerView/DynamicAnimation: Apache 2.0, resolved by Gradle; see the upstream dependency projects.
- Gradle wrapper: Gradle build tooling, Apache 2.0; the wrapper downloads the pinned distribution
  specified in `gradle/wrapper/gradle-wrapper.properties`.
- The companion's native ADB pairing transport adapts
  [RikkaApps/Shizuku](https://github.com/RikkaApps/Shizuku) at
  `b844bc491f1790c72328e1a8e5b2349f8978f0ea`. Its Apache 2.0 licence is retained in
  [settings-starter/licenses](settings-starter/licenses), with the exact revision also in
  [upstream-revision.json](settings-starter/upstream-revision.json).
- The companion downloads exact SHA256-pinned BoringSSL and Conscrypt dependencies. Their licence
  and notice files are retained in the same licence directory and packaged into the companion APK.
- Source comments retain relevant attribution. No manufacturer APK/library, streaming application,
  subscription, private signing/ADB key or personal device backup is redistributed.

The old upstream reference copies used during investigation are not required to build this project;
the actual adapted source, pinned downloads and notices are included. This repository is independent
of the upstream reference launcher's contribution and size policies and does not claim upstream approval.
