package com.webreattivo.apptest;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class EditNoteActivity extends ActionBarActivity {

    private long id = 0;
    private DbHelper dbhelper;
    private static final String TAG = "editNote";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        dbhelper = new DbHelper(this);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");
        id = intent.getLongExtra("id", 0);

        TextView titleInput = (TextView) findViewById(R.id.title_edit_note);
        titleInput.setText(title);

        TextView descriptionInput = (TextView) findViewById(R.id.description_edit_note);
        descriptionInput.setText(description);
    }

    public void updateNote(View v)
    {
        EditText title = (EditText) findViewById(R.id.title_edit_note);
        EditText description = (EditText) findViewById(R.id.description_edit_note);

        Log.d(TAG, "id: " + id);
        Log.d(TAG, "title: " + title);
        Log.d(TAG, "description: " + description);

        if (title.length() > 0 && description.length() > 0 && id > 0)
        {
            try {

                SQLiteDatabase db = dbhelper.getWritableDatabase();

                ContentValues values =new ContentValues();
                values.put(NoteEntity.FIELD_TITLE, title.getEditableText().toString());
                values.put(NoteEntity.FIELD_DESCRIPTION, description.getEditableText().toString());

                db.update(
                        NoteEntity.TBL_NAME,
                        values,
                        NoteEntity.FIELD_ID + "=" + id,
                        null
                );

                finish();

            } catch (SQLiteException sqle) {
                Toast.makeText(getApplicationContext(), "Errore: Problemi durante il salvtaggio. ", Toast.LENGTH_LONG).show();
            }

        } else {

            Toast.makeText(getApplicationContext(), "Tutti i campi sono obbliogatori ", Toast.LENGTH_LONG).show();
        }
    }
}