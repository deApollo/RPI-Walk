package com.tezra.rpiwalk.app.utils;


import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.tezra.rpiwalk.app.R;

public class MapSnippetAdapter implements GoogleMap.InfoWindowAdapter {

    private LayoutInflater li;
    private View popup = null;

    public MapSnippetAdapter(LayoutInflater li) {
        this.li = li;
    }

    @Override
    public View getInfoWindow(Marker arg0){
        return(null);
    }

    @Override
    public View getInfoContents(Marker marker){
        if(popup == null)
            popup = li.inflate(R.layout.map_snippet, null);

        TextView tv = (TextView)popup.findViewById(R.id.title);
        tv.setText(marker.getTitle());

        tv = (TextView)popup.findViewById(R.id.snippet);

        String snippet = marker.getSnippet();
        String [] split = snippet.split("splithere909");
        if(split.length > 1)
            tv.setText(split[0] + "\n" + split[1]);
        else
            tv.setText(marker.getSnippet());

        return popup;
    }
}
