package com.andy.simpletodo;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.andy.simpletodo.adapter.TodoAdapter;
import com.andy.simpletodo.data.TodoDatabaseHelper;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private LinearLayout emptyState;
    private ExtendedFloatingActionButton fabAdd;
    private TodoDatabaseHelper dbHelper;
    private TodoAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new TodoDatabaseHelper(this);

        recyclerView = findViewById(R.id.recycler_todo);
        emptyState = findViewById(R.id.empty_state);
        fabAdd = findViewById(R.id.fab_add);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadTodos();

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditTodoActivity.class);
            startActivityForResult(intent, 1);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTodos();
    }

    private void loadTodos() {
        Cursor cursor = dbHelper.getAllCursor();
        if (adapter == null) {
            adapter = new TodoAdapter(this, cursor);
            adapter.setOnItemClickListener(todoId -> {
                Intent intent = new Intent(MainActivity.this, TodoDetailActivity.class);
                intent.putExtra("todo_id", todoId);
                startActivityForResult(intent, 2);
            });
            recyclerView.setAdapter(adapter);
        } else {
            adapter.swapCursor(cursor);
        }

        int count = cursor != null ? cursor.getCount() : 0;
        boolean hasData = count > 0;

        emptyState.setVisibility(hasData ? View.GONE : View.VISIBLE);
        recyclerView.setVisibility(hasData ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loadTodos();
    }
}
