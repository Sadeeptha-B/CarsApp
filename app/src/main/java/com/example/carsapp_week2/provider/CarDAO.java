package com.example.carsapp_week2.provider;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

/*Define database interactions*/
@Dao
public interface CarDAO {

    @Query("select * from cars")
    LiveData<List<Car>> getAllCars();

    @Query("select * from cars where carId=:id")
    List<Car> getCar(int id);

    @Insert
    void addCar(Car car);

    @Query("delete from cars where carId=:id")
    void deleteCar(int id);

    @Query("delete from cars")
    void deleteAllCars();
}

