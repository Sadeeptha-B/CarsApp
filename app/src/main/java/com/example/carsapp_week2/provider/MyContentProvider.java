package com.example.carsapp_week2.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import androidx.annotation.Nullable;

/**Content provider for database **/
public class MyContentProvider extends ContentProvider {
    CarDatabase db;
    private final String tableName = "cars";

    public static final String CONTENT_AUTHORITY = "fit2081.app.sadeeptha.provider";
    public static final Uri CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public MyContentProvider() {
    }

    @Override
    public boolean onCreate() {
        db = CarDatabase.getDatabase(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(tableName);
        String query = builder.buildQuery(projection, selection, null, null, sortOrder, null);
        final Cursor cursor = db
                .getOpenHelper()
                .getReadableDatabase()
                .query(query, selectionArgs);
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long rowId = db
                .getOpenHelper()
                .getWritableDatabase()
                .insert(tableName, 0, values);
        return ContentUris.withAppendedId(CONTENT_URI, rowId);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int deletionCount;
        deletionCount = db
                .getOpenHelper()
                .getWritableDatabase()
                .delete(tableName, selection, selectionArgs);
        return deletionCount;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}