package com.example.rogaltasksapp

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Relation
import androidx.room.RoomDatabase
import androidx.room.Transaction
import androidx.room.TypeConverters
import androidx.room.Upsert
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.serialization.Serializable





data class ParentWithChildren(
    @Embedded val parent: ZadaniaEntity,
    @Relation(
        parentColumn = "ID",
        entityColumn = "parentID"
    )
    val children: List<ZadaniaEntity>
)

val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE zadania ADD COLUMN newColumn INTEGER DEFAULT 0")
    }
}

@Dao
interface RogalDao
{

    @Transaction
    @Query("SELECT * FROM zadania WHERE status != 100 AND parentID = 0 AND uzytkownik = :ID")
    suspend fun getParentsWithChildren(ID: Int): List<ParentWithChildren>
    @Upsert
    suspend fun syncTasks(tasks: List<ZadaniaEntity>)
    @Upsert
    suspend fun syncHarmo(tasks: List<Harmonogram>)
    @Query("SELECT * FROM harmonogram WHERE uzytkownik = :ID")
    suspend fun getHarmo(ID:Int) : List<Harmonogram>

}

@Database(entities = [ZadaniaEntity::class, Harmonogram::class], version = 2)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun zadaniaDao(): RogalDao
}
