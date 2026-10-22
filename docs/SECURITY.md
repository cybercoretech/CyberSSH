# 🛡️ Security Model

## SSH trust

On first contact, CyberSSH probes the server key without sending credentials or opening a shell. It displays the SHA-256 fingerprint and waits for explicit operator approval. Approval pins that fingerprint to the exact host and port in app-private preferences. Later key changes fail closed; the app does not silently trust a replacement key.

## Credentials

The current release supports passwords only. A password remains in process memory for the active connection attempt, is cleared after successful authentication or when leaving the authentication screen, and is never written to preferences or logs. Private-key authentication and saved credential storage are not implemented yet. Never store a private key or password in ordinary preferences, logs, crash reports, or AI context.

## Remote shell

The app opens an authenticated remote PTY shell and sends only text the operator submits. The current Compose terminal displays streamed text and supports line-oriented commands; full-screen terminal control-sequence rendering, resize updates, and interactive terminal applications are not implemented yet. The app does not start a local shell.

## AI boundary

AI is disabled until the user configures a provider. The app sends no session data by default. Each request previews selected context and strips likely secrets. Suggestions are untrusted text, accompanied by explanation and risk indicators. No suggestion runs without an explicit user action, and destructive operations require an additional confirmation.

## Operational protections

- Use modern SSH algorithms supported by the server/client and avoid legacy algorithms unless the user explicitly enables a compatibility override.
- Redact secrets from diagnostic events.
- Make connection cancellation and disconnect immediate and visible.
- Restrict any future local HTTP callback or deep-link surface to explicit, validated intents.
- Keep dependency versions pinned and review upstream license/security notes before release.
