package hslu.bda.medimemory.fragment.registration;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.MediaStore;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.Activity;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.common.collect.Iterables;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import hslu.bda.medimemory.R;
import hslu.bda.medimemory.database.DbAdapter;
import hslu.bda.medimemory.detection.PillDetection;
import hslu.bda.medimemory.entity.ConsumeIndividual;
import hslu.bda.medimemory.entity.ConsumeInterval;
import hslu.bda.medimemory.entity.Data;
import hslu.bda.medimemory.entity.Day;
import hslu.bda.medimemory.entity.Eat;
import hslu.bda.medimemory.fragment.MainActivity;
import hslu.bda.medimemory.fragment.overview.FragmentOverview;
import hslu.bda.medimemory.fragment.settings.FragmentSettings;
import hslu.bda.medimemory.services.CreateMediService;

/**
 * Created by Loana on 29.02.2016.
 */
public class FragmentRegistration extends Fragment {
    private ViewGroup root;
    private Context context;
    private FragmentRegistration fragmentRegistration;
    private Activity mActivity;
    private FragmentSettings fragmentSettings;

    private EditText edit_name;
    private final int REQUEST_CAMERA = 0;
    private final int SELECT_FILE = 1;
    private ImageView iv_selectedImage;
    private String imagePath;
    private Bitmap thumbnail;

    private TextView txt_reminder;
    private View dialogView;
    private View dialogNumberpickerView;
    private int selectedIntervalPosition;
    private boolean [] checkItems; //= {false,false,false,false,false};
    private ArrayList<Integer> selList = new ArrayList<Integer>();
    private Spinner sp_reminderInterval;
    private int numberofCheckedItems;
    private AlertDialog.Builder reminderDaytimeDialog;
    private RadioButton rd_reminderInterval;
    private TimePickerDialog tp_startEndTimeInterval;
    private StringBuilder daytimebuilder;
    private CharSequence[] daytimes;
    private SimpleDateFormat startTime;
    private Date startTimeDate;
    private Calendar startTimeCalendar;
    private Calendar endTimeCalendar;
    private Calendar intervalCalendar;
    private Date intervalDate;
    private int intervalMinute;
    private int intervalHour;
    private Button btn_starttime;
    private Button btn_endtime;
    private View dialogViewStartEnd;
    private SimpleDateFormat intervalTime;
    private String intervalTimeString;
    private List<String> weekdays;
    private AlertDialog.Builder dialogWeekday;
    private int weekday;
    private String weekdayString;
    private NumberPicker np_reminderInterval;
    private TimePickerDialog tpd_interval;
    private StringBuilder intervalbuilder;
    private int selectedValue = 1;
    private String selectedInterval;
    private CardView cv_foodInstruction;
    private RadioGroup rdg_duration;
    private TextView txt_duration = null;
    private StringBuilder numberOfBlisterString;
    private Calendar dateCalendarDuration;
    private int selectedYearDuration;
    private int selectedMonthDuration;
    private int selectedDayDuration;
    private NumberPicker np_numberofBlisters;
    private int numberOfBlisters;
    private StringBuilder dosageString;

    private TextView txt_dosage = null;
    private NumberPicker np_dosage;

    private TextView txt_foodInstruction = null;
    private RadioGroup rdg_foodInstruction;
    private RadioButton[] rd_foodinstruction;
    private RadioButton rd_foodId;
    private DbAdapter dbAdapter;


    private SimpleDateFormat endDate;

    private EditText edit_notes;
    private String startTimeString;
    private String endTimeString;
    private RadioButton rd_reminderdaytime;

    private Collection<Day> allDayTimes;
    private Collection<Day> selectedDayTimes = new ArrayList<Day>();
    private Collection<Eat> allFoodInstructions;
    private Collection<ConsumeInterval> consumeIntervals = new ArrayList<>();
    private Collection<ConsumeIndividual> consumeIndividuals = new ArrayList<>();
    private StringBuilder eatString;
    private int selectedFoodInstruction;
    private ImageButton iBtn_helpPhoto;
    private ImageButton iBtn_helpReminder;
    private ImageButton iBtn_helpDuration;
    private ImageButton iBtn_helpFoodInstruction;
    private ImageButton iBtn_helpOverview;
    private TextView txt_helpText;

