package ru.smakarov.shoppinglist.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import ru.smakarov.shoppinglist.R
import ru.smakarov.shoppinglist.databinding.ActivityMainBinding
import ru.smakarov.shoppinglist.dialogs.NewListDialog
import ru.smakarov.shoppinglist.fragments.FragmentManager
import ru.smakarov.shoppinglist.fragments.NoteFragment
import ru.smakarov.shoppinglist.fragments.ShopListNamesFragment
import ru.smakarov.shoppinglist.settings.SettingsActivity

class MainActivity : AppCompatActivity(), NewListDialog.Listener {
    lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        FragmentManager.setFragment(ShopListNamesFragment.newInstance(),this)
        setButtonNavListener()
    }

    private fun setButtonNavListener() {
        binding.bNav.setOnItemReselectedListener {
            when (it.itemId) {
                R.id.settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                }
                R.id.notes -> {
                    FragmentManager.setFragment(NoteFragment.newInstance(),this)
                }
                R.id.shop_list -> {
                    FragmentManager.setFragment(ShopListNamesFragment.newInstance(),this)

                }
                R.id.new_item -> {
                    FragmentManager.currentFragment?.onClickNew()

                }
            }
            true
        }
    }

    override fun onClick(name: String) {

    }
}