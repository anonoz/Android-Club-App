package com.anonoz.androidmmu;

import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
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

    public static final String TODO_LIST_ITEMS_URI = "URI";
    private final String LOG_TAG = TodoItemFragment.class.getSimpleName();
    private Uri mUri;

    private TodoItemAdapter mItemAdapter;
    private SharedPreferences sharedPref;

    private ListView mListView;
    private int mPosition = ListView.INVALID_POSITION;

    private int TODO_ITEM_LOADER = 0;

    private static final String SELECTED_KEY = "selected_position";

    public static final String[] TODO_ITEMS_COLUMNS = {
            TodoContract.TodoItemEntry.TABLE_NAME
                    + "."
                    + TodoContract.TodoItemEntry.COLUMN_TITLE,
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
        mItemAdapter = new TodoItemAdapter(getActivity(), null, 0);

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(TodoItemFragment.TODO_LIST_ITEMS_URI);
        }

        mListView = (ListView) rootView.findViewById(R.id.listview_todoitem);
        mListView.setAdapter(mItemAdapter);

        getLoaderManager().initLoader(TODO_ITEM_LOADER, null, this);

        return rootView;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(LOG_TAG, "onCreateLoader called");

        if (mUri != null) {
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    TODO_ITEMS_COLUMNS,
                    null,
                    null,
                    null);
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mItemAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mItemAdapter.swapCursor(null);
    }
}
