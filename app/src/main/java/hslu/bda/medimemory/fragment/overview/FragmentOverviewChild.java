package hslu.bda.medimemory.fragment.overview;

import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.res.ResourcesCompat;
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

import java.io.IOException;
import java.util.Collection;

import hslu.bda.medimemory.R;
import hslu.bda.medimemory.database.DbAdapter;
import hslu.bda.medimemory.entity.Data;
import hslu.bda.medimemory.entity.PillCoords;
import hslu.bda.medimemory.entity.Status;
import hslu.bda.medimemory.fragment.registration.FragmentRegistration;

/**
 * Created by Loana on 08.04.2016.
 */
public class FragmentOverviewChild extends Fragment  {
    private View root;
    private String childname;
    private Bitmap pillPhoto;
    private int id;
    private FragmentOverview fragmentOverview;
    private FragmentRegistration fragmentRegistration;
    private ImageView iv_status;
    private RelativeLayout rl_pillImage;
    private RelativeLayout.LayoutParams params;
    private ImageButton iBtn_helpOverview;
    private int xTouchPosition;
    private int yTouchPosition;
    private int xPillPosition;
    private int yPillPosition;
    private DbAdapter dbAdapter;
    private String status;
    private Collection<PillCoords> allPillCoordsById;

    //Testkommentar


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_overview_child, container, false);
        dbAdapter= new DbAdapter(getActivity().getApplicationContext());
        dbAdapter.open();
        fragmentOverview = new FragmentOverview();
        Bundle bundle = getArguments();
        childname = bundle.getString("pagename");
        pillPhoto = bundle.getParcelable("pillPicture");
        id = bundle.getInt("id");
        if (checkHelpTextVisibility()){
            showHelpText();
            iBtn_helpOverview.setVisibility(View.VISIBLE);
        } else {
            iBtn_helpOverview.setVisibility(View.GONE);
        }
        allPillCoordsById = PillCoords.getAllPillCoordsByMedid(id,dbAdapter);
        for (PillCoords pillCoords : allPillCoordsById){
            xPillPosition = (int)pillCoords.getCoords().x;
            yPillPosition = (int)pillCoords.getCoords().y;
            setupStatus(xPillPosition, yPillPosition);
            setTouchListener(xPillPosition,yPillPosition);
        }

        //setupStatus(50, 60);
        //setStatus(ResourcesCompat.getDrawable(getResources(), R.drawable.circle, null));
        getIDs(root);
        return root;
    }

    @Override
    public void onResume(){
        if(dbAdapter==null){
            dbAdapter= new DbAdapter(getActivity().getApplicationContext());
            dbAdapter.open();
        }
        super.onResume();
    }

    @Override
    public void onStop(){
        if(dbAdapter!=null) {
            dbAdapter.close();
            dbAdapter = null;
        }
        super.onStop();
    }


    private void getIDs(View view) {
        ImageView iv_example = (ImageView) view.findViewById(R.id.iv_example);

        if (pillPhoto.getWidth() > pillPhoto.getHeight()){
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            pillPhoto = Bitmap.createBitmap(pillPhoto, 0, 0, pillPhoto.getWidth(), pillPhoto.getHeight(), matrix, true);
        }
        iv_example.setImageBitmap(pillPhoto);
    }

    /**
     *
     * @param x x-Position of Drawable
     * @param y y-Position of Drawable
     */
    private void setupStatus(int x, int y){
        rl_pillImage = (RelativeLayout) root.findViewById(R.id.rl_pillImage);
        //size of Drawable
        params = new RelativeLayout.LayoutParams(40, 40);
        params.leftMargin = x;
        params.topMargin = y;
        iv_status = new ImageView(getActivity());
    }


    /**
     * sets the Status for the pill
     * @param status status-Drawable
     */
    private void setStatus(Drawable status){
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

    private void setTouchListener(final int xPillPosition, final int yPillPosition){
        final int range = 40;
        rl_pillImage = (RelativeLayout)root.findViewById(R.id.rl_pillImage);
        rl_pillImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
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

    /**
     * shows status-dialog (taken, forgot, lost)
     */
    private void setupStatusDialog(){
        AlertDialog.Builder statusDialog = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);
        statusDialog.setCancelable(false);
        statusDialog.setTitle(getResources().getString(R.string.title_dialogStatus));
        statusDialog.setSingleChoiceItems(R.array.array_status, 0, null);
        statusDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ListView lw = ((AlertDialog) dialog).getListView();
                int selectedItem = (int) lw.getAdapter().getItemId(lw.getCheckedItemPosition());
                rl_pillImage.removeView(iv_status);
                Log.i("which", String.valueOf(which));
                Log.i("checkedItem", String.valueOf(selectedItem));
                if (selectedItem == 0) {
                    setStatus(getResources().getDrawable(R.drawable.check_mark));
                    status = getResources().getString(R.string.txt_taken);
                } else if (selectedItem == 1) {
                    setStatus(getResources().getDrawable(R.drawable.question_mark));
                    status = getResources().getString(R.string.txt_forgot);
                } else if (selectedItem == 2) {
                    setStatus(getResources().getDrawable(R.drawable.x_mark));
                    status = getResources().getString(R.string.txt_lost);
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

    /**
     * checks whether help needs to be displayed
     * @return the preference
     */
    private boolean checkHelpTextVisibility(){
        iBtn_helpOverview = (ImageButton) root.findViewById(R.id.iBtn_helpOverview);
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
        boolean showHelp = pref.getBoolean("pref_key_showHelp",false);
        return showHelp;
    }

    /**
     * shows help dialog
     * @param anchorView
     */
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

    private void saveDataToDB(){
        Status status = new Status();
        status.setDescription(getStatus());
    }

    private String getStatus() {
        return status;
    }
}
