/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.anonoz.androidmmu.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import java.util.HashSet;

public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(TodoDbHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
    }


    /*
        Students: Uncomment this test once you've written the code to create the Location
        table.  Note that you will have to have chosen the same column names that I did in
        my solution for this test to compile, so if you haven't yet done that, this is
        a good time to change your column names to match mine.

        Note that this only tests that the Location table has the correct columns, since we
        give you the code for the weather table.  This test does not look at the
     */
    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(TodoContract.TodoListEntry.TABLE_NAME);
        tableNameHashSet.add(TodoContract.TodoItemEntry.TABLE_NAME);

        mContext.deleteDatabase(TodoDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new TodoDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Error: Your database was created without both the todo_lists entry and todo_items entry tables",
                tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + TodoContract.TodoListEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> locationColumnHashSet = new HashSet<String>();
        locationColumnHashSet.add(TodoContract.TodoListEntry._ID);
        locationColumnHashSet.add(TodoContract.TodoListEntry.COLUMN_TITLE);
        locationColumnHashSet.add(TodoContract.TodoListEntry.COLUMN_DEADLINE);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            locationColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required location
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required todo_lists entry columns",
                locationColumnHashSet.isEmpty());
        db.close();
    }

    /*
        Students:  Here is where you will build code to test that we can insert and query the
        location database.  We've done a lot of work for you.  You'll want to look in TestUtilities
        where you can uncomment out the "createNorthPoleLocationValues" function.  You can
        also make use of the ValidateCurrentRecord function from within TestUtilities.  Return
        the rowId of the inserted location.
    */
    public long testTodoListsTable() {
        // First step: Get reference to writable database
        SQLiteDatabase db = new TodoDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // Create ContentValues of what you want to insert
        // (you can use the createNorthPoleLocationValues if you wish)
        ContentValues installfest = TestUtilities.createSampleTodoList();

        // Insert ContentValues into database and get a row ID back
        long installfest_row = db.insert(TodoContract.TodoListEntry.TABLE_NAME, null, installfest);
        assertTrue("Error: Failure to insert North Pole Location Values", installfest_row != -1);

        // Query the database and receive a Cursor back
        Cursor installfest_cursor = db.query(TodoContract.TodoListEntry.TABLE_NAME,
                null, null, null, null, null, null, null);
        assertTrue("Error: No records returned from location query", installfest_cursor.moveToFirst());

        // Move the cursor to a valid database row
        installfest_cursor.moveToFirst();

        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        String error = null;
        TestUtilities.validateCurrentRecord(error, installfest_cursor, TestUtilities.createSampleTodoList());

        // Test no row after that
        assertFalse("Error: More than one record in location table", installfest_cursor.moveToNext());

        // Finally, close the cursor and database
        installfest_cursor.close();
        db.close();

        // Return the rowId of the inserted location, or "-1" on failure.
        return installfest_row;
    }


    /*
        Students:  Here is where you will build code to test that we can insert and query the
        database.  We've done a lot of work for you.  You'll want to look in TestUtilities
        where you can use the "createWeatherValues" function.  You can
        also make use of the validateCurrentRecord function from within TestUtilities.
     */
    public void testTodoItemsTable() {
        // First insert the location, and then use the locationRowId to insert
        // the weather. Make sure to cover as many failure cases as you can.
        long installfest_row_id = TestUtilities.insertInstallfestTodoListValues(this.mContext);
        assertTrue("Error installfest_row_id = " + installfest_row_id, installfest_row_id == 1);

        // We return the rowId of the inserted location in testLocationTable, so
        // you should just call that function rather than rewriting it

        // First step: Get reference to writable database
        SQLiteDatabase db = new TodoDbHelper(this.mContext).getWritableDatabase();
        assertTrue("Error: Database should be opened", db.isOpen());

        // Create ContentValues of what you want to insert
        // (you can use the createWeatherValues TestUtilities function if you wish)
        ContentValues todo_item = TestUtilities.createTodoValues(installfest_row_id);

        // Insert ContentValues into database and get a row ID back
        long todo_item_id = db.insert(TodoContract.TodoItemEntry.TABLE_NAME, null, todo_item);
        assertTrue("Error: TodoItem Row ID should be > -1", todo_item_id != -1);

        // Query the database and receive a Cursor back
        Cursor cursor = db.query(TodoContract.TodoItemEntry.TABLE_NAME,
                null, null, null, null, null, null, null);

        // Move the cursor to a valid database row
        assertTrue("Weather Cursor should move to first", cursor.moveToFirst());

        // Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Cursor thing should be same with intended contentvalues", cursor, TestUtilities.createTodoValues(installfest_row_id));

        // Finally, close the cursor and database
        cursor.close();
        db.close();
    }
}
