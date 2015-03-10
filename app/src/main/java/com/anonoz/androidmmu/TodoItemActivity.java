package com.anonoz.androidmmu;

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


public class TodoItemActivity extends ActionBarActivity {

    public static final String ACTIVITY_TITLE = "ACTIVITY_TITLE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo_item);
        if (savedInstanceState == null) {

            Bundle args = new Bundle();
            args.putParcelable(TodoItemFragment.TODO_LIST_ITEMS_URI, getIntent().getData());

            TodoItemFragment todoItemFragment = new TodoItemFragment();
            todoItemFragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container_fragment_todoitem, todoItemFragment)
                    .commit();

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_todo_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

}
