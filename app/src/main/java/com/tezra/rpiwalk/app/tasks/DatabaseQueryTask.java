package com.tezra.rpiwalk.app.tasks;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.AsyncTask;
import android.util.Log;

import com.tezra.rpiwalk.app.R;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class DatabaseQueryTask extends AsyncTask<Object,Void,String> {

    SQLiteDatabase db;
    Context c;

    public String doInBackground(Object... args){
        String query = (String)args[0];
        c = (Context)args[1];

        String DB_PATH = c.getFilesDir().getPath() + "rpi_locations.db";

        try {
            db = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            try {
                InputStream resourceDb = c.getResources().openRawResource(R.raw.rpi_locations);
                OutputStream readableDb = new FileOutputStream(DB_PATH);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = resourceDb.read(buffer)) > 0) {
                    readableDb.write(buffer, 0, length);
                }
                readableDb.flush();
                readableDb.close();
                resourceDb.close();
            } catch (IOException z) {
                Log.e("ERROR", "Error loading writing database");
            }
        }

        try {
            db = SQLiteDatabase.openDatabase(DB_PATH, null,SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            Log.e("ERROR", "Error opening external database");
        }

        final String table = "locations";
        final String [] columns = {"latitude","longitude"};
        final String final_query = "name="+query;

        Cursor result = db.query(table,columns,final_query,null,null,null,null,null);

        if(result.getColumnCount() == 2) {
            String latlng = String.valueOf(result.getDouble(0)) + "," + String.valueOf(result.getDouble(1));
            result.close();
            db.close();
            return latlng;
        }
        else {
            result.close();
            db.close();
            return null;
        }
    }
}
