import * as LocalAuthentication from 'expo-local-authentication';

export const checkBiometricsAvailable = async () => {
  try {
    const hasHardware = await LocalAuthentication.hasHardwareAsync();
    const isEnrolled = await LocalAuthentication.isEnrolledAsync();
    const supportedTypes = await LocalAuthentication.supportedAuthenticationTypesAsync();

    let biometricType = 'Biometrics';
    if (supportedTypes.includes(LocalAuthentication.AuthenticationType.FACIAL_RECOGNITION)) {
      biometricType = 'Face ID';
    } else if (supportedTypes.includes(LocalAuthentication.AuthenticationType.FINGERPRINT)) {
      biometricType = 'Fingerprint';
    }

    return {
      available: hasHardware && isEnrolled,
      hasHardware,
      isEnrolled,
      biometricType,
    };
  } catch (error) {
    console.warn('Error checking biometrics:', error);
    return { available: false, hasHardware: false, isEnrolled: false, biometricType: 'Biometrics' };
  }
};

export const authenticateWithBiometrics = async (promptMessage = 'Unlock SereneSpend') => {
  try {
    const result = await LocalAuthentication.authenticateAsync({
      promptMessage,
      fallbackLabel: 'Use PIN',
      cancelLabel: 'Cancel',
      disableDeviceFallback: false,
    });
    return result.success;
  } catch (error) {
    console.warn('Biometric authentication failed:', error);
    return false;
  }
};
