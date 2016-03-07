package hslu.bda.medimemory.fragment;

import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;
import android.widget.TimePicker;
import android.widget.Toast;


import hslu.bda.medimemory.R;

/**
 * Created by Loana on 03.03.2016.
 */
public class FragmentSettings extends PreferenceFragment {
    private int hour;
    private int minute;
        public void onCreate(Bundle savedInstanceState){
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
        }
}
