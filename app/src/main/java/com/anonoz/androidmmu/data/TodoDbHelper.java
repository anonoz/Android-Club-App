package com.anonoz.androidmmu.data;

import android.content.Context;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.anonoz.androidmmu.data.TodoContract.*;

/**
 * Created by anonoz on 3/4/15.
 */
public class TodoDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "todo.db";

    public TodoDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_TODO_LISTS_TABLE
                = "CREATE TABLE " + TodoListEntry.TABLE_NAME + " ("
                + TodoListEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TodoListEntry.COLUMN_TITLE + " TEXT NOT NULL, "
                + TodoListEntry.COLUMN_DEADLINE + " INTEGER NOT NULL"
                + ");";

        db.execSQL(SQL_CREATE_TODO_LISTS_TABLE);

        final String SQL_CREATE_TODO_ITEMS_TABLE
                = "CREATE TABLE " + TodoItemEntry.TABLE_NAME + " ("
                + TodoItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + TodoItemEntry.COLUMN_TITLE + " TEXT NOT NULL, "
                + TodoItemEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, "
                + TodoItemEntry.COLUMN_TODO_LIST + " INTEGER NOT NULL, "
                + "FOREIGN KEY (" + TodoItemEntry.COLUMN_TODO_LIST + ") REFERENCES "
                + TodoListEntry.TABLE_NAME + " (" + TodoListEntry._ID + ")"
                + ");";

        db.execSQL(SQL_CREATE_TODO_ITEMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TodoListEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TodoItemEntry.TABLE_NAME);
        onCreate(db);
    }
}
