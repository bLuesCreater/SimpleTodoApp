package com.andy.simpletodo.data;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class TodoDatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "todo.db";
    private static final int DB_VERSION = 1;

    public static final String TABLE_TODOS = "todos";
    public static final String COL_ID = "id";
    public static final String COL_TITLE = "title";
    public static final String COL_CONTENT = "content";
    public static final String COL_CREATED_AT = "created_at";
    public static final String COL_UPDATED_AT = "updated_at";
    public static final String COL_IS_COMPLETED = "is_completed";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_TODOS + " (" +
                    COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COL_TITLE + " TEXT NOT NULL, " +
                    COL_CONTENT + " TEXT, " +
                    COL_CREATED_AT + " INTEGER NOT NULL, " +
                    COL_UPDATED_AT + " INTEGER NOT NULL, " +
                    COL_IS_COMPLETED + " INTEGER DEFAULT 0)";

    public TodoDatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODOS);
        onCreate(db);
    }

    // CRUD operations
    public long insert(Todo todo) {
        SQLiteDatabase db = getWritableDatabase();
        android.content.ContentValues values = new android.content.ContentValues();
        values.put(COL_TITLE, todo.getTitle());
        values.put(COL_CONTENT, todo.getContent());
        values.put(COL_CREATED_AT, todo.getCreatedAt());
        values.put(COL_UPDATED_AT, todo.getUpdatedAt());
        values.put(COL_IS_COMPLETED, todo.isCompleted() ? 1 : 0);
        return db.insert(TABLE_TODOS, null, values);
    }

    public int update(Todo todo) {
        SQLiteDatabase db = getWritableDatabase();
        android.content.ContentValues values = new android.content.ContentValues();
        values.put(COL_TITLE, todo.getTitle());
        values.put(COL_CONTENT, todo.getContent());
        values.put(COL_UPDATED_AT, System.currentTimeMillis());
        values.put(COL_IS_COMPLETED, todo.isCompleted() ? 1 : 0);
        return db.update(TABLE_TODOS, values, COL_ID + " = ?",
                new String[]{String.valueOf(todo.getId())});
    }

    public int delete(long id) {
        SQLiteDatabase db = getWritableDatabase();
        return db.delete(TABLE_TODOS, COL_ID + " = ?",
                new String[]{String.valueOf(id)});
    }

    public Todo getById(long id) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.query(TABLE_TODOS, null, COL_ID + " = ?",
                new String[]{String.valueOf(id)}, null, null, null);
        Todo todo = null;
        if (cursor != null && cursor.moveToFirst()) {
            todo = cursorToTodo(cursor);
            cursor.close();
        }
        return todo;
    }

    public Cursor getAllCursor() {
        SQLiteDatabase db = getReadableDatabase();
        return db.query(TABLE_TODOS, null, null, null, null, null,
                COL_UPDATED_AT + " DESC");
    }

    private Todo cursorToTodo(Cursor cursor) {
        Todo todo = new Todo();
        todo.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COL_ID)));
        todo.setTitle(cursor.getString(cursor.getColumnIndexOrThrow(COL_TITLE)));
        todo.setContent(cursor.getString(cursor.getColumnIndexOrThrow(COL_CONTENT)));
        todo.setCreatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(COL_CREATED_AT)));
        todo.setUpdatedAt(cursor.getLong(cursor.getColumnIndexOrThrow(COL_UPDATED_AT)));
        todo.setCompleted(cursor.getInt(cursor.getColumnIndexOrThrow(COL_IS_COMPLETED)) == 1);
        return todo;
    }
}
