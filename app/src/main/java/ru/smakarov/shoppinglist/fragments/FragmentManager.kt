package ru.smakarov.shoppinglist.fragments

import androidx.appcompat.app.AppCompatActivity
import ru.smakarov.shoppinglist.R

object FragmentManager { //объект чтобы использовать функцию без инициализации класса
    var currentFragment: BaseFragment? = null//переменная для переключения между фрагментами

    fun setFragment(newFragment: BaseFragment, activity: AppCompatActivity) {//функция переключения между фрагментами
        val transaction = activity.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.placeHolder, newFragment)//куда помещать,что поместить
        transaction.commit()
        currentFragment = newFragment//после создания фрагмента помещаем в current
    }
}