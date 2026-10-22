# ⚡ CyberSSH — Secure Remote Operations

CyberSSH is Cybercore's Android-first SSH workspace: a focused host manager, interactive remote terminal, and optional AI command copilot. The operator stays in control: suggestions are reviewed before use, and the app never runs an AI-generated command automatically.

🌐 **Project site:** [cybercoretech.github.io/CyberSSH](https://cybercoretech.github.io/CyberSSH/) · [Source code](https://github.com/cybercoretech/CyberSSH)

## 🧭 Current build

CyberSSH now connects to SSH servers with Apache MINA SSHD using password authentication, verifies and pins host keys, and opens a remote PTY shell. The current terminal is a line-oriented remote shell view; full-screen terminal applications, resize signaling, key-based authentication, saved connection profiles, SFTP, and AI integration remain planned. Termux's emulator libraries are included for a later terminal-rendering adapter; the app never starts a local shell and labels only the remote channel as a session.

## 🧰 Stack

- Kotlin + Jetpack Compose for Android UI
- Apache MINA SSHD for SSH transport and SFTP
- Termux `terminal-view` and `terminal-emulator` for terminal behavior
- Android Keystore-backed encryption for saved secrets (planned)
- Optional AI providers behind an explicit, user-configured interface (planned)

## 🎨 Identity

The selected mark is **06 / Blacksite Console**: a hardened terminal hatch with graphite titanium, signal cyan, and a lime command cursor. The vector master, PNG, multi-size ICO, Android adaptive icon, and ten-option identity board live in [`brand/`](brand/).

## 🏗️ Build

Install JDK 17 or newer and Android SDK platform 36, then run:

```sh
./gradlew assembleDebug
```

The debug APK is written to `app/build/outputs/apk/debug/app-debug.apk`.

The release variant is built with `./gradlew assembleRelease`. Without a configured release keystore, Android writes an unsigned APK; sign it with a dedicated distribution key before sharing it. To preview the Pages site locally, run `python3 -m http.server 8080 --directory site` from the repository root and open `http://localhost:8080`.

## 🔐 Security principles

- Probe and display a server's SHA-256 host-key fingerprint before sending credentials on first contact.
- Never silently accept a changed host key; explain the mismatch and stop.
- Keep passwords and passphrases in memory only. Encrypt saved keys and secrets with Android Keystore-backed keys.
- Treat AI as an optional advisor. Send only context the user explicitly selects, redact secrets, label destructive commands, and require a deliberate operator action before terminal input.
- Do not log credentials, private key material, or raw terminal contents by default.

## 📚 Project notes

- [Architecture and delivery plan](docs/ARCHITECTURE.md)
- [Security model](docs/SECURITY.md)
- [Termux terminal integration spike](docs/TERMINAL-INTEGRATION.md)
- [CyberSSH landing page](site/index.html)

### 🧰 Install from source

The project site includes a copyable curl command that downloads the installer script. It builds the pinned CyberSSH source locally and installs a debug-signed APK over ADB. It requires JDK 17+, Android SDK platform 36, and exactly one authorized Android device with USB debugging enabled. Review [`scripts/install-android.sh`](scripts/install-android.sh) before running it. This is a developer/test install, not a public release build. See the [Cybercore F-Droid repository](https://cybercoretech.github.io/fdroid/) for its repository access and app availability.

## 🚀 Connect

1. Enter the SSH host, username, and port (normally `22`).
2. Enter the account password. On first contact, CyberSSH displays the server's SHA-256 fingerprint **before sending the password**. Compare it with a trusted source, then choose **Trust Host & Connect**.
3. After the host key is pinned, CyberSSH authenticates and opens a remote shell. Commands are sent only when you submit them.

Known host fingerprints are stored in app-private preferences, keyed by host and port. A changed key is rejected. Password authentication is currently supported; private-key authentication is not yet available in this release.

### 📦 Local release build

```sh
./gradlew assembleRelease
```

The APK is produced at `app/build/outputs/apk/release/app-release-unsigned.apk` unless a local signing configuration is supplied. Do not distribute a locally debug-signed APK as a public release; configure a dedicated signing key for distribution.

## 📄 Licensing

CyberSSH is licensed under Apache-2.0; see [LICENSE](LICENSE). Apache MINA SSHD is Apache-2.0. Termux's terminal libraries are Apache-2.0; see upstream notices and retain attribution when distributing. See [THIRD-PARTY-NOTICES.md](THIRD-PARTY-NOTICES.md).
