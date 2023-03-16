package ru.smakarov.shoppinglist.dialogs

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import ru.smakarov.shoppinglist.R
import ru.smakarov.shoppinglist.databinding.EditListItemDialogBinding
import ru.smakarov.shoppinglist.databinding.NewListDialogBinding
import ru.smakarov.shoppinglist.entity.ShopListItem

object EditListItemDialog {
    fun showDialog(context: Context, item: ShopListItem,  listener: Listener) {
        var dialog: AlertDialog? = null
        val builder = AlertDialog.Builder(context)
        val binding = EditListItemDialogBinding.inflate(LayoutInflater.from(context))
        builder.setView(binding.root)
        binding.apply {
            idName.setText(item.name)
            idInfo.setText(item.itemInfo)
            if(item.itemType == 1) idInfo.visibility = View.GONE
            bUpdate.setOnClickListener {
                if(idName.text.toString().isNotEmpty()) {
                    listener.onClick(item.copy(name = idName.text.toString(), itemInfo = idInfo.text.toString()))
                }
                dialog?.dismiss()
            }
            }
        dialog = builder.create()
        dialog.window?.setBackgroundDrawable(null)
        dialog.show()
    }
    interface Listener {
        fun onClick(item: ShopListItem)
    }
}