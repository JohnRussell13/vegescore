package com.vegescore.app.data

import android.content.Context
import android.net.Uri
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.BufferedReader
import java.io.InputStreamReader

class BackupManager(private val context: Context, private val dao: MealDao) {

    private val json = Json { prettyPrint = true; ignoreUnknownKeys = true }

    suspend fun exportToUri(uri: Uri): Result<Int> = runCatching {
        val entries = dao.getAllEntriesOnce()
        val data = json.encodeToString(entries)
        context.contentResolver.openOutputStream(uri)?.use { it.write(data.toByteArray()) }
        entries.size
    }

    suspend fun importFromUri(uri: Uri): Result<Int> = runCatching {
        val text = context.contentResolver.openInputStream(uri)?.use { stream ->
            BufferedReader(InputStreamReader(stream)).readText()
        } ?: error("Cannot open file")
        val entries = json.decodeFromString<List<MealEntry>>(text)
        val cleaned = entries.map { it.copy(id = 0) }
        dao.deleteAll()
        dao.insertAll(cleaned)
        cleaned.size
    }
}
