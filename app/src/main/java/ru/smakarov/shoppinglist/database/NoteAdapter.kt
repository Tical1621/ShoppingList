package ru.smakarov.shoppinglist.database

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter

import androidx.recyclerview.widget.RecyclerView
import ru.smakarov.shoppinglist.R
import ru.smakarov.shoppinglist.databinding.NoteListItemBinding
import ru.smakarov.shoppinglist.entity.NoteItem
import ru.smakarov.shoppinglist.utils.HtmlManager

class NoteAdapter(private val listener: Listener): ListAdapter<NoteItem, NoteAdapter.ItemHolder> (ItemComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder.create(parent)//для каждого элемента будет создавать ItemHolder
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        holder.setData(getItem(position), listener)//как только создана новая разметка-здесь она заполняется
    }

    class ItemHolder(view: View): RecyclerView.ViewHolder(view) {
        private val binding = NoteListItemBinding.bind(view)

        /*
       /with чтобы получить доступ сразу к id элемента,без binding
        */
        fun setData(note: NoteItem, listener: Listener) = with(binding) {//with чтобы получить доступ сразу к id элемента,без binding
            tvTitle.text = note.title
            tvDescription.text = HtmlManager.getFromHtml(note.content).trim()
            tvTime.text = note.time
            itemView.setOnClickListener {
                listener.onClickItem(note)
            }
            imDelete.setOnClickListener {
                listener.deleteItem(note.id!!)//знак того что id не будет равен null
            }
        }
        companion object {//статическая функция
            fun create(parent: ViewGroup): ItemHolder {//инициализирует ItemHolder и возвращает его
                return ItemHolder(LayoutInflater.from(parent.context)
                    .inflate(
                    R.layout.note_list_item, parent, false))
            }
        }
    }
    class ItemComparator: DiffUtil.ItemCallback<NoteItem>() {//тут происхродят сравнения элементов
        override fun areItemsTheSame(oldItem: NoteItem, newItem: NoteItem): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: NoteItem, newItem: NoteItem): Boolean {
            return oldItem == newItem
        }

    }

    interface Listener {
        fun deleteItem(id: Int)
        fun onClickItem(note: NoteItem)
    }

}