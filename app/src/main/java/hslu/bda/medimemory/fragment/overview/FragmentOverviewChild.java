package hslu.bda.medimemory.fragment.overview;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import hslu.bda.medimemory.R;

/**
 * Created by Loana on 08.04.2016.
 */
public class FragmentOverviewChild extends Fragment {
    String childname;
    TextView textViewChildName;
    ImageView iv_example;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_overview_child, container, false);
        Bundle bundle = getArguments();
        childname = bundle.getString("data");
        getIDs(view);
        setEvents();
        return view;
    }

    private void getIDs(View view) {
        textViewChildName = (TextView) view.findViewById(R.id.textViewChild);
        iv_example = (ImageView)view.findViewById(R.id.iv_example);
        textViewChildName.setText(childname);
        iv_example.setImageDrawable(getResources().getDrawable(R.drawable.example_pill));
    }


    private void setEvents() {

    }
}
