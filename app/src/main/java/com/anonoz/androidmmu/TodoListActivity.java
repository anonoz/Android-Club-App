package com.anonoz.androidmmu;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;

import com.anonoz.androidmmu.sync.TodoSyncAdapter;


public class TodoListActivity extends ActionBarActivity
    implements TodoListFragment.Callback{

    private final String LOG_TAG = TodoListActivity.class.getSimpleName();

    private static final String TODO_LIST_FRAG = "TLFRAG";
    private static final String TodoItemFragment_TAG = "TIFRAG";

    private static boolean mTwoPane = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_list);

        TodoListFragment todoListFragment =
                (TodoListFragment) getSupportFragmentManager().findFragmentById(
                        R.id.fragment_todolist
                );

        if (findViewById(R.id.container_fragment_todoitem) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container, new TodoListFragment(), TODO_LIST_FRAG)
                    .commit();
            }
        } else {
            mTwoPane = false;
        }

        todoListFragment.setUseUpcomingLayout(mTwoPane);

        TodoSyncAdapter.initializeSyncAdapter(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_todo_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_refresh) {
            TodoSyncAdapter.syncImmediately(this);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Uri uri) {
        if (mTwoPane) {
            Bundle args = new Bundle();
            args.putParcelable(TodoItemFragment.TODO_LIST_ITEMS_URI, uri);

            TodoItemFragment fragment  = new TodoItemFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container_fragment_todoitem,
                            fragment,
                            TodoItemFragment_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, TodoItemActivity.class)
                    .setData(uri);
            startActivity(intent);
        }
    }
}
