package ru.smakarov.shoppinglist.utils

import android.content.Intent
import ru.smakarov.shoppinglist.entity.ShopListItem

object ShareHelper {
    fun shareShopList(shopList: List<ShopListItem>,listName: String): Intent {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plane"
        intent.apply {
            putExtra(Intent.EXTRA_TEXT, makeSharetext(shopList, listName))
        }
        return intent
    }

    private fun makeSharetext(shopList: List<ShopListItem>,listName: String): String {
        val sBuilder = java.lang.StringBuilder()
        sBuilder.append("<<$listName>>")
        sBuilder.append("\n")
        var counter = 0
        shopList.forEach {
            sBuilder.append("${++counter} - ${it.name} / ${it.itemInfo}")
            sBuilder.append("\n")
        }
        return sBuilder.toString()
    }
}