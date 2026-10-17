const { withAppBuildGradle } = require('@expo/config-plugins');

/**
 * Expo Config Plugin to configure Android build.gradle for standalone release APKs.
 * Safely appends the configuration block at the bottom of build.gradle so Gradle
 * merges the build type settings cleanly without regex corruption or syntax errors.
 */
function withAndroidSigning(config) {
  return withAppBuildGradle(config, (modConfig) => {
    modConfig.modResults.contents += `

// --- SereneSpend Standalone Release Signing Configuration ---
android {
    buildTypes {
        release {
            signingConfig signingConfigs.debug
            minifyEnabled false
            shrinkResources false
        }
    }
}
`;
    return modConfig;
  });
}

module.exports = withAndroidSigning;
