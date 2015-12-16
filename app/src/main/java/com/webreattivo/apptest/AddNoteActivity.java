package com.webreattivo.apptest;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;


public class AddNoteActivity extends ActionBarActivity {

    private DbHelper dbhelper;
    static TextView mDateDisplay;
    private Button mPickDate;
    static final String TAG = "calendar";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        dbhelper = new DbHelper(this);



        mDateDisplay = (TextView) findViewById(R.id.viewDate);
        mPickDate = (Button) findViewById(R.id.select_data);
        mPickDate.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "datePicker");
            }
        });
    }

    public void addNote(View view) {

        EditText title = (EditText) findViewById(R.id.title_note);
        EditText description = (EditText) findViewById(R.id.description_note);
        TextView date = (TextView) findViewById(R.id.viewDate);


        if (title.length() > 0 && description.length() > 0) {

            SQLiteDatabase db = dbhelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(NoteEntity.FIELD_TITLE, title.getEditableText().toString());
            values.put(NoteEntity.FIELD_DESCRIPTION, description.getEditableText().toString());
            values.put(NoteEntity.FIELD_DATE, date.getText().toString());

            try {

                db.insert(NoteEntity.TBL_NAME, null, values);

                scheduleNotification(title.getEditableText().toString(), description.getEditableText().toString(), date.getText().toString());

                Toast.makeText(getApplicationContext(), "Nota aggiunta con successo. ", Toast.LENGTH_LONG).show();

                finish();

            } catch (SQLiteException sqle) {

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

    public void scheduleNotification(String title, String description, String date) {

        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        Notification.Builder notification = new Notification.Builder(this)
                .setContentTitle(title)
                .setContentText(description)
                .setSmallIcon(android.R.drawable.ic_notification_overlay)
                .setSound(sound);

        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification.build());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long futureInMillis = SystemClock.elapsedRealtime() + 30000;
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }
}
