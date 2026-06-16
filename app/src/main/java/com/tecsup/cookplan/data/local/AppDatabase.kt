package com.tecsup.cookplan.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [RecipeEntity::class, MealPlanEntity::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao
    abstract fun mealPlanDao(): MealPlanDao

    companion object {
        // v1 -> v2: agrega categoria, tiempo y porciones a recipes como columnas opcionales
        // (nullable, sin default) para conservar las recetas ya guardadas.
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("ALTER TABLE recipes ADD COLUMN category TEXT")
                db.execSQL("ALTER TABLE recipes ADD COLUMN timeMinutes INTEGER")
                db.execSQL("ALTER TABLE recipes ADD COLUMN servings INTEGER")
            }
        }

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "cookplan_db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
