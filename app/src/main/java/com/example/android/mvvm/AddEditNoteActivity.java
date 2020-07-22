package com.example.android.mvvm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;

public class AddEditNoteActivity extends AppCompatActivity {
    public static final String extra_id = "ID";
    public static final String extra_desc = "EXTRA";
    public static final String extra_title = "TITLE";
    public static final String extra_priority = "PRIORITY";
private EditText editTextTitle;
private EditText editTextDesc;
private NumberPicker numberPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        editTextDesc = findViewById(R.id.edit_text_desc);
        editTextTitle = findViewById(R.id.edit_text_title);
        numberPicker = findViewById(R.id.number_picker);

        numberPicker.setMaxValue(10);
        numberPicker.setMinValue(1);

        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);

        Intent intent = getIntent();
        if(intent.hasExtra(extra_id)){
            setTitle("Edit Note");
            editTextDesc.setText(intent.getStringExtra(extra_desc));
            editTextTitle.setText(intent.getStringExtra(extra_title));
            numberPicker.setValue(intent.getIntExtra(extra_priority, 1));
        }
        else
        setTitle("Add Note");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.add_note_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_note:
                saveNote();
            return true;

            default:
                return super.onOptionsItemSelected(item);

        }

    }
    private void saveNote(){
        String title = editTextTitle.getText().toString();
        String desc = editTextDesc.getText().toString();
        int priority = numberPicker.getValue();

        if(title.trim().isEmpty() || desc.trim().isEmpty()){
            Toast.makeText(this, "Please insert description and title" , Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(extra_desc, desc);
        intent.putExtra(extra_title, title);
        intent.putExtra(extra_priority, priority);
        int id = getIntent().getIntExtra(extra_id, -1);
        if(id!=-1){
            intent.putExtra(extra_id, id);

        }

        setResult(RESULT_OK, intent);
        finish();

    }
}
