package com.anonoz.androidmmu.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import com.anonoz.androidmmu.data.TodoContract.*;

import java.util.Map;
import java.util.Set;

/**
 * Created by anonoz on 3/8/15.
 */
public class TestUtilities extends AndroidTestCase {

    static final long TEST_DATE = 1419033600L;

    public static ContentValues createSampleTodoList() {
        ContentValues sample_todo_list = new ContentValues();
        sample_todo_list.put(TodoListEntry.COLUMN_TITLE, "Installfest");
        sample_todo_list.put(TodoListEntry.COLUMN_DEADLINE, TEST_DATE);

        return sample_todo_list;
    }

    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    static long insertInstallfestTodoListValues(Context context) {
        // insert our test records into the database
        TodoDbHelper dbHelper = new TodoDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createSampleTodoList();

        long installfest_row_id;
        installfest_row_id = db.insert(TodoListEntry.TABLE_NAME, null, testValues);

        // Verify we got a row back.
        assertTrue("Error: Failure to insert Installfest TodoList Values", installfest_row_id != -1);

        return installfest_row_id;
    }

    static ContentValues createTodoValues(long todo_list_id) {
        ContentValues todo_item_values = new ContentValues();
        todo_item_values.put(TodoItemEntry.COLUMN_TODO_LIST, todo_list_id);
        todo_item_values.put(TodoItemEntry.COLUMN_TITLE, "Install Android Studio");
        todo_item_values.put(TodoItemEntry.COLUMN_DESCRIPTION,
                "Lorem Ipsum Doloar Sit Amet.");

        return todo_item_values;
    }
}
