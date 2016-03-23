package hslu.bda.medimemory.fragment;

import android.app.Fragment;
import android.content.ContentValues;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import hslu.bda.medimemory.R;
import hslu.bda.medimemory.database.DbAdapter;
import hslu.bda.medimemory.entity.Data;
import hslu.bda.medimemory.services.CreateMediService;

/**
 * Created by Loana on 04.03.2016.
 */
public class FragmentOverview extends Fragment {
    private ViewGroup root;
    private DbAdapter dbAdapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.fragment_overview, container,false);
        return root;
    }


}
