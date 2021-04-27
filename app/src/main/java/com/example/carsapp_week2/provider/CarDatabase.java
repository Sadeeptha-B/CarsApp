package com.example.carsapp_week2.provider;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/* Interface to underlying SQLite database */
@Database(entities = {Car.class}, version = 1)
public abstract class CarDatabase extends RoomDatabase {
    public static final String CAR_DATABASE_NAME="car_database";

    public abstract CarDAO carDao();

    //volatile: Ensure atomic access to the variable
    private static volatile CarDatabase INSTANCE = null;

    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService dataBaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    static CarDatabase getDatabase(final Context context){
        if (INSTANCE == null){
            synchronized (CarDatabase.class){
                if (INSTANCE == null){
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            CarDatabase.class, CAR_DATABASE_NAME).build();
                }
            }
        }
        return INSTANCE;
    }


}
