package com.example.android.mvvm;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private NoteViewModel noteViewModel;
    public static final int REQUEST_CODE = 1;
    public static final int REQUEST_CODE_EDIT = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton button = findViewById(R.id.button_add);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, AddEditNoteActivity.class);
                startActivityForResult(i,REQUEST_CODE);
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        final NoteAdapter adapter = new NoteAdapter();
        recyclerView.setAdapter(adapter);

        noteViewModel = ViewModelProviders.of(this).get(NoteViewModel.class);
        noteViewModel.getAllNotes().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                //update RecyclerView
                adapter.setNotes(notes);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT |ItemTouchHelper.RIGHT ) {


            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                noteViewModel.delete(adapter.getNoteAt(viewHolder.getAdapterPosition()));
                Toast.makeText(MainActivity.this, "Note deleted", Toast.LENGTH_SHORT).show();
            }
        }).attachToRecyclerView(recyclerView);
        adapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Note note) {
                Intent i = new Intent(MainActivity.this, AddEditNoteActivity.class);
                i.putExtra(AddEditNoteActivity.extra_id, note.getId());
                i.putExtra(AddEditNoteActivity.extra_desc, note.getDescription());
                i.putExtra(AddEditNoteActivity.extra_title, note.getTitle());
                i.putExtra(AddEditNoteActivity.extra_priority, note.getPriority());
                startActivityForResult(i, REQUEST_CODE_EDIT);
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK){
            String title = data.getStringExtra(AddEditNoteActivity.extra_title);
            String desc = data.getStringExtra(AddEditNoteActivity.extra_desc);
            int priority = data.getIntExtra(AddEditNoteActivity.extra_priority, 1);

            Note note = new Note(title, desc, priority);
            noteViewModel.insert(note);
            Toast.makeText(this, "Note saved!", Toast.LENGTH_SHORT).show();
        }
        else
        if(requestCode == REQUEST_CODE_EDIT&& resultCode == RESULT_OK){
            int id = data.getIntExtra(AddEditNoteActivity.extra_id, -1);
            if(id == -1){
                Toast.makeText(this, "Note cannot be updated!", Toast.LENGTH_SHORT).show();
            }
            String title = data.getStringExtra(AddEditNoteActivity.extra_title);
            String desc = data.getStringExtra(AddEditNoteActivity.extra_desc);
            int priority = data.getIntExtra(AddEditNoteActivity.extra_priority, 1);
            Note note = new Note(title, desc, priority);
            note.setId(id);
            noteViewModel.update(note);
            Toast.makeText(this, "Note updated!", Toast.LENGTH_SHORT).show();
        }
        else{
            Toast.makeText(this, "Note is not saved", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.delete_all_notes:
                noteViewModel.deleteAllNotes();
                Toast.makeText(this, "All notes deleted", Toast.LENGTH_SHORT).show();
                return true;
                default:
                    return super.onOptionsItemSelected(item);
        }

    }
}
