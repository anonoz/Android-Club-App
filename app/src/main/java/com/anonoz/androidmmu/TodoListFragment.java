package com.anonoz.androidmmu;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.content.CursorLoader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.os.Build;

/**
 * Created by anonoz on 3/5/15.
 */
public class TodoListFragment extends Fragment {

    private final String LOG_TAG = TodoListFragment.class.getSimpleName();

    private ListView mListView;

    public TodoListFragment() {

    }

    public interface Callback {
        public void onItemSelected(Uri uri);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_todo_list, container, false);

        // Get data

        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
