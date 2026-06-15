package com.andy.simpletodo;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.andy.simpletodo.adapter.TodoAdapter;
import com.andy.simpletodo.data.TodoDatabaseHelper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView tvEmpty;
    private FloatingActionButton fabAdd;
    private TodoDatabaseHelper dbHelper;
    private TodoAdapter adapter;

    private static final int REQUEST_ADD = 1;
    private static final int REQUEST_DETAIL = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new TodoDatabaseHelper(this);

        recyclerView = findViewById(R.id.recycler_todo);
        tvEmpty = findViewById(R.id.tv_empty);
        fabAdd = findViewById(R.id.fab_add);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        loadTodos();

        fabAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, AddEditTodoActivity.class);
            startActivityForResult(intent, REQUEST_ADD);
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
                startActivityForResult(intent, REQUEST_DETAIL);
            });
            recyclerView.setAdapter(adapter);
        } else {
            adapter.swapCursor(cursor);
        }

        boolean hasData = cursor != null && cursor.getCount() > 0;
        tvEmpty.setVisibility(hasData ? View.GONE : View.VISIBLE);
        recyclerView.setVisibility(hasData ? View.VISIBLE : View.GONE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loadTodos();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbHelper != null) {
            // Cleanup handled by GC
        }
    }
}
