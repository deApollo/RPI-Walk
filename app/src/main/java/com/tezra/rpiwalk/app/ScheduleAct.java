package com.tezra.rpiwalk.app;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ScheduleAct extends ActionBarActivity {


    List<Map<String,String>> itemList = new ArrayList<Map<String,String>>();
    SimpleAdapter adapter;

    private void populateItemList(){
        for(Event e : MainAct.eventList) {
            Map<String,String> tempData = new HashMap<String,String>(2);
            tempData.put("main",e.getMainText());
            tempData.put("sub",e.getSubText());
            itemList.add(tempData);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule);

        populateItemList();

        ListView lis = (ListView) findViewById(R.id.item_list);
        adapter = new SimpleAdapter(this, itemList,android.R.layout.simple_list_item_2,new String [] {"main","sub"},new int [] {android.R.id.text1,android.R.id.text2});
        lis.setAdapter(adapter);

        final ListView l = lis;
        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, int myItemInt, long mylng) {
                MainAct.generateToast(myView.getContext(),"You selected an item!", Toast.LENGTH_LONG);
            }
        });
    }

    private void saveData(){
        File file = new File(this.getFilesDir(),"data.dat");
        try{
            if(!file.exists())
                file.createNewFile();
            FileOutputStream fileOut = new FileOutputStream(file);
            ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
            objOut.writeObject(MainAct.eventList);
        } catch (IOException e){

        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        saveData();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.schedule, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_about) {
            startActivity(new Intent(this,AboutAct.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void addItem(View view){
        startActivityForResult(new Intent(this,ScheduleItemAdder.class),0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case(0) : {
                if(resultCode == Activity.RESULT_OK) {
                    String [] result = data.getStringArrayExtra(MainAct.EXTRA_MSG);
                    Event e = new Event(result[0],result[1],result[2],result[3],result[4]);
                    Map<String,String> tempData = new HashMap<String,String>(2);
                    tempData.put("main",e.getMainText());
                    tempData.put("sub",e.getSubText());
                    itemList.add(tempData);
                    MainAct.eventList.add(e);
                    adapter.notifyDataSetChanged();
                }
            }
        }
    }
}
