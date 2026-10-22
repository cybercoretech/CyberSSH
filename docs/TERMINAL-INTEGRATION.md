# 🖥️ Termux Terminal Integration Spike

CyberSSH uses Termux's `terminal-view` and `terminal-emulator` libraries for the Android terminal experience. Upstream publishes them through JitPack; `terminal-view` depends on the emulator, so the app declares both explicitly to make the choice clear.

## Important API boundary

`TerminalView` is built to attach a Termux `TerminalSession`, which normally connects the emulator to a local process/PTY. Apache MINA's remote shell channel instead exposes SSH input/output streams. Directly calling `attachSession` with a fabricated local session would misrepresent the connection and could start an unintended local shell.

## Adapter requirement

The integration spike must determine the smallest supported adapter between MINA's remote shell channel and the emulator:

- remote channel stdout/stderr bytes feed the terminal parser;
- terminal key input becomes bytes written to the SSH channel;
- terminal dimensions are sent through the SSH `window-change` request on resize;
- terminal lifecycle tracks SSH channel open/close and handles backpressure/cancellation;
- no local process is created for a remote session.

Before implementing against internal Termux APIs, inspect the exact pinned artifact's public surface and license. If the supported `TerminalView` API cannot accept a remote stream adapter, isolate a small maintained adapter or renderer fork in this app and document the upstream delta. Keep transport and view interfaces replaceable.

## Acceptance checks

- ANSI colors, cursor movement, erase, and alternate screen behavior render correctly.
- Keyboard input, control keys, IME composition, copy/paste, and hardware keyboard work.
- Resize updates the remote PTY size.
- Disconnect releases the channel and emulator cleanly.
- Unit/instrumentation tests prove that opening a remote terminal never launches a local shell process.
