package ru.smakarov.shoppinglist.activities

import android.app.Application
import ru.smakarov.shoppinglist.database.MainDatabase

class MainApp: Application() {
    val database by lazy {MainDatabase.getDatabase(this) } //lazy= если инстанция инициализирована,она не будет создаваться еще раз
}