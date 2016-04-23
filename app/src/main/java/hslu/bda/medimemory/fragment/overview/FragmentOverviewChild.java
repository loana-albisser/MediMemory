package hslu.bda.medimemory.fragment.overview;

import android.app.Fragment;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import hslu.bda.medimemory.R;

/**
 * Created by Loana on 08.04.2016.
 */
public class FragmentOverviewChild extends Fragment {
    private View root;
    private String childname;
    private TextView textViewChildName;
    private ImageView iv_example;
    private ImageView iv_status;
    private ImageButton iBtn_helpOverview;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_overview_child, container, false);
        Bundle bundle = getArguments();
        childname = bundle.getString("data");
        if (checkHelpTextVisibility()){
            showHelpText();
            iBtn_helpOverview.setVisibility(View.VISIBLE);
        } else {
            iBtn_helpOverview.setVisibility(View.GONE);
        }

        getIDs(root);
        setEvents();
        return root;
    }

    private void getIDs(View view) {
        textViewChildName = (TextView) view.findViewById(R.id.textViewChild);
        iv_example = (ImageView)view.findViewById(R.id.iv_example);
        textViewChildName.setText(childname);
        iv_example.setImageDrawable(getResources().getDrawable(R.drawable.example_pill));
        setStatus(50,60);
    }

    private void setStatus(int x, int y){
        RelativeLayout rl = (RelativeLayout) root.findViewById(R.id.rl_pillImage);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(30, 40);
        params.leftMargin = x;
        params.topMargin = y;
        iv_status = new ImageView(getActivity());
        iv_status.setImageDrawable(getResources().getDrawable(R.drawable.circle));
        rl.addView(iv_status, params);


    }

    private void showHelpText(){
        //iBtn_helpOverview = (ImageButton) overviewView.findViewById(R.id.iBtn_helpOverview);
        iBtn_helpOverview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayPopupWindow(v);
            }
        });
    }

    private boolean checkHelpTextVisibility(){
        iBtn_helpOverview = (ImageButton) root.findViewById(R.id.iBtn_helpOverview);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
        boolean showHelp = pref.getBoolean("pref_key_showHelp",false);
        return showHelp;
    }

    private void displayPopupWindow(View anchorView) {
        PopupWindow popup = new PopupWindow(getActivity());
        View layout = getActivity().getLayoutInflater().inflate(R.layout.popup_help_overview, null);
        popup.setContentView(layout);
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        popup.showAsDropDown(anchorView);
    }


    private void setEvents() {

    }
}
