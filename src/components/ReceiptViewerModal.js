import React from 'react';
import {
  Modal,
  View,
  Text,
  Image,
  StyleSheet,
  TouchableOpacity,
  SafeAreaView,
  StatusBar,
} from 'react-native';
import { MaterialIcons } from '@expo/vector-icons';
import { THEME } from '../constants/theme';

export const ReceiptViewerModal = ({ visible, receiptUri, expense, onClose }) => {
  if (!visible || !receiptUri) return null;

  return (
    <Modal
      visible={visible}
      animationType="fade"
      transparent={false}
      onRequestClose={onClose}
    >
      <SafeAreaView style={styles.container}>
        <StatusBar barStyle="dark-content" backgroundColor={THEME.colors.background} />

        <View style={styles.header}>
          <TouchableOpacity
            style={styles.closeButton}
            onPress={onClose}
            activeOpacity={0.7}
          >
            <MaterialIcons name="close" size={24} color={THEME.colors.textPrimary} />
          </TouchableOpacity>

          <View style={styles.headerTitleWrap}>
            <Text style={styles.headerTitle}>Scanned Bill / Receipt</Text>
            <Text style={styles.headerSub}>Stored 100% offline on your device</Text>
          </View>

          <View style={{ width: 40 }} />
        </View>

        <View style={styles.imageContainer}>
          <Image
            source={{ uri: receiptUri }}
            style={styles.image}
            resizeMode="contain"
          />
        </View>

        {expense && (
          <View style={styles.bottomCard}>
            <View style={styles.bottomRow}>
              <View>
                <Text style={styles.expenseNote} numberOfLines={1}>
                  {expense.note || 'Scanned Receipt'}
                </Text>
                <Text style={styles.expenseDate}>{expense.date}</Text>
              </View>
              <Text style={styles.expenseAmount}>
                ${parseFloat(expense.amount || 0).toFixed(2)}
              </Text>
            </View>
          </View>
        )}
      </SafeAreaView>
    </Modal>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: THEME.colors.background,
  },
  header: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: THEME.spacing.lg,
    paddingVertical: THEME.spacing.md,
    borderBottomWidth: 1,
    borderBottomColor: THEME.colors.border,
  },
  closeButton: {
    width: 40,
    height: 40,
    borderRadius: 20,
    backgroundColor: THEME.colors.surfaceSecondary,
    justifyContent: 'center',
    alignItems: 'center',
  },
  headerTitleWrap: {
    alignItems: 'center',
  },
  headerTitle: {
    fontSize: 16,
    fontWeight: '600',
    color: THEME.colors.textPrimary,
  },
  headerSub: {
    fontSize: 11,
    color: THEME.colors.textMuted,
    marginTop: 2,
  },
  imageContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: THEME.spacing.md,
  },
  image: {
    width: '100%',
    height: '100%',
    borderRadius: THEME.borderRadius.lg,
  },
  bottomCard: {
    backgroundColor: THEME.colors.surface,
    padding: THEME.spacing.lg,
    borderTopWidth: 1,
    borderTopColor: THEME.colors.border,
  },
  bottomRow: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
  },
  expenseNote: {
    fontSize: 15,
    fontWeight: '600',
    color: THEME.colors.textPrimary,
    marginBottom: 2,
  },
  expenseDate: {
    fontSize: 13,
    color: THEME.colors.textSecondary,
  },
  expenseAmount: {
    fontSize: 20,
    fontWeight: '700',
    color: THEME.colors.primary,
  },
});
