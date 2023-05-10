package rsd.mad.mykasihv1.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import rsd.mad.mykasihv1.models.Redemption

@Database (entities = arrayOf(Redemption::class), version = 1, exportSchema = false)
abstract class RedemptionDatabase: RoomDatabase() {
    abstract fun redemptionDao(): RedemptionDao

    companion object{
        //Singleton prevents multiple instances of database opening at the same time
        @Volatile
        private var INSTANCE: RedemptionDatabase? = null

        fun getDatabase(context: Context) : RedemptionDatabase{
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }

            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RedemptionDatabase::class.java,
                    "redemption_db"
                ).build()

                INSTANCE = instance
                return instance
            }
        }
    }
}