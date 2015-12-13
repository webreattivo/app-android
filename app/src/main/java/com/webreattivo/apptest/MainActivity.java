package com.webreattivo.apptest;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity {

    private DbHelper dbHelper;
    private SQLiteDatabase db;
    private CursorAdapter adapter;
    private ListView listView;
    String sortOrder = NoteEntity.FIELD_ID + " DESC";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new DbHelper(this);
        db = dbHelper.getWritableDatabase();
        listView = (ListView) findViewById(R.id.listview);

        try {

            //get all notes
            Cursor cursor = db.query(
                    NoteEntity.TBL_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    sortOrder
            );
            adapter = new CursorAdapter(this, cursor, 0)
            {
                @Override
                public View newView(Context ctx, Cursor arg1, ViewGroup arg2)
                {
                    View v=getLayoutInflater().inflate(R.layout.listrow, null);
                    return v;
                }

                @Override
                public void bindView(View v, Context arg1, Cursor cursor)
                {
                    String title = cursor.getString(cursor.getColumnIndex(NoteEntity.FIELD_TITLE));
                    TextView txt = (TextView) v.findViewById(R.id.row_title);
                    txt.setText(title);
                }

                @Override
                public long getItemId(int position)
                {
                    Cursor crs=adapter.getCursor();
                    crs.moveToPosition(position);
                    return crs.getLong(crs.getColumnIndex(NoteEntity.FIELD_ID));
                }
            };

            listView.setAdapter(adapter);

        } catch(SQLiteException sqle) {

        }

        View empty = findViewById(R.id.empty);
        listView.setEmptyView(empty);
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.changeCursor(db.query(
                NoteEntity.TBL_NAME,
                null,
                null,
                null,
                null,
                null,
                sortOrder
        ));
    }

    public void onClickBtnDelete(View v)
    {
        final int position = listView.getPositionForView(v);
        new AlertDialog.Builder(this)
                .setTitle("Elimina")
                .setMessage("Sei sicuro di voler eliminare questa nota?")
                .setIcon(android.R.drawable.ic_delete)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {

                        long id = adapter.getItemId(position);
                        if (db.delete(NoteEntity.TBL_NAME, NoteEntity.FIELD_ID+"=?",
                                new String[]{Long.toString(id)})>0) {
                            adapter.changeCursor(db.query(
                                    NoteEntity.TBL_NAME,
                                    null,
                                    null,
                                    null,
                                    null,
                                    null,
                                    sortOrder
                            ));
                        }

                    }})
                .setNegativeButton(android.R.string.no, null).show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {

            case R.id.action_settings:
                Intent setting = new Intent(getApplicationContext(), SettingActivity.class);
                startActivity(setting);
                break;

            case R.id.action_add_note:
                Intent addNote = new Intent(getApplicationContext(), AddNoteActivity.class);
                startActivity(addNote);
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
