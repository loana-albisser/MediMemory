package hslu.bda.medimemory.fragment.registration;

import android.annotation.TargetApi;
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
import android.graphics.drawable.BitmapDrawable;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
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
import hslu.bda.medimemory.fragment.edit.FragmentEdit;
import hslu.bda.medimemory.fragment.overview.FragmentOverview;
import hslu.bda.medimemory.services.CreateMediService;
import hslu.bda.medimemory.services.DeleteMediService;

/**
 * Created by Loana on 29.02.2016.
 */
public class FragmentRegistration extends Fragment {
    private ViewGroup root;

    private EditText edit_name;
    private ImageView iv_selectedImage;
    private Bitmap thumbnail;

    private TextView txt_reminder;
    private View dialogReminderView;
    private View dialogNumberpickerView;
    private int selectedIntervalPosition;
    private boolean [] checkedDaytimes;
    private int checkedWeek;
    private ArrayList<Integer> selList = new ArrayList<>();
    private Spinner sp_reminderInterval;
    private AlertDialog.Builder reminderDaytimeDialog;
    private RadioButton rd_reminderInterval;
    private TimePickerDialog tp_startEndTimeInterval;
    private StringBuilder daytimebuilder;
    private CharSequence[] daytimes;
    private SimpleDateFormat startTime;
    private Calendar startTimeCalendar;
    private Calendar endTimeCalendar;
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
    private String selectedInterval;
    private CardView cv_foodInstruction;
    private RadioGroup rdg_duration;
    private TextView txt_duration = null;
    private StringBuilder numberOfBlisterString;
    private Calendar dateCalendarDuration;
    private int numberOfBlisters;
    private StringBuilder dosageString;

    private TextView txt_dosage = null;
    private NumberPicker np_dosage;

    private TextView txt_foodInstruction = null;
    private RadioButton[] rd_foodinstruction;
    private DbAdapter dbAdapter;


    private SimpleDateFormat endDate;

    private EditText edit_notes;
    private String startTimeString;
    private String endTimeString;
    private RadioButton rd_reminderdaytime;

    private Collection<Day> allDayTimes;
    private Collection<Day> selectedDayTimes = new ArrayList<>();
    private Collection<Eat> allFoodInstructions;
    private Collection<ConsumeInterval> consumeIntervals = new ArrayList<>();
    private Collection<ConsumeIndividual> consumeIndividuals = new ArrayList<>();
    private StringBuilder eatString;
    private int selectedFoodInstruction;
    private ImageButton iBtn_helpPhoto;
    private ImageButton iBtn_helpReminder;
    private ImageButton iBtn_helpDuration;
    private ImageButton iBtn_helpFoodInstruction;
    private TextView txt_helpText;

