package com.codebrahma.kamesh.todo;

import android.app.Activity;
import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Paint;
import android.os.Bundle;
import android.provider.UserDictionary;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class TodoActivity extends Activity {

    ListView listview;
    List<Todo> todos;
    EditText newTodo, textToChange;
    TextView completedTodo;
    Button addTodo, changeTodo, cancelTodo, eraseCompletedTodo;
    Dialog editTextDialog;
    View itemSelected;
    LinearLayout footer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_todo);

        initialize();

        bindEventsToViews();

        String[] columnsToProject =  {
            DBContract.Feed_Column.ID,
            DBContract.Feed_Column.TEXT,
            DBContract.Feed_Column.STATUS
        };

        Cursor cursor = getContentResolver().query(DBContract.CONTENT_URI, columnsToProject, null, null, null );

        int[] viewsToMap = {
            R.id.position,
            R.id.text,
            R.id.status
        };

        SimpleCursorAdapter simpleCursorAdapter = new SimpleCursorAdapter(TodoActivity.this, R.layout.todo, cursor ,columnsToProject, viewsToMap );
        simpleCursorAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int i) {

                int viewId = view.getId();
                String value;

                if(viewId == R.id.position){
                    value = cursor.getString(cursor.getColumnIndex("_id"));
                    ((TextView) view).setText(value);
                }

                if(viewId == R.id.text){
                    value = cursor.getString(cursor.getColumnIndex("text"));
                    ((TextView) view).setText(value);
                    ViewGroup parent = ((ViewGroup) view.getParent());
                    bindEventToDeleteTodo(parent.getChildAt(3), (TextView) parent.getChildAt(2));
                }

                if(viewId == R.id.status){
                    value = cursor.getString(cursor.getColumnIndex("status"));
                    CheckBox status = (CheckBox) view;
                    status.setChecked(Boolean.parseBoolean(value));
                    strikeThroughTodo(status);
                    status.setOnCheckedChangeListener(getStatusListener());
                }
                return true;
            }
        });
        listview.setAdapter(simpleCursorAdapter);
    }

    public void bindEventToDeleteTodo(View delete, final TextView position){
        ImageView deleteButton = (ImageView) delete;
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getContentResolver().delete(DBContract.CONTENT_URI, "_id = ?", new String[]{
                    position.getText().toString()
                });
                setCompletedTodos();
            }
        });
    }

    public void initialize() {
        todos           = new ArrayList<Todo>();
        newTodo         = (EditText) findViewById(R.id.new_todo);
        addTodo         = (Button) findViewById(R.id.enter_key);
        listview        = (ListView) findViewById(R.id.todo_list);
        editTextDialog  = new Dialog(this);
        View dialogView = editTextDialog.getWindow().getDecorView();
        editTextDialog.setContentView(R.layout.edit_dialog);
        editTextDialog.setTitle("Change Todo Description");
        textToChange    = (EditText) dialogView.findViewById(R.id.text_to_be_changed);
        changeTodo      = (Button) dialogView.findViewById(R.id.set_text);
        cancelTodo      = (Button) dialogView.findViewById(R.id.clear);
        footer          = (LinearLayout) findViewById(R.id.footer);
        completedTodo   = (TextView) findViewById(R.id.completed_todos);
        eraseCompletedTodo = (Button) findViewById(R.id.erase_completed);
        setCompletedTodos();
    }

    public void strikeThroughTodo(CheckBox status){
        TextView todoText = (TextView)((ViewGroup)status.getParent()).getChildAt(1);
        if(status.isChecked()){
            todoText.setPaintFlags(todoText.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else{
            todoText.setPaintFlags(0);
        }
    }

    public void setCompletedTodos(){
        Integer completedTodos = getCompletedTodos();
        if(completedTodos > 0){
            completedTodo.setText(completedTodos + " Todo(s) Completed");
            footer.setVisibility(View.VISIBLE);
        }
        else{
            footer.setVisibility(View.INVISIBLE);
        }
    }

    public Integer getCompletedTodos(){
        Cursor cursor = getContentResolver().query(DBContract.CONTENT_URI,
                new String[]{ DBContract.Feed_Column.ID },
                "status = ?",
                new String[]{ "true" },
                null);
        return cursor.getCount();
    }

    public CompoundButton.OnCheckedChangeListener getStatusListener(){
        return new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                ViewGroup layout = (ViewGroup) compoundButton.getParent();
                Integer position = Integer.parseInt(((TextView) layout.getChildAt(2)).getText().toString());
                updateStatus(position, (b ? "true" : "false"));
            }
        };
    }

    public void updateStatus(Integer position, String status){
        ContentValues newValuesToUpdate = new ContentValues();
        String[] selectionArgs = { position.toString() };

        newValuesToUpdate.put(DBContract.Feed_Column.STATUS, status);
        getContentResolver().update(DBContract.CONTENT_URI, newValuesToUpdate, "_id = ?", selectionArgs);
        setCompletedTodos();
    }

    public void updateText(Integer position, String text){
        ContentValues newValuesToUpdate = new ContentValues();
        String[] selectionArgs = { position.toString() };

        newValuesToUpdate.put(DBContract.Feed_Column.TEXT, text);
        getContentResolver().update(DBContract.CONTENT_URI, newValuesToUpdate, "_id = ?", selectionArgs);
    }

    public void bindEventsToViews(){

        /* Event Listener for button which adds a todo when clicked */
        addTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTodoToList();
            }
        });


        /* Event Listener for Textbox which adds a todo when Enter key pressed on it */
        newTodo.setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if(keyEvent.getKeyCode() == KeyEvent.KEYCODE_ENTER){
                    addTodoToList();
                    return true;
                }
                return false;
            }
        });

        /* Event Listener for List Items which shows a dialog for editing a todo, when long clicked */
        listview.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                showEditDialog((TextView)view.findViewById(R.id.text));
                itemSelected = view;
                return false;
            }
        });

        changeTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Integer position = Integer.parseInt(((TextView) ((ViewGroup) itemSelected).getChildAt(2)).getText().toString());
                String text = textToChange.getText().toString();
                updateText(position, text);
                editTextDialog.hide();
            }
        });

        cancelTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                itemSelected = null;
                editTextDialog.hide();
            }
        });

        eraseCompletedTodo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eraseAllCompletedTodos();
            }
        });

    }

    public void eraseAllCompletedTodos(){
        getContentResolver().delete(DBContract.CONTENT_URI,
                "status = ?",
                new String[]{ "true" }
        );
    }

    public void showEditDialog(TextView todoText){
        textToChange.setText(todoText.getText().toString());
        editTextDialog.show();
    }

    public void addTodoToList(){
        String textForNewTodo = newTodo.getText().toString();
        newTodo.setText("");
        if(textForNewTodo.length() > 0){
            ContentValues contentValues = new ContentValues();
            contentValues.put(DBContract.Feed_Column.TEXT, textForNewTodo);
            contentValues.put(DBContract.Feed_Column.STATUS, false);
            getContentResolver().insert(DBContract.CONTENT_URI, contentValues);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.todo, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
