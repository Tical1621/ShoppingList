package ru.smakarov.shoppinglist.database

import android.content.Context
import android.content.res.ColorStateList
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

import androidx.recyclerview.widget.RecyclerView
import ru.smakarov.shoppinglist.R
import ru.smakarov.shoppinglist.databinding.ListNameItemBinding
import ru.smakarov.shoppinglist.entity.ShopListNameItem

class ShopListNameAdapter(private val listener: Listener): ListAdapter<ShopListNameItem, ShopListNameAdapter.ItemHolder> (ItemComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder.create(parent)//для каждого элемента будет создавать ItemHolder
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.setData(getItem(position),listener)//как только создана новая разметка-здесь она заполняется
    }

    class ItemHolder(view: View): RecyclerView.ViewHolder(view) {
        private val binding = ListNameItemBinding.bind(view)

        fun setData(shopListNameItem: ShopListNameItem, listener: Listener) = with(binding) {//with чтобы получить доступ сразу к id элемента,без binding
            tvListname.text = shopListNameItem.name
            tvTime.text = shopListNameItem.time
            pBar.max = shopListNameItem.allItemCounter
            pBar.progress = shopListNameItem.checkedItemsCounter
            /*
             в colorState контекст передается из байндинга,потому что тут нет контекста
             */
            val colorState = ColorStateList.valueOf(getProgressColorState(shopListNameItem,binding.root.context))
            pBar.progressTintList = colorState
            counterCard.backgroundTintList = colorState
            val counterText = "${shopListNameItem.checkedItemsCounter}/${shopListNameItem.allItemCounter}"
            tvCounter.text = counterText
            itemView.setOnClickListener {
                listener.onClickItem(shopListNameItem)
            }
            imDelete.setOnClickListener {
                listener.deleteItem(shopListNameItem.id!!)

            }
            imEdit.setOnClickListener {
                listener.editItem(shopListNameItem)
            }
        }

        private fun getProgressColorState(item: ShopListNameItem, context: Context): Int {
            return if(item.checkedItemsCounter == item.allItemCounter) {
                ContextCompat.getColor(context, R.color.green_main)
            } else {
                ContextCompat.getColor(context, R.color.picker_red)
            }
        }
        companion object {//статическая функция
            fun create(parent: ViewGroup): ItemHolder {//инициализирует ItemHolder и возвращает его
                return ItemHolder(LayoutInflater.from(parent.context)
                    .inflate(
                    R.layout.list_name_item, parent, false))
            }
        }
    }
    class ItemComparator: DiffUtil.ItemCallback<ShopListNameItem>() {//тут происхродят сравнения элементов
        override fun areItemsTheSame(oldItem: ShopListNameItem, newItem: ShopListNameItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ShopListNameItem, newItem: ShopListNameItem): Boolean {
            return oldItem == newItem
        }

    }

    interface Listener {
        fun deleteItem(id: Int)
        fun onClickItem(shopListName: ShopListNameItem)
        fun editItem(shopListNameItem: ShopListNameItem)
    }

}