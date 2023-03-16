package ru.smakarov.shoppinglist.fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import ru.smakarov.shoppinglist.R
import ru.smakarov.shoppinglist.activities.MainApp
import ru.smakarov.shoppinglist.activities.NewNoteActivity
import ru.smakarov.shoppinglist.database.MainViewModel
import ru.smakarov.shoppinglist.database.NoteAdapter
import ru.smakarov.shoppinglist.databinding.FragmentNoteBinding
import ru.smakarov.shoppinglist.entity.NoteItem


class NoteFragment : BaseFragment(), NoteAdapter.Listener {
    private lateinit var binding: FragmentNoteBinding
    private lateinit var editLauncher: ActivityResultLauncher<Intent>
    private lateinit var adapter: NoteAdapter
    private val mainViewModel: MainViewModel by activityViewModels {
        /*
        инициализация контекста из класса MainApp
         */
        MainViewModel.MainViewModelFactory((context?.applicationContext as MainApp).database)
    }
    override fun onClickNew() {
        editLauncher.launch(Intent(activity, NewNoteActivity::class.java))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        onEditResult()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcView()
        observer()
    }

    private fun initRcView() = with(binding) {
        rcViewNote.layoutManager = LinearLayoutManager(activity)
        adapter = NoteAdapter(this@NoteFragment)//тут нужно явно указать на NoteFragment
        rcViewNote.adapter= adapter
    }

    private fun observer() {
        mainViewModel.allNotes.observe(viewLifecycleOwner, {
            adapter.submitList(it)
        })
    }

    private fun onEditResult() {
        editLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if(it.resultCode == Activity.RESULT_OK) {
                val editState = it.data?.getStringExtra(EDIT_STATE_KEY)
                if(editState == "update") {
                    mainViewModel.updateNote(it.data?.getSerializableExtra(NEW_NOTE_KEY) as NoteItem)

                } else {
                    mainViewModel.insertNote(it.data?.getSerializableExtra(NEW_NOTE_KEY) as NoteItem)
                }
            }
        }
    }

    companion object {
        const val NEW_NOTE_KEY = "new_note_key"
        const val EDIT_STATE_KEY =  "edit_state_key"
        @JvmStatic
        fun newInstance() = NoteFragment()
    }

    override fun deleteItem(id: Int) {
        mainViewModel.deleteNote(id)
    }

    override fun onClickItem(note: NoteItem) {
        val intent = Intent(activity, NewNoteActivity::class.java).apply {
            putExtra(NEW_NOTE_KEY, note)//нажали на кнопку-отправляем заметку
        }
        editLauncher.launch(intent)
    }
}