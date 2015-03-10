package com.anonoz.androidmmu;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.anonoz.androidmmu.data.TodoContract;

import java.util.Calendar;

/**
 * Created by anonoz on 3/5/15.
 */
public class TodoListFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = TodoListFragment.class.getSimpleName();

    private TodoListAdapter mListAdapter;
    private SharedPreferences sharedPref;

    private ListView mListView;
    private int mPosition = ListView.INVALID_POSITION;
    private boolean mUseUpcomingLayout = false;

    private static final int TODO_LIST_LOADER = 0;

    private static final String SELECTED_KEY = "selected_position";

    public static final String[] TODO_LISTS_COLUMNS = {
            TodoContract.TodoListEntry.COLUMN_TITLE,
            TodoContract.TodoListEntry.COLUMN_DEADLINE
    };

    static final int COLUMN_TITLE = 1;
    static final int COLUMN_DEADLINE = 2;

    public TodoListFragment() {
        Log.v(LOG_TAG, "TodoListFragment constructed");
    }

    public void setUseUpcomingLayout(boolean boo) {
        mUseUpcomingLayout = boo;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.d(LOG_TAG, "onCreateLoader called");

        String selection = "deadline > ?";
        String [] selectionArgs = new String[] {
            Long.toString(Calendar.getInstance().getTimeInMillis() / 1000) };

        String sortOrder = TodoContract.TodoListEntry.COLUMN_DEADLINE + " ASC";
        Uri uri = TodoContract.TodoListEntry.CONTENT_URI;

        return new CursorLoader(getActivity(),
                                uri,
                                TODO_LISTS_COLUMNS,
                                selection,
                                selectionArgs,
                                sortOrder);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mListAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mListAdapter.swapCursor(null);
    }

    public interface Callback {
        public void onItemSelected(Uri uri);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_todo_list, container, false);
        mListAdapter = new TodoListAdapter(getActivity(), null, 0);

        Log.d(LOG_TAG, "onCreateView called");
        getLoaderManager().initLoader(TODO_LIST_LOADER, null, this);

        mListView = (ListView) rootView.findViewById(R.id.listview_todo);
        mListView.setAdapter(mListAdapter);

        // On Click
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = mListAdapter.getCursor();
                if (cursor != null && cursor.moveToPosition(position)) {
                    ((Callback) getActivity())
                            .onItemSelected(
                                    TodoContract.TodoListEntry.buildTodoListUri(id));
                }

                mPosition = position;


            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }

        super.onSaveInstanceState(outState);
    }
}
