package com.example.tabatatimer;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;

public class SettingsFragment extends PreferenceFragmentCompat {
    //SharedPreferences sharedPreferences;
    SettingsActivity settingsActivity;

    @Override
    public void onAttach(@NonNull Context context){
        super.onAttach(context);

        settingsActivity = (SettingsActivity) context;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.main_preference, rootKey);

        SwitchPreferenceCompat preference_theme = findPreference("night_mode_preference_key");
        assert preference_theme != null;
        preference_theme.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                settingsActivity.changeMode(newValue);
                return true;
            }
        });
        androidx.preference.Preference language_preference = findPreference("set_language_preference");
        assert language_preference != null;
        language_preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                String value = (String)newValue;
                if (value.equals("1")) {
                    Toast.makeText(requireActivity().getBaseContext(), "English", Toast.LENGTH_LONG).show();
                }
                else if(value.equals("2")) {
                    Toast.makeText(requireActivity().getBaseContext(), "Spanish", Toast.LENGTH_LONG).show();
                }
                else{
                    Toast.makeText(requireActivity().getBaseContext(), "Russian", Toast.LENGTH_LONG).show();
                }

                return true;
            }
        });

        ListPreference font_preference = findPreference("font_preference");
        assert font_preference != null;
        font_preference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                settingsActivity.changeFont(newValue);
                return true;
            }
        });

    }


}
