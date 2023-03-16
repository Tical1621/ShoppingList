package ru.smakarov.shoppinglist.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.MenuItem.OnActionExpandListener
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ru.smakarov.shoppinglist.R
import ru.smakarov.shoppinglist.database.MainViewModel
import ru.smakarov.shoppinglist.database.ShopListItemAdapter
import ru.smakarov.shoppinglist.databinding.ActivityShopListBinding
import ru.smakarov.shoppinglist.dialogs.EditListItemDialog
import ru.smakarov.shoppinglist.entity.LibraryItem
import ru.smakarov.shoppinglist.entity.ShopListItem
import ru.smakarov.shoppinglist.entity.ShopListNameItem
import ru.smakarov.shoppinglist.utils.ShareHelper

class ShopListActivity : AppCompatActivity(), ShopListItemAdapter.Listener {
    private lateinit var saveItem: MenuItem
    private lateinit var binding: ActivityShopListBinding
    private var shopListNameItem: ShopListNameItem? = null
    private var edItem: EditText? = null
    private lateinit var adapter: ShopListItemAdapter
    private lateinit var  textWatcher: TextWatcher
    private val mainViewModel: MainViewModel by viewModels {
        MainViewModel.MainViewModelFactory((applicationContext as MainApp).database)//MVVM
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        initRcView()
        listItemObserver()
    }

    private fun init() {//получение из интента  информации о том какой список открыт
        shopListNameItem= intent.getSerializableExtra(SHOP_LIST_NAME) as ShopListNameItem

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.shop_list_menu, menu)
        saveItem = menu?.findItem(R.id.save_item)!!
        val newItem = menu.findItem(R.id.new_item)
        edItem = newItem.actionView?.findViewById(R.id.edNewShopItem) as EditText
        newItem.setOnActionExpandListener(expandActionView())
        saveItem.isVisible = false
        textWatcher = textWatcher()
        return true
    }

    private fun textWatcher(): TextWatcher {
        return object: TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                /*
                поиск по каждой букве
                 */
                mainViewModel.getAllLibraryItems("%$s%")
            }

            override fun afterTextChanged(p0: Editable?) {

            }

        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.save_item -> {
                addNewShopItem(edItem?.text.toString())
            }
            R.id.delete_list -> {
                mainViewModel.deleteShopList(shopListNameItem?.id!!, true)
                finish()
            }
            R.id.clear_list -> {
                mainViewModel.deleteShopList(shopListNameItem?.id!!, false)
            }
            R.id.share_list -> {
                /*
                createChooser- создаёт список приложений с помощью которых можно поделиться
                 */
                startActivity(Intent.createChooser(
                    ShareHelper.shareShopList(adapter.currentList, shopListNameItem?.name!!),
                            "Share by"))
            }

        }
        return super.onOptionsItemSelected(item)
    }

    private fun addNewShopItem(name: String) {
        if (name.isEmpty()) return
        val item = ShopListItem(
            null,
            name,
            " ",
            false,
            shopListNameItem?.id!!,
            0
        )
        edItem?.setText(" ")
        mainViewModel.insertShopItem(item)
    }

    private fun expandActionView(): MenuItem.OnActionExpandListener {
        /*
        функция,которая при нажатии на кнопку добавить shop list показывает кнопку сохранить
         */
        return object : OnActionExpandListener {
            override fun onMenuItemActionExpand(p0: MenuItem): Boolean {
                saveItem.isVisible = true
                edItem?.addTextChangedListener(textWatcher)
                libraryItemObserver()
                mainViewModel.getAllItemsFromList(shopListNameItem?.id!!).removeObservers(this@ShopListActivity)
                mainViewModel.getAllLibraryItems("%%")
                return true
            }

            override fun onMenuItemActionCollapse(p0: MenuItem): Boolean {
                saveItem.isVisible = false
                edItem?.removeTextChangedListener(textWatcher)
                invalidateOptionsMenu()//перерисовать меню чтобы появилась кнопка сохранения
                mainViewModel.libraryItems.removeObservers(this@ShopListActivity)
                edItem?.setText("")
                listItemObserver()
                return true
            }

        }
    }

    private fun listItemObserver() {
        mainViewModel.getAllItemsFromList(shopListNameItem?.id!!).observe(this, {
            adapter?.submitList(it)
            binding.tvEmpty.visibility = if (it.isEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }
        })
    }

    private fun libraryItemObserver() {
        /*
        следит за либрари,достает элементы
         */
        mainViewModel.libraryItems.observe(this, {
            val tempShopList = ArrayList<ShopListItem>()
            it.forEach { item ->
                val shopItem = ShopListItem(
                    item.id,
                    item.name,
                    "",
                    false,
                    0,
                    1
                )
                tempShopList.add(shopItem)
            }
            adapter?.submitList(tempShopList)
            binding.tvEmpty.visibility = if (it.isEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }
        })
    }

    private fun initRcView() = with(binding) {
        adapter = ShopListItemAdapter(this@ShopListActivity)
        rcView.layoutManager = LinearLayoutManager(this@ShopListActivity)
        rcView.adapter = adapter
    }

    companion object {
        const val SHOP_LIST_NAME = "shop_list_name"
    }

    override fun onClickItem(shopListItem: ShopListItem, state: Int) {
        when (state) {
            ShopListItemAdapter.CHECK_BOX -> mainViewModel.updateListItem(shopListItem)
            ShopListItemAdapter.EDIT -> editListItem(shopListItem)
            ShopListItemAdapter.EDIT_LIBRARY_ITEM -> editLibraryItem(shopListItem)
            ShopListItemAdapter.DELETE_LIBRARY_ITEM -> {
                mainViewModel.deleteLibraryItem(shopListItem.id!!)
                mainViewModel.getAllLibraryItems("%${edItem?.text.toString()}%")
            }
            ShopListItemAdapter.ADD -> addNewShopItem(shopListItem.name)

        }
    }

    private fun editListItem(item: ShopListItem) {
        EditListItemDialog.showDialog(this, item, object : EditListItemDialog.Listener {
            override fun onClick(item: ShopListItem) {
                mainViewModel.updateListItem(item)
            }

        })
    }

    private fun editLibraryItem(item: ShopListItem) {
        EditListItemDialog.showDialog(this, item, object: EditListItemDialog.Listener {
            override fun onClick(item: ShopListItem) {
                mainViewModel.updateLibraryItem(LibraryItem(
                    item.id,
                    item.name
                ))
                mainViewModel.getAllLibraryItems("%${edItem?.text.toString()}%")
            }
        })
    }

    private fun saveItemCount() {
        var checkedItemCounter = 0
        adapter.currentList.forEach {
            if(it.itemChecked) checkedItemCounter++
        }
        val tempShopListNameItem = shopListNameItem?.copy(
            allItemCounter = adapter.itemCount,
            checkedItemsCounter = checkedItemCounter
        )
        mainViewModel.updateListName(tempShopListNameItem!!)
    }
    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        saveItemCount()
        super.onBackPressed()
    }
}






