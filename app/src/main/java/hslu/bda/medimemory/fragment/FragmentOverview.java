package hslu.bda.medimemory.fragment;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hslu.bda.medimemory.R;

/**
 * Created by Loana on 04.03.2016.
 */
public class FragmentOverview extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_overview, container,false);
        return root;
    }
}