    private int mediId;
    private Data pillData;
    private int spinnerIntervalPosition;
    private int intervalNpValue;
    private ViewGroup ln_dosage;
    private int dosage;
    private StringBuilder numDaysString;
    private StringBuilder durationString;
    private ArrayList<String> strDayTimes;
    private NumberPicker np_numberOfBlisters;
    private AlertDialog.Builder dialogBuilderBlisters;
    private AlertDialog.Builder saveAlertMessage;
    private RadioButton rd_numberOfDays;
    private RadioButton rd_always;
    private RadioButton rd_packageEnd;
    private LayoutInflater inflater;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.fragment_registration, container, false);
        super.onCreateView(inflater, container, savedInstanceState);
        dbAdapter= new DbAdapter(getActivity().getApplicationContext());
        dbAdapter.open();

        init();
        registrationInit();

        saveItem();
        deleteItem();

        setDeleteButtonVisibility();
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

    /**
     * sets all inital values
     */
    private void init(){
        setupShowImage();
        setupReminderDaytimes();
        setupReminderIntervalTimes();
        setupDuration();
        setupDosage();
        showFoodInstruction();
        setupHelptextVisibilty();
    }

    private void setupReminderDaytimes(){
        setOnReminderDayTimeRadioButtonClickEvent();
        daytimebuilder = new StringBuilder();
        allDayTimes = Day.getAllDayValues(dbAdapter);
        strDayTimes = new ArrayList<>();
        for(Day day:allDayTimes){
            strDayTimes.add(day.getDescription());
        }
        daytimes = strDayTimes.toArray(new CharSequence[allDayTimes.size()]);
        checkedDaytimes = new boolean[allDayTimes.size()];
        for (Day day : allDayTimes){
            setCheckDayItems(day.getId(), false);
        }
    }

    private void setupReminderIntervalTimes(){
        intervalbuilder = new StringBuilder();
        LayoutInflater inflater = getActivity().getLayoutInflater();
        dialogReminderView = inflater.inflate(R.layout.dialog_reminderinterval, null);
        sp_reminderInterval = (Spinner) dialogReminderView.findViewById(R.id.sp_reminderInterval);
        weekdays = Arrays.asList(getResources().getStringArray(R.array.array_weekday));
        setOnReminderIntervalRadioButtonClickEvent();
        showReminderDetails();
        setupReminderIntervalHour();
        setSelectedIntervalPosition(0);
        setIntervalNpValue(1);
        setCheckedWeekItem(Calendar.MONDAY);
    }

    private void setupReminderIntervalHour(){
        LayoutInflater inflaterStartEnd = getActivity().getLayoutInflater();
        dialogViewStartEnd = inflaterStartEnd.inflate(R.layout.dialog_reminderstartendtime, null);
        btn_starttime = (Button)dialogViewStartEnd.findViewById(R.id.btn_starttime);
        btn_endtime = (Button)dialogViewStartEnd.findViewById(R.id.btn_endtime);
        startTimeCalendar = Calendar.getInstance();
        endTimeCalendar = Calendar.getInstance();
        setTime(startTimeCalendar, 0, 0);
        setTime(endTimeCalendar, 23, 59);
        //setReminderTime(getStartTimeCalendar(), 0, 0);
        //setReminderTime(getEndTimeCalendar(), 23, 59);
    }

    private void setupDuration(){
        dateCalendarDuration = Calendar.getInstance();
        inflater = getActivity().getLayoutInflater();
        dialogNumberpickerView = inflater.inflate(R.layout.dialog_numberofblisters, null);
        np_numberOfBlisters = (NumberPicker)dialogNumberpickerView.findViewById(R.id.np_numberOfBlisters);
        showDuration();
        setOnDurationDateRadioButtonClickEvent();
        setOnDurationNumOfBlistersRadioButtonClick();
        changeNumberOfBlisterValue();
        setDurationDate(dateCalendarDuration);
        setNumberOfBlistersValue(1);
    }

    private void setupDosage(){
        ln_dosage = (ViewGroup) root.findViewById(R.id.ln_dosage);
        txt_dosage = new TextView(getActivity());
        txt_dosage.setText(getResources().getString(R.string.txt_dosageInitial));
        dosageString = new StringBuilder();
        setupDosageNumberPicker();
        setDosage(1);
    }

    /**
     * set the help Button, if set in Settings
     */
    private void setupHelptextVisibilty(){
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

    private String getName(){
        edit_name = (EditText) root.findViewById(R.id.edit_name);
        return String.valueOf(edit_name.getText());
    }

    private void setName(String name){
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

    private static Bitmap getImageFromResult(Context context, int resultCode, Intent imageReturnedIntent) {
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

    private static int getRotation(Context context, Uri imageUri, boolean isCamera) {
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

    private Intent getPickImageIntent(Context context) {
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

    /**
     * get the selected picture in right orientation
     * @return selected picture
     */
    private Bitmap getPicture(){
        if (thumbnail != null){
            if (thumbnail.getWidth() > thumbnail.getHeight()){
                Matrix matrix = new Matrix();
                matrix.postRotate(90);
                thumbnail = Bitmap.createBitmap(thumbnail, 0, 0, thumbnail.getWidth(), thumbnail.getHeight(), matrix, true);
            }
        }
        return thumbnail;
    }

    private boolean isImageSet(){
        Log.i("Imageview", String.valueOf(iv_selectedImage.getDrawable()));
        if (iv_selectedImage.getDrawable()==null){
            return false;
        } else {
            return true;
        }
    }

    private void setPicture(Bitmap bitmap){
        iv_selectedImage.setImageBitmap(bitmap);
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

    /**
     * setup the dialog to show the daytimes (morning, noon, afternoon, night)
     */
    public void showReminderDaytimeDialog(){
        reminderDaytimeDialog = new AlertDialog.Builder(getActivity(),R.style.DialogTheme);
        reminderDaytimeDialog.setCancelable(false);
        reminderDaytimeDialog.setTitle(getResources().getString(R.string.title_reminderDaytime));
        reminderDaytimeDialog.setMultiChoiceItems(daytimes, getCheckedDaytimes(), dayTimeListener);
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

    DialogInterface.OnMultiChoiceClickListener dayTimeListener = new DialogInterface.OnMultiChoiceClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
            if (isChecked) {
                selList.add(which);
                checkedDaytimes[which] = true;
            } else if (selList.contains(which)) {
                selList.remove(which);
                checkedDaytimes[which] = false;
            }
        }
    };

    /**
     * sets the daytimes to the textview field
     */
    private void setDaytimeText(){
        daytimebuilder.setLength(0);
        daytimebuilder.append(getResources().getString(R.string.taking)).append(" ");
        for (int id : selList) {
            selectedDayTimes.add(Iterables.get(allDayTimes, id));
            daytimebuilder.append(daytimes[id]).append(" ");
        }
        if (selectedDayTimes.size() > 0) {
            txt_reminder.setText(daytimebuilder);
        }
    }

    private Collection<Day> getDaytimes(){
        return selectedDayTimes;
    }

    /**
     *
     * @return all selected daytimes from dialog
     */
    private int countDayTime(){
        return selectedDayTimes.size();
    }

    /**
     * sets all daytimes to true if they need to be checked
     * @param dayId id of the daytime
     * @param isChecked true if checked
     */
    private void setCheckDayItems(int dayId, boolean isChecked){
        checkedDaytimes[dayId] = isChecked;
        if (isChecked){
            selList.add(dayId);
        }
    }

    private boolean[] getCheckedDaytimes(){
        return checkedDaytimes;
    }

    /**
     * sets all daytimes values to database
     * @return a Collection that contains all ConsumeIndividual data
     */
    private Collection<ConsumeIndividual> getReminderDayTime(){
        for (Day day : getDaytimes()){
            ConsumeIndividual consumeIndividual = new ConsumeIndividual();
            consumeIndividual.setEatpart(getFoodInstruction());
            consumeIndividual.setDaypart(Iterables.get(allDayTimes, day.getId()));
            consumeIndividuals.add(consumeIndividual);
        }
        return consumeIndividuals;
    }

    /**
     * shows reminderinterval dialog
     */
    public void showReminderIntervalDialog(){
        final AlertDialog.Builder dialogBuilderReminder = new AlertDialog.Builder(getActivity(),R.style.DialogTheme);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        dialogReminderView = inflater.inflate(R.layout.dialog_reminderinterval, null);
        sp_reminderInterval = (Spinner) dialogReminderView.findViewById(R.id.sp_reminderInterval);
        dialogBuilderReminder.setView(dialogReminderView);
        dialogBuilderReminder.setTitle(getResources().getString(R.string.title_intervalDialog));
        setupReminderIntervalNumberPicker();
        setReminderIntervalTimes(getIntervalNpValue(), getSelectedIntervalPosition());
        intervalbuilder.setLength(0);
        dialogBuilderReminder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (getSelectedIntervalPosition() == 0) {
                    showStartEndTimeDialog();
                } else if (getSelectedIntervalPosition() == 1) {
                    showIntervalTimePickerDialog();
                } else if (getSelectedIntervalPosition() == 2) {
                    showWeekdayDialog();
                }
                setReminderIntervalText();
                setSelectedIntervalPosition(getSelectedIntervalPosition());
            }
        });
        dialogBuilderReminder.setCancelable(false);
        AlertDialog b = dialogBuilderReminder.create();
        b.show();
    }

    private void setupReminderIntervalNumberPicker(){
        np_reminderInterval = (NumberPicker) dialogReminderView.findViewById(R.id.np_reminderInterval);
        np_reminderInterval.setMinValue(1);
    }

    /**
     *
     * @param selectedNumber the previous selected interval (1 if none)
     * @param selectedIntervalPosition the previous selected spinnter item (0 if none)
     */
    private void setReminderIntervalTimes(final int selectedNumber, final int selectedIntervalPosition){
        setIntervalNpValue(selectedNumber);
        setSelectedIntervalPosition(selectedIntervalPosition);
        np_reminderInterval.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                setIntervalNpValue(newVal);
            }
        });
        sp_reminderInterval.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                setSelectedIntervalString(parent.getItemAtPosition(position).toString());
                if (getSelectedIntervalString().equals(getResources().getString(R.string.hour))) {
                    np_reminderInterval.setMaxValue(23);
                    setSelectedIntervalPosition(0);
                    np_reminderInterval.setValue(getIntervalNpValue());
                } else if (getSelectedIntervalString().equals(getResources().getString(R.string.day))) {
                    np_reminderInterval.setMaxValue(31);
                    setSelectedIntervalPosition(1);
                    np_reminderInterval.setValue(getIntervalNpValue());
                } else if (getSelectedIntervalString().equals(getResources().getString(R.string.week))) {
                    np_reminderInterval.setMaxValue(60);
                    setSelectedIntervalPosition(2);
                    np_reminderInterval.setValue(getIntervalNpValue());
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                setSelectedIntervalPosition(getSelectedIntervalPosition());
            }

        });
    }

    /**
     *
     * @param selectedIntervalPosition position in the spinner
     */
    private void setSelectedIntervalPosition(int selectedIntervalPosition){
        this.selectedIntervalPosition = selectedIntervalPosition;
        sp_reminderInterval.setSelection(selectedIntervalPosition);
    }

    private int getSelectedIntervalPosition(){
        return  selectedIntervalPosition;
    }

    /**
     *
     * @param intervalNpValue value of the interval numberpicker
     */
    private void setIntervalNpValue(int intervalNpValue){
        this.intervalNpValue = intervalNpValue;
    }

    private int getIntervalNpValue(){
        return intervalNpValue;
    }

    /**
     * sets the text to the texview field, according to selected spinner item and numberpicker value
     */
    private void setReminderIntervalText(){
        intervalbuilder.append(getResources().getString(R.string.taking)).append(" ");
        if (getIntervalNpValue() == 1) {
            if (getSelectedIntervalString().equals(getResources().getString(R.string.hour))) {
                intervalbuilder.append(getResources().getString(R.string.everyHour));
            } else if (getSelectedIntervalString().equals(getResources().getString(R.string.day))) {
                intervalbuilder.append(getResources().getString(R.string.everyDay));
            } else if (getSelectedIntervalString().equals(getResources().getString(R.string.week))) {
                intervalbuilder.append(getResources().getString(R.string.everyWeek));
            }
        } else {
            intervalbuilder.append(getResources().getString(R.string.every)).append(" ");
            intervalbuilder.append(getIntervalNpValue()).append(" ");
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

    private String getSelectedIntervalString(){
        return selectedInterval;
    }

    private void setSelectedIntervalString (String selectedInterval){
        this.selectedInterval = selectedInterval;
    }

    /**
     * shows the dialog, if spinner item hour is checked to set starttime and endtime
     */
    private void showStartEndTimeDialog(){
        startTimeString = "00:00";
        endTimeString = "23:59";
        final AlertDialog.Builder dialogStartEndTime = new AlertDialog.Builder(getActivity(),R.style.DialogTheme);
        LayoutInflater inflaterStartEnd = getActivity().getLayoutInflater();
        dialogViewStartEnd = inflaterStartEnd.inflate(R.layout.dialog_reminderstartendtime, null);
        btn_starttime = (Button)dialogViewStartEnd.findViewById(R.id.btn_starttime);
        btn_endtime = (Button)dialogViewStartEnd.findViewById(R.id.btn_endtime);
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
                } else if (getStartTimeCalendar().getTimeInMillis() > getEndTimeCalendar().getTimeInMillis()) {
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

    private void setTime(Calendar calendar, int hour, int minute){
        calendar.set(Calendar.HOUR_OF_DAY, hour);
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

    /**
     * adds selected time to textview field
     */
    private void setIntervalHourText(){
        intervalbuilder.append(" ").append(getResources().getString(R.string.from)).append(" ").append(startTimeString).append(" ")
                .append(getResources().getString(R.string.till)).append(" ").append(endTimeString);
        txt_reminder.setText(intervalbuilder);
    }

    /**
     * sets the selected time to the starttimebutton in dialog
     */
    private void setStartTimeButtonText(){
        final StringBuilder startString = new StringBuilder();
        startTime = new SimpleDateFormat("HH:mm");
        startTimeString = startTime.format(getStartTimeCalendar().getTime());
        startString.append(startTimeString);
        btn_starttime.setText(startString);
    }

    /**
     * sets the selected time to the endtimebutton in dialog
     */
    private void setEndTimeButtonText(){
        final StringBuilder endString = new StringBuilder();
        SimpleDateFormat endTime = new SimpleDateFormat("HH:mm");
        endTimeString = endTime.format(getEndTimeCalendar().getTime());
        endString.append(endTimeString);
        btn_endtime.setText(endString);
    }

    /**
     * show dialog if selected end- or starttime is not correct
     * @param message a alertmessage
     */
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

    /**
     * shows dialog if interval day or week is selected
     */
    private void showIntervalTimePickerDialog() {
        tpd_interval = new TimePickerDialog(getActivity(),R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                startTimeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                startTimeCalendar.set(Calendar.MINUTE, minute);
                setIntervalTimeText();
            }
        },getTime(startTimeCalendar,Calendar.HOUR_OF_DAY) , getTime(startTimeCalendar, Calendar.MINUTE), true);
        tpd_interval.setButton(DialogInterface.BUTTON_NEGATIVE, null, tpd_interval);
        tpd_interval.setTitle(getResources().getString(R.string.title_chooseTime));
        tpd_interval.show();
    }

    /**
     * sets selected time to textview field
     */
    private void setIntervalTimeText(){
        intervalTime = new SimpleDateFormat("HH:mm");
        intervalTimeString = intervalTime.format(startTimeCalendar.getTime());
        intervalbuilder.append(" ").append(getResources().getString(R.string.at)).append(" ").append(intervalTimeString);
        txt_reminder.setText(intervalbuilder);
    }

    /**
     * shows dialog if interval spinner item week is selected
     */
    private void showWeekdayDialog(){
        dialogWeekday = new AlertDialog.Builder(getActivity(),R.style.DialogTheme);
        dialogWeekday.setTitle(getResources().getString(R.string.title_dialogWeekday));
        dialogWeekday.setSingleChoiceItems(R.array.array_weekday, getCheckedWeekItem(), dialogWeekListener);
        dialogWeekday.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setReminderWeekdayText();
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
            if (which == 0) {
                setWeekday(Calendar.MONDAY);
                setWeekdayString(getResources().getString(R.string.monday));
            } else if (which == 1) {
                setWeekday(Calendar.TUESDAY);
                setWeekdayString(getResources().getString(R.string.tuesday));
            } else if (which == 2) {
                setWeekday(Calendar.WEDNESDAY);
                setWeekdayString(getResources().getString(R.string.wednesday));
            } else if (which == 3) {
                setWeekday(Calendar.THURSDAY);
                setWeekdayString(getResources().getString(R.string.thursday));
            } else if (which == 4) {
                setWeekday(Calendar.FRIDAY);
                setWeekdayString(getResources().getString(R.string.friday));
            } else if (which == 5) {
                setWeekday(Calendar.SATURDAY);
                setWeekdayString(getResources().getString(R.string.saturday));
            } else if (which == 6) {
                setWeekday(Calendar.SUNDAY);
                setWeekdayString(getResources().getString(R.string.sunday));
            }
            setCheckedWeekItem(getWeekday());
        }
    };

    private void setCheckedWeekItem(int checkedWeek){
        this.checkedWeek = checkedWeek;
    }

    /**
     * get the converted checked week item
     * @return the checked week
     */
    private int getCheckedWeekItem(){
        if (checkedWeek == Calendar.MONDAY) {this.checkedWeek = 0;}
        else if (checkedWeek == Calendar.TUESDAY) {this.checkedWeek = 1;}
        else if (checkedWeek == Calendar.WEDNESDAY) {this.checkedWeek = 2;}
        else if (checkedWeek == Calendar.THURSDAY) {this.checkedWeek = 3;}
        else if (checkedWeek == Calendar.FRIDAY) {this.checkedWeek = 4;}
        else if (checkedWeek == Calendar.SATURDAY) {this.checkedWeek = 5;}
        else if (checkedWeek == Calendar.SUNDAY) {this.checkedWeek = 6;}
        return checkedWeek;
    }

    private void setWeekday(int weekday){
        this.weekday = weekday;
    }

    private int getWeekday(){
        return weekday;
    }

    /**
     * sets the selected week to the textview field
     */
    private void setReminderWeekdayText(){
        intervalbuilder.append(" ").append(getResources().getString(R.string.on)).append(" ").append(getWeekdayString());
    }

    private void setWeekdayString(String weekdayString){
        this.weekdayString = weekdayString;
    }

    private String getWeekdayString(){
        return weekdayString;
    }

    private void setWeekdayStringById(int weekday){
        if (weekday == Calendar.MONDAY) {
            setWeekdayString(getResources().getString(R.string.monday));
        } else if (weekday == Calendar.TUESDAY) {
            setWeekdayString(getResources().getString(R.string.tuesday));
        } else if (weekday == Calendar.WEDNESDAY) {
            setWeekdayString(getResources().getString(R.string.wednesday));
        } else if (weekday == Calendar.THURSDAY) {
            setWeekdayString(getResources().getString(R.string.thursday));
        } else if (weekday == Calendar.FRIDAY) {
            setWeekdayString(getResources().getString(R.string.friday));
        } else if (weekday == Calendar.SATURDAY) {
            setWeekdayString(getResources().getString(R.string.saturday));
        } else if (weekday == Calendar.SUNDAY) {
            setWeekdayString(getResources().getString(R.string.sunday));
        }
    }

    /**
     * saves all selected interval data to database
     * @return consumeinterval data
     */
    private Collection<ConsumeInterval> getReminderInterval(){
        ConsumeInterval consumeInterval = new ConsumeInterval();
        consumeInterval.setStartTime(getStartTimeCalendar());
        if (getSelectedIntervalPosition()==0){
            consumeInterval.setEndTime(getEndTimeCalendar());
        } else {
            consumeInterval.setEndTime(getStartTimeCalendar());
        }
        consumeInterval.setInterval(getIntervalNpValue());
        consumeInterval.setWeekday(getWeekday());
        consumeIntervals.add(consumeInterval);
        return consumeIntervals;
    }

    private void showDuration() {
        rdg_duration = (RadioGroup) root.findViewById(R.id.rdg_duration);
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

    /**
     * shows the dialog to select a specific date
     */
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

    /**
     * sets the selected date to the textview field
     */
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

    private void setDurationDate(Calendar calendar){
        dateCalendarDuration.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
        dateCalendarDuration.set(Calendar.MONTH, calendar.get(Calendar.MONTH));
        dateCalendarDuration.set(Calendar.DAY_OF_MONTH, calendar.get(Calendar.DAY_OF_MONTH));
    }

    private int getDurationDate(int time){
        return dateCalendarDuration.get(time);
    }

    private Calendar getDurationDate(){
        return dateCalendarDuration;
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

    private void setDurationAlwaysText(){
        durationString.append(getResources().getString(R.string.txt_durationAlways));
        txt_duration.setText(durationString);
    }

    public void showNumberOfBlistersNumberPickerDialog(){
        dialogBuilderBlisters = new AlertDialog.Builder(getActivity(),R.style.DialogTheme);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        dialogNumberpickerView = inflater.inflate(R.layout.dialog_numberofblisters, null);
        np_numberOfBlisters = (NumberPicker)dialogNumberpickerView.findViewById(R.id.np_numberOfBlisters);
        dialogBuilderBlisters.setView(dialogNumberpickerView);
        dialogBuilderBlisters.setTitle(getResources().getString(R.string.d_packagEnd));
        dialogBuilderBlisters.setCancelable(false);
        np_numberOfBlisters.setMaxValue(20);
        np_numberOfBlisters.setMinValue(1);
        setNumberOfBlistersValue(numberOfBlisters);
        dialogBuilderBlisters.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setNumberOfBlisterText(getNumberOfBlistersValue());
                setNumberOfBlistersValue(getNumberOfBlistersValue());
            }
        });
        final Dialog dialog = dialogBuilderBlisters.create();
        dialog.show();
    }

    public void changeNumberOfBlisterValue() {
        np_numberOfBlisters.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                numberOfBlisters = newVal;
            }
        });
    }

    private void setNumberOfBlistersValue(int numberOfBlisters){
        this.numberOfBlisters = numberOfBlisters;
        np_numberOfBlisters.setValue(this.numberOfBlisters);
    }

    private int getNumberOfBlistersValue(){
        return np_numberOfBlisters.getValue();
    }

    /**
     * sets the selected number of blisters to the textview
     * @param numberOfBlisters the number of blisters
     */
    private void setNumberOfBlisterText(int numberOfBlisters){
        numberOfBlisterString = new StringBuilder();
        numberOfBlisterString.append(getResources().getString(R.string.taking)).append(" ");
        numberOfBlisterString.append(numberOfBlisters).append(" ");
        numberOfBlisterString.append(getResources().getString(R.string.blister));
        txt_duration.setText(numberOfBlisterString);
    }

    /**
     * calculates the number of intakes, according to the differnt choices
     * @param pillsPerBlister number of pills per blister
     * @return number of intakes
     */
    private int calculateNumberOfIntakes(int pillsPerBlister){
        int numberOfIntakes = -1;
        rd_numberOfDays = (RadioButton)root.findViewById(R.id.rd_numberOfDays);
        rd_always = (RadioButton)root.findViewById(R.id.rd_always);
        rd_packageEnd = (RadioButton)root.findViewById(R.id.rd_durationPackageEnd);
        if (rd_numberOfDays.isChecked()){
            if (rd_reminderInterval.isChecked()){
                if (getSelectedIntervalPosition() ==0){
                    numberOfIntakes = (int) ((getTimeInUnit(2,getStartTimeCalendar(),getEndTimeCalendar())/ getIntervalNpValue()) * getTimeInUnit(3,getStartTimeCalendar(),getEndTimeCalendar()));
                } else if (getSelectedIntervalPosition() == 1){
                    numberOfIntakes = (int) (getTimeInUnit(3,getStartTimeCalendar(),getEndTimeCalendar()) * getIntervalNpValue());
                } else if (getSelectedIntervalPosition() == 2){
                    numberOfIntakes = (int) ((getTimeInUnit(3,getStartTimeCalendar(),getEndTimeCalendar())/7) * getIntervalNpValue());
                }
            } else if (rd_reminderdaytime.isChecked()){
                numberOfIntakes = (int) (getTimeInUnit(3,getStartTimeCalendar(),getEndTimeCalendar()) * countDayTime());
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

    /**
     * returns time in given unit
     * @param unit 0:second , 1:minute, 2:hours, 3:minute
     * @return time
     */
    private long getTimeInUnit(int unit, Calendar startTimeCalendar, Calendar endTimeCalendar){
        long difference = endTimeCalendar.getTimeInMillis()-startTimeCalendar.getTimeInMillis();
        long x = difference / 1000;
        long time = 0;
        if (unit == 0){
            time = x % 60;
        } else if (unit == 1){
            x /= 60;
            time = x % 60;
        } else if (unit ==2){
            x = x/60/60;
            time = x % 24;
        } else if (unit == 3){
            x = x/60/60/24;
            time = x;
        }
        return time;
    }

    /**
     * calculates how many blisters a package contains
     * @param numberOfIntakes number of intakes
     * @param dosage number of pills per intake
     * @param pills number of pills per blister
     * @return the number of blisters
     */
    private int calcNumberOfBlisters(int numberOfIntakes, int dosage, int pills ){
        int numOfBlister = (numberOfIntakes*dosage)/pills;
        return numOfBlister;
    }

   private void setupDosageNumberPicker(){
        np_dosage = (NumberPicker)root.findViewById(R.id.np_dosage);
        np_dosage.setMinValue(1);
        np_dosage.setMaxValue(20);
        np_dosage.setValue(1);
    }

    private void setDosage(int value){
        dosage = value;
        np_dosage.setValue(dosage);
        np_dosage.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                dosage = newVal;
                setDosageText();
            }
        });
        showInfoTextField(txt_dosage, ln_dosage);
    }

    private int getDosage(){
        return np_dosage.getValue();
    }

    /**
     * set the selected dosage to textview field
     */
    private void setDosageText(){
        dosageString.setLength(0);
        dosageString.append(getResources().getString(R.string.txt_dosage)).append(" ");
        dosageString.append(getDosage()).append(" ");
        if (getDosage() == 1) {
            dosageString.append(getResources().getString(R.string.txt_dosageOneTab));
        } else {
            dosageString.append(getResources().getString(R.string.txt_dosageMoreTab));
        }
        txt_dosage.setText(dosageString);
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

    /**
     * shows specific helptext to different topics
     */
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
    private void showAlertDialog (String message, final CardView cardView){
        saveAlertMessage.setMessage(message);
        saveAlertMessage.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                cardView.setFocusable(true);
                cardView.setFocusableInTouchMode(true);
                cardView.requestFocus();
            }
        });
        saveAlertMessage.show();
    }

    /**
     * checks if there are no mistakes in registration to save item
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
        saveAlertMessage = new AlertDialog.Builder(getActivity(),R.style.DialogTheme);
        btn_save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit_name.getText().toString().trim().length() == 0) {
                    showAlertDialog(getResources().getString(R.string.dialog_nameMessage), cv_name);
                } else if (!isImageSet()) {
                    showAlertDialog(getResources().getString(R.string.dialog_photoMessage), cv_photo);
                } else if (rdg_reminder.getCheckedRadioButtonId() == -1) {
                    showAlertDialog(getResources().getString(R.string.dialog_reminderMessage), cv_reminder);
                } else if (rdg_duration.getCheckedRadioButtonId() == -1) {
                    showAlertDialog(getResources().getString(R.string.dialog_durationMessage), cv_duration);
                } else if (rd_reminderdaytime.isChecked() && selectedDayTimes.size() == 0) {
                    showAlertDialog(getResources().getString(R.string.dialog_reminderDaytimeMessage), cv_reminder);
                } else if (rd_reminderdaytime.isChecked() && txt_foodInstruction == null) {
                    showAlertDialog(getResources().getString(R.string.dialog_foodInstructionMessage), cv_foodInstruction);
                } else {
                    if (((MainActivity) getActivity()).getCurrentMenuItem() == R.id.nav_registration) {
                        saveDataToDB();
                        saveAlertMessage.setMessage(getResources().getString(R.string.dialog_Medisave));
                        saveAlertMessage.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showOverviewFragment();
                                //FragmentOverviewPagerAdapter fragmentOverviewPagerAdapter = new FragmentOverviewPagerAdapter(fragmentManager,getActivity());
                                //fragmentOverview.getViewPager().setCurrentItem(fragmentOverviewPagerAdapter.getCount());
                            }
                        });
                        saveAlertMessage.setCancelable(false);
                        saveAlertMessage.show();
                    } else {
                        //updateMedi();
                        saveAlertMessage.setMessage(getResources().getString(R.string.dialog_MediUpdate));
                        saveAlertMessage.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showEditFragment();
                            }
                        });
                        saveAlertMessage.setCancelable(false);
                        saveAlertMessage.show();
                    }

                }
            }
        });
    }

    private void showOverviewFragment(){
        FragmentOverview fragmentOverview = new FragmentOverview();
        ((MainActivity) getActivity()).getFab().show();
        ((MainActivity) getActivity()).getNavigationView().setCheckedItem(R.id.nav_list);
        ((MainActivity) getActivity()).getNavigationView().getMenu().getItem(0).setChecked(true);
        getActivity().setTitle(getResources().getString(R.string.nav_list));
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main, fragmentOverview, "Fragment_Overview").commit();
    }

    private void showEditFragment(){
        FragmentEdit fragmentEdit = new FragmentEdit();
        ((MainActivity) getActivity()).getFab().show();
        ((MainActivity) getActivity()).getNavigationView().setCheckedItem(R.id.nav_edit);
        ((MainActivity) getActivity()).getNavigationView().getMenu().getItem(2).setChecked(true);
        getActivity().setTitle(getResources().getString(R.string.nav_edit));
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.main, fragmentEdit, "Fragment_Edit").commit();
    }

    /**
     * save the entered registration data to db
     */
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
            data.setDuration(calculateNumberOfIntakes(pillDetection.getAllPillPoints(mediId).size()));
            if (rd_numberOfDays.isChecked()){
                data.setEndDate(getDurationDate());
            } else if (rd_always.isChecked() || rd_packageEnd.isChecked()){
                data.setEndDate(null);
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        data.setAmount(getDosage());
        data.setNote(getNotes());
        data.setActive(1);
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

    /**
     * load the selected medi from edit fragment in registration fragment
     * @param id the mediId
     */
    public void loadData(String id){
        pillData = Data.getDataById(id,dbAdapter);
        setName(pillData.getDescription());
        setPicture(pillData.getPicture());
        if (pillData.getAllConsumeIndividual().size() != 0){
            setReminderRadioButton(R.id.rd_daytime);
            Collection<ConsumeIndividual> consumeIndividuals;
            consumeIndividuals = ConsumeIndividual.getAllConsumeIndividualByMedid(mediId, dbAdapter);
            for (ConsumeIndividual consume: consumeIndividuals){
                setCheckDayItems(consume.getDaypart().getId(), true);
                setDaytimeText();
                setFoodInstruction(consume.getEatpart().getId());
            }
        } else {
            setReminderRadioButton(R.id.rd_interval);
            Collection<ConsumeInterval> consumeInterval;
            consumeInterval = ConsumeInterval.getAllConsumeIntervalByMedid(mediId, dbAdapter);
            for (ConsumeInterval consume: consumeInterval){
                setIntervalNpValue(consume.getInterval());
                setTime(getStartTimeCalendar(), getTime(consume.getStartTime(), Calendar.HOUR_OF_DAY), getTime(consume.getStartTime(), Calendar.MINUTE));
                if (getTime(consume.getStartTime(),Calendar.HOUR_OF_DAY)!=getTime(consume.getEndTime(),Calendar.HOUR_OF_DAY)){
                    spinnerIntervalPosition = 0;
                    setSelectedIntervalString(getResources().getString(R.string.hour));
                    setTime(getEndTimeCalendar(), getTime(consume.getEndTime(), Calendar.HOUR_OF_DAY), getTime(consume.getEndTime(), Calendar.MINUTE));
                    setStartTimeButtonText();
                    setEndTimeButtonText();
                    setReminderIntervalText();
                    setIntervalHourText();
                } else if (consume.getWeekday() != 0){
                    spinnerIntervalPosition = 2;
                    setSelectedIntervalString(getResources().getString(R.string.week));
                    setCheckedWeekItem(consume.getWeekday());
                    setReminderIntervalText();
                    setWeekdayStringById(consume.getWeekday());
                    setReminderWeekdayText();
                    setIntervalTimeText();
                } else {
                    spinnerIntervalPosition = 1;
                    setSelectedIntervalString(getResources().getString(R.string.day));
                    setReminderIntervalText();
                    setIntervalTimeText();
                }
                setSelectedIntervalPosition(spinnerIntervalPosition);
            }
        }

        if (pillData.getEndDate()!=null){
            setDurationRadioButton(0);
            setDurationDate(pillData.getEndDate());
            setDateText();
        } else if (pillData.getDuration() == -1){
            setDurationRadioButton(1);
        } else {
            setDurationRadioButton(2);
            setNumberOfBlistersValue(calcNumberOfBlisters(pillData.getDuration(), pillData.getAmount(), pillData.getAllPillCoords().size() ));
            setNumberOfBlisterText(calcNumberOfBlisters(pillData.getDuration(), pillData.getAmount(), pillData.getAllPillCoords().size() ));
        }
        setDosage(pillData.getAmount());
        setDosageText();
        setNotes(pillData.getNote());
    }

    private void setReminderRadioButton(int checkedRadioButton){
        RadioGroup rdg_reminder = (RadioGroup)root.findViewById(R.id.rdg_reminder);
        rdg_reminder.check(checkedRadioButton);
    }

    private void setDurationRadioButton(int duration){
        if (duration == 0){
            rdg_duration.check(R.id.rd_numberOfDays);
        } else if (duration == 1){
            rdg_duration.check(R.id.rd_always);
        } else if (duration == 2){
            rdg_duration.check(R.id.rd_durationPackageEnd);
        }
    }

    /**
     * delete a item
     */
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
                        Toast.makeText(getActivity(), getResources().getString(R.string.pillDeleted), Toast.LENGTH_SHORT).show();
                        try {
                            DeleteMediService.deleteDbObject(pillData, dbAdapter);
                        } catch (Throwable throwable) {
                            throwable.printStackTrace();
                        }
                        showEditFragment();
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
    }




}
