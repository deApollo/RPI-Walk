package com.tezra.rpiwalk.app.acts;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import com.tezra.rpiwalk.app.utils.Event;
import com.tezra.rpiwalk.app.R;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ScheduleAct extends Fragment {


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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.activity_schedule,container,false);

        populateItemList();

        ListView lis = (ListView) v.findViewById(R.id.item_list);
        adapter = new SimpleAdapter(getActivity(), itemList,android.R.layout.simple_list_item_2,new String [] {"main","sub"},new int [] {android.R.id.text1,android.R.id.text2});
        lis.setAdapter(adapter);

        final ListView l = lis;
        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView,final int myItemInt, long mylng) {
                AlertDialog.Builder builder = new AlertDialog.Builder(myView.getContext());
                builder.setTitle("Edit Item")
                        .setPositiveButton("Delete",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                itemList.remove(myItemInt);
                                MainAct.eventList.remove(myItemInt);
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();
                            }
                        }).show();
            }
        });

        return v;
    }

    private void saveData(){
        File file = new File(getActivity().getFilesDir(),"data.dat");
        try{
            if(!file.exists())
                file.createNewFile();
            FileOutputStream fileOut = new FileOutputStream(file);
            ObjectOutputStream objOut = new ObjectOutputStream(fileOut);
            objOut.writeObject(MainAct.eventList);
        } catch (IOException e){
            Log.i("Error:","There was an issue saving the data file",e.getCause());
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        saveData();
    }

    @Override
    public void onStop() {
        super.onStop();
        saveData();
    }

    public void addItem(View view){
        startActivityForResult(new Intent(getActivity(),ScheduleItemAdder.class),0);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case(0) : {
                if(resultCode == Activity.RESULT_OK) {
                    String [] result = data.getStringArrayExtra(MainAct.EXTRA_MSG);
                    Event e = new Event(result[0],result[1],data.getBooleanArrayExtra(MainAct.EXTRA_MSG_2),result[2],result[3],Double.valueOf(result[4]),Double.valueOf(result[5]));
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
