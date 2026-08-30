#!/usr/bin/env bash
#
# Idempotent Cloud Agent bootstrap for RepeaTodo (Kotlin Multiplatform).
#
# Installs the JDK and Android SDK the Gradle build needs, wires them up for
# Gradle without mutating committed files, and warms the build so the first
# agent action is fast. Safe to re-run: every step is guarded.
#
# Note: the iOS target (iosApp / iosX64 / iosArm64 / iosSimulatorArm64) requires
# macOS + Xcode and cannot be built on this Linux VM. The JVM/Android surface is
# the full development experience available here.

set -euo pipefail

# --- Pinned versions -------------------------------------------------------
JDK_APT_PACKAGE="openjdk-17-jdk-headless"           # AGP 7.4.2 + Gradle 7.5 need JDK 11-17
JAVA_HOME_DIR="/usr/lib/jvm/java-17-openjdk-amd64"
ANDROID_SDK_ROOT="${ANDROID_SDK_ROOT:-$HOME/android-sdk}"
CMDLINE_TOOLS_ZIP="commandlinetools-linux-11076708_latest.zip"
CMDLINE_TOOLS_URL="https://dl.google.com/android/repository/${CMDLINE_TOOLS_ZIP}"
# Package list is derived from the module Gradle files:
#   androidApp and shared compileSdk/targetSdk 36.
ANDROID_PACKAGES=(
  "platform-tools"
  "platforms;android-36"
  "build-tools;36.0.0"
)

log() { printf '\n\033[1;34m==> %s\033[0m\n' "$*"; }

# --- 1. JDK 17 -------------------------------------------------------------
if [ ! -x "${JAVA_HOME_DIR}/bin/java" ]; then
  log "Installing ${JDK_APT_PACKAGE}"
  sudo apt-get update -qq
  sudo DEBIAN_FRONTEND=noninteractive apt-get install -y -qq "${JDK_APT_PACKAGE}"
else
  log "JDK 17 already present at ${JAVA_HOME_DIR}"
fi

# --- 2. Android SDK command line tools ------------------------------------
export ANDROID_SDK_ROOT
export ANDROID_HOME="${ANDROID_SDK_ROOT}"
SDKMANAGER="${ANDROID_SDK_ROOT}/cmdline-tools/latest/bin/sdkmanager"

if [ ! -x "${SDKMANAGER}" ]; then
  log "Installing Android command line tools into ${ANDROID_SDK_ROOT}"
  mkdir -p "${ANDROID_SDK_ROOT}/cmdline-tools"
  tmp_zip="$(mktemp --suffix=.zip)"
  curl -fsSL -o "${tmp_zip}" "${CMDLINE_TOOLS_URL}"
  rm -rf "${ANDROID_SDK_ROOT}/cmdline-tools/latest" "${ANDROID_SDK_ROOT}/cmdline-tools/cmdline-tools"
  unzip -q "${tmp_zip}" -d "${ANDROID_SDK_ROOT}/cmdline-tools"
  mv "${ANDROID_SDK_ROOT}/cmdline-tools/cmdline-tools" "${ANDROID_SDK_ROOT}/cmdline-tools/latest"
  rm -f "${tmp_zip}"
else
  log "Android command line tools already present"
fi

# --- 3. SDK packages + licenses -------------------------------------------
log "Accepting SDK licenses and installing packages"
export JAVA_HOME="${JAVA_HOME_DIR}"
yes | "${SDKMANAGER}" --licenses >/dev/null 2>&1 || true
"${SDKMANAGER}" --install "${ANDROID_PACKAGES[@]}"

# --- 4. Wire up Gradle without touching committed files --------------------
# Gradle must run on JDK 17 regardless of the machine default (VM ships JDK 21).
log "Configuring Gradle to use JDK 17"
mkdir -p "${HOME}/.gradle"
GRADLE_PROPS="${HOME}/.gradle/gradle.properties"
touch "${GRADLE_PROPS}"
if grep -q '^org.gradle.java.home=' "${GRADLE_PROPS}" 2>/dev/null; then
  sed -i "s#^org.gradle.java.home=.*#org.gradle.java.home=${JAVA_HOME_DIR}#" "${GRADLE_PROPS}"
else
  echo "org.gradle.java.home=${JAVA_HOME_DIR}" >> "${GRADLE_PROPS}"
fi

# local.properties (git-ignored) tells the Android Gradle Plugin where the SDK is.
REPO_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
log "Writing ${REPO_DIR}/local.properties"
printf 'sdk.dir=%s\n' "${ANDROID_SDK_ROOT}" > "${REPO_DIR}/local.properties"

# Expose the toolchain to interactive agent shells (adb, sdkmanager, java 17).
log "Exposing toolchain via /etc/profile.d/repeatodo-android.sh"
sudo tee /etc/profile.d/repeatodo-android.sh >/dev/null <<EOF
export JAVA_HOME="${JAVA_HOME_DIR}"
export ANDROID_HOME="${ANDROID_SDK_ROOT}"
export ANDROID_SDK_ROOT="${ANDROID_SDK_ROOT}"
export PATH="\$JAVA_HOME/bin:${ANDROID_SDK_ROOT}/platform-tools:${ANDROID_SDK_ROOT}/cmdline-tools/latest/bin:\$PATH"
EOF

# --- 5. Warm the build -----------------------------------------------------
log "Warming the Gradle build (assembleDebug)"
cd "${REPO_DIR}"
./gradlew --no-daemon :androidApp:assembleDebug

log "RepeaTodo environment ready"
