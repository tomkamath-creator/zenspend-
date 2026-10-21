package com.vk.zenspend.data.local.converter

import androidx.room.TypeConverter
import com.vk.zenspend.domain.model.DebtType
import com.vk.zenspend.domain.model.TransactionType

class Converters {
    @TypeConverter
    fun fromTransactionType(value: TransactionType): String = value.name

    @TypeConverter
    fun toTransactionType(value: String): TransactionType = runCatching {
        TransactionType.valueOf(value)
    }.getOrDefault(TransactionType.EXPENSE)

    @TypeConverter
    fun fromDebtType(value: DebtType): String = value.name

    @TypeConverter
    fun toDebtType(value: String): DebtType = runCatching {
        DebtType.valueOf(value)
    }.getOrDefault(DebtType.LENT)
}
