package com.example.carsapp_week2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {

    public static final String CAR_LIST_VIEW = "CAR_LIST_VIEW";

    /*Shared preference files and keys */
    private static SharedPreferences singleCarFile;
    private static SharedPreferences.Editor singleCarFileEditor;
    public static final String SINGLE_CAR_FILE = "single_car_data";

    private static SharedPreferences carsListFile;
    private static SharedPreferences.Editor carsListEditor;
    public static final String CARS_LIST_FILE = "CARS_LIST_FILE";
    public static final String CARS_LIST = "CARS_LIST";


    /*Fields*/
    private CompactEditText editTextMaker;
    private CompactEditText editTextModel;
    private CompactEditText editTextYear;
    private CompactEditText editTextColor;
    private CompactEditText editTextSeats;
    private CompactEditText editTextPrice;
    private CompactEditText editTextAddress;

    /*UI components */
    DrawerLayout drawer;
    Toolbar toolbar;
    NavigationView navigationView;
    FloatingActionButton fabBtn;
    ListView listView;

    ArrayList<String> dataSource = new ArrayList<String>();
    ArrayList<String> completeData = new ArrayList<String>();

    ArrayAdapter adapter;
    Gson gson = new Gson();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);

        /*Permissions*/
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS}, 0);


        /*SharedPreference Files */
        singleCarFile = getSharedPreferences(SINGLE_CAR_FILE, 0);
        singleCarFileEditor = singleCarFile.edit();

        carsListFile = getSharedPreferences(CARS_LIST_FILE, 0);
        carsListEditor = carsListFile.edit();

        String cars = carsListFile.getString(CARS_LIST, "");

        if (cars != ""){
            Type type = new TypeToken<ArrayList<String>>() {}.getType();
            completeData = gson.fromJson(cars, type);

            for (int i=0; i< completeData.size(); i++){
                String[] strArr = completeData.get(i).split("\\ \\|\\ ");
                dataSource.add(strArr[0] + " | " + strArr[1]);
            }
        }

        initializeUIComponents();

        /* Broadcast receivers*/
        IntentFilter intentFilter = new IntentFilter(SMSReceiver.SMS_FILTER);
        registerReceiver(myBroadcastReceiver, intentFilter);

        //EditText field instantiated
        editTextMaker = new CompactEditText(this, "editTextMaker");
        editTextModel = new CompactEditText(this, "editTextModel");
        editTextYear = new CompactEditText(this, "editTextYear");
        editTextColor = new CompactEditText(this, "editTextColor");
        editTextSeats = new CompactEditText(this, "editTextSeats");
        editTextPrice = new CompactEditText(this, "editTextPrice");
        editTextAddress = new CompactEditText(this, "editTextAddress");
    }


    private void initializeUIComponents(){
        /* Toolbar */
        toolbar = findViewById(R.id.toolbar1);
        setSupportActionBar(toolbar);

        /*Link drawer and toolbar*/
        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.acc_desc_drw_open, R.string.acc_desc_drw_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        /*Set navigation handler*/
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavHandler());

        /*Floating Action Button*/
        fabBtn = findViewById(R.id.fab);
        fabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCar();
            }
        });

        /*ListView*/
        listView = findViewById(R.id.listView);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataSource);
        listView.setAdapter(adapter);
        listView.setNestedScrollingEnabled(true);
    }


    @Override
    protected void onStart() {
        super.onStart();
        retrieveStringElement(editTextMaker);
        retrieveStringElement(editTextModel);
        retrieveNumElement(editTextYear);
        retrieveStringElement(editTextColor);
        retrieveNumElement(editTextSeats);
        retrieveNumElement(editTextPrice);
        retrieveStringElement(editTextAddress);
    }

    @Override
    protected void onStop() {
        super.onStop();
        storeData();
    }


    private void retrieveStringElement(CompactEditText field){
        String content = singleCarFile.getString(field.getKey(), "");
        field.setEditTextString(content);
    }

    private void retrieveNumElement(CompactEditText field){
        int content = singleCarFile.getInt(field.getKey(), 0);
        if (content == 0){
            field.setEditTextString("");
        } else{
            field.setEditTextNum(content);
        }
    }

    /*For storing persistent data*/
    private void storeData(){
        singleCarFileEditor.putString(editTextMaker.getKey(), editTextMaker.getEditTextString());
        singleCarFileEditor.putString(editTextModel.getKey(), editTextModel.getEditTextString());
        singleCarFileEditor.putInt(editTextYear.getKey(), editTextYear.getEditTextNum());
        singleCarFileEditor.putString(editTextColor.getKey(), editTextColor.getEditTextString());
        singleCarFileEditor.putInt(editTextSeats.getKey(), editTextSeats.getEditTextNum());
        singleCarFileEditor.putInt(editTextPrice.getKey(), editTextPrice.getEditTextNum());
        singleCarFileEditor.putString(editTextAddress.getKey(), editTextAddress.getEditTextString());
        singleCarFileEditor.apply();

        String carsList = gson.toJson(completeData);
        carsListEditor.putString(CARS_LIST, carsList).apply();
    }


    private void addCar(){
        boolean added = !editTextMaker.getEditTextString().isEmpty();
        String msg;

        if (added){
            msg = "We added a new car (" + editTextMaker.getEditTextString() +")";
            dataSource.add(editTextMaker.getEditTextString() + " | " + editTextModel.getEditTextString());
            completeData.add(generateAddString());
            adapter.notifyDataSetChanged();
        } else{
            msg = "Please enter a maker";
        }
        showToast(msg, Toast.LENGTH_SHORT);
    }

    private String generateAddString(){
        return editTextMaker.getEditTextString() + " | " + editTextModel.getEditTextString() + " | " +
                editTextYear.getEditTextString() + " | " + editTextColor.getEditTextString() + " | " +
                editTextSeats.getEditTextString() + " | " + editTextPrice.getEditTextString() + " | " +
                editTextAddress.getEditTextString();
    }

    private void removeLastCar(){
        if (!dataSource.isEmpty()){
            dataSource.remove(dataSource.size() - 1);
            completeData.remove(completeData.size() - 1);
            adapter.notifyDataSetChanged();
        }

    }

    private void removeAllCars(){
        if (!dataSource.isEmpty()){
            dataSource.clear();
            completeData.clear();
            adapter.notifyDataSetChanged();
        }
    }

    private void listAllItems(){
        Intent carListIntent = new Intent(this, ListCarsActivity.class);
        carListIntent.putExtra(CARS_LIST, completeData);
        startActivity(carListIntent);
    }

    private void showToast(String msg, int length){
        Toast myToast = Toast.makeText(this, msg, length);
        myToast.show();
    }

    /**Loops through all elements and clears them.**/
    private void clear(){
        ArrayList<EditText> fields = new ArrayList<>();

        fields.add(findViewById(R.id.editTextMaker));
        fields.add(findViewById(R.id.editTextModel));
        fields.add(findViewById(R.id.editTextYear));
        fields.add(findViewById(R.id.editTextColor));
        fields.add(findViewById(R.id.editTextSeats));
        fields.add(findViewById(R.id.editTextPrice));
        fields.add(findViewById(R.id.editTextAddress));

        for (int i =0; i < fields.size(); i++) {
            if (!fields.get(i).toString().isEmpty()){
                fields.get(i).setText("");
            }

        }
    }

    /**Clear and forget persistent data.**/
    private void clearAll(){
        clear();

        singleCarFileEditor.clear();
        singleCarFileEditor.apply();
    }

    BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String msg = intent.getStringExtra(SMSReceiver.SMS_MSG_KEY);

             try {
                StringTokenizer sT = new StringTokenizer(msg, ";");
                 editTextMaker.setEditTextString(sT.nextToken());
                 editTextModel.setEditTextString(sT.nextToken());
                 editTextYear.setEditTextNum(Integer.parseInt(sT.nextToken()));
                 editTextColor.setEditTextString(sT.nextToken());

                 int seats = Integer.parseInt(sT.nextToken());
                 if (seats >= 4 && seats <=8){
                     editTextSeats.setEditTextNum(seats);
                 } else{
                     editTextSeats.setEditTextString("Seat number should be between 5 and 8");
                 }

                 editTextPrice.setEditTextNum(Integer.parseInt(sT.nextToken()));
                 editTextAddress.setEditTextString(sT.nextToken());
            } catch (Exception e){}
            showToast(msg, Toast.LENGTH_LONG);
        }
    };


    class NavHandler implements NavigationView.OnNavigationItemSelectedListener{

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();

            switch (id){
                case R.id.add_car_nav:
                    addCar();
                    break;
                case R.id.remove_last_nav:
                    removeLastCar();
                    break;
                case R.id.remove_all_nav:
                    removeAllCars();
                    break;
                case R.id.list_all_items:
                    listAllItems();
                    break;
                case R.id.close:
                    finish();
                    break;
            }

            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id){
            case R.id.clear_all:
                clearAll();
                break;
            case R.id.total_cars:
                showToast("Total number of cars: " + dataSource.size(), Toast.LENGTH_SHORT);
                break;
        }

        return true;
    }
}