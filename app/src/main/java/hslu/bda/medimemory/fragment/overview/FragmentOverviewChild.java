package hslu.bda.medimemory.fragment.overview;

import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import hslu.bda.medimemory.R;

/**
 * Created by Loana on 08.04.2016.
 */
public class FragmentOverviewChild extends Fragment  {
    private View root;
    private String childname;
    private TextView textViewChildName;
    private ImageView iv_example;
    private ImageView iv_status;
    private RelativeLayout rl_pillImage;
    private RelativeLayout.LayoutParams params;
    private ImageButton iBtn_helpOverview;
    private AlertDialog.Builder statusDialog;
    private int selectedItem;
    private int xTouchPosition;
    private int yTouchPosition;


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
        setupStatus(50, 60);
        setStatus(getResources().getDrawable(R.drawable.circle));
        getIDs(root, getResources().getDrawable(R.drawable.example_pill));
        return root;
    }


    private void getIDs(View view, Drawable pillPicture) {
        textViewChildName = (TextView) view.findViewById(R.id.textViewChild);
        iv_example = (ImageView)view.findViewById(R.id.iv_example);
        textViewChildName.setText(childname);
        iv_example.setImageDrawable(pillPicture);
        setTouchListener(50, 60);
    }

    private void setupStatus(int x, int y){
        rl_pillImage = (RelativeLayout) root.findViewById(R.id.rl_pillImage);
        params = new RelativeLayout.LayoutParams(30, 40);
        params.leftMargin = x;
        params.topMargin = y;
        iv_status = new ImageView(getActivity());
    }

    public void setStatus(Drawable status){
        iv_status.setImageDrawable(status);
        rl_pillImage.addView(iv_status, params);
    }

    private void setYTouchPosition(int yTouchPosition){
        this.yTouchPosition = yTouchPosition;
    }

    private void setXTouchPosition(int xTouchPosition){
        this.xTouchPosition = xTouchPosition;
    }

    private int getXTouchPosition(){
        return xTouchPosition;
    }

    private int getYTouchPosition(){
        return yTouchPosition;
    }

    public void setTouchListener(final int xPillPosition, final int yPillPosition){
        final int range = 40;
        rl_pillImage = (RelativeLayout)root.findViewById(R.id.rl_pillImage);
        rl_pillImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN){
                    xTouchPosition = (int) event.getX();
                    yTouchPosition = (int) event.getY();
                    setXTouchPosition((int) event.getX());
                    setYTouchPosition((int) event.getY());
                    if (isBetween(getXTouchPosition() - range, getXTouchPosition() + range, xPillPosition) && isBetween(getYTouchPosition() - range, getYTouchPosition() + range, yPillPosition)) {
                        setupStatusDialog();
                    }
                    Log.i("PositionTouchx", String.valueOf(getXTouchPosition()));
                    Log.i("PositionTouchy", String.valueOf(getYTouchPosition()));
                    Log.i("PositionPillx", String.valueOf(xPillPosition));
                    Log.i("PositionPilly", String.valueOf(yPillPosition));

                }
                return false;
            }
        });
    }

    private void setupStatusDialog(){
        statusDialog = new AlertDialog.Builder(getActivity(),R.style.DialogTheme);
        statusDialog.setCancelable(false);
        statusDialog.setTitle(getResources().getString(R.string.title_dialogStatus));
        statusDialog.setSingleChoiceItems(R.array.array_status, 0, null);
        statusDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ListView lw = ((AlertDialog)dialog).getListView();
                int selectedItem = (int) lw.getAdapter().getItemId(lw.getCheckedItemPosition());
                //Toast.makeText(getActivity(),selectedItem,Toast.LENGTH_LONG).show();
                rl_pillImage.removeView(iv_status);
                Log.i("which", String.valueOf(which));
                Log.i("checkedItem", String.valueOf(selectedItem));

                if (selectedItem == 0) {
                    setStatus(getResources().getDrawable(R.drawable.check_mark));
                } else if (selectedItem == 1) {
                    setStatus(getResources().getDrawable(R.drawable.question_mark));
                } else if (selectedItem == 2) {
                    setStatus(getResources().getDrawable(R.drawable.x_mark));
                }
            }
        });
        statusDialog.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        Dialog d = statusDialog.create();
        d.show();
    }

    private static boolean isBetween(int lowNumber, int highNumber, int compareNumber) {
        return highNumber > lowNumber ? compareNumber > lowNumber && compareNumber < highNumber : compareNumber > highNumber && compareNumber < lowNumber;
    }

    private void showHelpText(){
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
}
