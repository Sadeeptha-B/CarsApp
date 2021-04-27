package com.example.carsapp_week2.provider;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class CarViewModel extends AndroidViewModel {
    private CarRepository mRepository;
    private LiveData<List<Car>> mAllCars;

    public CarViewModel(@NonNull Application application) {
        super(application);
        mRepository = new CarRepository(application);
        mAllCars = mRepository.getAllCars();
    }

    public LiveData<List<Car>> getAllCars(){
        return mAllCars;
    }

    public void insert(Car car){
        mRepository.insert(car);
    }

    public void deleteAll(){
        mRepository.deleteAll();
    }

    public LiveData<Integer> getCarCount() { return mRepository.getCarCount();}

    public void deleteCar(String model){ mRepository.deleteCar(model);}
}
