package com.steventran.securenote

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.EditText
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import java.util.*

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_NOTE_ID = "note_id"

/**
 * A simple [Fragment] subclass.
 * Use the [NotesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class NotesFragment : Fragment() {


    private lateinit var note: Note
    private lateinit var titleView: EditText
    private lateinit var textBody: EditText

    private val notesViewModel: NotesViewModel  by lazy {
        ViewModelProvider(this).get(NotesViewModel::class.java)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        val noteId: UUID = arguments?.getSerializable(ARG_NOTE_ID) as UUID
        notesViewModel.loadNote(noteId)
        setHasOptionsMenu(true)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_notes, container, false)
        titleView = view.findViewById(R.id.edit_title)
        textBody = view.findViewById(R.id.write_text_body)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        notesViewModel.noteLiveData.observe(viewLifecycleOwner, androidx.lifecycle.Observer {
            if (it != null) {
                note = it
                updateUI()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_notes_frag, menu)
    }

    override fun onStart() {
        super.onStart()


        val textWatcher: TextWatcher = object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(sequence: CharSequence, p1: Int, p2: Int, p3: Int) {



                        note.body = sequence.toString()
                        Log.d("NotesFragmentDebug", note.body)

                }
            }


        titleView.addTextChangedListener(EditTextWatcher(R.id.edit_title))
        textBody.addTextChangedListener(textWatcher)
    }

    override fun onStop() {
        super.onStop()
        notesViewModel.saveNote(note)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.save_note -> {

                notesViewModel.saveNote(note)
                activity?.onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun updateUI() {
        titleView.setText(note.title)
        textBody.setText(note.body)
    }

    private inner class EditTextWatcher(val viewId: Int): TextWatcher {
        override fun afterTextChanged(p0: Editable?) {

        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

        }

        override fun onTextChanged(sequence: CharSequence, p1: Int, p2: Int, p3: Int) {
            when(viewId) {
                R.id.edit_title -> note.title = sequence.toString()

                R.id.write_text_body ->
                {
                    note.body = sequence.toString()
                    Log.d("NotesFragmentDebug", note.title)
                }
            }
        }

    }

    companion object {

        fun newInstance(uid: UUID) =
            NotesFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(ARG_NOTE_ID, uid)
                }
            }
    }
}