package samer.ynote;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import static samer.ynote.MainActivity.list_notes;

public class AdapterForRecyclerView extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final String TAG = "AdapterForRecyclerView";
    private Context context;

    //Default constructor:
    AdapterForRecyclerView(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.note, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        //Gets note data based on position
        Note note = list_notes.get(position);
        ((ListViewHolder) holder).bind(note);
    }

    //Returns size of list_notes
    @Override
    public int getItemCount() {
        if (list_notes.isEmpty()) {
            return 0;
        } else {
            return list_notes.size();
        }
    }

    private OnItemClickListener listener;
    public interface OnItemClickListener{
        void OnItemClick(View itemView, int position);
    }
    public void setOnItemClickListener(OnItemClickListener listener){
        this.listener = listener;
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    static class ListViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        private TextView titleTextView;
        private TextView noteTextView;
        private TextView timestampTextView;


        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        ListViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);

            titleTextView = itemView.findViewById(R.id.Note_title);
            noteTextView = itemView.findViewById(R.id.Note_note);
            timestampTextView = itemView.findViewById(R.id.Note_timestamp);
        }

        void bind(Note note) {
            titleTextView.setText(note.getTitle());
            noteTextView.setText(note.getNote());
            timestampTextView.setText(note.getTimestamp());

        }
    }
}
