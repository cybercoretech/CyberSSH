# 🛡️ Security Model

## SSH trust

The first connection to a host must stop at a fingerprint review step. Acceptance stores a host-key pin. A later key change is a hard failure with the old and newly observed fingerprints shown to the user; there is no silent fallback to trust-on-first-use again.

## Credentials

Password and key passphrases stay in memory for the active authentication attempt and are cleared as soon as practical. Saved private keys are encrypted with a non-exportable Android Keystore key. Never store a private key or password in ordinary preferences, logs, crash reports, or AI context.

## AI boundary

AI is disabled until the user configures a provider. The app sends no session data by default. Each request previews selected context and strips likely secrets. Suggestions are untrusted text, accompanied by explanation and risk indicators. No suggestion runs without an explicit user action, and destructive operations require an additional confirmation.

## Operational protections

- Use modern SSH algorithms supported by the server/client and avoid legacy algorithms unless the user explicitly enables a compatibility override.
- Redact secrets from diagnostic events.
- Make connection cancellation and disconnect immediate and visible.
- Restrict any future local HTTP callback or deep-link surface to explicit, validated intents.
- Keep dependency versions pinned and review upstream license/security notes before release.
