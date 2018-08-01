package com.example.kageboshi.contacts_debug.utils;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

public class ContactsUtils {

    public static void addContact(Context context, String name, String phone) {
        ContentValues localContentValues = new ContentValues();
        long l = ContentUris.parseId(context.getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, localContentValues));
        localContentValues.clear();
        localContentValues.put("raw_contact_id", Long.valueOf(l));
        localContentValues.put("mimetype", "vnd.android.cursor.item/name");
        localContentValues.put("data2", name);
        context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, localContentValues);
        localContentValues.clear();
        localContentValues.put("raw_contact_id", Long.valueOf(l));
        localContentValues.put("mimetype", "vnd.android.cursor.item/phone_v2");
        localContentValues.put("data1", phone);
        localContentValues.put("data2", Integer.valueOf(2));
        context.getContentResolver().insert(ContactsContract.Data.CONTENT_URI, localContentValues);
        localContentValues.clear();
    }

    public static void deleteAll(Context context) {
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI, null,
                null, null, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                String name = cursor.getString(cursor
                        .getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
                Cursor cursor1 = cr.query(uri, new String[]{ContactsContract.Data._ID}, "display_name=?", new String[]{name}, null);
                if (cursor1.moveToFirst()) {
                    int id = cursor1.getInt(0);
                    //根据id删除data中的相应数据
                    cr.delete(uri, "display_name=?", new String[]{name});
                    uri = Uri.parse("content://com.android.contacts/data");
                    cr.delete(uri, "raw_contact_id=?", new String[]{id + ""});
                }
            }
        }
    }

}
