package ru.smakarov.shoppinglist.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_list_name")
data class ShopListNameItem(
    @PrimaryKey (autoGenerate = true)
    val id: Int?,//? = может быть null

    @ColumnInfo (name = "name")
    val name: String,

    @ColumnInfo (name = "time")
    val time: String,

    @ColumnInfo (name = "allItemCounter")
    val allItemCounter: Int,

    @ColumnInfo (name = "checkedItemsCounter")
    val checkedItemsCounter: Int,

    @ColumnInfo (name = "itemIds")
    val itemsIds: String,

): java.io.Serializable
