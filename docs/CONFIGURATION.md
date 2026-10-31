# Home configuration

Both editions share the same appearance configuration and package ID. The selected APK determines
whether Settings is stock or integrated. A JSON profile does not change the edition or grant authority.

The shipped [Basic](../profiles/basic/config.json) and [Advanced](../profiles/advanced/config.json)
profiles contain the same visual defaults. Change appearance in the launcher itself where offered:
Basic: hold Settings and choose Launcher settings; Advanced: Other settings, Appearance (dock, wallpaper, holiday themes and motion).
Use the remote's reorder/dock controls for app positions. Only installed apps appear.

For defaults built into a custom APK, edit `app/src/main/res/values/config.xml` and rebuild with your
own consistent signing key. Relevant settings:

| Setting | Purpose |
|---|---|
| `dock_apps` | Package names in left-to-right preferred dock order |
| `hidden_apps` | Package names hidden from Home, without disabling/removing them |
| `columns` | Dock/grid column count, 4 through 7 |
| `top_bar_apps` | Installed app shortcuts, including the existing casting/USB receivers |
| `show_greeting`, `greeting_name` | Show greeting and optional text/name |
| `wallpaper` in JSON | Whether a supported local wallpaper/theme image is drawn |
| `seasonal_mode` (config.xml), `seasonal` (JSON) | Holiday skins: `off`, `auto` or one holiday. Basic defaults to off, Advanced to auto; see [Seasonal themes](hy320pro/SEASONAL.md) |
| `uniform_tiles` | False keeps the shipped artwork layout; true uses the alternate uniform-card layout |

The launcher clock follows Android's system format. Basic changes it through stock Settings; Advanced
also provides the integrated 12/24-hour setting with its documented access. No account, subscription
or streaming APK is created by the configuration.

The existing ThemeProvider also supports an optional configuration overlay. An authorised device shell
can write `content://com.gathin.tvhome.theme/config.json` using Android's `content write`, after backing
up the previous file or recording its absence. It accepts supported theme paths, including local
wallpaper images. Advanced helper/projection work must be finished before restarting/updating Home.
The default setup needs no overlay: profiles document the bundled layout. This repository does not
ship the unrelated upstream theme library or its parent-checkout-dependent theme scripts.

Never import another unit's keystone, zoom, orientation, calibration or wall presets as a visual theme.
