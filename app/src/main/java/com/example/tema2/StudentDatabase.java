package com.example.tema2;

import android.content.Context;
import android.content.Context;

import androidx.room.Room;

public class StudentDatabase {
    private Context context;
    private static StudentDatabase studentDatabase;
    private AppDatabase appDatabase;

    private StudentDatabase(Context context) {
        this.context = context;
        appDatabase = Room.databaseBuilder(context, AppDatabase.class, "Students").build();
    }

    public static synchronized StudentDatabase getInstance(Context context) {
        if (studentDatabase == null)
            studentDatabase = new StudentDatabase(context);
        return studentDatabase;
    }

    public AppDatabase getAppDatabase() {
        return appDatabase;
    }
}
