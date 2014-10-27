package com.tezra.rpiwalk.app.acts;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.tezra.rpiwalk.app.R;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences_layout);
    }
}
