package ru.smakarov.shoppinglist.database

import androidx.lifecycle.*
import kotlinx.coroutines.launch
import ru.smakarov.shoppinglist.entity.LibraryItem
import ru.smakarov.shoppinglist.entity.NoteItem
import ru.smakarov.shoppinglist.entity.ShopListItem
import ru.smakarov.shoppinglist.entity.ShopListNameItem

class MainViewModel(database: MainDatabase): ViewModel() { //похоже на @Service
    val dao = database.getDao()
    val libraryItems = MutableLiveData<List<LibraryItem>>()
    val allNotes: LiveData<List<NoteItem>> = dao.getAllNotes().asLiveData()
    val allShopListNames: LiveData<List<ShopListNameItem>> = dao.getAllShopListNames().asLiveData()

    fun insertNote(note: NoteItem) = viewModelScope.launch {
        dao.insertNote(note)
    }

    fun getAllLibraryItems(name: String) = viewModelScope.launch{
        /*
        передать данные на обсервер
         */
        libraryItems.postValue(dao.getAllLibraryItems(name))
    }

    fun updateLibraryItem(item: LibraryItem) = viewModelScope.launch {
        dao.updateLibraryItem(item)
    }

    fun insertListNote(listName: ShopListNameItem) = viewModelScope.launch {
        dao.insertShopListName(listName)
    }

    fun insertShopItem(shopListItem: ShopListItem) = viewModelScope.launch {
        dao.insertItem(shopListItem)
        if(!isLibraryItemExist(shopListItem.name)) dao.insertLibraryItem(LibraryItem(null, shopListItem.name))
    }

    fun updateNote(note: NoteItem) = viewModelScope.launch {
        dao.updateNote(note)
    }

    fun deleteLibraryItem(id: Int) = viewModelScope.launch {
        dao.deleteLibraryItem(id)
    }

    fun deleteNote(id: Int) = viewModelScope.launch {
        dao.deleteNote(id)
    }

    fun updateListItem(item: ShopListItem) = viewModelScope.launch {
        dao.updateListItem(item)
    }

    fun deleteShopList(id: Int, deleteList: Boolean) = viewModelScope.launch {
        if(deleteList)dao.deleteShopListName(id)
        dao.deleteShopListItemsByListId(id)
    }

    fun updateListName(shopListName: ShopListNameItem) = viewModelScope.launch {
        dao.updateListName(shopListName)
    }

    fun getAllItemsFromList(listId: Int) : LiveData<List<ShopListItem>> {
        return dao.getAllShopListItems(listId).asLiveData()
    }

    private suspend fun isLibraryItemExist(name: String): Boolean {
        return dao.getAllLibraryItems(name).isNotEmpty()
    }
  /*
  каждый раз при создании ViewModelClass надо создавать его экземпляр через MainViewModelFactory
  он всегда будет один и тот же
   */
    class MainViewModelFactory(val database: MainDatabase): ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("unchecked_cast")
                return MainViewModel(database) as T
            }
            throw  java.lang.IllegalArgumentException("unknown ViewModelClass")
        }
    }
}