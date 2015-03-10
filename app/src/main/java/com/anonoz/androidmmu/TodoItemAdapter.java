package com.anonoz.androidmmu;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by anonoz on 3/10/15.
 */
public class TodoItemAdapter extends CursorAdapter {

    public TodoItemAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int layout_id = R.layout.list_item_todo_item;

        View view = LayoutInflater.from(context).inflate(layout_id, parent, false);
        ViewHolder view_holder = new ViewHolder(view);
        view.setTag(view_holder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder view_holder = (ViewHolder) view.getTag();
        LinearLayout root = (LinearLayout) view;

        // Bind values to view
        view_holder.title_view.setText(
                cursor.getString(TodoListFragment.COLUMN_TITLE));
        view_holder.description_view.setText(
                Long.toString(cursor.getLong(TodoListFragment.COLUMN_DEADLINE)));
    }

    public static class ViewHolder {
        public final TextView title_view;
        public final TextView description_view;

        public ViewHolder(View view) {
            title_view = (TextView) view.findViewById(R.id.todo_item_title);
            description_view = (TextView) view.findViewById(R.id.todo_item_description);
        }
    }
}
