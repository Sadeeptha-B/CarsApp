package com.example.carsapp_week2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Telephony;
import android.telephony.SmsMessage;

public class SMSReceiver extends BroadcastReceiver {

    public static final String SMS_FILTER = "SMS_FILTER";
    public static final String SMS_MSG_KEY = "SMS_MSG_KEY";

    @Override
    public void onReceive(Context context, Intent intent) {
        SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
        for (int i = 0; i < messages.length; i++){
            SmsMessage currentMessage = messages[i];
            String message = currentMessage.getDisplayMessageBody();

            Intent myIntent = new Intent();
            myIntent.setAction(SMS_FILTER);
            myIntent.putExtra(SMS_MSG_KEY, message);
            context.sendBroadcast(myIntent);
        }
    }
}
