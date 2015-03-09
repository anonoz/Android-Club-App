package com.anonoz.androidmmu.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import com.anonoz.androidmmu.data.TodoContract.*;

public class TodoProvider extends ContentProvider {

    // Build URI Matcher
    private static UriMatcher sUriMatcher = buildUriMatcher();
    private TodoDbHelper mOpenHelper;

    // URI Matching constants
    static final int TODO_LISTS = 100;
    static final int TODO_LIST_ITEMS = 102;
    static final int TODO_ITEMS = 200;

    // Sir you got empty seat? Join table lah.
    private static final SQLiteQueryBuilder sTodoListAndItemQueryBuilder;

    static {
        sTodoListAndItemQueryBuilder = new SQLiteQueryBuilder();
        sTodoListAndItemQueryBuilder.setTables(
                TodoListEntry.TABLE_NAME + " INNER JOIN " + TodoItemEntry.TABLE_NAME
                + " ON "
                + TodoItemEntry.TABLE_NAME + "." + TodoItemEntry.COLUMN_TODO_LIST
                + " = "
                + TodoListEntry.TABLE_NAME + "." + TodoListEntry._ID
        );
    }

    public TodoProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsAffected = 0;

        switch (match) {
            case TODO_LISTS: {
                rowsAffected = db.delete(TodoListEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            case TODO_ITEMS:
            case TODO_LIST_ITEMS: {
                rowsAffected = db.delete(TodoItemEntry.TABLE_NAME, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Invalid URI " + uri);
        }

        if (rowsAffected != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return rowsAffected;
    }

    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case TODO_LISTS:
                return TodoListEntry.CONTENT_TYPE;
            case TODO_LIST_ITEMS:
            case TODO_ITEMS:
                return TodoItemEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case TODO_LISTS: {
                normalizeDate(values);
                long _id = db.insert(TodoListEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = TodoListEntry.buildTodoListUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row " + values.toString() + " into " + uri);
                break;
            }
            case TODO_ITEMS: {
                long _id = db.insert(TodoItemEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = TodoItemEntry.buildTodoItemUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row " + values.toString() + " into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);

        Log.v("Provider.Insert", "Inserted into " + uri.toString() + ": " + values.toString());

        return returnUri;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new TodoDbHelper(getContext());
        return false;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor return_cursor;

        switch (sUriMatcher.match(uri)) {
            case TODO_LISTS: {
                return_cursor = getTodoLists();
                break;
            }
            case TODO_LIST_ITEMS: {
                return_cursor = getTodoListItems(uri, projection, sortOrder);
                break;
            }
            case TODO_ITEMS: {
                return_cursor = getTodoItems();
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        return_cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return return_cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);

        int rowsAffected = 0;

        // If nothing but the truth
        if (selection == null) selection = "1";

        switch (match) {
            case TODO_LISTS: {
                rowsAffected = db.update(TodoListEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            case TODO_ITEMS:
            case TODO_LIST_ITEMS: {
                rowsAffected = db.update(TodoItemEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown URI: " + uri);
        }

        if (rowsAffected != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return rowsAffected;
    }

    /******************
     * HELPER METHODS
     */
    static UriMatcher buildUriMatcher() {
        final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = TodoContract.CONTENT_AUTHORITY;

        sUriMatcher.addURI(authority, TodoContract.PATH_LIST, TODO_LISTS);
        sUriMatcher.addURI(authority, TodoContract.PATH_LIST + "/#", TODO_LIST_ITEMS);
        sUriMatcher.addURI(authority, TodoContract.PATH_ITEM, TODO_ITEMS);

        return sUriMatcher;
    }

    private void normalizeDate(ContentValues values) {
        // normalize the date value
        if (values.containsKey(TodoListEntry.COLUMN_DEADLINE)) {
            long dateValue = values.getAsLong(TodoListEntry.COLUMN_DEADLINE);
            values.put(TodoListEntry.COLUMN_DEADLINE, TodoContract.normalizeDate(dateValue));
        }
    }

    /******************
     * All the Getters
     */
    private Cursor getTodoLists() {
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        return db.query(TodoListEntry.TABLE_NAME,
                null, null, null, null, null, null);
    }

    private Cursor getTodoItems() {
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        return db.query(TodoItemEntry.TABLE_NAME,
                null, null, null, null, null, null);
    }

    private Cursor getTodoListItems(Uri uri, String[] projection, String sortOrder) {
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        String todoListId = Long.toString(TodoContract.getTodoListIdFromUri(uri));

        String selection = "todo_list_id = ?";
        String[] selectionArgs = new String[]{ todoListId };

        return sTodoListAndItemQueryBuilder.query(
            db,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            sortOrder
        );
    }
}
