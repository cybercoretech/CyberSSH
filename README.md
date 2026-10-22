# ⚡ CyberSSH — Secure Remote Operations

CyberSSH is Cybercore's Android-first SSH workspace: a focused host manager, interactive remote terminal, and optional AI command copilot. The operator stays in control: suggestions are reviewed before use, and the app never runs an AI-generated command automatically.

## 🧭 Current build

This repository currently contains the first Android shell and visual direction, project configuration, and dependency wiring for Apache MINA SSHD and Termux's terminal libraries. The current screen flow is a UI scaffold; SSH authentication, host-key persistence, the remote terminal adapter, and AI provider integration are not complete yet. In particular, Termux `TerminalView` expects a `TerminalSession` backed by a local PTY, while an SSH client provides remote channel streams. The integration must bridge those models deliberately instead of accidentally opening a local shell and presenting it as remote.

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

## 🔐 Security principles

- Verify and display a server's host-key fingerprint before sending credentials.
- Never silently accept a changed host key; explain the mismatch and stop.
- Keep passwords and passphrases in memory only. Encrypt saved keys and secrets with Android Keystore-backed keys.
- Treat AI as an optional advisor. Send only context the user explicitly selects, redact secrets, label destructive commands, and require a deliberate operator action before terminal input.
- Do not log credentials, private key material, or raw terminal contents by default.

## 📚 Project notes

- [Architecture and delivery plan](docs/ARCHITECTURE.md)
- [Security model](docs/SECURITY.md)
- [Termux terminal integration spike](docs/TERMINAL-INTEGRATION.md)

## 📄 Licensing

CyberSSH is licensed under Apache-2.0; see [LICENSE](LICENSE). Apache MINA SSHD is Apache-2.0. Termux's terminal libraries are Apache-2.0; see upstream notices and retain attribution when distributing. See [THIRD-PARTY-NOTICES.md](THIRD-PARTY-NOTICES.md).
