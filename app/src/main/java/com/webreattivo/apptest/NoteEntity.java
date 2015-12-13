package com.webreattivo.apptest;

/**
 * Created by vikey_89 on 13/12/15.
 */
public class NoteEntity {

    public static final String FIELD_ID ="_id";
    public static final String FIELD_TITLE ="title";
    public static final String FIELD_DESCRIPTION ="description";
    public static final String FIELD_DATE="data";
    public static final String TBL_NAME="note";

    public String createTable() {
        String query  =
                "CREATE TABLE "+TBL_NAME+
                " ( _id INTEGER PRIMARY KEY AUTOINCREMENT," +
                FIELD_TITLE+" TEXT," +
                FIELD_DESCRIPTION+" TEXT," +
                FIELD_DATE+" DATE)";
        return query;
    }

    public String dropTable() {
        String query  = "DROP TABLE IF EXISTS " + TBL_NAME;
        return query;
    }
}
