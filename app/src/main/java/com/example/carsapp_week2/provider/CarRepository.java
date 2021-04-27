package com.example.carsapp_week2.provider;

import android.app.Application;
import androidx.lifecycle.LiveData;

import java.util.List;

public class CarRepository {
    private CarDAO mCarDao;
    private LiveData<List<Car>> mAllCars;

    CarRepository(Application application){
        CarDatabase db = CarDatabase.getDatabase(application);
        mCarDao = db.carDao();
        mAllCars = mCarDao.getAllCars();
    }

    LiveData<List<Car>> getAllCars(){
        return mAllCars;
    }

    void insert(Car car){
        CarDatabase.dataBaseWriteExecutor.execute(() -> mCarDao.addCar(car));
    }

    void deleteAll(){
        CarDatabase.dataBaseWriteExecutor.execute(() -> {
            mCarDao.deleteAllCars();
        });
    }

    LiveData<Integer> getCarCount(){
        return mCarDao.getCarCount();
    }

    void deleteCar(String model){
        CarDatabase.dataBaseWriteExecutor.execute(() -> mCarDao.deleteCar(model));
    }

}
