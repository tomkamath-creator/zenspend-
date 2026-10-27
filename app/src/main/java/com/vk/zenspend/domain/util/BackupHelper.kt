package com.vk.zenspend.domain.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.vk.zenspend.data.local.ZenSpendDatabase
import com.vk.zenspend.data.local.entity.BudgetEntity
import com.vk.zenspend.data.local.entity.CategoryEntity
import com.vk.zenspend.data.local.entity.EmiEntity
import com.vk.zenspend.data.local.entity.TransactionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class ZenSpendBackupData(
    val appVersion: String = "1.0.0",
    val exportDate: String,
    val timestamp: Long = System.currentTimeMillis(),
    val categories: List<CategoryEntity> = emptyList(),
    val transactions: List<TransactionEntity> = emptyList(),
    val commitments: List<EmiEntity> = emptyList(),
    val budgets: List<BudgetEntity> = emptyList()
)

object BackupHelper {

    private val gson: Gson = GsonBuilder().setPrettyPrinting().create()

    suspend fun exportDataJson(database: ZenSpendDatabase): String = withContext(Dispatchers.IO) {
        val categories = database.categoryDao().getAllCategoriesList()
        val transactions = database.transactionDao().getAllTransactionsList()
        val emis = database.emiDao().getAllEmisList()
        val budgets = database.budgetDao().getAllBudgetsList()

        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val backup = ZenSpendBackupData(
            exportDate = dateFormat.format(Date()),
            categories = categories,
            transactions = transactions,
            commitments = emis,
            budgets = budgets
        )
        gson.toJson(backup)
    }

    suspend fun createBackupFileAndShare(context: Context, database: ZenSpendDatabase): Boolean = withContext(Dispatchers.IO) {
        try {
            val json = exportDataJson(database)
            val dateStr = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "ZenSpend_Backup_$dateStr.json"
            val backupFile = File(context.cacheDir, fileName)

            FileOutputStream(backupFile).use { fos ->
                fos.write(json.toByteArray(Charsets.UTF_8))
            }

            val fileUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                backupFile
            )

            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                type = "application/json"
                putExtra(Intent.EXTRA_STREAM, fileUri)
                putExtra(Intent.EXTRA_SUBJECT, "ZenSpend Backup $dateStr")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }

            val chooser = Intent.createChooser(shareIntent, "Save or Export ZenSpend Backup").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    suspend fun importDataFromJson(database: ZenSpendDatabase, jsonString: String): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val backup = gson.fromJson(jsonString, ZenSpendBackupData::class.java)
                ?: return@withContext Result.failure(IllegalArgumentException("Invalid or empty backup file"))

            var totalRestored = 0

            if (backup.categories.isNotEmpty()) {
                database.categoryDao().insertCategories(backup.categories)
                totalRestored += backup.categories.size
            }

            if (backup.transactions.isNotEmpty()) {
                database.transactionDao().insertTransactions(backup.transactions)
                totalRestored += backup.transactions.size
            }

            if (backup.commitments.isNotEmpty()) {
                database.emiDao().insertEmis(backup.commitments)
                totalRestored += backup.commitments.size
            }

            if (backup.budgets.isNotEmpty()) {
                database.budgetDao().upsertBudgets(backup.budgets)
                totalRestored += backup.budgets.size
            }

            Result.success(totalRestored)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
