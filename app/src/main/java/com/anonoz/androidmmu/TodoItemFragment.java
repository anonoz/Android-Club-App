package com.anonoz.androidmmu;

import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.anonoz.androidmmu.data.TodoContract;

/**
 * Created by anonoz on 3/10/15.
 */
public class TodoItemFragment extends Fragment
    implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = TodoItemFragment.class.getSimpleName();

    private TodoItemAdapter mItemAdapter;
    private SharedPreferences sharedPref;

    private ListView mListView;
    private int mPosition = ListView.INVALID_POSITION;

    private int TODO_ITEM_LOADER = 0;

    private static final String SELECTED_KEY = "selected_position";

    public static final String[] TODO_ITEMS_COLUMNS = {
            TodoContract.TodoItemEntry.COLUMN_TITLE,
            TodoContract.TodoItemEntry.COLUMN_DESCRIPTION
    };

    static final int COLUMN_TITLE = 1;
    static final int COLUMN_DESCRIPTION = 2;

    public TodoItemFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_todo_item, container, false);
        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
