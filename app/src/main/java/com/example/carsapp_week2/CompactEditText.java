package com.example.carsapp_week2;

import android.app.Activity;
import android.icu.lang.UCharacter;
import android.text.InputType;
import android.widget.EditText;

import androidx.appcompat.widget.AppCompatEditText;

public class CompactEditText {

    private String key;
    private Activity activity;
    private EditText editText;

    public CompactEditText(Activity activity, String key){
        this.activity = activity;
        this.key = key;


        int id = this.activity.getResources().getIdentifier(key, "id", this.activity.getPackageName());
        this.editText = this.activity.findViewById(id);

    }



    public String getEditTextString(){
        return this.editText.getText().toString();
    }

    public int getEditTextNum(){
        String strVal = this.editText.getText().toString();
        return (!strVal.isEmpty()) ? Integer.parseInt(strVal) : 0;
    }

    public void setEditTextString(String text){
        this.editText.setText(text);
    }

    public void setEditTextNum(int num){
        this.editText.setText(String.valueOf(num));
    }

    public String getKey(){
        return this.key;
    }

    public EditText getEditTextObject(){
        return editText;
    }
}
