package com.example.carsapp_week2;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

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
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.example.carsapp_week2.provider.Car;
import com.example.carsapp_week2.provider.CarViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.concurrent.Callable;

public class MainActivity extends AppCompatActivity {

    public static final String CAR_LIST_VIEW = "CAR_LIST_VIEW";

    /*Shared preference files and keys */
    private static SharedPreferences singleCarFile;
    private static SharedPreferences.Editor singleCarFileEditor;
    public static final String SINGLE_CAR_FILE = "single_car_data";

    /*UI components */
    View constraintLayout;
    DrawerLayout drawer;
    Toolbar toolbar;
    NavigationView navigationView;
    FloatingActionButton fabBtn;

    /*Fields*/
    private CompactEditText editTextMaker;
    private CompactEditText editTextModel;
    private CompactEditText editTextYear;
    private CompactEditText editTextColor;
    private CompactEditText editTextSeats;
    private CompactEditText editTextPrice;
    private CompactEditText editTextAddress;


    ArrayAdapter<String> adapter;
    public static CarViewModel mCarViewModel;
    private int carCount;

    /* Firebase */
    DatabaseReference myRef;

    /*Touch Coordinates*/
    double xInit;
    double yInit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);

        constraintLayout = findViewById(R.id.constraintLayout);

        /*Permissions*/
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS}, 0);

        initializeDataSources();
        initializeUIComponents();

        /* Firebase */
        firebaseInit();

        /* Broadcast receiver*/
        IntentFilter intentFilter = new IntentFilter(SMSReceiver.SMS_FILTER);
        registerReceiver(myBroadcastReceiver, intentFilter);
    }

    /** Initializes shared preference files and view model */
    private void initializeDataSources(){
        /*SharedPreference Files */
        singleCarFile = getSharedPreferences(SINGLE_CAR_FILE, 0);
        singleCarFileEditor = singleCarFile.edit();

        mCarViewModel = new ViewModelProvider(this).get(CarViewModel.class);

        mCarViewModel.getCarCount().observe(this, newData -> {
            carCount = newData;
        });
    }

    /** Get firebase instance and set reference. Create a listener to listen to changes **/
    private void firebaseInit(){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("autoShowroom/fleet");
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

        //EditText field instantiated
        editTextMaker = new CompactEditText(this, "editTextMaker");
        editTextModel = new CompactEditText(this, "editTextModel");
        editTextYear = new CompactEditText(this, "editTextYear");
        editTextColor = new CompactEditText(this, "editTextColor");
        editTextSeats = new CompactEditText(this, "editTextSeats");
        editTextPrice = new CompactEditText(this, "editTextPrice");
        editTextAddress = new CompactEditText(this, "editTextAddress");

        constraintLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getActionMasked();
                switch (action){
                    case MotionEvent.ACTION_DOWN:
                        xInit = event.getX();
                        yInit = event.getY();

                        if (yInit < 100){
                            if (xInit < 100) {
                                int price = editTextPrice.getEditTextNum();
                                editTextPrice.setEditTextNum(price < 50 ? 0 : price - 50);
                                showToast("$50 subtracted with a minimum of $0", Toast.LENGTH_SHORT);
                            } else if (constraintLayout.getWidth() - xInit < 100){
                                editTextPrice.setEditTextNum(editTextPrice.getEditTextNum() + 50);
                                showToast("$50 added", Toast.LENGTH_SHORT);
                            }
                        }

                        break;
                    case MotionEvent.ACTION_UP:
                        if (Math.abs(yInit - event.getY()) < 40) {
                            leftToRightAction(xInit, event.getX());
                        }
                        if (Math.abs(xInit - event.getX()) < 40) {
                            topToBottomAction(yInit, event.getY());
                        }
                        break;
                }

                return true;
            }
        });
    }

    /** Retrieves only field data on start. **/
    @Override
    protected void onStart() {
        super.onStart();
        retrieveStringField(editTextMaker);
        retrieveStringField(editTextModel);
        retrieveNumField(editTextYear);
        retrieveStringField(editTextColor);
        retrieveNumField(editTextSeats);
        retrieveNumField(editTextPrice);
        retrieveStringField(editTextAddress);
    }

    /**Stores all data to be kept persistent when stopping the activity.
    **/
    @Override
    protected void onStop() {
        super.onStop();
        storeFieldData();
    }

    /** Retrieve text field data **/
    private void retrieveStringField(CompactEditText field){
        String content = singleCarFile.getString(field.getKey(), "");
        field.setEditTextString(content);
    }

    /** Retrieve number field data **/
    private void retrieveNumField(CompactEditText field){
        int content = singleCarFile.getInt(field.getKey(), 0);
        if (content == 0){
            field.setEditTextString("");
        } else{
            field.setEditTextNum(content);
        }
    }

    /** For storing field data persistently **/
    private void storeFieldData(){
        singleCarFileEditor.putString(editTextMaker.getKey(), editTextMaker.getEditTextString());
        singleCarFileEditor.putString(editTextModel.getKey(), editTextModel.getEditTextString());
        singleCarFileEditor.putInt(editTextYear.getKey(), editTextYear.getEditTextNum());
        singleCarFileEditor.putString(editTextColor.getKey(), editTextColor.getEditTextString());
        singleCarFileEditor.putInt(editTextSeats.getKey(), editTextSeats.getEditTextNum());
        singleCarFileEditor.putInt(editTextPrice.getKey(), editTextPrice.getEditTextNum());
        singleCarFileEditor.putString(editTextAddress.getKey(), editTextAddress.getEditTextString());
        singleCarFileEditor.apply();
    }

    private void addCar(){
        boolean validEntry = !editTextMaker.getEditTextString().isEmpty();
        String msg;

        if (validEntry){
            msg = "We added a new car (" + editTextMaker.getEditTextString() +")";
            Car car = new Car(editTextMaker.getEditTextString(),editTextModel.getEditTextString(),editTextYear.getEditTextNum(),
                    editTextColor.getEditTextString(), editTextSeats.getEditTextNum() ,editTextPrice.getEditTextNum(),editTextAddress.
                    getEditTextString());
            mCarViewModel.insert(car);
            myRef.push().setValue(car);
        } else{
            msg = "Please enter a maker";
        }
        showToast(msg, Toast.LENGTH_SHORT);
    }

    private void removeLastCar(){
//        mCarViewModel.deleteCar(carCount);
    }

    private void removeAllCars(){
        myRef.removeValue();
        mCarViewModel.deleteAll();
    }

    private void moveToListCarsActivity(){
        Intent carListIntent = new Intent(this, ListCarsActivity.class);
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

    /**Clear and forget persistent field data.**/
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
                    moveToListCarsActivity();
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
                showToast("Total number of cars: " + carCount, Toast.LENGTH_SHORT);
                break;
        }

        return true;
    }

    private void leftToRightAction(double xInit, double currentX){
        if (xInit - currentX < 0) {
            showToast("Left to right gesture", Toast.LENGTH_SHORT);
            addCar();
        }
    }

    private void topToBottomAction(double yInit, double currentY){
        if (yInit - currentY < 0){
            showToast("Top to bottom gesture: Cleared", Toast.LENGTH_SHORT);
            clear();
        }
    }
}