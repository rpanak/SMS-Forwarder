package com.radovanpanak.android.SimleSmsForwarder;

import android.annotation.TargetApi;
import android.content.*;
//import android.os.Bundle;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

import java.util.*;
//import java.util.List;

/**
 * User: rado
 * Date: 30. 11. 2014
 * Time: 22:08
 */
public class Forwarder extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        final SharedPreferences settings = context.getSharedPreferences(Settings.SETTINGS, Context.MODE_PRIVATE);
        final boolean enabled = settings.getBoolean(Settings.SETTINGS_ENABLED, false);
        final String targetNo = settings.getString(Settings.SETTINGS_TARGET_NO, null);
        final SmsManager smsMgr = SmsManager.getDefault();
        if (enabled && targetNo != null && targetNo.length() > 0 && smsMgr != null) {
            final Map<String, List<SmsMessage>> messagesBySender = getMessagesBySender(Telephony.Sms.Intents.getMessagesFromIntent(intent));
            final StringBuilder sb = new StringBuilder();
            for (Map.Entry<String, List<SmsMessage>> entry : messagesBySender.entrySet()) {
                final String senderNumber = entry.getKey();
                final List<SmsMessage> messages = entry.getValue();
                if (targetNo.equals(senderNumber)) {
                    //TODO: forward reply FROM targetNo
                }
                sb.setLength(0);
                sb.append(senderNumber);
                if (true) {
                    sb.append(" (").append(findContactWithNumber(context, senderNumber)).append(")");
                }
                sb.append(": ");
                for (SmsMessage message : messages) {
                    sb.append(message.getMessageBody());
                }
                smsMgr.sendMultipartTextMessage(targetNo, null, smsMgr.divideMessage(sb.toString()), null, null);
                Log.i("Forwarder", "Forwarded to: " + targetNo);
            }
        }
    }

    private Map<String, List<SmsMessage>> getMessagesBySender(SmsMessage... smsMessages) {
        final Map<String, List<SmsMessage>> messagesBySender = new HashMap<String, List<SmsMessage>>();
        for (SmsMessage smsMessage : smsMessages) {
            final String senderNumber = smsMessage.getDisplayOriginatingAddress();
            List<SmsMessage> messages = messagesBySender.get(senderNumber);
            if (messages == null) {
                messages = new LinkedList<SmsMessage>();
            }
            messages.add(smsMessage);
            messagesBySender.put(senderNumber, messages);
        }
        return messagesBySender;
    }

    private String findContactWithNumber(Context context, String number) {
        String ret = "?";
        final Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
        final ContentResolver contentResolver = context.getContentResolver();
        final Cursor cursor = contentResolver.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        try {
            if (cursor != null && cursor.getCount() > 0 && cursor.moveToFirst()) {
                ret = cursor.getString(cursor.getColumnIndex(ContactsContract.Data.DISPLAY_NAME));
            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return ret;
    }
}

