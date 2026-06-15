package com.andy.simpletodo.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.andy.simpletodo.R;
import com.andy.simpletodo.data.Todo;
import com.andy.simpletodo.data.TodoDatabaseHelper;

public class TodoAdapter extends RecyclerView.Adapter<TodoAdapter.ViewHolder> {

    private Cursor cursor;
    private final Context context;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(long todoId);
    }

    public TodoAdapter(Context context, Cursor cursor) {
        this.context = context;
        this.cursor = cursor;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void swapCursor(Cursor newCursor) {
        if (cursor != null) {
            cursor.close();
        }
        cursor = newCursor;
        notifyDataSetChanged();
    }

    public Todo getTodoAt(int position) {
        if (cursor == null || !cursor.moveToPosition(position)) {
            return null;
        }
        Todo todo = new Todo();
        todo.setId(cursor.getLong(cursor.getColumnIndexOrThrow(TodoDatabaseHelper.COL_ID)));
        todo.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(TodoDatabaseHelper.COL_TITLE)));
        todo.setContent(cursor.getString(cursor.getColumnIndexOrThrow(TodoDatabaseHelper.COL_CONTENT)));
        todo.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(TodoDatabaseHelper.COL_CREATED_AT)));
        todo.setUpdatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(TodoDatabaseHelper.COL_UPDATED_AT)));
        todo.setCompleted(cursor.getInt(cursor.getColumnIndexOrThrow(TodoDatabaseHelper.COL_IS_COMPLETED)) == 1);
        return todo;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_todo, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Todo todo = getTodoAt(position);
        if (todo == null) return;

        holder.tvTitle.setText(todo.getTitle());

        if (todo.getContent() != null && !todo.getContent().isEmpty()) {
            holder.tvPreview.setVisibility(View.VISIBLE);
            holder.tvPreview.setText(todo.getContent());
        } else {
            holder.tvPreview.setVisibility(View.GONE);
        }

        if (todo.isCompleted()) {
            holder.tvStatus.setText(R.string.status_completed);
            holder.tvStatus.setTextColor(context.getColor(R.color.status_completed));
        } else {
            holder.tvStatus.setText(R.string.status_uncompleted);
            holder.tvStatus.setTextColor(context.getColor(R.color.text_secondary));
        }

        holder.tvTime.setText(todo.getFormattedTime());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(todo.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return cursor == null ? 0 : cursor.getCount();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvPreview, tvStatus, tvTime;

        public ViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvPreview = itemView.findViewById(R.id.tv_preview);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvTime = itemView.findViewById(R.id.tv_time);
        }
    }
}
