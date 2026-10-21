package com.vk.zenspend.data.mapper

import com.vk.zenspend.data.local.entity.BudgetEntity
import com.vk.zenspend.data.local.entity.CategoryEntity
import com.vk.zenspend.data.local.entity.DebtEntity
import com.vk.zenspend.data.local.entity.TransactionEntity
import com.vk.zenspend.domain.model.Budget
import com.vk.zenspend.domain.model.Category
import com.vk.zenspend.domain.model.Debt
import com.vk.zenspend.domain.model.Transaction

fun TransactionEntity.toDomain(): Transaction = Transaction(
    id = id,
    title = title,
    amount = amount,
    type = type,
    categoryId = categoryId,
    categoryName = categoryName,
    categoryIcon = categoryIcon,
    categoryColor = categoryColor,
    timestamp = timestamp,
    note = note,
    receiptImagePath = receiptImagePath,
    isExcludedFromCalmAllowance = isExcludedFromCalmAllowance
)

fun Transaction.toEntity(): TransactionEntity = TransactionEntity(
    id = id,
    title = title,
    amount = amount,
    type = type,
    categoryId = categoryId,
    categoryName = categoryName,
    categoryIcon = categoryIcon,
    categoryColor = categoryColor,
    timestamp = timestamp,
    note = note,
    receiptImagePath = receiptImagePath,
    isExcludedFromCalmAllowance = isExcludedFromCalmAllowance
)

fun CategoryEntity.toDomain(): Category = Category(
    id = id,
    name = name,
    iconName = iconName,
    colorHex = colorHex,
    type = type,
    isDefault = isDefault,
    monthlyBudgetLimit = monthlyBudgetLimit
)

fun Category.toEntity(): CategoryEntity = CategoryEntity(
    id = id,
    name = name,
    iconName = iconName,
    colorHex = colorHex,
    type = type,
    isDefault = isDefault,
    monthlyBudgetLimit = monthlyBudgetLimit
)

fun BudgetEntity.toDomain(): Budget = Budget(
    id = id,
    month = month,
    year = year,
    totalMonthlyBudget = totalMonthlyBudget,
    calmDailyTarget = calmDailyTarget,
    carryOverSurplus = carryOverSurplus
)

fun Budget.toEntity(): BudgetEntity = BudgetEntity(
    id = id,
    month = month,
    year = year,
    totalMonthlyBudget = totalMonthlyBudget,
    calmDailyTarget = calmDailyTarget,
    carryOverSurplus = carryOverSurplus
)

fun DebtEntity.toDomain(): Debt = Debt(
    id = id,
    personName = personName,
    amount = amount,
    type = type,
    title = title,
    timestamp = timestamp,
    dueDate = dueDate,
    isSettled = isSettled,
    settledDate = settledDate,
    notes = notes
)

fun Debt.toEntity(): DebtEntity = DebtEntity(
    id = id,
    personName = personName,
    amount = amount,
    type = type,
    title = title,
    timestamp = timestamp,
    dueDate = dueDate,
    isSettled = isSettled,
    settledDate = settledDate,
    notes = notes
)

fun com.vk.zenspend.data.local.entity.EmiEntity.toDomain(): com.vk.zenspend.domain.model.Emi = com.vk.zenspend.domain.model.Emi(
    id = id,
    title = title,
    monthlyAmount = monthlyAmount,
    dueDayOfMonth = dueDayOfMonth,
    totalTenureMonths = totalTenureMonths,
    remainingMonths = remainingMonths,
    isPaidThisMonth = isPaidThisMonth,
    notes = notes
)

fun com.vk.zenspend.domain.model.Emi.toEntity(): com.vk.zenspend.data.local.entity.EmiEntity = com.vk.zenspend.data.local.entity.EmiEntity(
    id = id,
    title = title,
    monthlyAmount = monthlyAmount,
    dueDayOfMonth = dueDayOfMonth,
    totalTenureMonths = totalTenureMonths,
    remainingMonths = remainingMonths,
    isPaidThisMonth = isPaidThisMonth,
    notes = notes
)
