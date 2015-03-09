package com.anonoz.androidmmu.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.anonoz.androidmmu.R;
import com.anonoz.androidmmu.data.TodoContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by anonoz on 3/8/15.
 */
public class TodoSyncAdapter extends AbstractThreadedSyncAdapter {

    public static final int SYNC_INTERVAL = 60 * 180;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    private static final long DAY_IN_MILLIS = 1000 * 60 * 60 * 24;

    private static final String LOG_TAG = TodoSyncAdapter.class.getSimpleName();

    public TodoSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras,
                              String authority, ContentProviderClient provider,
                              SyncResult syncResult) {
        String todo_list_json_string = null;
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        try {

            final String TODO_LISTS_API_ENDPOINT
                    = "https://androidmmu.herokuapp.com/api/todo_lists";

            URL url = new URL(TODO_LISTS_API_ENDPOINT);

            // K connect
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            // Rwad input stream into string
            InputStream input_stream = connection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (input_stream == null) {
                // nothing
            }

            reader = new BufferedReader(new InputStreamReader(input_stream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream is empty no need parsing
            }

            todo_list_json_string = buffer.toString();

            // Now we gonna put JSON data into database
            insertJsonDataToDatabase(todo_list_json_string);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            if (connection != null) connection.disconnect();

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void insertJsonDataToDatabase(String todo_list_json)
        throws JSONException {

        // Keys inside JSON
        final String LISTS = "todo_lists";

        final String LIST_ID = "id";
        final String LIST_TITLE = "title";
        final String LIST_DEADLINE = "deadline";
        final String LIST_ITEMS = "todo_items";

        final String ITEM_ID = "id";
        final String ITEM_TITLE = "title";
        final String ITEM_DESCRIPTION = "description";

        try {
            // Clear local database
            getContext().getContentResolver().delete(
                    TodoContract.TodoListEntry.CONTENT_URI, "1", null);
            getContext().getContentResolver().delete(
                    TodoContract.TodoItemEntry.CONTENT_URI, "1", null);

            // Parse JSON
            JSONObject json = new JSONObject(todo_list_json);

            // Looping through todo_lists
            JSONArray todo_lists = json.getJSONArray(LISTS);

            for (int i = 0; i < todo_lists.length(); i++) {
                JSONObject todo_list = todo_lists.getJSONObject(i);

                // Extract data
                int list_id = todo_list.getInt(LIST_ID);
                String list_title = todo_list.getString(LIST_TITLE);
                long list_deadline = todo_list.getLong(LIST_DEADLINE);

                // Persist todo_list to database
                ContentValues todo_list_cv = new ContentValues();
                todo_list_cv.put(LIST_TITLE, list_title);
                todo_list_cv.put(LIST_DEADLINE, list_deadline);

                Uri todo_list_uri = getContext().getContentResolver().insert(
                                TodoContract.TodoListEntry.CONTENT_URI, todo_list_cv);

                long todo_list_row_id = TodoContract.getTodoListIdFromUri(todo_list_uri);

                // And the associated todo_items
                JSONArray todo_items = todo_list.getJSONArray(LIST_ITEMS);

                for (int j = 0; j < todo_items.length(); j++) {
                    JSONObject todo_item = todo_items.getJSONObject(j);

                    int item_id = todo_item.getInt(ITEM_ID);
                    String item_title = todo_item.getString(ITEM_TITLE);
                    String item_description = todo_item.getString(ITEM_DESCRIPTION);

                    // Persist todo_item to database
                    ContentValues todo_item_cv = new ContentValues();
                    todo_item_cv.put(TodoContract.TodoItemEntry.COLUMN_TODO_LIST, todo_list_row_id);
                    todo_item_cv.put(ITEM_TITLE, item_title);
                    todo_item_cv.put(ITEM_DESCRIPTION, item_description);

                    Uri todo_item_uri = getContext().getContentResolver().insert(
                            TodoContract.TodoItemEntry.CONTENT_URI, todo_item_cv);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } finally {

        }
    }

    public static void syncImmediately(Context context) {
        Log.d(LOG_TAG, "Running syncImmediately");
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }

            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */
            onAccountCreated(newAccount, context);
        }

        return newAccount;
    }

    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }


    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        TodoSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
