package samer.ynote;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    //General:
    private static final String TAG = "MainActivity";
    //RecyclerView:
    private RecyclerView recyclerView;
    private AdapterForRecyclerView adapterRecyclerView;
    private RecyclerView.LayoutManager layoutManager;
    //Data:
    protected static List<Note> list_notes = new ArrayList<>();
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupRecyclerView();

        databaseHelper = new DatabaseHelper(this);

        //list_notes.clear();
        list_notes.addAll(databaseHelper.getAllItemsFromDatabase());
        Log.d(TAG, "VIP_Main_ListFromDB: " + list_notes.toString());
        Log.d(TAG, "VIP_Main: " + list_notes.size());


    }//END OF OnCreate

    private void setupRecyclerView() {
        // Lookup the recyclerView in activity layout
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView_Notes);
        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerView.setHasFixedSize(true);
        // Create adapter passing in the sample user data
        adapterRecyclerView = new AdapterForRecyclerView(this);
        // Attach the adapter to the recyclerView to populate items
        recyclerView.setAdapter(adapterRecyclerView);
        // Set layout manager to position the items
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        recyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(this, recyclerView, new RecyclerViewTouchListener.OnItemClickListener() {
            //On normal click, edit note:
            @Override
            public void onItemClick(View view, int position) {
                Log.i(TAG, "OnItemClick, position:" + position);
                updateNote(position);
            }

            //On long click, delete note:
            @Override
            public void onItemLongClick(View view, final int position) {
                Log.i(TAG, "onItemLongClick  " + position);
                //Last chance for user to confirm if they want to delete note:
                new AlertDialog.Builder(MainActivity.this).setIcon(android.R.drawable.ic_dialog_alert)
                        .setTitle("Delete selected note?").setMessage("Action cannot be undone.")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        deleteNote(position);
                                    }
                                }
                        ).setNegativeButton("No", null).show();
            }//END onItemLongClick
        }));
    }

    public void deleteNote(int listIndex) {
        try {
            Note note = list_notes.get(listIndex);
            note.setListIndex(listIndex);
            databaseHelper.deleteItem(note);//remove from the database
            list_notes.remove(listIndex);//remove from the ArrayList
            adapterRecyclerView.notifyDataSetChanged();
        } catch (Exception e) {
            Log.e(TAG, "Unable to DELETE note");
        }
    }

    public void createNote() {
        long noteID = -1;
        try {
            int listIndex = list_notes.size(); //index of new note is obv current size of list
            Note note = new Note();
            note.setListIndex(listIndex);
            list_notes.add(note);
            noteID = databaseHelper.addItem(note);
            note.setID(noteID);
            startTextEditor(note);
        } catch (Exception e) {
            Log.e(TAG, "Unable to CREATE note");
        }
    }

    public void updateNote(int listIndex) {
        try {
            Note note = list_notes.get(listIndex);
            note.setListIndex(listIndex);
            startTextEditor(note);
        } catch (Exception e) {
            Log.e(TAG, "Unable to EDIT note");
        }
    }

    public void startTextEditor(Note note) {
        Intent intent = new Intent(this, NoteEditorActivity.class);
        intent.putExtra("note", note);
        onPause();
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                //Gets note from NoteEditorActivity
                Note note = (Note) data.getSerializableExtra("note");
                int listIndex = note.getListIndex(); //TODO NPE
                String title = note.getTitle();
                //If note is empty, set to default string
                if (note.getNote().isEmpty()) {
                    note.setNote("Empty");
                }
                //If title is empty, set to num of note
                if (title.isEmpty()) {
                    note.setTitle("Note " + list_notes.size());
                } else if (title.length() > 25) {
                    note.setTitle(title.substring(0, 25));
                }
                //Update SQLite database_notes
                databaseHelper.updateItem(note);
                //Update List
                list_notes.set(listIndex, note);
                //Update Recycler View Adapter
                adapterRecyclerView.notifyItemChanged(listIndex);
                adapterRecyclerView.notifyDataSetChanged();
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Log.e(TAG, "OnActivityResult returned nothing");
                Toast.makeText(MainActivity.this, "Error. Note was not saved.", Toast.LENGTH_LONG).show();
            }
        }
    }//onActivityResult

    @Override
    public void onBackPressed() {
        Log.i(TAG, "onBackPressed Called");
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        finish();
        startActivity(intent);
    }

    public void button_createNote(View view) {
        Log.i(TAG, "button_createNote clicked.");
        createNote();
    }

    public void button_delete(View view) {
        Log.i(TAG, "button_delete clicked.");
        databaseHelper.deleteAllItemsFromDatabase();
    }

}