    private int mediId;
    private Data pillData;
    Collection<Object> consumeList = new ArrayList<>();
    private int spinnerIntervalPosition;
    private int number = 1;
    private ViewGroup ln_dosage;
    private int dosage;
    private StringBuilder numDaysString;
    private StringBuilder durationString;
    private ArrayList<String> strDayTimes;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.fragment_registration, container, false);
        super.onCreateView(inflater, container, savedInstanceState);
        fragmentSettings = new FragmentSettings();
        dbAdapter= new DbAdapter(getActivity().getApplicationContext());
        dbAdapter.open();

        init();
        registrationInit();

        saveItem();
        deleteItem();

        setDeleteButtonVisibility();
        return root;
    }

    private void init(){
        setupShowImage();
        daytimebuilder = new StringBuilder();
        setupDaytimes();
        for (Day day : allDayTimes){
            checkItems = new boolean[allDayTimes.size()];
            //checkItems [allDayTimes.size()] = new int[];
            setCheckItems(day.getId(),false);
        }
        showReminderDetails();
        setOnReminderDayTimeRadioButtonClickEvent();
        setOnReminderIntervalRadioButtonClickEvent();
        showDuration();
        setOnDurationDateRadioButtonClickEvent();
        setOnDurationNumOfBlistersRadioButtonClick();
        dosageString = new StringBuilder();
        setupDosageNumberPicker();
        setupDosage();
        setDosage(1);
        showFoodInstruction();

        if (checkHelpTextVisibility()){
            showHelpText();
            iBtn_helpPhoto.setVisibility(View.VISIBLE);
            iBtn_helpReminder.setVisibility(View.VISIBLE);
            iBtn_helpDuration.setVisibility(View.VISIBLE);
            iBtn_helpFoodInstruction.setVisibility(View.VISIBLE);
        } else {
            iBtn_helpPhoto.setVisibility(View.GONE);
            iBtn_helpReminder.setVisibility(View.GONE);
            iBtn_helpDuration.setVisibility(View.GONE);
            iBtn_helpFoodInstruction.setVisibility(View.GONE);
        }
        LayoutInflater inflaterStartEnd = getActivity().getLayoutInflater();
        dialogViewStartEnd = inflaterStartEnd.inflate(R.layout.dialog_reminderstartendtime, null);
        btn_starttime = (Button)dialogViewStartEnd.findViewById(R.id.btn_starttime);
        btn_endtime = (Button)dialogViewStartEnd.findViewById(R.id.btn_endtime);
        intervalCalendar = Calendar.getInstance();
        startTimeCalendar = Calendar.getInstance();
        endTimeCalendar = Calendar.getInstance();
        setTime(startTimeCalendar,0,0);
        setTime(endTimeCalendar, 23, 59);
        setReminderTime(getStartTimeCalendar(), 0, 0);
        setReminderTime(getEndTimeCalendar(), 23, 59);
        setNpReminderValue(1);
        setSpinnerReminderValue(0);

        dateCalendarDuration = Calendar.getInstance();
        setDurationDate(dateCalendarDuration.get(Calendar.YEAR), dateCalendarDuration.get(Calendar.MONTH), dateCalendarDuration.get(Calendar.DAY_OF_MONTH));

        changeNumberOfBlisterTextField();
        intervalbuilder = new StringBuilder();


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
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public void onStop(){
        if(dbAdapter!=null) {
            dbAdapter.close();
            dbAdapter = null;
        }
        super.onStop();
    }

    private void setOnReminderDayTimeRadioButtonClickEvent(){
        RadioButton rdnReminderDayTime = (RadioButton)root.findViewById((R.id.rd_daytime));
        rdnReminderDayTime.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showReminderDaytimeDialog();
            }
        });
    }

    private void setOnDurationDateRadioButtonClickEvent(){
        RadioButton rd_durationDate = (RadioButton)root.findViewById((R.id.rd_numberOfDays));
        rd_durationDate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog();
            }
        });
    }

    private void setOnDurationNumOfBlistersRadioButtonClick(){
        RadioButton rd_durationPackageEnd = (RadioButton)root.findViewById((R.id.rd_durationPackageEnd));
        rd_durationPackageEnd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showNumberOfBlistersNumberPickerDialog();
            }
        });
    }

    private void setOnReminderIntervalRadioButtonClickEvent(){
        RadioButton rd_ReminderInterval = (RadioButton)root.findViewById((R.id.rd_interval));
        rd_ReminderInterval.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showReminderIntervalDialog();
            }
        });
    }

    /**
     * checks whether the delete button needs to be displayed
     */
    public void setDeleteButtonVisibility(){
        Button btn_delete = (Button)root.findViewById(R.id.btn_delete);
        if (((MainActivity)getActivity()).getCurrentMenuItem() == R.id.nav_registration){
            btn_delete.setVisibility(View.GONE);
        } else if (((MainActivity)getActivity()).getCurrentMenuItem() == R.id.nav_edit){
            btn_delete.setVisibility(View.VISIBLE);
        }
    }

    private String getName(){
        edit_name = (EditText) root.findViewById(R.id.edit_name);
        return String.valueOf(edit_name.getText());
    }

    public void setName(String name){
        EditText edit_name = (EditText) root.findViewById(R.id.edit_name);
        edit_name.setText(name);
    }

    private void setupShowImage() {
        Button btn_selectPhoto = (Button) root.findViewById(R.id.btn_selectPhoto);
        btn_selectPhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent chooseImageIntent = getPickImageIntent(getActivity());
                startActivityForResult(chooseImageIntent, 234);
            }
        });
        iv_selectedImage = (ImageView) root.findViewById(R.id.iv_selectedImage);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case 234:
                thumbnail = getImageFromResult(getActivity(), resultCode, data);
                iv_selectedImage.setImageBitmap(thumbnail);
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }

    }

    public static Bitmap getImageFromResult(Context context, int resultCode,
                                            Intent imageReturnedIntent) {
        Log.d("ImageResult", "getImageFromResult, resultCode: " + resultCode);
        Bitmap bm = null;
        File imageFile = getTempFile(context);
        if (resultCode == Activity.RESULT_OK) {
            Uri selectedImage;
            boolean isCamera = (imageReturnedIntent == null ||
                    imageReturnedIntent.getData() == null  ||
                    imageReturnedIntent.getData().toString().contains(imageFile.toString()));
            if (isCamera) {     /** CAMERA **/
                selectedImage = Uri.fromFile(imageFile);
            } else {            /** ALBUM **/
                selectedImage = imageReturnedIntent.getData();
            }
            Log.d("selected", "selectedImage: " + selectedImage);

            bm = getImageResized(context, selectedImage);
            int rotation = getRotation(context, selectedImage, isCamera);
            bm = rotate(bm, rotation);
        }
        return bm;
    }

    private static File getTempFile(Context context) {
        File imageFile = new File(context.getExternalCacheDir(), "Temp.jpg");
        imageFile.getParentFile().mkdirs();
        return imageFile;
    }

    public static int getRotation(Context context, Uri imageUri, boolean isCamera) {
        int rotation;
        if (isCamera) {
            rotation = getRotationFromCamera(context, imageUri);
        } else {
            rotation = getRotationFromGallery(context, imageUri);
        }
        Log.d("Rotation:", "Image rotation: " + rotation);
        return rotation;
    }

    private static Bitmap rotate(Bitmap bm, int rotation) {
        if (rotation != 0) {
            Matrix matrix = new Matrix();
            matrix.postRotate(rotation);
            Bitmap bmOut = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(), bm.getHeight(), matrix, true);
            return bmOut;
        }
        return bm;
    }

    public Intent getPickImageIntent(Context context) {
        Intent chooserIntent = null;
        List<Intent> intentList = new ArrayList<>();
        Intent pickIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        Intent takePhotoIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        takePhotoIntent.putExtra("return-data", true);
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempFile(context)));
        intentList = addIntentsToList(context, intentList, pickIntent);
        intentList = addIntentsToList(context, intentList, takePhotoIntent);

        if (intentList.size() > 0) {
            chooserIntent = Intent.createChooser(intentList.remove(intentList.size() - 1),
                    context.getString(R.string.dialog_photoMessage));
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, intentList.toArray(new Parcelable[]{}));
        }

        return chooserIntent;
    }

    private static List<Intent> addIntentsToList(Context context, List<Intent> list, Intent intent) {
        List<ResolveInfo> resInfo = context.getPackageManager().queryIntentActivities(intent, 0);
        for (ResolveInfo resolveInfo : resInfo) {
            String packageName = resolveInfo.activityInfo.packageName;
            Intent targetedIntent = new Intent(intent);
            targetedIntent.setPackage(packageName);
            list.add(targetedIntent);
        }
        return list;
    }

    private static Bitmap decodeBitmap(Context context, Uri theUri, int sampleSize) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = sampleSize;
        AssetFileDescriptor fileDescriptor = null;
        try {
            fileDescriptor = context.getContentResolver().openAssetFileDescriptor(theUri, "r");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        Bitmap actuallyUsableBitmap = BitmapFactory.decodeFileDescriptor(fileDescriptor.getFileDescriptor(), null, options);
        return actuallyUsableBitmap;
    }

    public static Bitmap getImageResized(Context context, Uri selectedImage) {
        Bitmap bm = null;
        int[] sampleSizes = new int[]{5, 3, 2, 1};
        int i = 0;
        do {
            bm = decodeBitmap(context, selectedImage, sampleSizes[i]);
            Log.d("Resized", "resizer: new bitmap width = " + bm.getWidth());
            i++;
        } while (bm.getWidth() < 400 && i < sampleSizes.length);
        return bm;
    }

    private static int getRotationFromCamera(Context context, Uri imageFile) {
        int rotate = 0;
        try {
            context.getContentResolver().notifyChange(imageFile, null);
            ExifInterface exif = new ExifInterface(imageFile.getPath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    public static int getRotationFromGallery(Context context, Uri imageUri) {
        int result = 0;
        String[] columns = {MediaStore.Images.Media.ORIENTATION};
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(imageUri, columns, null, null, null);
            if (cursor != null && cursor.moveToFirst()) {
                int orientationColumnIndex = cursor.getColumnIndex(columns[0]);
                result = cursor.getInt(orientationColumnIndex);
            }
        } catch (Exception e) {
            e.getMessage();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return result;
    }

    public Bitmap getPicture(){
        if (thumbnail.getWidth() > thumbnail.getHeight()){
            Matrix matrix = new Matrix();
            matrix.postRotate(90);
            thumbnail = Bitmap.createBitmap(thumbnail, 0, 0, thumbnail.getWidth(), thumbnail.getHeight(), matrix, true);
        }
        return thumbnail;
    }

    private void setPicture(Bitmap bitmap){
        iv_selectedImage.setImageBitmap(bitmap);
    }


    /**
     * shows textView with selected information
     * @param textView selected textView
     * @param viewGroup current view
     */
    private void showInfoTextField(TextView textView, ViewGroup viewGroup) {
        viewGroup.removeView(textView);
        textView.setPadding(10, 10, 10, 10);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        viewGroup.addView(textView);
    }

    private void showReminderDetails(){
        RadioGroup rdg_reminder = (RadioGroup) root.findViewById(R.id.rdg_reminder);
        rd_reminderInterval = (RadioButton)root.findViewById(R.id.rd_interval);
        final LinearLayout ln_reminder = (LinearLayout)root.findViewById(R.id.ln_reminder);
        cv_foodInstruction = (CardView)root.findViewById(R.id.cv_foodInstruction);
        txt_reminder = new TextView(getActivity());
        txt_reminder.setVisibility(View.GONE);
        showInfoTextField(txt_reminder, ln_reminder);
        rdg_reminder.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                txt_reminder.setVisibility(View.VISIBLE);
                if (checkedId == rd_reminderInterval.getId()) {
                    cv_foodInstruction.setVisibility(View.GONE);
                } else {
                    cv_foodInstruction.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setupDaytimes(){
        allDayTimes = Day.getAllDayValues(dbAdapter);
        strDayTimes = new ArrayList<String>();
        for(Day day:allDayTimes){
            strDayTimes.add(day.getDescription());
        }
    }
    public void showReminderDaytimeDialog(){
        reminderDaytimeDialog = new AlertDialog.Builder(getActivity(),R.style.DialogTheme);
        reminderDaytimeDialog.setCancelable(false);
        reminderDaytimeDialog.setTitle(getResources().getString(R.string.title_reminderDaytime));
        reminderDaytimeDialog.setMultiChoiceItems(daytimes, checkItems, dayTimeListener);
        reminderDaytimeDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setDaytimeText();
            }
        });
        reminderDaytimeDialog.setCancelable(false);
        Dialog dialog = reminderDaytimeDialog.create();
        dialog.show();
    }

    private void setDaytimeText(){
        daytimebuilder.setLength(0);
        daytimebuilder.append(getResources().getString(R.string.taking)).append(" ");
        daytimes = strDayTimes.toArray(new CharSequence[allDayTimes.size()]);
        for (int id : selList) {
            selectedDayTimes.add(Iterables.get(allDayTimes, id));
            daytimebuilder.append(daytimes[id]).append(" ");
        }
        if (selectedDayTimes.size() > 0) {
            txt_reminder.setText(daytimebuilder);
        }
    }

    DialogInterface.OnMultiChoiceClickListener dayTimeListener = new DialogInterface.OnMultiChoiceClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
            if (isChecked) {
                selList.add(which);
                checkItems[which] = true;
            } else if (selList.contains(which)) {
                selList.remove(which);
                checkItems[which] = false;
            }
        }
    };

    private Collection<Day> getDaytimes(){
        return selectedDayTimes;
    }

    private int countDayTime(){
        return selectedDayTimes.size();
    }

    private void setCheckItems(int dayId, boolean isChecked ){
        checkItems[dayId] = isChecked;
        if (isChecked){
            selList.add(dayId);
        }
    }

    private void setReminderDayTime(Day day){
       checkItems[day.getId()] = true;
       reminderDaytimeDialog.setMultiChoiceItems(daytimes, checkItems, dayTimeListener);
    }

    public void showReminderIntervalDialog(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity(),R.style.DialogTheme);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog_reminderinterval, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle(getResources().getString(R.string.title_intervalDialog));
        setupReminderIntervalNumberPicker();
        setReminderIntervalTimes(getNpReminderValue());
        intervalbuilder.setLength(0);
        dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (sp_reminderInterval.getSelectedItemPosition() == 0) {
                    showStartEndTimeDialog();
                } else if (sp_reminderInterval.getSelectedItemPosition() == 1) {
                    showIntervalTimePickerDialog();
                } else if (sp_reminderInterval.getSelectedItemPosition() == 2) {
                    showWeekdayDialog();
                }
                setReminderIntervalText();
            }
        });
        sp_reminderInterval.setSelection(getSpinnerReminderValue());
        dialogBuilder.setCancelable(false);
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private void setReminderIntervalText(){
        intervalbuilder.append(getResources().getString(R.string.taking)).append(" ");
        if (getNpReminderValue() == 1) {
            if (getSelectedIntervalString().equals(getResources().getString(R.string.hour))) {
                intervalbuilder.append(getResources().getString(R.string.everyHour));
            } else if (getSelectedIntervalString().equals(getResources().getString(R.string.day))) {
                intervalbuilder.append(getResources().getString(R.string.everyDay));
            } else if (getSelectedIntervalString().equals(getResources().getString(R.string.week))) {
                intervalbuilder.append(getResources().getString(R.string.everyWeek));
            }
        } else {
            intervalbuilder.append(getResources().getString(R.string.every)).append(" ");
            intervalbuilder.append(selectedValue).append(" ");
            if (getSelectedIntervalString().equals(getResources().getString(R.string.hour))) {
                intervalbuilder.append(getResources().getString(R.string.hourMult));
            } else if (getSelectedIntervalString().equals(getResources().getString(R.string.day))) {
                intervalbuilder.append(getResources().getString(R.string.dayMult));
            } else if (getSelectedIntervalString().equals(getResources().getString(R.string.week))) {
                intervalbuilder.append(getResources().getString(R.string.weekMult));
            }

        }
        txt_reminder.setText(intervalbuilder);
    }

    private void setSpinnerReminderValue(int selectedIntervalPosition){
        this.selectedIntervalPosition = selectedIntervalPosition;
    }

    private int getSpinnerReminderValue(){
        return selectedIntervalPosition;
    }

    private void setNpReminderValue (int number){
        this.number = number;
    }

    private int getNpReminderValue(){
        return number;
    }

    private void setupReminderIntervalNumberPicker(){
        np_reminderInterval = (NumberPicker)dialogView.findViewById(R.id.np_reminderInterval);
        np_reminderInterval.setMinValue(1);
    }

    private void setReminderIntervalTimes(final int selectedNumber){
        number = selectedNumber;
        sp_reminderInterval = (Spinner) dialogView.findViewById(R.id.sp_reminderInterval);
        np_reminderInterval.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                number = newVal;
            }
        });
        sp_reminderInterval.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedIntervalPosition = (int) parent.getItemIdAtPosition(position);
                setSelectedIntervalString(parent.getItemAtPosition(position).toString());
                //selectedInterval = parent.getItemAtPosition(position).toString();
                if (getSelectedIntervalString().equals(getResources().getString(R.string.hour))) {
                    np_reminderInterval.setMaxValue(23);
                    np_reminderInterval.setValue(selectedNumber);
                } else if (getSelectedIntervalString().equals(getResources().getString(R.string.day))) {
                    np_reminderInterval.setMaxValue(31);
                    np_reminderInterval.setValue(selectedNumber);
                } else if (getSelectedIntervalString().equals(getResources().getString(R.string.week))) {
                    np_reminderInterval.setMaxValue(60);
                    np_reminderInterval.setValue(selectedNumber);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                setSelectedIntervalString(getResources().getString(R.string.hour));
                //selectedInterval = getResources().getString(R.string.hour);
            }
        });
    }

    private String getSelectedIntervalString(){
        return selectedInterval;
    }

    private void setSelectedIntervalString (String selectedInterval){
        this.selectedInterval = selectedInterval;
    }

    private void showIntervalTimePickerDialog() {
        tpd_interval = new TimePickerDialog(getActivity(),R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                intervalCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                intervalCalendar.set(Calendar.MINUTE, minute);
                intervalHour = hourOfDay;
                intervalMinute = minute;
                intervalTime = new SimpleDateFormat("HH:mm");
                intervalDate = intervalCalendar.getTime();
                intervalTimeString = intervalTime.format(intervalCalendar.getTime());
                intervalbuilder.append(" ").append(getResources().getString(R.string.at)).append(" ").append(intervalTimeString);
                txt_reminder.setText(intervalbuilder);
            }
        },intervalHour , intervalMinute, true);
        tpd_interval.setButton(DialogInterface.BUTTON_NEGATIVE, null, tpd_interval);
        tpd_interval.setTitle(getResources().getString(R.string.title_chooseTime));
        tpd_interval.show();
    }

    private void showWeekdayDialog(){
        weekdayString = getResources().getString(R.string.monday);
        weekdays = Arrays.asList(getResources().getStringArray(R.array.array_weekday));
        dialogWeekday = new AlertDialog.Builder(getActivity(),R.style.DialogTheme);

        dialogWeekday.setTitle(getResources().getString(R.string.title_dialogWeekday));
        dialogWeekday.setSingleChoiceItems(R.array.array_weekday, 0, dialogWeekListener);
        dialogWeekday.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                intervalbuilder.append(" ").append(getResources().getString(R.string.on)).append(" ").append(weekdayString);
                showIntervalTimePickerDialog();
            }
        });
        dialogWeekday.setCancelable(false);
        Dialog d = dialogWeekday.create();
        d.show();
    }

    DialogInterface.OnClickListener dialogWeekListener = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            if (which == weekdays.indexOf(getString(R.string.monday))) {
                weekday = Calendar.MONDAY;
                weekdayString = getResources().getString(R.string.monday);
            } else if (which == weekdays.indexOf(getString(R.string.tuesday))) {
                weekday = Calendar.TUESDAY;
                weekdayString = getResources().getString(R.string.tuesday);
            } else if (which == weekdays.indexOf(getString(R.string.wednesday))) {
                weekday = Calendar.WEDNESDAY;
                weekdayString = getResources().getString(R.string.wednesday);
            } else if (which == weekdays.indexOf(getString(R.string.thursday))) {
                weekday = Calendar.THURSDAY;
                weekdayString = getResources().getString(R.string.thursday);
            } else if (which == weekdays.indexOf(getString(R.string.friday))) {
                weekday = Calendar.FRIDAY;
                weekdayString = getResources().getString(R.string.friday);
            } else if (which == weekdays.indexOf(getString(R.string.saturday))) {
                weekday = Calendar.SATURDAY;
                weekdayString = getResources().getString(R.string.saturday);
            } else if (which == weekdays.indexOf(getString(R.string.sunday))) {
                weekday = Calendar.SUNDAY;
                weekdayString = getResources().getString(R.string.sunday);
            }
        }

    };

    private int getWeekday(){
        return weekday;
    }

    public void setReminderRadioButton(int checkedRadioButton){
        RadioGroup rdg_reminder = (RadioGroup)root.findViewById(R.id.rdg_reminder);
        rdg_reminder.check(checkedRadioButton);
    }

    public void setReminderInterval(int position){
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_reminderinterval, null);
        Spinner sp_reminderInterval = (Spinner)dialogView.findViewById(R.id.sp_reminderInterval);
        sp_reminderInterval.setSelection(position);
    }

    private void setReminderValue(int value){
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_reminderinterval, null);
        NumberPicker np_reminderInterval = (NumberPicker)dialogView.findViewById(R.id.np_reminderInterval);
        np_reminderInterval.setValue(value);
    }

    public void setReminderTime(Calendar calendar,int hour, int minute) {
        if (getSpinnerReminderValue()==0){
            calendar.set(Calendar.HOUR_OF_DAY,hour);
            calendar.set(Calendar.MINUTE,minute);
        } else if (getSpinnerReminderValue() ==1 || getSpinnerReminderValue() ==2){
            intervalCalendar.set(Calendar.HOUR_OF_DAY,hour);
            intervalCalendar.set(Calendar.MINUTE,minute);
        }
    }

    private void setTime(Calendar calendar, int hour, int minute){
        calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE, minute);
    }

    private int getTime(Calendar calendar, int time){
        return calendar.get(time);
    }

    private Calendar getStartTimeCalendar(){
        return startTimeCalendar;
    }

    private  Calendar getEndTimeCalendar(){
        return endTimeCalendar;
    }

    public void setWeekday(int checkedItems){
        dialogWeekday.setSingleChoiceItems(R.array.array_weekday, checkedItems, dialogWeekListener);
    }

    private void showStartEndTimeDialog(){
        startTimeString = "00:00";
        endTimeString = "23:59";
        final AlertDialog.Builder dialogStartEndTime = new AlertDialog.Builder(getActivity(),R.style.DialogTheme);
        dialogStartEndTime.setView(dialogViewStartEnd);
        dialogStartEndTime.setTitle("Start/ Endzeit festlegen");
        btn_starttime.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tp_startEndTimeInterval = new TimePickerDialog(getActivity(), R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        startTimeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        startTimeCalendar.set(Calendar.MINUTE, minute);
                        setStartTimeButtonText();
                    }
                }, getTime(startTimeCalendar, Calendar.HOUR_OF_DAY), getTime(startTimeCalendar, Calendar.MINUTE), true);
                tp_startEndTimeInterval.setButton(DialogInterface.BUTTON_NEGATIVE, null, tp_startEndTimeInterval);
                showTimeDialog(getResources().getString(R.string.title_chooseStartTime));
            }
        });
        btn_endtime.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                tp_startEndTimeInterval = new TimePickerDialog(getActivity(), R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        endTimeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        endTimeCalendar.set(Calendar.MINUTE, minute);

                        setEndTimeButtonText();
                    }
                }, getTime(endTimeCalendar, Calendar.HOUR_OF_DAY), getTime(endTimeCalendar, Calendar.MINUTE), true);
                tp_startEndTimeInterval.setButton(DialogInterface.BUTTON_NEGATIVE, null, tp_startEndTimeInterval);
                showTimeDialog(getResources().getString(R.string.title_chooseEndTime));
            }
        });
        dialogStartEndTime.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (startTimeString.equals(endTimeString)) {
                    showAlertTimeDialog(getResources().getString(R.string.sameTimeMessage));
                } else if (getStartTimeCalendar().getTimeInMillis() > getEndTimeCalendar().getTimeInMillis()){
                    showAlertTimeDialog(getResources().getString(R.string.littleTime));
                } else {
                    setIntervalHourText();
                }

            }
        });
        dialogStartEndTime.setCancelable(false);
        Dialog d = dialogStartEndTime.create();
        d.show();
    }

    private void setIntervalHourText(){
        intervalbuilder.append(" ").append(getResources().getString(R.string.from)).append(" ").append(startTimeString).append(" ")
                .append(getResources().getString(R.string.till)).append(" ").append(endTimeString);
        txt_reminder.setText(intervalbuilder);
    }

    private void setStartTimeButtonText(){
        final StringBuilder startString = new StringBuilder();
        startTime = new SimpleDateFormat("HH:mm");
        startTimeString = startTime.format(getStartTimeCalendar().getTime());
        startString.append(startTimeString);
        btn_starttime.setText(startString);
    }

    private void setEndTimeButtonText(){
        final StringBuilder endString = new StringBuilder();
        SimpleDateFormat endTime = new SimpleDateFormat("HH:mm");
        endTimeString = endTime.format(getEndTimeCalendar().getTime());
        endString.append(endTimeString);
        btn_endtime.setText(endString);
    }

    private void showAlertTimeDialog(String message){
        AlertDialog.Builder warnbuilder = new AlertDialog.Builder(getActivity(),R.style.DialogTheme);
        warnbuilder.setTitle(getResources().getString(R.string.warning));
        warnbuilder.setCancelable(false);
        warnbuilder.setMessage(message);
        warnbuilder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showStartEndTimeDialog();
            }
        });
        Dialog d = warnbuilder.create();
        d.show();
    }

    private void showTimeDialog(String title){
        tp_startEndTimeInterval.setTitle(title);
        tp_startEndTimeInterval.show();
    }

    private int getIntervalValue(){
        return np_reminderInterval.getValue();
    }

    private int getInterval(){
        return sp_reminderInterval.getSelectedItemPosition();
    }

    private long getPeriodOfTime(){
        return startTimeCalendar.getTimeInMillis() - endTimeCalendar.getTimeInMillis();
    }

    private Collection<ConsumeInterval> getReminderInterval(){
        ConsumeInterval consumeInterval = new ConsumeInterval();
        consumeInterval.setStartTime(getStartTimeCalendar());
        consumeInterval.setEndTime(getEndTimeCalendar());
        consumeInterval.setInterval(getIntervalValue());
        consumeInterval.setWeekday(getWeekday());
        consumeIntervals.add(consumeInterval);
        return consumeIntervals;
    }

    private Collection<ConsumeIndividual> getReminderDayTime(){
        for (Day day : getDaytimes()){
            ConsumeIndividual consumeIndividual = new ConsumeIndividual();
            consumeIndividual.setEatpart(getFoodInstruction());
            consumeIndividual.setDaypart(Iterables.get(allDayTimes, day.getId()));
            consumeIndividuals.add(consumeIndividual);
        }
        return consumeIndividuals;
    }

    private void showDuration() {
        rdg_duration = (RadioGroup)root.findViewById(R.id.rdg_duration);
        final LinearLayout ln_duration = (LinearLayout) root.findViewById(R.id.ln_duration);
        txt_duration = new TextView(getActivity());
        showInfoTextField(txt_duration, ln_duration);
        txt_duration.setVisibility(View.GONE);
        rdg_duration.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                durationString = new StringBuilder();
                txt_duration.setVisibility(View.VISIBLE);
                if (checkedId == R.id.rd_always) {
                    setDurationAlwaysText();
                }
            }
        });
    }

    private void setDurationAlwaysText(){
        durationString.append(getResources().getString(R.string.txt_durationAlways));
        txt_duration.setText(durationString);
    }

    private long getSelectedDate(){
        Log.i("Selected Date", String.valueOf(dateCalendarDuration.getTimeInMillis()));
        return dateCalendarDuration.getTimeInMillis();

    }

    private long getCurrentSystemTime(){
        Calendar currentDateCal = Calendar.getInstance();
        long currentDate = currentDateCal.getTimeInMillis();
        Log.i("Current Date", String.valueOf(currentDate));
        return currentDate;
    }

    private int numberOfDays(){
        int days = ((int)getSelectedDate()/(24*60*60*1000))-((int)getCurrentSystemTime()/(24*60*60*1000));
        Log.i("Number Of Days", String.valueOf(days));
        return days;
    }

    private int getDuration(int pillsPerBlister){
        int numberOfIntakes = -1;
        RadioButton rd_numberOfDays = (RadioButton)root.findViewById(R.id.rd_numberOfDays);
        RadioButton rd_always = (RadioButton)root.findViewById(R.id.rd_always);
        RadioButton rd_packageEnd = (RadioButton)root.findViewById(R.id.rd_durationPackageEnd);
        if (rd_numberOfDays.isChecked()){
            if (rd_reminderInterval.isChecked()){
                if (getInterval() ==0){
                    numberOfIntakes = (int) ((getPeriodOfTime()/getIntervalValue()) * numberOfDays());
                } else if (getInterval() == 1){
                    numberOfIntakes = numberOfDays() * getIntervalValue();
                } else if (getInterval() == 2){
                    numberOfIntakes = (numberOfDays()/7) * getIntervalValue();
                }
            } else if (rd_reminderdaytime.isChecked()){
                numberOfIntakes = numberOfDays() / (getDosage()*countDayTime());
            }
        } else if (rd_always.isChecked()){
            numberOfIntakes = -1;
        } else if (rd_packageEnd.isChecked()){
            int totalPills = getNumberOfBlistersValue() *pillsPerBlister;
            if (rd_reminderInterval.isChecked()){
                numberOfIntakes = totalPills / getDosage();
            } else if (rd_reminderdaytime.isChecked()){
                numberOfIntakes = totalPills / getDosage()*countDayTime();
            }
        }
        Log.i("Number Of Intakes", String.valueOf(numberOfIntakes));
        return numberOfIntakes;
    }

    public void setDurationRd(int duration, Data data){
        if (duration == 0){
            rdg_duration.check(R.id.rd_numberOfDays);
        } else if (duration == 1){
            rdg_duration.check(R.id.rd_always);
        } else if (duration == 2){
            rdg_duration.check(R.id.rd_durationPackageEnd);
        }

        data.getDuration();
        data.getAllPillCoords().size();
    }

    public void setDuration(int checkedItem,int value,int year,int month, int day){
        if (checkedItem==0){
            dateCalendarDuration.set(Calendar.YEAR, year);
            dateCalendarDuration.set(Calendar.MONTH,month);
            dateCalendarDuration.set(Calendar.DAY_OF_MONTH,day);
        } else if (checkedItem==2){
            setNumberOfBlistersValue(value);
        }
    }

    private void setNumberOfBlistersValue(int numberOfBlisters){
        //np_numberofBlisters = (NumberPicker)root.findViewById(R.id.np_numberofBlisters);
        this.numberOfBlisters = numberOfBlisters;
        np_numberofBlisters.setValue(this.numberOfBlisters);
    }

    private int getNumberOfBlistersValue(){
        return np_numberofBlisters.getValue();
    }

    public void showDateDialog(){
        int style;
        if (isBrokenSamsungDevice()){
            style = android.R.style.Theme_Holo_Light_Dialog;
        } else {
            style = R.style.DialogTheme;
        }
        DatePickerDialog dpd = new DatePickerDialog(getActivity(),  style,new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                monthOfYear = monthOfYear+1;
                setDurationDate(year, monthOfYear-1,dayOfMonth);
                setDateText();
            }
        }, getDurationDate(Calendar.YEAR), getDurationDate(Calendar.MONTH), getDurationDate(Calendar.DAY_OF_MONTH));
        dpd.setCancelable(false);
        dpd.setButton(DialogInterface.BUTTON_NEGATIVE, null, dpd);
        dpd.show();
    }

    private void setDateText(){
        numDaysString = new StringBuilder();
        endDate = new SimpleDateFormat("dd.MM.yyyy");
        String dateString = endDate.format(dateCalendarDuration.getTime());
        numDaysString.append(getResources().getString(R.string.taking)).append(" ")
                .append(getResources().getString(R.string.till)).append(" ").append(dateString);
        txt_duration.setText(numDaysString);
    }

    private void setDurationDate(int year, int month, int day){
        dateCalendarDuration.set(Calendar.YEAR, year);
        dateCalendarDuration.set(Calendar.MONTH, month);
        dateCalendarDuration.set(Calendar.DAY_OF_MONTH, day);
    }

    private int getDurationDate(int time){
        return dateCalendarDuration.get(time);

    }

    /**
     * Samsung Bug: samsung device with lollipop can't display date dialog correctly
     * @return whether it's a samsung device with lollipop
     */
    private static boolean isBrokenSamsungDevice() {
        return (Build.MANUFACTURER.equalsIgnoreCase("samsung")
                && isBetweenAndroidVersions(
                Build.VERSION_CODES.LOLLIPOP,
                Build.VERSION_CODES.LOLLIPOP_MR1));
    }

    private static boolean isBetweenAndroidVersions(int min, int max) {
        return Build.VERSION.SDK_INT >= min && Build.VERSION.SDK_INT <= max;
    }

    public void changeNumberOfBlisterTextField() {
        np_numberofBlisters = new NumberPicker(getActivity());
        np_numberofBlisters.setId(R.id.np_numberofBlisters);
        np_numberofBlisters.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                numberOfBlisters = newVal;
            }
        });
    }

    /**
     * toDo IllegalStateException second Call
     */
    public void showNumberOfBlistersNumberPickerDialog(){
        AlertDialog.Builder npb_numberofBlisters = new AlertDialog.Builder(getActivity(),R.style.DialogTheme);
        npb_numberofBlisters.setCancelable(false);
        np_numberofBlisters.setMaxValue(20);
        np_numberofBlisters.setMinValue(1);
        setNumberOfBlistersValue(numberOfBlisters);
        FrameLayout parent = new FrameLayout(getActivity());
        parent.removeView(np_numberofBlisters);
        parent.addView(np_numberofBlisters, new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        npb_numberofBlisters.setView(parent);
        npb_numberofBlisters.setTitle(getResources().getString(R.string.d_packagEnd));
        npb_numberofBlisters.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setNumberOfBlisterText();
            }
        });
        npb_numberofBlisters.setCancelable(false);
        Dialog dialog = npb_numberofBlisters.create();
        dialog.show();
        /*AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity(),R.style.DialogTheme);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        dialogNumberpickerView = inflater.inflate(R.layout.dialog_numberofblisters, null);
        NumberPicker np_numberOfBlisters = (NumberPicker)root.findViewById(R.id.np_numberofBlisters);
        dialogBuilder.setView(dialogNumberpickerView);
        dialogBuilder.setTitle(getResources().getString(R.string.d_packagEnd));
        dialogBuilder.setCancelable(false);
        np_numberOfBlisters.setMaxValue(20);
        np_numberOfBlisters.setMinValue(1);
        setNumberOfBlistersValue(numberOfBlisters);
        dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setNumberOfBlisterText();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();*/
    }

    private void setNumberOfBlisterText(){
        numberOfBlisterString = new StringBuilder();
        numberOfBlisterString.append(getResources().getString(R.string.taking)).append(" ");
        numberOfBlisterString.append(numberOfBlisters);
        txt_duration.setText(numberOfBlisterString);
    }

    private void setupDosage(){
        ln_dosage = (ViewGroup) root.findViewById(R.id.ln_dosage);
        txt_dosage = new TextView(getActivity());
        txt_dosage.setText(getResources().getString(R.string.txt_dosageInitial));
    }

    private void setDosage(int value){
        dosage = value;
        np_dosage.setValue(dosage);
        np_dosage.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                dosage = newVal;

            }
        });
        setDosageText();
        showInfoTextField(txt_dosage, ln_dosage);
    }

    private void setDosageText(){
        dosageString.setLength(0);
        dosageString.append(getResources().getString(R.string.txt_dosage)).append(" ");
        dosageString.append(dosage).append(" ");
        if (dosage == 1) {
            dosageString.append(getResources().getString(R.string.txt_dosageOneTab));
        } else {
            dosageString.append(getResources().getString(R.string.txt_dosageMoreTab));
        }
        txt_dosage.setText(dosageString);
    }

    private int getDosage(){
        return np_dosage.getValue();
    }

    private void setupDosageNumberPicker(){
        np_dosage = (NumberPicker)root.findViewById(R.id.np_dosage);
        np_dosage.setMinValue(1);
        np_dosage.setMaxValue(20);
        np_dosage.setValue(1);
    }

    private void showFoodInstruction() {
        final ViewGroup ln_foodInstruction = (ViewGroup) root.findViewById(R.id.ln_foodInstruction);

        txt_foodInstruction = new TextView(getActivity());
        txt_foodInstruction.setVisibility(View.GONE);

        allFoodInstructions = Eat.getAllEatValues(dbAdapter);
        rd_foodinstruction = new RadioButton[allFoodInstructions.size()];
        final RadioGroup rdg_foodInstruction = new RadioGroup(getActivity().getApplicationContext());

       for (Eat eat : allFoodInstructions) {
           rd_foodinstruction[eat.getId()] = (RadioButton)getActivity().getLayoutInflater().inflate(R.layout.template_radiobutton, null);
           rdg_foodInstruction.addView(rd_foodinstruction[eat.getId()]);
           rd_foodinstruction[eat.getId()].setText(eat.getDescription());
           rd_foodinstruction[eat.getId()].setTextColor(Color.BLACK);
           rd_foodinstruction[eat.getId()].setTextSize(14);
        }
        ln_foodInstruction.addView(rdg_foodInstruction);
        showInfoTextField(txt_foodInstruction, ln_foodInstruction);
        rdg_foodInstruction.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @TargetApi(Build.VERSION_CODES.M)
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                txt_foodInstruction.setVisibility(View.VISIBLE);
                eatString = new StringBuilder();
                if (checkedId == rd_foodinstruction[0].getId()) {
                    eatString.append(getResources().getString(R.string.txt_eatBefore));
                    selectedFoodInstruction = 0;

                } else if (checkedId == rd_foodinstruction[1].getId()) {
                    eatString.append(getResources().getString(R.string.txt_eatAfter));
                    selectedFoodInstruction = 1;

                } else if (checkedId == rd_foodinstruction[2].getId()) {
                    eatString.append(getResources().getString(R.string.txt_eatDuring));
                    selectedFoodInstruction = 2;

                }
                txt_foodInstruction.setText(eatString);
            }
        });
    }

    private Eat getFoodInstruction(){
        return Iterables.get(allFoodInstructions, selectedFoodInstruction);
    }

    private void setFoodInstruction(int selectedFoodInstruction){
        rd_foodinstruction[selectedFoodInstruction].setChecked(true);
    }

    private String getNotes(){
        edit_notes = (EditText)root.findViewById(R.id.edit_notes);
        return String.valueOf(edit_notes.getText());
    }

    public void setNotes(String notes){
        EditText edit_notes = (EditText) root.findViewById(R.id.edit_notes);
        edit_notes.setText(notes);
    }

    private void showHelpText(){
        iBtn_helpPhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                displayPopupWindow(v, getActivity().getString(R.string.helptext_photo));
            }
        });
        iBtn_helpReminder.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                displayPopupWindow(v, getActivity().getString(R.string.helptext_reminder));
            }
        });
        iBtn_helpDuration.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                displayPopupWindow(v, getActivity().getString(R.string.helptext_duration));
            }
        });
        iBtn_helpFoodInstruction.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                displayPopupWindow(v, getActivity().getString(R.string.helptext_FoodInstruction));
            }
        });
    }

    /**
     * checks whether help needs to be displayed
     * @return the preference
     */
    public boolean checkHelpTextVisibility(){
        iBtn_helpPhoto = (ImageButton) root.findViewById(R.id.iBtn_helpPhoto);
        iBtn_helpReminder = (ImageButton) root.findViewById(R.id.iBtn_helpReminder);
        iBtn_helpDuration = (ImageButton)root.findViewById(R.id.iBtn_helpDuration);
        iBtn_helpFoodInstruction = (ImageButton) root.findViewById(R.id.iBtn_helpfoodInstruction);
        iBtn_helpOverview = (ImageButton)root.findViewById(R.id.iBtn_helpOverview);

        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
        boolean showHelp = pref.getBoolean("pref_key_showHelp",false);
        return showHelp;
    }

    /**
     * shows help dialog
     * @param anchorView
     * @param helptext text to be shown
     */
    private void displayPopupWindow(View anchorView, String helptext) {
        PopupWindow popup = new PopupWindow(getActivity());
        View layout = getActivity().getLayoutInflater().inflate(R.layout.popup_help_content, null);
        txt_helpText = (TextView)layout.findViewById(R.id.txt_helpText);
        txt_helpText.setText(helptext);
        popup.setContentView(layout);
        popup.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popup.setOutsideTouchable(true);
        popup.setFocusable(true);
        popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        popup.showAsDropDown(anchorView);
    }

    /**
     * checks weather required items are selected
     */
    private void saveItem(){
        Button btn_save = (Button)root.findViewById(R.id.btn_save);
        final RadioGroup rdg_reminder = (RadioGroup)root.findViewById(R.id.rdg_reminder);
        final RadioGroup rdg_duration = (RadioGroup)root.findViewById(R.id.rdg_duration);
        final CardView cv_reminder = (CardView)root.findViewById(R.id.cv_reminder);
        final CardView cv_name = (CardView)root.findViewById(R.id.cv_name);
        final CardView cv_photo = (CardView)root.findViewById(R.id.cv_photo);
        final CardView cv_duration = (CardView)root.findViewById(R.id.cv_duration);
        final CardView cv_foodInstruction = (CardView)root.findViewById(R.id.cv_foodInstruction);
        rd_reminderdaytime = (RadioButton)root.findViewById(R.id.rd_daytime);
        final EditText edit_name = (EditText)root.findViewById(R.id.edit_name);
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity(),R.style.DialogTheme);
        btn_save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit_name.getText().toString().trim().length() == 0) {
                    alertBuilder.setMessage(getResources().getString(R.string.dialog_nameMessage));
                    alertBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cv_name.setFocusable(true);
                            cv_name.setFocusableInTouchMode(true);
                            cv_name.requestFocus();
                        }
                    });
                    alertBuilder.show();
                } else if (getPicture() ==null){
                    alertBuilder.setMessage(getResources().getString(R.string.dialog_photoMessage));
                    alertBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cv_photo.setFocusable(true);
                            cv_photo.setFocusableInTouchMode(true);
                            cv_photo.requestFocus();

                        }
                    });
                    alertBuilder.show();
                }
                else if (rdg_reminder.getCheckedRadioButtonId() == -1) {
                    alertBuilder.setMessage(getResources().getString(R.string.dialog_reminderMessage));
                    alertBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cv_reminder.setFocusable(true);
                            cv_reminder.setFocusableInTouchMode(true);
                            cv_reminder.requestFocus();

                        }
                    });
                    alertBuilder.show();
                } else if (rdg_duration.getCheckedRadioButtonId() == -1) {
                    alertBuilder.setMessage(getResources().getString(R.string.dialog_durationMessage));
                    alertBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cv_duration.setFocusable(true);
                            cv_duration.setFocusableInTouchMode(true);
                            cv_duration.requestFocus();
                        }
                    });
                    alertBuilder.show();
                } else if (rd_reminderdaytime.isChecked() && selectedDayTimes.size() == 0) {
                    alertBuilder.setMessage(getResources().getString(R.string.dialog_reminderDaytimeMessage));
                    alertBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cv_reminder.setFocusable(true);
                            cv_reminder.setFocusableInTouchMode(true);
                            cv_reminder.requestFocus();
                        }
                    });
                    alertBuilder.show();
                } else if (rd_reminderdaytime.isChecked() && txt_foodInstruction == null ) {
                    alertBuilder.setMessage(getResources().getString(R.string.dialog_foodInstructionMessage));
                    alertBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cv_foodInstruction.setFocusableInTouchMode(true);
                            cv_foodInstruction.setFocusable(true);
                            cv_foodInstruction.requestFocus();
                        }
                    });
                    alertBuilder.show();
                } else {
                    saveDataToDB();
                    alertBuilder.setMessage(getResources().getString(R.string.dialog_Medisave));
                    alertBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            FragmentOverview fragmentOverview = new FragmentOverview();
                            ((MainActivity) getActivity()).getFab().show();
                            ((MainActivity) getActivity()).getNavigationView().setCheckedItem(R.id.nav_list);
                            ((MainActivity) getActivity()).getNavigationView().getMenu().getItem(0).setChecked(true);
                            ((MainActivity) getActivity()).setTitle("Übersicht");
                            FragmentManager fragmentManager = getFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.main, fragmentOverview, "Fragment_Overview").commit();
                        }
                    });
                    alertBuilder.setCancelable(false);
                    alertBuilder.show();

                }
            }
        });
    }

    public void deleteItem(){
        Button btn_delete = (Button)root.findViewById(R.id.btn_delete);
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity(), R.style.DialogTheme);
                alertBuilder.setTitle(getResources().getString(R.string.title_dialogDelete));
                alertBuilder.setPositiveButton(getResources().getString(R.string.yes), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity(), "Medikament wurde gelöscht", Toast.LENGTH_SHORT).show();
                    }
                });
                alertBuilder.setNegativeButton(getResources().getString(R.string.no), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
                alertBuilder.setCancelable(false);
                alertBuilder.show();
            }
        });
        //DeleteMediService.deleteAllEntryByTableAndMedId()
    }

    private void saveDataToDB(){
        RadioButton rd_reminderDayTime = (RadioButton)root.findViewById(R.id.rd_daytime);
        Data data = new Data();
        PillDetection pillDetection = new PillDetection(getPicture(),getPicture().getWidth(),getPicture().getHeight());
        data.setDescription(getName());
        data.setPicture(getPicture());
        if (rd_reminderInterval.isChecked()){
            data.setAllConsumeInterval(getReminderInterval());
        } else if (rd_reminderDayTime.isChecked()) {
            data.setAllConsumeIndividual(getReminderDayTime());
        }
        try {
            data.setDuration(getDuration(pillDetection.getAllPillPoints(mediId).size()));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        data.setAmount(getDosage());
        data.setNote(getNotes());
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        data.setCreateDate(cal);
        try {
            data.setAllPillCoords(pillDetection.getAllPillPoints(data.getId()));
            data.setId(CreateMediService.addNewMedi(data, dbAdapter));
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

    private void registrationInit(){
        if (((MainActivity)getActivity()).getCurrentMenuItem() == R.id.nav_edit){
            Bundle bundle = getArguments();
            mediId = bundle.getInt("mediId");
            loadData(String.valueOf(mediId));
        }

    }

    public void loadData(String id){
        pillData = Data.getDataById(id,dbAdapter);
        setName(pillData.getDescription());
        setPicture(pillData.getPicture());
        /**
         * toDo Id??
         */
        //int interval = 0;
        if (pillData.getAllConsumeIndividual().size() != 0){
            setReminderRadioButton(R.id.rd_daytime);
            Collection<ConsumeIndividual> consumeIndividuals;
            consumeIndividuals = ConsumeIndividual.getAllConsumeIndividualByMedid(mediId, dbAdapter);
            for (ConsumeIndividual consume: consumeIndividuals){
                setCheckItems(consume.getDaypart().getId(),true);
                setDaytimeText();
                //setReminderDayTime(consume.getDaypart());
                setFoodInstruction(consume.getEatpart().getId());
            }

        } else {
            setReminderRadioButton(R.id.rd_interval);
            Collection<ConsumeInterval> consumeInterval;
            consumeInterval = ConsumeInterval.getAllConsumeIntervalByMedid(mediId, dbAdapter);
            for (ConsumeInterval consume: consumeInterval){
                if (!consume.getStartTime().equals(consume.getEndTime())){
                    spinnerIntervalPosition = 0;
                    setSelectedIntervalString(getResources().getString(R.string.hour));
                    setTime(getStartTimeCalendar(), consume.getStartTime().HOUR_OF_DAY, consume.getStartTime().MINUTE);
                    setTime(getEndTimeCalendar(), consume.getEndTime().HOUR_OF_DAY, consume.getEndTime().MINUTE);
                    setStartTimeButtonText();
                    setEndTimeButtonText();
                    setIntervalHourText();
                    //consume.getStartTime().HOUR_OF_DAY;
                    //setReminderStartTime(consume.getStartTime());
                } if (consume.getWeekday() != 0){
                    spinnerIntervalPosition = 2;
                    setSelectedIntervalString(getResources().getString(R.string.week));
                    setWeekday(consume.getWeekday());
                    setTime(intervalCalendar, consume.getStartTime().HOUR_OF_DAY, consume.getStartTime().MINUTE);
                    setReminderIntervalText();
                } else {
                    spinnerIntervalPosition = 1;
                    setSelectedIntervalString(getResources().getString(R.string.day));
                    setTime(intervalCalendar, consume.getStartTime().HOUR_OF_DAY, consume.getStartTime().MINUTE);
                    setReminderIntervalText();
                }

                /**
                 * toDo consume.getInterval always returns 1
                 */
                setNpReminderValue(consume.getInterval());
                setSpinnerReminderValue(spinnerIntervalPosition);
                /*setSpinnerReminderValue(0);
                setTime(getStartTimeCalendar(), 15, 23);
                setTime(getEndTimeCalendar(), 20, 50);*/
            }
        }



        //ConsumeInterval.getAllConsumeIntervalByMedid(id,dbAdapter);
        //Log.i("consumeIndividual", String.valueOf(pillData.getAllConsumeIndividual()));
        //pillData.getAllConsumeIndividual();
        //;

        /**
         * 0: beschränkt, -1 : kontinuierlich, 1: Packung Ende if (entry == dosage * numBlister)
         */
        if (pillData.getDuration() == 0){
            setDurationRd(0,pillData);
            setDurationDate(2016, 8, 17);
            setDateText();
        } else if (pillData.getDuration() == -1){
            setDurationRd(1,pillData);
        } else if (pillData.getDuration() == 0){
            setDurationRd(2,pillData);
            setNumberOfBlistersValue(1);
            setNumberOfBlisterText();
        }
        setDosage(pillData.getAmount());
        setNotes(pillData.getNote());
    }


}
