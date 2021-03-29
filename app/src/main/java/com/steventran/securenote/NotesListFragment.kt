package com.steventran.securenote

import android.content.Context
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.selection.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*
private const val SELECTION_ID = "selectionString"
class NotesListFragment : Fragment() {

    interface Callback {
        fun openNote(noteId: UUID)
    }


    companion object {
        fun newInstance() = NotesListFragment()
    }

    private var callbacks: Callback? = null
    private lateinit var noteViewModel: NotesListViewModel
    private lateinit var notesRecyclerView: RecyclerView

    private var tracker: SelectionTracker<Long>? = null
    private var adapter: NoteAdapter? = NoteAdapter(emptyList())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*if (savedInstanceState != null) {
            tracker?.onRestoreInstanceState(savedInstanceState)
        }*/
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.notes_list_fragment, container, false)
        val fab: FloatingActionButton = view.findViewById(R.id.add_notes_fab)
        fab.setOnClickListener {
            val note = Note()
            noteViewModel.addNote(note)
            callbacks?.openNote(note.uuid)
        }
        notesRecyclerView = view.findViewById(R.id.notes_view)
        notesRecyclerView.layoutManager = GridLayoutManager(context, 2)
        notesRecyclerView.adapter = adapter?.apply {
            setTracker(tracker)
        }


        return view
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callbacks = context as Callback?
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        noteViewModel = ViewModelProvider(this).get(NotesListViewModel::class.java)
        noteViewModel.notesLiveData.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer {notes ->
                notes?.let {
                    updateUI(it as List<Note>)
                }

            }
        )
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        /*if(outState != null) {
            tracker?.onSaveInstanceState(outState)
        }*/
    }

    private fun updateUI(notes: List<Note>) {
        adapter = NoteAdapter(notes)
        notesRecyclerView.adapter = adapter
    }

    private inner class NoteHolder(view: View): RecyclerView.ViewHolder(view) {
        private lateinit var note: Note

        private val titleTextView: TextView = view.findViewById(R.id.note_title)
        private val textBodyView: TextView = view.findViewById(R.id.text_body)


        fun bind(note: Note) {
            this.note = note
            titleTextView.text = note.title
            textBodyView.text = note.body
        }
        fun getItemDetails(): ItemDetailsLookup.ItemDetails<Long> = object:
            ItemDetailsLookup.ItemDetails<Long>() {
            override fun getPosition(): Int {
                return adapterPosition
            }

            override fun getSelectionKey(): Long? {
                return itemId
            }

        }


    }

    private inner class NoteAdapter(var notesList: List<Note>): RecyclerView.Adapter<NoteHolder>(),
        ActionMode.Callback{
        private var tracker: SelectionTracker<Long>? = null

        private var multiSelect = false
        private val selectedNotes = arrayListOf<Note>()
        init {
            setHasStableIds(true)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {
            val view = layoutInflater.inflate(R.layout.list_item_note, parent, false)
            return NoteHolder(view)
        }

        override fun getItemCount(): Int = notesList.size

        override fun onBindViewHolder(holder: NoteHolder, position: Int) {
            holder.bind(notesList[position])
            holder.itemView.setOnLongClickListener {
                if (!multiSelect) {
                    multiSelect = true
                    activity?.startActionMode(this)
                    selectItem(holder, notesList[position])

                }
                true
            }
            holder.itemView.setOnClickListener {
                if (multiSelect) {
                    selectItem(holder, notesList[position])
                } else {
                    callbacks?.openNote(notesList[position].uuid)
                }
            }


        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        fun setTracker(tracker: SelectionTracker<Long>?) {
            this.tracker = tracker
        }

        private fun selectItem(holder: NoteHolder, note: Note) {
            if(selectedNotes.contains(note)) {
                selectedNotes.remove(note)
                holder.itemView.alpha = 1.0f
            }
            else {
                selectedNotes.add(note)
                holder.itemView.alpha = 0.3f
            }
        }

        override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
            val inflater: MenuInflater? = mode?.menuInflater
            inflater?.inflate(R.menu.menu_select_notes, menu)
            return true
        }

        override fun onPrepareActionMode(p0: ActionMode?, p1: Menu?): Boolean {
            return true
        }

        override fun onActionItemClicked(mode: ActionMode?, menuItem: MenuItem?): Boolean {
            when(menuItem?.itemId) {
                R.id.action_delete -> {
                    noteViewModel.deleteNotes(selectedNotes)
                    Toast.makeText(context, "Notes deleted", Toast.LENGTH_SHORT).show()
                    mode?.finish()
                }
            }
            return true
        }

        override fun onDestroyActionMode(p0: ActionMode?) {
            multiSelect = false
            selectedNotes.clear()
            notifyDataSetChanged()
        }



    }
    private inner class NoteDetailsLookup(private val recyclerView: RecyclerView):
            ItemDetailsLookup<Long>() {
        override fun getItemDetails(e: MotionEvent): ItemDetails<Long>? {
            val view = recyclerView.findChildViewUnder(e.x, e.y)

            if(view != null) {
                return (recyclerView.getChildViewHolder(view) as NoteHolder).getItemDetails()
            }
            return null
        }

    }

}