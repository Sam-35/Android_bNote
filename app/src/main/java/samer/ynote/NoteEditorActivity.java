package samer.ynote;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;

public class NoteEditorActivity extends AppCompatActivity {
    //General:
    private static final String TAG = "NoteEditorActivity";
    //Data:
    private Note note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_editor);

        note = (Note) getIntent().getSerializableExtra("note");

        //Title:
        EditText editText_title = findViewById(R.id.editText_title);
        editText_title.setText(note.getTitle());
        //Note:
        EditText editText_note = findViewById(R.id.editText_note);
        editText_note.setText(note.getNote());

        //Checks for text changes for title:
        editText_title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            //Used to save text entered by the user as it is being entered
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                //Calls checkTitleLength
                note.setTitle(String.valueOf(charSequence));
                Log.i(TAG, ("Title: " + charSequence));
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        //Checks for text changes for note:
        editText_note.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            //Used to save text entered by the user as it is being entered
            @Override
            public void onTextChanged(CharSequence charSequence, int start, int count, int after) {
                note.setNote(String.valueOf(charSequence));
                Log.i(TAG, "Note: :" + charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    //Returns note and navigates back to MainActivity:
    @Override
    public void onBackPressed() {
        Log.i(TAG, "onBackPressed Called");
        Intent returnIntent = new Intent();
        returnIntent.putExtra("note", note);
        Log.d(TAG, "putExtra: " + note.toString()); //TODO REMOVE after testing
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }
}
