package ru.smakarov.shoppinglist.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import ru.smakarov.shoppinglist.entity.LibraryItem
import ru.smakarov.shoppinglist.entity.NoteItem
import ru.smakarov.shoppinglist.entity.ShopListItem
import ru.smakarov.shoppinglist.entity.ShopListNameItem

@Database (entities = [LibraryItem::class,
                       NoteItem::class,
                       ShopListItem::class,
                       ShopListNameItem::class],
                       version = 1)   //создание db и её сущностей
abstract  class MainDatabase : RoomDatabase() {
    abstract fun getDao(): Dao

    companion object {     //позволяет использовать функцию без инициализации класса
        @Volatile
        private var instance: MainDatabase? = null
        fun getDatabase(context: Context): MainDatabase { //создание db коннекта
            return instance ?: synchronized(this) {
                val instanceVal  = Room.databaseBuilder(
                    context.applicationContext,
                    MainDatabase::class.java,
                    "shopping_list.db").build() //так будет назвываться бд
                instanceVal
            }
        }
    }
}