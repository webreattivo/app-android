package com.webreattivo.apptest;

import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


public class AddNoteActivity extends ActionBarActivity {

    private DbHelper dbhelper;

    private static final String TAG = "addNote";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);
        dbhelper = new DbHelper(this);
    }

    public void addNote(View view) {

        EditText title = (EditText) findViewById(R.id.title_note);
        EditText description = (EditText) findViewById(R.id.description_note);

        if (title.length() > 0 && description.length() > 0) {

            SQLiteDatabase db = dbhelper.getWritableDatabase();

            ContentValues values = new ContentValues();
            values.put(NoteEntity.FIELD_TITLE, title.getEditableText().toString());
            values.put(NoteEntity.FIELD_DESCRIPTION, description.getEditableText().toString());

            try {

                db.insert(NoteEntity.TBL_NAME, null, values);

                Uri sound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                final NotificationCompat.Builder notification  = new NotificationCompat.Builder(this)
                        .setContentTitle("Nota Aggiunta")
                        .setContentText("Questa è una notifica per segnalare che la nota è stata aggiunta.")
                        .setSmallIcon(android.R.drawable.ic_dialog_email)
                        .setSound(sound);

                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(0, notification.build());

                finish();

            } catch (SQLiteException sqle) {

            }

        } else {

            Toast.makeText(getApplicationContext(), "Tutti i campi sono obbliogatori ", Toast.LENGTH_LONG).show();
        }
    }
}
