const { withAppBuildGradle } = require('@expo/config-plugins');

/**
 * Expo Config Plugin to configure Android build.gradle for standalone release APKs.
 * Ensures the release build type signs with the universal debug key and disables minification
 * so the APK runs 100% standalone and offline without requiring EAS or a custom keystore.
 */
function withAndroidSigning(config) {
  return withAppBuildGradle(config, (modConfig) => {
    let contents = modConfig.modResults.contents;

    // 1. Ensure release signing config exists and points to debug.keystore
    const releaseSigningBlock = `
        release {
            storeFile file('debug.keystore')
            storePassword 'android'
            keyAlias 'androiddebugkey'
            keyPassword 'android'
        }`;

    if (!contents.includes("release {\n            storeFile")) {
      contents = contents.replace(
        /signingConfigs\s*\{/,
        `signingConfigs {${releaseSigningBlock}`
      );
    }

    // 2. Ensure buildTypes.release has signingConfig signingConfigs.debug
    // and disable minification so React Native runtime is fully preserved
    contents = contents.replace(
      /release\s*\{/,
      `release {
            signingConfig signingConfigs.debug
            minifyEnabled false
            shrinkResources false`
    );

    modConfig.modResults.contents = contents;
    return modConfig;
  });
}

module.exports = withAndroidSigning;
