package com.anonoz.androidmmu;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by anonoz on 3/8/15.
 */
public class TodoAdapter extends CursorAdapter {

    private final int VIEW_TYPE_UPNEXT = 0;
    private final int VIEW_TYPE_QUEUEING = 1;

    public TodoAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return VIEW_TYPE_UPNEXT;
        else return VIEW_TYPE_QUEUEING;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        int view_type = getItemViewType(cursor.getPosition());
        int layout_id = (view_type == VIEW_TYPE_UPNEXT) ? R.layout.list_item_todo_list_upcoming:R.layout.list_item_todo_list;

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
        view_holder.title_view.setText("Testing");
        view_holder.deadline_view.setText("2 days from now.");
    }

    public static class ViewHolder {
        public final TextView title_view;
        public final TextView deadline_view;

        public ViewHolder(View view) {
            title_view = (TextView) view.findViewById(R.id.todo_list_title);
            deadline_view = (TextView) view.findViewById(R.id.todo_list_deadline);
        }
    }
}
