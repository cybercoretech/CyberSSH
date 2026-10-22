#!/usr/bin/env bash
set -euo pipefail

# Reproducible source snapshot used by the CyberSSH Pages install box.
SOURCE_REF="9cead181ead72c84e5dd4fba1262de8178c36610"
REPOSITORY="cybercoretech/CyberSSH"

for command_name in curl tar java adb; do
  command -v "$command_name" >/dev/null 2>&1 || {
    printf 'Missing required command: %s\n' "$command_name" >&2
    exit 1
  }
done

java_version="$(java -version 2>&1 | awk -F '"' '/version/ { print $2; exit }')"
java_major="${java_version%%.*}"
if [[ "$java_major" == "1" ]]; then java_major="$(cut -d. -f2 <<<"$java_version")"; fi
if [[ ! "$java_major" =~ ^[0-9]+$ ]] || (( java_major < 17 )); then
  printf 'CyberSSH requires JDK 17 or newer (detected: %s).\n' "${java_version:-unknown}" >&2
  exit 1
fi

device_count="$(adb devices | awk 'NR > 1 && $2 == "device" { count++ } END { print count+0 }')"
if [[ "$device_count" != "1" ]]; then
  printf 'Connect and authorize exactly one Android device with USB debugging enabled (found %s).\n' "$device_count" >&2
  exit 1
fi

printf 'This builds the pinned CyberSSH source snapshot and installs a locally debug-signed APK.\n'
printf 'It requires Android SDK platform 36 and preserves app data when the signing key matches.\n\n'
read -r -p 'Continue? [y/N] ' answer </dev/tty
[[ "$answer" =~ ^[Yy]$ ]] || { printf 'Cancelled.\n'; exit 0; }

tmp_dir="$(mktemp -d "${TMPDIR:-/tmp}/cyberssh-install.XXXXXX")"
trap 'rm -rf "$tmp_dir"' EXIT
archive="$tmp_dir/source.tar.gz"
printf '\nDownloading CyberSSH source at %s…\n' "$SOURCE_REF"
curl --fail --location --silent --show-error \
  "https://github.com/${REPOSITORY}/archive/${SOURCE_REF}.tar.gz" --output "$archive"
tar -xzf "$archive" -C "$tmp_dir"
source_dir="$(find "$tmp_dir" -mindepth 1 -maxdepth 1 -type d -print -quit)"
[[ -n "$source_dir" ]] || { printf 'Could not unpack the source archive.\n' >&2; exit 1; }

cd "$source_dir"
printf 'Building debug APK…\n'
./gradlew --no-daemon :app:assembleDebug
apk="$source_dir/app/build/outputs/apk/debug/app-debug.apk"
[[ -f "$apk" ]] || { printf 'Build completed without producing the expected APK.\n' >&2; exit 1; }

printf 'Installing CyberSSH on the connected device…\n'
adb install -r "$apk"
printf '\nCyberSSH installed. Launch it from the app drawer.\n'
