package hslu.bda.medimemory.fragment.overview;

import android.app.ActionBar;
import android.app.Dialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.nsd.NsdManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Display;
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

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import hslu.bda.medimemory.R;
import hslu.bda.medimemory.database.DbAdapter;
import hslu.bda.medimemory.detection.PillDetection;
import hslu.bda.medimemory.entity.Consumed;
import hslu.bda.medimemory.entity.Data;
import hslu.bda.medimemory.entity.PillCoords;
import hslu.bda.medimemory.entity.Status;
import hslu.bda.medimemory.fragment.MainActivity;
import hslu.bda.medimemory.fragment.registration.FragmentRegistration;
import hslu.bda.medimemory.services.PillDetectionService;
import hslu.bda.medimemory.services.UpdateMediService;

import static hslu.bda.medimemory.entity.Consumed.getAllConsumedByMedid;

/**
 * Created by Loana on 08.04.2016.
 */
public class FragmentOverviewChild extends Fragment  {
    private View root;
    private Bitmap pillPhoto;
    private int id;
    private ImageView iv_status;
    private RelativeLayout rl_pillImage;
    private RelativeLayout.LayoutParams params;
    private ImageButton iBtn_helpOverview;
    private Point touchPoint;
    private int statusWidth;
    private int statusHeight;
    private ArrayList<Point>points;
    private ArrayList<PillCoords>allpillCoords;
    private Collection<Consumed> allConsumed;
    private DbAdapter dbAdapter;
    private String status;
    private Collection<PillCoords> allPillCoordsById;
    ArrayList <Status> statusList;
    private ImageView statusImage;
    private int displayWidth;
    private int displayHeight;
    private int height;
    private int width;
    private PillCoords selectedPoint;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_overview_child, container, false);
        dbAdapter= new DbAdapter(getActivity().getApplicationContext());
        dbAdapter.open();
        Bundle bundle = getArguments();
        pillPhoto = bundle.getParcelable("pillPicture");
        id = bundle.getInt("id");
        Display display = getActivity().getWindowManager().getDefaultDisplay();
        android.graphics.Point size = new android.graphics.Point();
        display.getSize(size);
        displayWidth = size.x;
        displayHeight = size.y;
        if (checkHelpTextVisibility()){
            showHelpText();
            iBtn_helpOverview.setVisibility(View.VISIBLE);
        } else {
            iBtn_helpOverview.setVisibility(View.GONE);
        }
        points = new ArrayList<>();
        allpillCoords = new ArrayList<>();
        allPillCoordsById = new ArrayList<>();
        statusList = new ArrayList<>();
        allConsumed = new ArrayList<>();
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
        ImageView iv_pillImage = (ImageView) view.findViewById(R.id.iv_example);
        if (pillPhoto.getWidth() > pillPhoto.getHeight()){
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            pillPhoto = Bitmap.createBitmap(pillPhoto, 0, 0, pillPhoto.getWidth(), pillPhoto.getHeight(), matrix, true);
        }
        iv_pillImage.setImageBitmap(pillPhoto);
        PillDetection pillDetection = new PillDetection(pillPhoto, pillPhoto.getWidth(), pillPhoto.getHeight());
        Collection<PillCoords> pillCoordsNew;
        try {
            pillCoordsNew = pillDetection.getAllPillPoints(id);
            //UpdateMediService.updateTableEntry(,dbAdapter);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        allPillCoordsById = PillCoords.getAllPillCoordsByMedid(id,dbAdapter);
        allConsumed =  getAllConsumedByMedid(id, dbAdapter);
        int pointId = 0;
        for (PillCoords pillCoords : allPillCoordsById){
            points.add(pillCoords.getCoords());
            allpillCoords.add(pillCoords);
            statusHeight = pillCoords.getHeight();
            statusWidth = pillCoords.getWidth();
            setupStatus((int) points.get(pointId).x, (int) points.get(pointId).y, pillCoords.getId());
            showTouchPoints();
            pointId++;
        }
        showNextPill();
        showAllStatus();
        setTouchListener();
    }

    private void showTouchPoints() {
        statusImage.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.transparent));
        rl_pillImage.addView(statusImage, params);
    }

    private void showAllStatus(){
        for (Consumed consumed:allConsumed){
            consumed.getStatus();
            iv_status = (ImageView) root.findViewById(consumed.getPillCoord().getId());
            if (consumed.getStatus().getId() == Integer.valueOf(Status.STATUS_EINGENOMMEN)){
                iv_status.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.check_mark));
            } else if (consumed.getStatus().getId() == Integer.valueOf(Status.STATUS_VERGESSEN)){
                iv_status.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.question_mark));
            }else if(consumed.getStatus().getId() == Integer.valueOf(Status.STATUS_VERLOREN)){
                iv_status.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.x_mark));
            }
        }
    }

    /**
     *
     * @param x x-Position of Drawable
     * @param y y-Position of Drawable
     */
    private void setupStatus(int x, int y, int id){
        rl_pillImage = (RelativeLayout) root.findViewById(R.id.rl_pillImage);
        //size of Drawable
        if (statusHeight < statusWidth){
            params = new RelativeLayout.LayoutParams(statusHeight/2, statusHeight/2);
        } else {
            params = new RelativeLayout.LayoutParams(statusWidth/2, statusWidth/2);
        }
        //params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        width = (displayWidth - pillPhoto.getWidth()) / 2;
        height = (displayHeight - pillPhoto.getHeight());//2;
        Log.i("width", String.valueOf(width));
        Log.i("height", String.valueOf(height));
        params.leftMargin = x - width;
        params.topMargin = y-height;
        statusImage = new ImageView(getActivity());
        statusImage.setId(id);
    }

    /**
     * sets the Status for the pill
     * @param status status-Drawable
     */
    private void setStatus(Drawable status){
        statusImage.setImageDrawable(status);
        rl_pillImage.addView(statusImage, params);
    }

    private void setTouchListener(){
        rl_pillImage = (RelativeLayout)root.findViewById(R.id.rl_pillImage);
        rl_pillImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    touchPoint = new Point(event.getX(), event.getY());
                    int pointId = 0;
                    for (Point point : points) {
                        if (comparePoints(touchPoint, points.get(pointId))) {
                            setupStatus((int) points.get(pointId).x, (int) points.get(pointId).y, allpillCoords.get(pointId).getId());
                            //setupStatus((int) points.get(pointId).x, (int) points.get(pointId).y, pointId);
                            statusImage = (ImageView) root.findViewById(allpillCoords.get(pointId).getId());
                            selectedPoint = allpillCoords.get(pointId);
                            setupStatusDialog();
                        }
                        pointId++;
                    }
                    Log.i("PositionTouchx", String.valueOf(event.getX()));
                    Log.i("PositionTouchy", String.valueOf(event.getY()));

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
                statusImage.setImageDrawable(null);
                rl_pillImage.removeView(statusImage);
                if (selectedItem == 0) {
                    setStatus(ContextCompat.getDrawable(getActivity(), R.drawable.check_mark));
                    try {
                        PillDetectionService.setConsumed(Status.getStatusById(Status.STATUS_EINGENOMMEN, dbAdapter), selectedPoint, dbAdapter);
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                } else if (selectedItem == 1) {
                    setStatus(ContextCompat.getDrawable(getActivity(), R.drawable.question_mark));
                    try {
                        PillDetectionService.setConsumed(Status.getStatusById(Status.STATUS_VERGESSEN,dbAdapter),selectedPoint,dbAdapter);
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }
                } else if (selectedItem == 2) {
                    setStatus(ContextCompat.getDrawable(getActivity(), R.drawable.x_mark));
                    try {
                        PillDetectionService.setConsumed(Status.getStatusById(Status.STATUS_VERLOREN, dbAdapter), selectedPoint, dbAdapter);
                    } catch (Throwable throwable) {
                        throwable.printStackTrace();
                    }

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

    private void showNextPill(){
        PillCoords pillpoint  = PillCoords.getNextPillByMedid(id,dbAdapter);
        iv_status = (ImageView) root.findViewById(pillpoint.getId());
        iv_status.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.circle));
    }

    /**
     * @param touchPoint point touched
     * @param pillPoint point of the pill
     * @return if they match
     */
    private boolean comparePoints(Point touchPoint, Point pillPoint) {
        int range = 60;
        boolean x = touchPoint.x - range <= pillPoint.x - width && pillPoint.x - width <= touchPoint.x+range ;
        boolean y = touchPoint.y - range <= pillPoint.y - height && pillPoint.y - height <= touchPoint.y+range ;
        return x && y;
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
        boolean showHelp = pref.getBoolean("pref_key_showHelp",true);
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

}
