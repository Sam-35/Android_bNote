package samer.ynote

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterForRecyclerView //Default constructor:
internal constructor(private val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.note, parent, false)
        return ListViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) { //Gets note data based on position
        val note = MainActivity.list_notes[position]
        (holder as ListViewHolder).bind(note)
    }

    //Returns size of list_notes
    override fun getItemCount(): Int {
        return if (MainActivity.list_notes.isEmpty()) {
            0
        } else {
            MainActivity.list_notes.size
        }
    }

    private var listener: OnItemClickListener? = null

    interface OnItemClickListener {
        fun OnItemClick(itemView: View?, position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener?) {
        this.listener = listener
    }

    // Provide a direct reference to each of the views within a data item
// Used to cache the views within the item layout for fast access
    internal class ListViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {
        // Your holder should contain a member variable
// for any view that will be set as you render a row
        private val titleTextView: TextView
        private val noteTextView: TextView
        private val timestampTextView: TextView
        fun bind(note: Note) {
            titleTextView.text = note.title
            noteTextView.text = note.note
            timestampTextView.text = note.timestamp
        }

        // We also create a constructor that accepts the entire item row
// and does the view lookups to find each subview
        init { // Stores the itemView in a public final member variable that can be used
// to access the context from any ViewHolder instance.
            titleTextView = itemView.findViewById(R.id.Note_title)
            noteTextView = itemView.findViewById(R.id.Note_note)
            timestampTextView = itemView.findViewById(R.id.Note_timestamp)
        }
    }

    companion object {
        private const val TAG = "AdapterForRecyclerView"
    }

}