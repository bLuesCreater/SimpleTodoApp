package com.andy.simpletodo;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.andy.simpletodo.data.Todo;
import com.andy.simpletodo.data.TodoDatabaseHelper;
import com.google.android.material.button.MaterialButton;

public class TodoDetailActivity extends AppCompatActivity {

    private static final int REQUEST_EDIT = 1;

    private TextView tvToolbarTitle, tvTitle, tvContent, tvStatus;
    private ImageButton btnBack, btnEdit;
    private MaterialButton btnStatus, btnDelete;
    private TodoDatabaseHelper dbHelper;
    private Todo currentTodo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        dbHelper = new TodoDatabaseHelper(this);

        tvToolbarTitle = findViewById(R.id.tv_toolbar_title);
        tvTitle = findViewById(R.id.tv_title);
        tvContent = findViewById(R.id.tv_content);
        tvStatus = findViewById(R.id.tv_status);
        btnBack = findViewById(R.id.btn_back);
        btnEdit = findViewById(R.id.btn_edit);
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

        btnBack.setOnClickListener(v -> finish());

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddEditTodoActivity.class);
            intent.putExtra("todo_id", currentTodo.getId());
            startActivityForResult(intent, REQUEST_EDIT);
        });

        btnStatus.setOnClickListener(v -> {
            currentTodo.setCompleted(!currentTodo.isCompleted());
            dbHelper.update(currentTodo);
            updateStatusUI();
            String msg = currentTodo.isCompleted() ? "已标记为完成 ✓" : "已标记为未完成";
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            if (currentTodo.isCompleted()) {
                btnStatus.setText(R.string.btn_mark_uncompleted);
            } else {
                btnStatus.setText(R.string.btn_mark_completed);
            }
        });

        btnDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("删除待办")
                    .setMessage("确定要删除「" + currentTodo.getTitle() + "」吗？")
                    .setPositiveButton("删除", (dialog, which) -> {
                        dbHelper.delete(currentTodo.getId());
                        Toast.makeText(this, "已删除 ✓", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });
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
        tvToolbarTitle.setText(currentTodo.getTitle());
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
}
