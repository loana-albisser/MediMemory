package hslu.bda.medimemory.fragment;

import android.os.Bundle;
import android.preference.PreferenceFragment;


import hslu.bda.medimemory.R;

/**
 * Created by Loana on 03.03.2016.
 */
public class FragmentSettings extends PreferenceFragment {



    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
    }

}
