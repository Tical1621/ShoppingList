package ru.smakarov.shoppinglist.activities

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.AnimationUtils
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.text.getSpans
import androidx.preference.PreferenceManager
import ru.smakarov.shoppinglist.R
import ru.smakarov.shoppinglist.databinding.ActivityNewNoteBinding
import ru.smakarov.shoppinglist.entity.NoteItem
import ru.smakarov.shoppinglist.fragments.NoteFragment
import ru.smakarov.shoppinglist.utils.HtmlManager
import ru.smakarov.shoppinglist.utils.MyTouchListener
import ru.smakarov.shoppinglist.utils.TimeManager
import ru.smakarov.shoppinglist.utils.TimeManager.getCurrentTime
import java.text.SimpleDateFormat
import java.util.*

class NewNoteActivity : AppCompatActivity() {
    private var note: NoteItem? = null
    private var pref: SharedPreferences? = null
    private lateinit var binding: ActivityNewNoteBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewNoteBinding.inflate(layoutInflater)
        setContentView(binding.root)
        actionBarSettings()
        getNote()
        init()
        setTextSize()
        onClickColorPicker()
        actionMenuCallback()
    }

    private fun onClickColorPicker() = with(binding) {
        imRed.setOnClickListener {
            setColorForSelectedText(R.color.picker_red)
        }
        imBlack.setOnClickListener {
            setColorForSelectedText(R.color.picker_black)
        }
        imBlue.setOnClickListener {
            setColorForSelectedText(R.color.picker_blue)
        }
        imGreen.setOnClickListener {
            setColorForSelectedText(R.color.picker_green)
        }
        imOrange.setOnClickListener {
            setColorForSelectedText(R.color.picker_orange)
        }
        imYellow.setOnClickListener {
            setColorForSelectedText(R.color.picker_orange)
        }
    }

    private fun getNote() {//если заметка не передана,значит идет создание новой заметки
        val sNote = intent.getSerializableExtra(NoteFragment.NEW_NOTE_KEY)
        if (sNote != null) {
            note = sNote as NoteItem
            fillNote()
        }
    }

    private fun fillNote() = with(binding) {
        edTitle.setText(note?.title)
        edDescription.setText(HtmlManager.getFromHtml(note?.content!!).trim())

    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init() {
        binding.colorPicker.setOnTouchListener(MyTouchListener())
        /*
        инициализация настроек через экран preference screen
         */
        pref = PreferenceManager.getDefaultSharedPreferences(this)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.new_note_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.id_save) {//условие проверяет на какую кнопку нажатие,это кнопка сохранения
            setMainResult()
        } else if (item.itemId == android.R.id.home) {//кнопка возврата
            finish()
        } else if (item.itemId == R.id.id_bold) {//кнопка выбора стиля
            setBoldForSelectedText()
        } else if (item.itemId == R.id.ic_color) {//кнопка выбора цвета
            if (binding.colorPicker.isShown) {
                closeColorPicker()
            } else {
                opeColorPicker()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setBoldForSelectedText() = with(binding) {
        val startPos = edDescription.selectionStart
        val endPos = edDescription.selectionEnd
        /*
        проверка выделенного текста на стиль
         */
        val styles = edDescription.text.getSpans(startPos, endPos, StyleSpan::class.java)
        var boldStyle: StyleSpan? = null
        if (styles.isNotEmpty()) {
            /*
            будет всего 1 стиль,значит убираем 1 элемент
             */
            edDescription.text.removeSpan(styles[0])
        } else {
            boldStyle = StyleSpan(Typeface.BOLD)
        }
        edDescription.text.setSpan(boldStyle, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        edDescription.text.trim()
        edDescription.setSelection(startPos)
    }

    private fun setColorForSelectedText(colorId: Int) = with(binding) {
        val startPos = edDescription.selectionStart
        val endPos = edDescription.selectionEnd
        /*
        функция изменения цвета текста
         */
        val styles = edDescription.text.getSpans(startPos, endPos, ForegroundColorSpan::class.java)
        if (styles.isNotEmpty()) edDescription.text.removeSpan(styles[0])

        edDescription.text.setSpan(
            ForegroundColorSpan(
                ContextCompat.getColor(this@NewNoteActivity, colorId)
            ), startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        edDescription.text.trim()
        edDescription.setSelection(startPos)
    }


    private fun setMainResult() {
        var editState = "new"
        val tempNote: NoteItem? = if (note == null) {
            createNewNote()
        } else {
            editState = "update"
            updateNote()
        }
        val i = Intent().apply {
            putExtra(NoteFragment.NEW_NOTE_KEY, tempNote)
            putExtra(NoteFragment.EDIT_STATE_KEY, editState)
        }
        setResult(RESULT_OK, i)
        finish()
    }

    private fun updateNote(): NoteItem? = with(binding) {
        return note?.copy(
            title = edTitle.text.toString(),
            content = HtmlManager.toHtml(edDescription.text)
        )
    }

    private fun createNewNote(): NoteItem {
        return NoteItem(
            null,
            binding.edTitle.text.toString(),
            HtmlManager.toHtml(binding.edDescription.text),
            TimeManager.getCurrentTime(),
            ""
        )
    }

    private fun actionBarSettings() {
        val actionBar = supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    private fun opeColorPicker() {
        binding.colorPicker.visibility = View.VISIBLE
        val openAnim = AnimationUtils.loadAnimation(this, R.anim.open_color_picker)
        binding.colorPicker.startAnimation(openAnim)
    }

    private fun closeColorPicker() {
        val openAnim = AnimationUtils.loadAnimation(this, R.anim.close_color_picker)
        openAnim.setAnimationListener(object : AnimationListener {
            override fun onAnimationStart(p0: Animation?) {

            }

            override fun onAnimationEnd(p0: Animation?) {
                binding.colorPicker.visibility = View.GONE
            }

            override fun onAnimationRepeat(p0: Animation?) {

            }

        })
        binding.colorPicker.startAnimation(openAnim)
    }

    private fun actionMenuCallback() { //функция для работы с всплывающим меню,которое появляется при выделении текста
        val actionCallback = object : ActionMode.Callback {
            override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                menu?.clear()
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                menu?.clear()
                return true
            }

            override fun onActionItemClicked(p0: ActionMode?, p1: MenuItem?): Boolean {
                return true
            }

            override fun onDestroyActionMode(mode: ActionMode?) {}

        }
        //action menu просто стирается
        binding.edDescription.customSelectionActionModeCallback = actionCallback
    }
    /*
    функция берет размер текста из настроек,конвертит во флоат и применяет для EditText(extension function)
     */
    private fun setTextSize()  = with(binding){
        edTitle.setTextSize(pref?.getString("title_size_key","16"))
        edDescription.setTextSize(pref?.getString("content_size_key","16"))

    }
    private fun EditText.setTextSize(size: String?) {
        if(size != null) this.textSize = size.toFloat()
    }

}