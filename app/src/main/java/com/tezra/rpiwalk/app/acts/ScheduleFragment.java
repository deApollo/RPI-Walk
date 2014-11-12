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


public class ScheduleFragment extends Fragment {


    List<Map<String,String>> itemList = new ArrayList<Map<String,String>>(); //Data structure to store the actual contents of the ListView
    SimpleAdapter adapter; //The adapter that controls the ListView

    //Function that populates the ListView from the data read from file in the MainAct
    private void populateItemList(){
        itemList.clear();
        for(Event e : MainAct.eventList) {
            Map<String,String> tempData = new HashMap<String,String>(2);
            tempData.put("main",e.getMainText());
            tempData.put("sub",e.getSubText());
            itemList.add(tempData);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_schedule,container,false);

        populateItemList(); //Populate the eventList

        //Get the list view and set up its adapter
        ListView lis = (ListView) v.findViewById(R.id.item_list);
        adapter = new SimpleAdapter(getActivity(), itemList,android.R.layout.simple_list_item_2,new String [] {"main","sub"},new int [] {android.R.id.text1,android.R.id.text2});
        lis.setAdapter(adapter);

        //Set up the listener so that items can be removed
        final ListView l = lis;
        l.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> myAdapter, View myView, final int myItemInt, long mylng) {
                AlertDialog.Builder builder = new AlertDialog.Builder(myView.getContext());
                builder.setTitle("Edit Item")
                        .setPositiveButton("Delete",new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                itemList.remove(myItemInt);
                                MainAct.eventList.remove(myItemInt);
                                adapter.notifyDataSetChanged();
                                saveData();
                            }
                        })
                        .setNeutralButton("Edit",new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int id) {
                                Intent i = new Intent(getActivity(),ScheduleItemAdder.class);
                                i.putExtra(MainAct.EXTRA_MSG,true);
                                i.putExtra(MainAct.EXTRA_MSG_2,MainAct.eventList.get(myItemInt));
                                itemList.remove(myItemInt);
                                MainAct.eventList.remove(myItemInt);
                                adapter.notifyDataSetChanged();
                                saveData();
                                startActivityForResult(i,0);
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

    //Function to save the event data to a binary file
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

    //Function that starts the ScheduleItemAdder activity
    public void addItem(View view){
        Intent i = new Intent(getActivity(),ScheduleItemAdder.class);
        i.putExtra(MainAct.EXTRA_MSG,false);
        startActivityForResult(i,0);
    }

    //Function that parses the results from the ScheduleItemAdder activity
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case(0) : {
                if(resultCode == Activity.RESULT_OK) { //If the activity returned successfully
                    //Parse the results into an event
                    Event e = (Event) data.getSerializableExtra(MainAct.EXTRA_MSG);

                    //Add the event to the list of events
                    Map<String,String> tempData = new HashMap<String,String>(2);
                    tempData.put("main",e.getMainText());
                    tempData.put("sub",e.getSubText());
                    itemList.add(tempData);

                    //Add the event to the data in the MainAct
                    MainAct.eventList.add(e);

                    //Update the adapter
                    adapter.notifyDataSetChanged();

                    //Save the new data
                    saveData();
                }
            }
        }
    }
}
