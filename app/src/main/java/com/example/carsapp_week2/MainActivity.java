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

import java.util.ArrayList;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {

    public static final String CAR_LIST_VIEW = "CAR_LIST_VIEW";
    /*Shared preference files and keys */
    private static SharedPreferences carFile;
    public static final String CAR_FILE = "car_data";

    private static SharedPreferences.Editor carFileEditor;
    private static SharedPreferences.Editor carMakerEditor;

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
    ArrayAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);

        /* Toolbar */
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*Set navigation handler*/
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavHandler());


        drawer = findViewById(R.id.drawer_layout);

        /*Link drawer and toolbar*/
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.acc_desc_drw_open, R.string.acc_desc_drw_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


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
        if (savedInstanceState != null){
            dataSource = savedInstanceState.getStringArrayList(CAR_LIST_VIEW);
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, dataSource);
        listView.setAdapter(adapter);
        listView.setNestedScrollingEnabled(true);

        /*Permissions*/
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS}, 0);


        /*SharedPreference Files */
        carFile = getSharedPreferences(CAR_FILE, 0);
        carFileEditor = carFile.edit();


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

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(CAR_LIST_VIEW, dataSource);
    }

//    @Override
//    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
//        super.onRestoreInstanceState(savedInstanceState);
//        dataSource = savedInstanceState.getStringArrayList(CAR_LIST_VIEW);
//        adapter.notifyDataSetChanged();
//    }

    private void retrieveStringElement(CompactEditText field){
        String content = carFile.getString(field.getKey(), "");
        field.setEditTextString(content);
    }

    private void retrieveNumElement(CompactEditText field){
        int content = carFile.getInt(field.getKey(), 0);
        if (content == 0){
            field.setEditTextString("");
        } else{
            field.setEditTextNum(content);
        }
    }

    /*For storing persistent data*/
    private void storeData(){
        carFileEditor.putString(editTextMaker.getKey(), editTextMaker.getEditTextString());
        carFileEditor.putString(editTextModel.getKey(), editTextModel.getEditTextString());
        carFileEditor.putInt(editTextYear.getKey(), editTextYear.getEditTextNum());
        carFileEditor.putString(editTextColor.getKey(), editTextColor.getEditTextString());
        carFileEditor.putInt(editTextSeats.getKey(), editTextSeats.getEditTextNum());
        carFileEditor.putInt(editTextPrice.getKey(), editTextPrice.getEditTextNum());
        carFileEditor.putString(editTextAddress.getKey(), editTextAddress.getEditTextString());
        carFileEditor.apply();
    }


    public void addCar(){
        boolean added = !editTextMaker.getEditTextString().isEmpty();
        String msg;

        if (added){
            msg = "We added a new car (" + editTextMaker.getEditTextString() +")";
            dataSource.add(editTextMaker.getEditTextString() + " | " + editTextModel.getEditTextString());
            adapter.notifyDataSetChanged();
        } else{
            msg = "Please enter a maker";
        }
        showToast(msg, Toast.LENGTH_SHORT);
    }

    public void removeLastCar(){
        if (!dataSource.isEmpty()){
            dataSource.remove(dataSource.size() - 1);
            adapter.notifyDataSetChanged();
        }
    }

    public void removeAllCars(){
        if (!dataSource.isEmpty()){
            dataSource.clear();
            adapter.notifyDataSetChanged();
        }
    }


    private void showToast(String msg, int length){
        Toast myToast = Toast.makeText(this, msg, length);
        myToast.show();
    }

    /**Loops through all elements and clears them.**/
    public void clear(){
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
    public void clearAll(){
        clear();

        carFileEditor.clear();
        carFileEditor.apply();
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
        }
        return true;
    }
}