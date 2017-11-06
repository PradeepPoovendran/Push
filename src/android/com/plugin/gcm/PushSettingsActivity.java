package com.plugin.gcm;

import com.smartfmone.smartfmone.v1.R;
import android.os.Bundle;
import android.preference.PreferenceActivity;

public class PushSettingsActivity extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.pushsettings);

    }
}
