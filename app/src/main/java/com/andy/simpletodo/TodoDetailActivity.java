package com.andy.simpletodo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.andy.simpletodo.data.Todo;
import com.andy.simpletodo.data.TodoDatabaseHelper;
import com.google.android.material.button.MaterialButton;

public class TodoDetailActivity extends AppCompatActivity {

    private static final int REQUEST_EDIT = 1;

    private TextView tvTitle, tvContent, tvStatus;
    private MaterialButton btnStatus, btnDelete;
    private Toolbar toolbar;
    private TodoDatabaseHelper dbHelper;
    private Todo currentTodo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = new TodoDatabaseHelper(this);

        tvTitle = findViewById(R.id.tv_title);
        tvContent = findViewById(R.id.tv_content);
        tvStatus = findViewById(R.id.tv_status);
        btnStatus = findViewById(R.id.btn_status);
        btnDelete = findViewById(R.id.btn_delete);

        long todoId = getIntent().getLongExtra("todo_id", -1);
        if (todoId == -1) {
            finish();
            return;
        }

        currentTodo = dbHelper.getById(todoId);
        if (currentTodo == null) {
            finish();
            return;
        }

        displayTodo();
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        if (item.getItemId() == R.id.action_edit) {
            Intent intent = new Intent(this, AddEditTodoActivity.class);
            intent.putExtra("todo_id", currentTodo.getId());
            startActivityForResult(intent, REQUEST_EDIT);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EDIT && resultCode == RESULT_OK) {
            currentTodo = dbHelper.getById(currentTodo.getId());
            if (currentTodo != null) {
                displayTodo();
            }
        }
    }

    private void displayTodo() {
        tvTitle.setText(currentTodo.getTitle());

        if (currentTodo.getContent() != null && !currentTodo.getContent().isEmpty()) {
            tvContent.setText(currentTodo.getContent());
        } else {
            tvContent.setText("暂无内容");
        }

        updateStatusUI();

        if (currentTodo.isCompleted()) {
            btnStatus.setText(R.string.btn_mark_uncompleted);
        } else {
            btnStatus.setText(R.string.btn_mark_completed);
        }
    }

    private void updateStatusUI() {
        if (currentTodo.isCompleted()) {
            tvStatus.setText(R.string.status_completed);
            tvStatus.setTextColor(getColor(R.color.status_completed));
        } else {
            tvStatus.setText(R.string.status_uncompleted);
            tvStatus.setTextColor(getColor(R.color.text_secondary));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        btnStatus.setOnClickListener(v -> {
            currentTodo.setCompleted(!currentTodo.isCompleted());
            dbHelper.update(currentTodo);
            updateStatusUI();
            if (currentTodo.isCompleted()) {
                btnStatus.setText(R.string.btn_mark_uncompleted);
                Toast.makeText(this, "已标记为完成", Toast.LENGTH_SHORT).show();
            } else {
                btnStatus.setText(R.string.btn_mark_completed);
                Toast.makeText(this, "已标记为未完成", Toast.LENGTH_SHORT).show();
            }
        });

        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setMessage(R.string.msg_delete_confirm)
                    .setPositiveButton(R.string.confirm, (dialog, which) -> {
                        dbHelper.delete(currentTodo.getId());
                        Toast.makeText(this, R.string.msg_delete_success, Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    })
                    .setNegativeButton(R.string.cancel, null)
                    .show();
        });
    }
}
