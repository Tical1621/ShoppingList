package ru.smakarov.shoppinglist.database

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

import androidx.recyclerview.widget.RecyclerView
import ru.smakarov.shoppinglist.R
import ru.smakarov.shoppinglist.databinding.ListNameItemBinding
import ru.smakarov.shoppinglist.databinding.ShopLibraryListItemBinding
import ru.smakarov.shoppinglist.databinding.ShopListItemBinding
import ru.smakarov.shoppinglist.entity.ShopListNameItem
import ru.smakarov.shoppinglist.entity.ShopListItem

class ShopListItemAdapter(private val listener: Listener): ListAdapter<ShopListItem, ShopListItemAdapter.ItemHolder> (ItemComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return if (viewType == 0)
            ItemHolder.createShopItem(parent)
        else
            ItemHolder.createLibraryItem(parent)

    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        if (getItem(position).itemType == 0) {
            holder.setItemData(getItem(position), listener)
        } else {
            holder.setLibraryData(getItem(position), listener)

        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).itemType
    }

    class ItemHolder( val view: View): RecyclerView.ViewHolder(view) {


        fun setItemData(shopListItem: ShopListItem, listener: Listener) {
            val binding = ShopListItemBinding.bind(view)
            binding.apply {
                tvName.text = shopListItem.name
                tvInfo.text = shopListItem.itemInfo
                tvInfo.visibility = infoVisibility(shopListItem)
                checkBox.isChecked = shopListItem.itemChecked
                setPaintFlagAndColor(binding)
                checkBox.setOnClickListener {

                    /*
                    .copy == переделать
                     */
                    listener.onClickItem(shopListItem.copy(itemChecked = checkBox.isChecked),
                        CHECK_BOX)
                }
                imEdit.setOnClickListener {
                    listener.onClickItem(shopListItem, EDIT)
                }
        }
        }

        fun setLibraryData(shopListItem: ShopListItem, listener: Listener) {
            val binding = ShopLibraryListItemBinding.bind(view)
            binding.apply {
                tvName.text = shopListItem.name
                libraryEditButton.setOnClickListener {
                    listener.onClickItem(shopListItem, EDIT_LIBRARY_ITEM)
                }
                libraryDelete.setOnClickListener {
                    listener.onClickItem(shopListItem, DELETE_LIBRARY_ITEM)
                }
                /*
                если надо на весь вью добавить что-то,обращаться нужно именно к вью
                 */
                itemView.setOnClickListener {
                    listener.onClickItem(shopListItem, ADD)

                }
            }
        }
        /*
        если чекбокс отмечен-меняет пэинт флаг(перечеркивает и затемняет)
         */
        private fun setPaintFlagAndColor(binding: ShopListItemBinding) {
            binding.apply {
                if(checkBox.isChecked) {
                    tvName.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    tvInfo.paintFlags = Paint.STRIKE_THRU_TEXT_FLAG
                    tvName.setTextColor(ContextCompat.getColor(binding.root.context, R.color.grey))
                    tvInfo.setTextColor(ContextCompat.getColor(binding.root.context, R.color.grey))
                } else {
                    tvName.paintFlags = Paint.ANTI_ALIAS_FLAG
                    tvInfo.paintFlags = Paint.ANTI_ALIAS_FLAG
                    tvName.setTextColor(ContextCompat.getColor(binding.root.context, R.color.black))
                    tvInfo.setTextColor(ContextCompat.getColor(binding.root.context, R.color.black))
                }
            }

        }

         private fun infoVisibility(shopListItem: ShopListItem): Int {
            return if(shopListItem.itemInfo.isNullOrEmpty()) {
                View.GONE
            } else {
                View.VISIBLE
            }
        }

        companion object {//статическая функция
            fun createShopItem(parent: ViewGroup): ItemHolder {//инициализирует ItemHolder и возвращает его
                return ItemHolder(LayoutInflater.from(parent.context)
                    .inflate(
                    R.layout.shop_list_item, parent, false))
            }
            fun createLibraryItem(parent: ViewGroup): ItemHolder {//инициализирует ItemHolder и возвращает его
                return ItemHolder(LayoutInflater.from(parent.context)
                    .inflate(
                        R.layout.shop_library_list_item, parent, false))
            }
        }
    }
    class ItemComparator: DiffUtil.ItemCallback<ShopListItem>() {//тут происхродят сравнения элементов
        override fun areItemsTheSame(oldItem: ShopListItem, newItem: ShopListItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ShopListItem, newItem: ShopListItem): Boolean {
            return oldItem == newItem
        }

    }

    interface Listener {
        fun onClickItem(shopListItem: ShopListItem, state: Int)
    }

    companion object {
        const val EDIT = 0
        const val CHECK_BOX = 1
        const val EDIT_LIBRARY_ITEM = 2
        const val DELETE_LIBRARY_ITEM = 3
        const val ADD = 4
    }

}