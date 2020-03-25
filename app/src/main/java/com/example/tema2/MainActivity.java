package com.example.tema2;


import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;


public class MainActivity extends AppCompatActivity {

    private EditText fullName, mark;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable  Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        recyclerView = findViewById(R.id.recycler_view);
        //recyclerView.hasFixedSize(true);

        fullName = findViewById(R.id.edit_1);
        mark = findViewById(R.id.edit_2);

        findViewById(R.id.button_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveStudent();
            }
        });

        findViewById(R.id.button_remove).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeStudent();
            }
        });

        getStudents();
    }

    private void saveStudent() {
        final String mName = fullName.getText().toString().trim();
        final String mMark = mark.getText().toString().trim();

        if (mName.isEmpty()) {
            fullName.setError("Introduceti numele studentului");
            fullName.requestFocus();
            return;
        }

        if (mMark.isEmpty()) {
            mark.setError("Introduceti nota studentului");
            mark.requestFocus();
            return;
        }

        if (Integer.parseInt(mMark) < 1 || Integer.parseInt(mMark) > 10) {
            mark.setError("Nota trebuie sa se incadreze intre 1 si 10");
            mark.requestFocus();
            return;
        }

        class saveTask extends AsyncTask<Void, Void, Void> {
            @Override
            protected Void doInBackground(Void...voids) {
                Student student = new Student();
                student.setName(mName);
                student.setMark(Integer.parseInt(mMark));

                StudentDatabase.getInstance(getApplicationContext()).getAppDatabase().studentDAO().insert(student);
                return null;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                finish();
                startActivity(new Intent(getApplicationContext(),MainActivity.class));
                Toast.makeText(getApplicationContext(),"Added",Toast.LENGTH_LONG).show();
            }
        }

        saveTask save = new saveTask();
        save.execute();
    }

    private void removeStudent() {
        final String mName = fullName.getText().toString().trim();

        if (mName.isEmpty()) {
            fullName.setError("Introduceti numele studentului");
            fullName.requestFocus();
            return;
        }

        class removeStudents extends AsyncTask<Void, Void, Void> {
            Boolean found = false;

            @Override
            protected Void doInBackground(Void... voids) {
                Student student = StudentDatabase.getInstance(getApplicationContext()).getAppDatabase().studentDAO().findByName(mName);
                if (student!=null) {
                    this.found = true;
                    StudentDatabase.getInstance(getApplicationContext()).getAppDatabase().studentDAO().deleteByName(mName);
                }
                return null;
            }

            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (this.found) {
                    Toast.makeText(getApplicationContext(), "Student sters", Toast.LENGTH_LONG).show();
                    finish();
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                } else {
                    mark.setText("");
                    Toast.makeText(getApplicationContext(), "Not found", Toast.LENGTH_LONG).show();
                }
            }
        }

        removeStudents remove = new removeStudents();
        remove.execute();
    }

    private void getStudents() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        class GetStudents extends AsyncTask<Void, Void, List<Student>> {

            @Override
            protected List<Student> doInBackground(Void...voids) {
                List<Student> students = StudentDatabase.getInstance(getApplicationContext()).getAppDatabase().studentDAO().getAll();
                return students;
            }

            @Override
            protected void onPostExecute(List<Student> students) {
                super.onPostExecute(students);

                RecyclerViewAdapter adapter = new RecyclerViewAdapter(students);
                recyclerView.setAdapter(adapter);
            }
        }

        GetStudents st = new GetStudents();
        st.execute();
    }
}
