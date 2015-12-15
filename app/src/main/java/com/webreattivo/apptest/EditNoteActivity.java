package com.webreattivo.apptest;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.util.Calendar;


public class EditNoteActivity extends ActionBarActivity {

    private long id = 0;
    private DbHelper dbhelper;
    static TextView mDateDisplay;
    private Button mPickDate;
    private static final String TAG = "editNote";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);
        dbhelper = new DbHelper(this);

        mDateDisplay = (TextView) findViewById(R.id.viewDate);
        mPickDate = (Button) findViewById(R.id.select_data);
        mPickDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String description = intent.getStringExtra("description");
        String date = intent.getStringExtra("date");
        id = intent.getLongExtra("id", 0);

        TextView titleInput = (TextView) findViewById(R.id.title_edit_note);
        titleInput.setText(title);

        TextView descriptionInput = (TextView) findViewById(R.id.description_edit_note);
        descriptionInput.setText(description);

        TextView dateInput = (TextView) findViewById(R.id.viewDate);
        dateInput.setText(date);
    }

    public void updateNote(View v) {
        EditText title = (EditText) findViewById(R.id.title_edit_note);
        EditText description = (EditText) findViewById(R.id.description_edit_note);
        TextView date = (TextView) findViewById(R.id.viewDate);

        if (title.length() > 0 && description.length() > 0 && date.length() > 0 && id > 0) {
            try {

                SQLiteDatabase db = dbhelper.getWritableDatabase();

                ContentValues values = new ContentValues();
                values.put(NoteEntity.FIELD_TITLE, title.getEditableText().toString());
                values.put(NoteEntity.FIELD_DESCRIPTION, description.getEditableText().toString());
                values.put(NoteEntity.FIELD_DATE, date.getText().toString());

                db.update(
                        NoteEntity.TBL_NAME,
                        values,
                        NoteEntity.FIELD_ID + "=" + id,
                        null
                );

                Toast.makeText(getApplicationContext(), "Nota modificata con successo. ", Toast.LENGTH_LONG).show();

                finish();

            } catch (SQLiteException sqle) {
                Toast.makeText(getApplicationContext(), "Errore: Problemi durante il salvataggio. ", Toast.LENGTH_LONG).show();
            }

        } else {

            Toast.makeText(getApplicationContext(), "Tutti i campi sono obbliogatori ", Toast.LENGTH_LONG).show();
        }
    }


    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {

            mDateDisplay.setText(String.valueOf(day) + "/"
                    + String.valueOf(month + 1) + "/" + String.valueOf(year));
        }
    }
}