# ⚡ StormBeats

An Amoled black music player for Android powered by JioSaavn API.

[![Latest Release](https://img.shields.io/github/v/release/YOUR_USERNAME/StormBeats?style=flat-square&color=000000&labelColor=ffffff)](https://github.com/YOUR_USERNAME/StormBeats/releases)
[![License](https://img.shields.io/github/license/YOUR_USERNAME/StormBeats?style=flat-square)](LICENSE)

## Features

- 🎵 Stream millions of songs via JioSaavn
- 🔍 Search songs and artists
- ⬛ Pure Amoled black UI (#000000)
- 🔔 Background playback with notification controls
- 🔄 Auto-update — app checks GitHub for new versions

## Download

Get the latest APK from [Releases](https://github.com/YOUR_USERNAME/StormBeats/releases/latest)

## Building from Source

```bash
git clone https://github.com/YOUR_USERNAME/StormBeats.git
cd StormBeats
./gradlew assembleDebug
```

## Auto-Update System

When a new release is published on GitHub, the app automatically detects it on next launch and prompts the user to update.

To release a new version:
1. Push a tag: `git tag v1.1.0 && git push origin v1.1.0`
2. GitHub Actions builds the APK automatically
3. A release is created with the APK attached
4. Users get notified in-app on next launch

## Tech Stack

- Kotlin + Jetpack
- ExoPlayer / Media3 for playback
- Retrofit + OkHttp for API
- JioSaavn API (`savanapi-eta.vercel.app`)
- GitHub Actions for CI/CD

## License

GPL-3.0
