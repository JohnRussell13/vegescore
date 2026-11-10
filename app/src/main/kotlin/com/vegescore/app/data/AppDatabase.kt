package com.vegescore.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters

class MealTypeConverter {
    @TypeConverter fun fromMealType(value: MealType): String = value.name
    @TypeConverter fun toMealType(value: String): MealType = MealType.valueOf(value)
}

@Database(entities = [MealEntry::class], version = 1, exportSchema = false)
@TypeConverters(MealTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun mealDao(): MealDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "vegescore.db")
                    .build().also { INSTANCE = it }
            }
    }
}
