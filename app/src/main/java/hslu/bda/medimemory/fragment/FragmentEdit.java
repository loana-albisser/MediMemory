package hslu.bda.medimemory.fragment;


import android.app.Fragment;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hslu.bda.medimemory.R;

/**
 * Created by Loana on 01.03.2016.
 */
public class FragmentEdit extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_edit, container,false);
        return root;
    }
}
