import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  TouchableOpacity,
  SafeAreaView,
  StatusBar,
  Vibration,
} from 'react-native';
import { MaterialIcons } from '@expo/vector-icons';
import { THEME } from '../constants/theme';
import { authenticateWithBiometrics } from '../services/authService';

export const PinLockScreen = ({
  visible,
  savedPin = null,
  useBiometrics = true,
  onUnlockSuccess,
}) => {
  const [enteredPin, setEnteredPin] = useState('');
  const [errorMessage, setErrorMessage] = useState('');

  useEffect(() => {
    if (visible && useBiometrics) {
      triggerBiometrics();
    } else {
      setEnteredPin('');
      setErrorMessage('');
    }
  }, [visible]);

  const triggerBiometrics = async () => {
    const success = await authenticateWithBiometrics('Unlock your SereneSpend records');
    if (success) {
      onUnlockSuccess();
    }
  };

  const handleKeyPress = (num) => {
    if (enteredPin.length >= 4) return;
    const nextPin = enteredPin + num;
    setEnteredPin(nextPin);
    setErrorMessage('');

    if (nextPin.length === 4) {
      if (!savedPin || nextPin === savedPin) {
        setTimeout(() => {
          onUnlockSuccess();
          setEnteredPin('');
        }, 150);
      } else {
        Vibration.vibrate(200);
        setErrorMessage('Incorrect PIN. Please try again.');
        setTimeout(() => {
          setEnteredPin('');
        }, 500);
      }
    }
  };

  const handleBackspace = () => {
    if (enteredPin.length > 0) {
      setEnteredPin(enteredPin.slice(0, -1));
      setErrorMessage('');
    }
  };

  if (!visible) return null;

  return (
    <SafeAreaView style={styles.container}>
      <StatusBar barStyle="dark-content" backgroundColor={THEME.colors.background} />

      <View style={styles.content}>
        <View style={styles.brandContainer}>
          <View style={styles.iconCircle}>
            <MaterialIcons name="lock" size={32} color={THEME.colors.primary} />
          </View>
          <Text style={styles.brandTitle}>SereneSpend Vault</Text>
          <Text style={styles.brandSubtitle}>
            Your financial data is stored 100% offline and private.
          </Text>
        </View>

        <View style={styles.pinDotsRow}>
          {[0, 1, 2, 3].map((index) => {
            const isFilled = enteredPin.length > index;
            return (
              <View
                key={index}
                style={[
                  styles.pinDot,
                  isFilled && styles.pinDotFilled,
                  errorMessage && styles.pinDotError,
                ]}
              />
            );
          })}
        </View>

        {errorMessage ? (
          <Text style={styles.errorText}>{errorMessage}</Text>
        ) : (
          <Text style={styles.instructionText}>Enter 4-digit PIN or use Biometrics</Text>
        )}

        <View style={styles.keypad}>
          {[['1', '2', '3'], ['4', '5', '6'], ['7', '8', '9']].map((row, rIdx) => (
            <View key={rIdx} style={styles.keypadRow}>
              {row.map((item) => (
                <TouchableOpacity
                  key={item}
                  style={styles.keyButton}
                  onPress={() => handleKeyPress(item)}
                  activeOpacity={0.6}
                >
                  <Text style={styles.keyNumberText}>{item}</Text>
                </TouchableOpacity>
              ))}
            </View>
          ))}

          <View style={styles.keypadRow}>
            <TouchableOpacity
              style={styles.keyButtonSpecial}
              onPress={triggerBiometrics}
              activeOpacity={0.6}
            >
              <MaterialIcons name="fingerprint" size={28} color={THEME.colors.primary} />
            </TouchableOpacity>

            <TouchableOpacity
              style={styles.keyButton}
              onPress={() => handleKeyPress('0')}
              activeOpacity={0.6}
            >
              <Text style={styles.keyNumberText}>0</Text>
            </TouchableOpacity>

            <TouchableOpacity
              style={styles.keyButtonSpecial}
              onPress={handleBackspace}
              activeOpacity={0.6}
            >
              <MaterialIcons name="backspace" size={24} color={THEME.colors.textSecondary} />
            </TouchableOpacity>
          </View>
        </View>
      </View>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    ...StyleSheet.absoluteFillObject,
    backgroundColor: THEME.colors.background,
    zIndex: 99999,
  },
  content: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    paddingHorizontal: THEME.spacing.xl,
  },
  brandContainer: {
    alignItems: 'center',
    marginBottom: THEME.spacing.xxl,
  },
  iconCircle: {
    width: 64,
    height: 64,
    borderRadius: 32,
    backgroundColor: THEME.colors.primaryLight,
    justifyContent: 'center',
    alignItems: 'center',
    marginBottom: 16,
  },
  brandTitle: {
    fontSize: 22,
    fontWeight: '700',
    color: THEME.colors.textPrimary,
    marginBottom: 6,
  },
  brandSubtitle: {
    fontSize: 13,
    color: THEME.colors.textSecondary,
    textAlign: 'center',
    maxWidth: 260,
  },
  pinDotsRow: {
    flexDirection: 'row',
    gap: 16,
    marginBottom: 16,
  },
  pinDot: {
    width: 16,
    height: 16,
    borderRadius: 8,
    borderWidth: 2,
    borderColor: THEME.colors.border,
    backgroundColor: THEME.colors.surface,
  },
  pinDotFilled: {
    backgroundColor: THEME.colors.primary,
    borderColor: THEME.colors.primary,
  },
  pinDotError: {
    borderColor: THEME.colors.danger,
    backgroundColor: THEME.colors.dangerBg,
  },
  instructionText: {
    fontSize: 13,
    color: THEME.colors.textMuted,
    marginBottom: 32,
  },
  errorText: {
    fontSize: 13,
    color: THEME.colors.danger,
    fontWeight: '600',
    marginBottom: 32,
  },
  keypad: {
    width: '100%',
    maxWidth: 280,
    gap: 12,
  },
  keypadRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
  },
  keyButton: {
    width: 72,
    height: 72,
    borderRadius: 36,
    backgroundColor: THEME.colors.surface,
    borderWidth: 1,
    borderColor: THEME.colors.border,
    justifyContent: 'center',
    alignItems: 'center',
    ...THEME.shadows.card,
  },
  keyNumberText: {
    fontSize: 26,
    fontWeight: '600',
    color: THEME.colors.textPrimary,
  },
  keyButtonSpecial: {
    width: 72,
    height: 72,
    borderRadius: 36,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: THEME.colors.surfaceSecondary,
  },
});
