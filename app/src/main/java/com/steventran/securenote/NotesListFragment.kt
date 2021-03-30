package com.steventran.securenote

import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.Filter
import android.widget.Filterable
import androidx.fragment.app.Fragment
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.selection.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*
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

    private var adapter: NoteAdapter? = NoteAdapter(emptyList())


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_search_notes, menu)
        val searchView = menu.findItem(R.id.action_search_notes).actionView as android.widget.SearchView
        searchView.setOnQueryTextListener(object: android.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return query(query)
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return query(newText)
            }
            fun query(text: String?): Boolean{
                adapter?.filter?.filter(text)
                return false
            }
        })
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



    }

    private inner class NoteAdapter(var notesList: List<Note>): RecyclerView.Adapter<NoteHolder>(),
        ActionMode.Callback, Filterable{
        private var multiSelect = false
        private var filteredNotes: List<Note> = notesList
        private val selectedNotes = arrayListOf<Note>()
        init {
            setHasStableIds(true)
        }
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteHolder {
            val view = layoutInflater.inflate(R.layout.list_item_note, parent, false)
            return NoteHolder(view)
        }

        override fun getItemCount(): Int = filteredNotes.size

        override fun onBindViewHolder(holder: NoteHolder, position: Int) {
            holder.bind(filteredNotes[position])
            holder.itemView.setOnLongClickListener {
                if (!multiSelect) {
                    multiSelect = true
                    activity?.startActionMode(this)
                    selectItem(holder, filteredNotes[position])

                }
                true
            }
            holder.itemView.setOnClickListener {
                if (multiSelect) {
                    selectItem(holder, filteredNotes[position])
                } else {
                    callbacks?.openNote(filteredNotes[position].uuid)
                }
            }


        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
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

        override fun getFilter(): Filter {
            return object: Filter() {
                override fun performFiltering(sequence: CharSequence?): FilterResults {
                    val filteredResults = FilterResults()
                    filteredNotes = if (!sequence.isNullOrBlank()) {
                        val filteredList = mutableListOf<Note>()
                        notesList.forEach { note: Note ->
                            if(note.title.toLowerCase().contains(sequence) || note.body.toLowerCase().contains(sequence)) {
                                filteredList.add(note)
                            }
                        }
                        filteredList
                    } else {
                        notesList
                    }
                    filteredResults.values = filteredNotes
                    return filteredResults
                }

                override fun publishResults(sequence: CharSequence?, filterResults: FilterResults?) {
                    if (filterResults != null) {
                        filteredNotes = filterResults.values as List<Note>
                    }
                    notifyDataSetChanged()
                }
            }
        }



    }


}