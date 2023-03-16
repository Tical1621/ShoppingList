package ru.smakarov.shoppinglist.database

import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import ru.smakarov.shoppinglist.entity.LibraryItem
import ru.smakarov.shoppinglist.entity.NoteItem
import ru.smakarov.shoppinglist.entity.ShopListItem
import ru.smakarov.shoppinglist.entity.ShopListNameItem

@androidx.room.Dao
interface Dao {
    @Query(value = "SELECT * FROM note_list")
    fun getAllNotes(): Flow<List<NoteItem>>//flow это корутина

    @Query(value = "SELECT * FROM shopping_list_name")
    fun getAllShopListNames(): Flow<List<ShopListNameItem>>//flow это корутина

    @Query(value = "SELECT * FROM shop_list_item where listId like :listId")
    fun getAllShopListItems(listId: Int): Flow<List<ShopListItem>>

    @Query(value = "SELECT * FROM library where name like :name")
    suspend fun getAllLibraryItems(name: String): List<LibraryItem>

    @Query(value = "delete from note_list where id is :id")
    suspend fun deleteNote(id: Int)

    @Query(value = "delete from library where id is :id")
    suspend fun deleteLibraryItem(id: Int)

    @Query(value = "delete from shopping_list_name where id is :id")
    suspend fun deleteShopListName(id: Int)

    @Query(value = "delete from  shop_list_item where listId like :listId")
    suspend fun deleteShopListItemsByListId(listId: Int)

    @Update
    suspend fun updateNote(note:NoteItem)

    @Update
    suspend fun updateLibraryItem(item: LibraryItem)

    @Update
    suspend fun updateListItem(item:ShopListItem)

    @Update
    suspend fun updateListName(shopListName:ShopListNameItem)

    @Insert
    suspend fun insertLibraryItem(libraryItem:LibraryItem)

    @Insert
    suspend fun insertNote(note: NoteItem)//suspend это корутина

    @Insert
    suspend fun insertShopListName(name: ShopListNameItem)

    @Insert
    suspend fun insertItem(shopListItem: ShopListItem)


}