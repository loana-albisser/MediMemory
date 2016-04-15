package hslu.bda.medimemory.fragment.help;



import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import hslu.bda.medimemory.R;

/**
 * Created by Loana on 29.02.2016.
 */
public class FragmentHelp extends Fragment {

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup root = (ViewGroup) inflater.inflate(R.layout.fragment_help, container, false);
        ExpandableListView expList_help = (ExpandableListView) root.findViewById(R.id.expList_help);
        expList_help.setAdapter(new FragmentHelpAdapter(getActivity().getApplicationContext()));
        return root;
    }



}
