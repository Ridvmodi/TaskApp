package com.example.android.taskapp;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.taskapp.Model.Data;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.util.Date;

public class HomeActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private FloatingActionButton fabBtn;
    private RecyclerView recyclerView;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private FirebaseUser mUser;

    private EditText updateTitleView;
    private EditText updateNoteView;
    private Button updateBtn;
    private Button deleteBtn;

    private String updateTitle;
    private String updateNote;
    private String post_id;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Task App");

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        final String userId = mUser.getUid();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("TaskNote").child(userId);

        mDatabase.keepSynced(true);

        recyclerView = findViewById(R.id.recycler);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);

        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        fabBtn = findViewById(R.id.fab_btn);

        fabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder myDialog = new AlertDialog.Builder(HomeActivity.this);
                LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);
                View view = inflater.inflate(R.layout.custominputfield, null);
                myDialog.setView(view);
                final AlertDialog dialog = myDialog.create();

                final EditText titleView = view.findViewById(R.id.edt_title);
                final EditText noteView = view.findViewById(R.id.edt_note);
                Button saveBtn = view.findViewById(R.id.save_btn);

                saveBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String title = titleView.getText().toString().trim();
                        String note = noteView.getText().toString().trim();
                        if(TextUtils.isEmpty(title)) {
                            titleView.setError("Required..");
                            return;
                        }
                        if(TextUtils.isEmpty(note)) {
                            noteView.setError("Required..");
                            return;
                        }

                        String id = mDatabase.push().getKey();
                        String date = DateFormat.getDateInstance().format(new Date());

                        Data data = new Data(title, note, date, id);

                        mDatabase.child(id).setValue(data);

                        Toast.makeText(getApplicationContext(), "Task Added..", Toast.LENGTH_SHORT).show();

                        dialog.dismiss();

                    }
                });

                dialog.show();

            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseRecyclerAdapter<Data, MyViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Data, MyViewHolder>(
                Data.class, R.layout.item_data,
                MyViewHolder.class, mDatabase ) {
            @Override
            protected void populateViewHolder(MyViewHolder viewHolder, final Data model, final int position) {
                viewHolder.setTitle(model.getTitle());
                viewHolder.setNote(model.getNote());
                viewHolder.setDate(model.getDate());

                viewHolder.myView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        updateTitle = model.getTitle();
                        updateNote = model.getNote();
                        post_id = getRef(position).getKey();

                        updateData();
                    }
                });
            }
        };

        recyclerView.setAdapter(firebaseRecyclerAdapter);
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        View myView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            myView = itemView;
        }

        public void setTitle(String title) {
            TextView titleView = myView.findViewById(R.id.title);
            titleView.setText(title);
        }
        public void setNote(String note) {
            TextView noteView = myView.findViewById(R.id.note);
            noteView.setText(note);
        }
        public void setDate(String date) {
            TextView dateView = myView.findViewById(R.id.date);
            dateView.setText(date);
        }
    }

    public void updateData() {
        AlertDialog.Builder myDialog = new AlertDialog.Builder(HomeActivity.this);
        LayoutInflater inflater = LayoutInflater.from(HomeActivity.this);
        View view = inflater.inflate(R.layout.update_field,null);
        myDialog.setView(view);
        final AlertDialog dialog = myDialog.create();

        updateTitleView = view.findViewById(R.id.edt_title_upd);
        updateNoteView = view.findViewById(R.id.edt_note_upd);
        updateBtn = view.findViewById(R.id.update_btn);
        deleteBtn = view.findViewById(R.id.delete_btn);

        updateTitleView.setText(updateTitle);
        updateTitleView.setSelection(updateTitle.length());
        updateNoteView.setText(updateNote);
        updateNoteView.setSelection(updateNote.length());

        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateTitle = updateTitleView.getText().toString().trim();
                updateNote = updateNoteView.getText().toString().trim();

                if(TextUtils.isEmpty(updateTitle)) {
                    updateTitleView.setError("Required..");
                    return;
                }
                if(TextUtils.isEmpty(updateNote)) {
                    updateNoteView.setError("Required..");
                    return;
                }

                String date = DateFormat.getDateInstance().format(new Date());

                Data data = new Data(updateTitle, updateNote, date, post_id);
                mDatabase.child(post_id).setValue(data).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                            Toast.makeText(getApplicationContext(), "Successfully updated", Toast.LENGTH_SHORT).show();
                        else {
                            Toast.makeText(getApplicationContext(), "Error in updating", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                dialog.dismiss();

            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mDatabase.child(post_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                            Toast.makeText(getApplicationContext(), "Successfully deleted..", Toast.LENGTH_SHORT).show();
                        else {
                            Toast.makeText(getApplicationContext(), "Error in deleting", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                dialog.dismiss();

            }
        });

        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.mainmenu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logout:
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if(mAuth.getCurrentUser() != null) {
            ActivityCompat.finishAffinity(HomeActivity.this);
        } else {
            super.onBackPressed();
        }
    }
}
