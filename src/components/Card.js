import React from 'react';
import { View, StyleSheet } from 'react-native';
import { THEME } from '../constants/theme';

export const Card = ({ children, style, variant = 'default' }) => {
  return (
    <View
      style={[
        styles.card,
        variant === 'highlight' && styles.highlightCard,
        variant === 'flat' && styles.flatCard,
        style,
      ]}
    >
      {children}
    </View>
  );
};

const styles = StyleSheet.create({
  card: {
    backgroundColor: THEME.colors.surface,
    borderRadius: THEME.borderRadius.xl,
    padding: THEME.spacing.lg,
    borderWidth: 1,
    borderColor: THEME.colors.border,
    ...THEME.shadows.card,
  },
  highlightCard: {
    backgroundColor: THEME.colors.surfaceHighlight,
    borderColor: THEME.colors.sageMuted,
  },
  flatCard: {
    shadowOpacity: 0,
    elevation: 0,
    backgroundColor: THEME.colors.surfaceSecondary,
  },
});
