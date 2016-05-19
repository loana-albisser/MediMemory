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
import hslu.bda.medimemory.entity.Data;
import hslu.bda.medimemory.entity.PillCoords;
import hslu.bda.medimemory.entity.Status;
import hslu.bda.medimemory.fragment.MainActivity;
import hslu.bda.medimemory.fragment.registration.FragmentRegistration;
import hslu.bda.medimemory.services.PillDetectionService;
import hslu.bda.medimemory.services.UpdateMediService;

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
    private Point touchPoint;
    private int statusWidth;
    private int statusHeight;
    private int pictureHeight;
    private ArrayList<Point>points;
    private DbAdapter dbAdapter;
    private String status;
    private Collection<PillCoords> allPillCoordsById;
    private int tabHeight = 112;
    private ImageView statusImage;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_overview_child, container, false);
        dbAdapter= new DbAdapter(getActivity().getApplicationContext());
        dbAdapter.open();
        tabHeight = ((MainActivity) getActivity()).getTabHeigth();
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
        points = new ArrayList<>();
        allPillCoordsById = new ArrayList<>();

        getIDs(root);
        return root;
    }

    @Override
    public void onResume(){
        if(dbAdapter==null){
            dbAdapter= new DbAdapter(getActivity().getApplicationContext());
            dbAdapter.open();
        }
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, getActivity(), mLoaderCallback);
        super.onResume();
    }

    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(getActivity()) {
        /**
         * This is the callback method called once the OpenCV //manager is connected
         * @param status
         */
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i("Example Loaded", "OpenCV loaded successfully");
                    //mOpenCvCameraView.enableView();
                } break;
                default: {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };


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
        PillDetection pillDetection = new PillDetection(pillPhoto, pillPhoto.getWidth(), pillPhoto.getHeight());
        Collection<PillCoords> pillCoordsNew;
        try {
            pillCoordsNew = pillDetection.getAllPillPoints(id);
            //UpdateMediService.updateTableEntry(,dbAdapter);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        allPillCoordsById = PillCoords.getAllPillCoordsByMedid(id,dbAdapter);
        for (PillCoords pillCoords : allPillCoordsById){
            points.add(pillCoords.getCoords());
            statusHeight = pillCoords.getHeight();
            statusWidth = pillCoords.getWidth();
            showTouchPoints((int) pillCoords.getCoords().x, (int) pillCoords.getCoords().y, pillCoords.getId());
        }
        setTouchListener();

    }

    private void showTouchPoints(int xCoord, int yCoord, int id) {
        setupStatus(xCoord, yCoord, id);
        iv_status.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.circle));
        int x = ContextCompat.getDrawable(getActivity(), R.drawable.circle).getBounds().width();
        int y = ContextCompat.getDrawable(getActivity(), R.drawable.circle).getBounds().height();
        Log.i("leftMargin"+xCoord+","+yCoord, String.valueOf(params.leftMargin));
        params.leftMargin = params.leftMargin;
        Log.i("topMargin"+xCoord+","+yCoord, String.valueOf(params.topMargin));
        params.topMargin = params.topMargin;// - 112;
        rl_pillImage.addView(iv_status, params);
        //setStatus(ContextCompat.getDrawable(getActivity(), R.drawable.circle));
    }

    /**
     *
     * @param x x-Position of Drawable
     * @param y y-Position of Drawable
     */
    private void setupStatus(int x, int y, int id){
        rl_pillImage = (RelativeLayout) root.findViewById(R.id.rl_pillImage);
        int layoutHeight = rl_pillImage.getHeight();
        int layoutWidth = rl_pillImage.getWidth();
        Log.i("tabheight", String.valueOf(tabHeight));
        //size of Drawable
        /*if (statusHeight < statusWidth){
            params = new RelativeLayout.LayoutParams(statusHeight, statusHeight);
        } else {
            params = new RelativeLayout.LayoutParams(statusWidth, statusWidth);
        }*/
        params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        Log.i("setupLeftMargin", String.valueOf(params.leftMargin));
        Log.i("setupTopMargin", String.valueOf(params.topMargin));
        params.leftMargin = x -39; //- statusWidth/2;
        //params.rightMargin = x;
        //params.bottomMargin = y - statusHeight/2;
        params.topMargin = y-tabHeight; //- 50; //- tabHeight;
        iv_status = new ImageView(getActivity());
        iv_status.setId(id);
    }


    /**
     * sets the Status for the pill
     * @param status status-Drawable
     */
    private void setStatus(Drawable status){
        //iv_status.setImageDrawable(status);
        //rl_pillImage.addView(iv_status, params);
        iv_status.setImageDrawable(status);
        rl_pillImage.addView(iv_status,params);
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
                            statusImage = (ImageView) root.findViewById(pointId);
                            setupStatus((int) points.get(pointId).x, (int) points.get(pointId).y, pointId);
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
                iv_status.setImageDrawable(null);
                rl_pillImage.removeView(iv_status);
                //setStatus(ContextCompat.getDrawable(getActivity(),null));
                if (selectedItem == 0) {
                    setStatus(ContextCompat.getDrawable(getActivity(), R.drawable.check_mark));
                    status = getResources().getString(R.string.txt_taken);
                } else if (selectedItem == 1) {
                    setStatus(ContextCompat.getDrawable(getActivity(), R.drawable.question_mark));
                    status = getResources().getString(R.string.txt_forgot);
                } else if (selectedItem == 2) {
                    setStatus(ContextCompat.getDrawable(getActivity(), R.drawable.x_mark));
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

    /**
     * @param touchPoint point touched
     * @param pillPoint point of the pill
     * @return if they match
     */
    private boolean comparePoints(Point touchPoint, Point pillPoint) {
        int range = 40;
        boolean x = touchPoint.x - range <= pillPoint.x - 39 && pillPoint.x - 39 <= touchPoint.x+range ;
        boolean y = touchPoint.y - range <= pillPoint.y - tabHeight && pillPoint.y - tabHeight <= touchPoint.y+range ;
        return x && y;
    }

    private int calcLeftMargin(){
        return 39;
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

    private void saveDataToDB(){
        Status status = new Status();
        status.setDescription(getStatus());

    }

    private String getStatus() {
        return status;
    }
}
