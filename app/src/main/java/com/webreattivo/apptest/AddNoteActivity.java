package com.webreattivo.apptest;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class AddNoteActivity extends ActionBarActivity {

    private DbHelper dbhelper;
    private Button mPickDate;
    private Button mPickTime;
    static TextView mDateDisplay;
    static TextView mTimeDisplay;

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

        mTimeDisplay = (TextView) findViewById(R.id.viewTime);
        mPickTime = (Button) findViewById(R.id.select_time);
        mPickTime.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getFragmentManager(), "timePicker");
            }
        });
    }

    public void addNote(View view) {

        EditText title = (EditText) findViewById(R.id.title_note);
        EditText description = (EditText) findViewById(R.id.description_note);
        TextView date = (TextView) findViewById(R.id.viewDate);
        TextView time = (TextView) findViewById(R.id.viewTime);


        if (title.length() > 0 && description.length() > 0) {

            SQLiteDatabase db = dbhelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(NoteEntity.FIELD_TITLE, title.getEditableText().toString());
            values.put(NoteEntity.FIELD_DESCRIPTION, description.getEditableText().toString());
            values.put(NoteEntity.FIELD_DATE, date.getText().toString() + " " + time.getText().toString());

            try {

                long id = db.insert(NoteEntity.TBL_NAME, null, values);

                scheduleNotification(
                        id,
                        title.getEditableText().toString(),
                        description.getEditableText().toString(),
                        date.getText().toString(),
                        time.getText().toString()
                );

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

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            mTimeDisplay.setText(String.valueOf(hourOfDay) + ":" + String.valueOf(String.format("%02d", minute)));
        }
    }

    public void scheduleNotification(
            long id,
            String title,
            String description,
            String myDate,
            String time
    ) {

        long[] pattern = {0, 500};
        Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Notification.Builder notification = new Notification.Builder(this)
                .setContentTitle(title)
                .setContentText(description)
                .setSmallIcon(android.R.drawable.ic_notification_overlay)
                .setSound(sound)
                .setLights(Color.RED, 500, 1000)
                .setVibrate(pattern)
                .setAutoCancel(true);

        Intent notificationIntent = new Intent(this, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, id);
        Intent in = new Intent(this, MainActivity.class);
        PendingIntent pendingIntentTest = PendingIntent.getActivity(this, 0, in, 0);
        notification.setContentIntent(pendingIntentTest);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification.build());
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        try {

            java.text.DateFormat fromFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            String dateStr = myDate + " " + time + ":00";
            Date date = fromFormat.parse(dateStr);

            Calendar c = Calendar.getInstance();
            c.setTime(date);

            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
    }
}
