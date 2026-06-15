package com.andy.simpletodo;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.andy.simpletodo.data.Todo;
import com.andy.simpletodo.data.TodoDatabaseHelper;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AddEditTodoActivity extends AppCompatActivity {

    private TextInputEditText etTitle, etContent;
    private MaterialButton btnSave;
    private TodoDatabaseHelper dbHelper;
    private long editId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);

        dbHelper = new TodoDatabaseHelper(this);

        etTitle = findViewById(R.id.et_title);
        etContent = findViewById(R.id.et_content);
        btnSave = findViewById(R.id.btn_save);

        // Check if editing an existing todo
        editId = getIntent().getLongExtra("todo_id", -1);
        if (editId != -1) {
            setTitle(R.string.title_edit_todo);
            loadTodoForEdit(editId);
        } else {
            setTitle(R.string.title_add_todo);
        }

        btnSave.setOnClickListener(v -> saveTodo());
    }

    private void loadTodoForEdit(long id) {
        Todo todo = dbHelper.getById(id);
        if (todo != null) {
            etTitle.setText(todo.getTitle());
            etContent.setText(todo.getContent());
        }
    }

    private void saveTodo() {
        String title = etTitle.getText().toString().trim();
        String content = etContent.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "请输入标题", Toast.LENGTH_SHORT).show();
            return;
        }

        if (editId != -1) {
            // Update existing
            Todo todo = dbHelper.getById(editId);
            if (todo != null) {
                todo.setTitle(title);
                todo.setContent(content);
                dbHelper.update(todo);
            }
        } else {
            // Insert new
            Todo todo = new Todo(title, content);
            dbHelper.insert(todo);
        }

        Toast.makeText(this, R.string.msg_save_success, Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }
}
