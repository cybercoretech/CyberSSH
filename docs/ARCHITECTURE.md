# 🧩 CyberSSH Architecture

## Product shape

CyberSSH is an Android app built around SSH operations rather than a generic local shell. Compose owns navigation and the Cybercore interface. A domain layer will own hosts, credentials, known host keys, active connections, and command suggestions. Apache MINA SSHD owns SSH negotiation, authentication, shell channels, and SFTP. The terminal UI consumes the remote channel's byte streams through a purpose-built adapter.

## Runtime layers

1. **Presentation:** Compose screens for connections, fingerprint review, authentication, terminal, files, and AI assistance.
2. **Application:** lifecycle-aware connection/session orchestration, cancellation, reconnect state, and operator decisions.
3. **SSH transport:** MINA client, pinned host-key verification, password authentication, and a remote PTY shell. SFTP is planned.
4. **Terminal:** The current release displays remote output in a line-oriented Compose shell and sends submitted commands to the PTY. Termux terminal-emulator integration, full ANSI behavior, terminal key input, and resize propagation remain planned.
5. **Persistence:** Room for non-secret connection metadata; Android Keystore-wrapped encrypted storage for credentials and accepted fingerprints.
6. **AI boundary:** provider interface with explicit context selection, redaction, and a command-review card. AI must not receive credentials or execute commands.

## Initial milestones

- **0.1 Shell:** Compose app structure, colors, navigation, and build baseline.
- **0.2 Secure SSH:** endpoint validation, first-contact fingerprint review, password authentication, pinned keys, remote PTY, and visible failure states (implemented in the current working build).
- **0.3 Terminal:** full-screen terminal rendering, terminal sizing/resizing, ANSI control sequences, IME, extra keys, scrollback, and disconnect recovery.
- **0.4 Connections:** saved hosts and encrypted credential references.
- **0.5 Assistant:** pluggable provider, explain/translate/diagnose, redaction, risk classification, and explicit copy/send actions.
- **0.6 Files:** SFTP browser and safe transfer progress.

## Platform boundary

Compose and domain models can later be shared with desktop. The current Termux terminal view is Android-specific, so terminal rendering and input remain behind an interface rather than leaking Android classes into shared modules. Desktop can adopt a native terminal implementation independently.
