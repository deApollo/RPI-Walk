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

public class DatabaseQuery {

    /*
    This task queries the rpi_locations.db as to whether the supplied location exists in the database.
    If the entry exists, the task returns a string containing the LatLnt pair for use in the RouteRetrieverTask and LocationValidatorTask
     */

    SQLiteDatabase db;

    public DatabaseQuery(Context c){
        String DB_PATH = c.getFilesDir().getPath() + "rpi_locations.db";
        try {
            db = SQLiteDatabase.openDatabase(DB_PATH, null, SQLiteDatabase.OPEN_READONLY); //Attempt to open the databae
        } catch (SQLiteException e) { //The database doesn't yet exist in a readable location
            try {
                //Write the database from the resource directory to a directory from which it can be successfully read from, then open it.
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
                db = SQLiteDatabase.openDatabase(DB_PATH, null,SQLiteDatabase.OPEN_READONLY);
            } catch (IOException z) {
                Log.e("ERROR", "Error loading writing database");
            }
        }
    }

    public String doQuery(String query){

        //Set up the strings for the query, then do the query
        final String table = "locations";
        final String [] columns = {"latitude","longitude"};
        final String final_query = "name=\""+query+"\"";

        Cursor result = db.query(table,columns,final_query,null,null,null,null,null);
        result.moveToFirst();

        //Parse the query result to see if it matches the expected format, if it does, return the parsed data, otherwise return null
        if(result.getColumnCount() == 2) {

            String lat = String.valueOf(result.getDouble(0));
            String lon = String.valueOf(result.getDouble(1));

            result.close();
            db.close();
            return lat + ',' + lon;
        }
        else {
            result.close();
            db.close();
            return null;
        }
    }
}
