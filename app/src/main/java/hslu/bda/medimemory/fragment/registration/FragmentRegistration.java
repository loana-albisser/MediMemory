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
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import android.app.Activity;
import android.provider.MediaStore.MediaColumns;
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

import hslu.bda.medimemory.R;
import hslu.bda.medimemory.database.DbAdapter;
import hslu.bda.medimemory.entity.ConsumeIndividual;
import hslu.bda.medimemory.entity.ConsumeInterval;
import hslu.bda.medimemory.entity.Data;
import hslu.bda.medimemory.entity.Day;
import hslu.bda.medimemory.entity.Eat;
import hslu.bda.medimemory.fragment.MainActivity;
import hslu.bda.medimemory.fragment.overview.FragmentOverview;
import hslu.bda.medimemory.services.CreateMediService;

/**
 * Created by Loana on 29.02.2016.
 */
public class FragmentRegistration extends Fragment {
    private ViewGroup root;
    private Context context;
    private FragmentRegistration fragmentRegistration;
    private Activity mActivity;

    private EditText edit_name;
    private final int REQUEST_CAMERA = 0;
    private final int SELECT_FILE = 1;
    private ImageView iv_selectedImage;
    private String imagePath;
    private Bitmap thumbnail;

    private TextView txt_reminder;
    private View dialogView;
    private int selectedIntervalPosition;
    private final boolean [] checkItems = {false,false,false,false,false};
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
    private final Calendar dateCalendarDuration;
    private int selectedYearDuration;
    private int selectedMonthDuration;
    private int selectedDayDuration;
    private NumberPicker np_numberofBlisters;
    private int numberOfBlisters = 1;

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


    public FragmentRegistration() {
        dateCalendarDuration = Calendar.getInstance();
        selectedYearDuration = dateCalendarDuration.get(Calendar.YEAR);
        selectedMonthDuration = dateCalendarDuration.get(Calendar.MONTH);
        selectedDayDuration = dateCalendarDuration.get(Calendar.DAY_OF_MONTH);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.fragment_registration, container, false);
        super.onCreateView(inflater, container, savedInstanceState);

        dbAdapter= new DbAdapter(getActivity().getApplicationContext());
        dbAdapter.open();

        setupShowImage();
        showReminderDetails();
        setOnReminderDayTimeRadioButtonClickEvent();
        setOnReminderIntervalRadioButtonClickEvent();
        showDuration();
        setOnDurationDateRadioButtonClickEvent();
        setOnDurationNumOfBlistersRadioButtonClick();
        setupDosageNumberPicker();
        setDosage();
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
                changeNumberOfBlisterTextField();
                showNumberOfBlistersNumberPickerDialog();
                setCurrentNumberOfBlistersValue();
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
        edit_name.setText(name);
    }

    private void setupShowImage() {
        Button btn_selectPhoto = (Button) root.findViewById(R.id.btn_selectPhoto);
        btn_selectPhoto.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        iv_selectedImage = (ImageView) root.findViewById(R.id.iv_selectedImage);

    }

    /**
     * setup dialog to choose between take photo and choose photo from gallery
     */
    private void selectImage() {
        final CharSequence[] items = { getResources().getString(R.string.d_selectPhoto), getResources().getString(R.string.d_chooseLibrary),
                getResources().getString(R.string.cancel) };

        android.support.v7.app.AlertDialog.Builder builder_selectImage = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder_selectImage.setTitle(getResources().getString(R.string.dialog_title_photo));
        builder_selectImage.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals(getResources().getString(R.string.d_selectPhoto))) {
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(intent, REQUEST_CAMERA);
                } else if (items[item].equals(getResources().getString(R.string.d_chooseLibrary))) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult(
                            Intent.createChooser(intent, getResources().getString(R.string.d_selectFile)),
                            SELECT_FILE);
                } else if (items[item].equals(getResources().getString(R.string.cancel))) {
                    dialog.dismiss();
                }
            }
        });
        builder_selectImage.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                onSelectFromGalleryResult(data);
            } else if (requestCode == REQUEST_CAMERA){
                onCaptureImageResult(data);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);

    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    private void onCaptureImageResult(Intent data) {
        LinearLayout ln_photo = (LinearLayout)root.findViewById(R.id.ln_photo);
        //ImageView iv_selectedImage = (ImageView)root.findViewById(R.id.iv_selectedImage);
        //ln_photo.getWidth();
        //ln_photo.getHeight();
        thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        assert thumbnail != null;
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        String filename = System.currentTimeMillis()+".jpg";
        File destination = new File(Environment.getRootDirectory(), filename);
        imagePath = destination.toString();
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        rotate(filename);
        //iv_selectedImage.setImageBitmap(thumbnail);
        //iv_selectedImage.setAdjustViewBounds(true);
        iv_selectedImage.setImageBitmap(getResizedBitmap(thumbnail, iv_selectedImage.getWidth(), 450                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                      ));

    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);
        matrix.postRotate(90);
        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    private void getOrientation(){

    }

    private void rotate(String filename){
        Matrix matrix = new Matrix();

        ExifInterface exifReader = null;
        try {
            exifReader = new ExifInterface(filename);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int orientation = exifReader.getAttributeInt(ExifInterface.TAG_ORIENTATION, -1);

        if (orientation ==ExifInterface.ORIENTATION_NORMAL) {

        // Do nothing. The original image is fine.
        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
            matrix.postRotate(90);

        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {

            matrix.postRotate(180);

        } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {

            matrix.postRotate(270);

        }

        else if(orientation == 0){
            matrix.postRotate(90);
        }
    }

    private void onSelectFromGalleryResult(Intent data) {
        Uri selectedImageUri = data.getData();
        String[] projection = { MediaColumns.DATA };
        Cursor cursor = getActivity().getContentResolver().query(selectedImageUri, projection, null, null, null);
        assert cursor != null;
        int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
        cursor.moveToFirst();
        imagePath = cursor.getString(column_index);
        cursor.close();
        Bitmap bm;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imagePath, options);
        final int REQUIRED_SIZE = 200;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(imagePath, options);

        iv_selectedImage.setImageBitmap(bm);
        thumbnail = bm;
    }




    private Bitmap getPicture(){
        return thumbnail;
    }

    public void setPicture(Bitmap picture){
        ImageView iv_selectedImage = (ImageView)root.findViewById(R.id.iv_selectedImage);
        iv_selectedImage.setImageBitmap(picture);
    }

    /**
     * shows textView with selected information
     * @param textView selected textView
     * @param viewGroup current view
     */
    private void showInfoTextField(TextView textView, ViewGroup viewGroup) {
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

    public void showReminderDaytimeDialog(){
        allDayTimes = Day.getAllDayValues(dbAdapter);
        ArrayList<String> strDayTimes = new ArrayList<String>();
        for(Day day:allDayTimes){
            strDayTimes.add(day.getDescription());
        }
        daytimes = strDayTimes.toArray(new CharSequence[allDayTimes.size()]);
        daytimebuilder = new StringBuilder();
        reminderDaytimeDialog = new AlertDialog.Builder(getActivity(),R.style.DialogTheme);
        daytimebuilder.append(getResources().getString(R.string.taking)).append(" ");
        reminderDaytimeDialog.setCancelable(false);
        reminderDaytimeDialog.setTitle(getResources().getString(R.string.title_reminderDaytime));
        reminderDaytimeDialog.setMultiChoiceItems(daytimes, checkItems, dayTimeListener);
        reminderDaytimeDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                for (int id : selList) {
                    selectedDayTimes.add(Iterables.get(allDayTimes, id));
                    daytimebuilder.append(daytimes[id]).append(" ");
                }
                if (selectedDayTimes.size() > 0) {
                    txt_reminder.setText(daytimebuilder);
                }
            }
        });
        Dialog dialog = reminderDaytimeDialog.create();
        dialog.show();
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

    public void showReminderIntervalDialog(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity(),R.style.DialogTheme);
        LayoutInflater inflater = getActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog_reminderinterval, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle(getResources().getString(R.string.title_intervalDialog));
        setupReminderIntervalNumberPicker();
        getReminderIntervalTimes();
        intervalbuilder = new StringBuilder();
        intervalbuilder.append(getResources().getString(R.string.taking)).append(" ");
        dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (selectedValue == 1) {
                    if (selectedInterval.equals(getResources().getString(R.string.hour))) {
                        intervalbuilder.append(getResources().getString(R.string.everyHour));
                    } else if (selectedInterval.equals(getResources().getString(R.string.day))) {
                        intervalbuilder.append(getResources().getString(R.string.everyDay));
                    } else if (selectedInterval.equals(getResources().getString(R.string.week))) {
                        intervalbuilder.append(getResources().getString(R.string.everyWeek));
                    }
                } else {
                    intervalbuilder.append(getResources().getString(R.string.every)).append(" ");
                    intervalbuilder.append(selectedValue).append(" ");
                    if (selectedInterval.equals(getResources().getString(R.string.hour))) {
                        intervalbuilder.append(getResources().getString(R.string.hourMult));
                    } else if (selectedInterval.equals(getResources().getString(R.string.day))) {
                        intervalbuilder.append(getResources().getString(R.string.dayMult));
                    } else if (selectedInterval.equals(getResources().getString(R.string.week))) {
                        intervalbuilder.append(getResources().getString(R.string.weekMult));
                    }

                }
                txt_reminder.setText(intervalbuilder);
                if (sp_reminderInterval.getSelectedItemPosition() == 0) {
                    showStartEndTimeDialog();
                } else if (sp_reminderInterval.getSelectedItemPosition() == 1){
                    showIntervalTimePickerDialog();

                } else if (sp_reminderInterval.getSelectedItemPosition() == 2) {
                    showWeekdayDialog();
                }
            }
        });
        sp_reminderInterval.setSelection(selectedIntervalPosition);
        AlertDialog b = dialogBuilder.create();
        b.show();
    }


    private void setupReminderIntervalNumberPicker(){
        np_reminderInterval = (NumberPicker)dialogView.findViewById(R.id.np_reminderInterval);
        np_reminderInterval.setMinValue(1);
    }

    private void getReminderIntervalTimes(){
        sp_reminderInterval = (Spinner) dialogView.findViewById(R.id.sp_reminderInterval);

        np_reminderInterval.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                selectedValue = newVal;
            }
        });
        sp_reminderInterval.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedIntervalPosition = (int) parent.getItemIdAtPosition(position);
                selectedInterval = parent.getItemAtPosition(position).toString();
                if (selectedInterval.equals(getResources().getString(R.string.hour))) {
                    np_reminderInterval.setMaxValue(23);
                    np_reminderInterval.setValue(selectedValue);
                } else if (selectedInterval.equals(getResources().getString(R.string.day))) {
                    np_reminderInterval.setMaxValue(31);
                    np_reminderInterval.setValue(selectedValue);
                } else if (selectedInterval.equals(getResources().getString(R.string.week))) {
                    np_reminderInterval.setMaxValue(60);
                    np_reminderInterval.setValue(selectedValue);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedInterval = getResources().getString(R.string.hour);
            }
        });
    }

    private void showIntervalTimePickerDialog() {
        intervalCalendar = Calendar.getInstance();
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
        dialogWeekday.setCancelable(false);

        dialogWeekday.setTitle(getResources().getString(R.string.title_dialogWeekday));
        dialogWeekday.setSingleChoiceItems(R.array.array_weekday, 0, dialogWeekListener);
        dialogWeekday.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                intervalbuilder.append(" ").append(getResources().getString(R.string.on)).append(" ").append(weekdayString);
                showIntervalTimePickerDialog();
            }
        });
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

    public void setReminderDayTime(boolean [] checkedItems){
        reminderDaytimeDialog.setMultiChoiceItems(daytimes, checkedItems, dayTimeListener);
    }

    public void setReminderInterval(int value, int position){
        NumberPicker np_reminderInterval = (NumberPicker)root.findViewById(R.id.np_reminderInterval);
        np_reminderInterval.setValue(value);
        Spinner sp_reminderInterval = (Spinner)root.findViewById(R.id.sp_reminderInterval);
        sp_reminderInterval.setSelection(position);
    }

    public void setReminderStartTime(int hour, int minute){
        if (sp_reminderInterval.getSelectedItemPosition() ==0){
            startTimeCalendar.set(Calendar.HOUR_OF_DAY,hour);
            startTimeCalendar.set(Calendar.MINUTE,minute);
        } else if (sp_reminderInterval.getSelectedItemPosition() ==1 || sp_reminderInterval.getSelectedItemPosition() ==2){
            intervalCalendar.set(Calendar.HOUR_OF_DAY,hour);
            intervalCalendar.set(Calendar.MINUTE,minute);
        }
    }

    public void setReminderEndTime(int hour, int minute){
        if (sp_reminderInterval.getSelectedItemPosition() ==0){
            endTimeCalendar.set(Calendar.HOUR_OF_DAY,hour);
            endTimeCalendar.set(Calendar.MINUTE,minute);
        } else if (sp_reminderInterval.getSelectedItemPosition() ==1 || sp_reminderInterval.getSelectedItemPosition() ==2){
            intervalCalendar.set(Calendar.HOUR_OF_DAY,hour);
            intervalCalendar.set(Calendar.MINUTE,minute);
        }
    }

    public void setWeekday(int checkedItems){
        dialogWeekday.setSingleChoiceItems(R.array.array_weekday, checkedItems, dialogWeekListener);

    }

    private void showStartEndTimeDialog(){
        startTimeCalendar = Calendar.getInstance();
        endTimeCalendar = Calendar.getInstance();
        startTimeString = "00:00";
        endTimeString = "23:59";
        final AlertDialog.Builder dialogStartEndTime = new AlertDialog.Builder(getActivity(),R.style.DialogTheme);
        LayoutInflater inflaterStartEnd = getActivity().getLayoutInflater();
        View dialogViewStartEnd = inflaterStartEnd.inflate(R.layout.dialog_reminderstartendtime, null);
        final Button btn_starttime = (Button)dialogViewStartEnd.findViewById(R.id.btn_starttime);
        final Button btn_endtime = (Button)dialogViewStartEnd.findViewById(R.id.btn_endtime);
        dialogStartEndTime.setView(dialogViewStartEnd);
        dialogStartEndTime.setTitle("Start/ Endzeit festlegen");
        btn_starttime.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final StringBuilder startString = new StringBuilder();
                tp_startEndTimeInterval = new TimePickerDialog(getActivity(),R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        startTimeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        startTimeCalendar.set(Calendar.MINUTE, minute);
                        startTime = new SimpleDateFormat("HH:mm");
                        startTimeDate = startTimeCalendar.getTime();
                        startTimeString = startTime.format(startTimeCalendar.getTime());
                        startString.append(startTimeString);
                        btn_starttime.setText(startString);
                    }
                }, 0, 0, true);
                tp_startEndTimeInterval.setButton(DialogInterface.BUTTON_NEGATIVE, null, tp_startEndTimeInterval);
                showTimeDialog(getResources().getString(R.string.title_chooseStartTime));
            }
        });
        btn_endtime.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final StringBuilder endString = new StringBuilder();
                tp_startEndTimeInterval = new TimePickerDialog(getActivity(),R.style.DialogTheme, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        endTimeCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        endTimeCalendar.set(Calendar.MINUTE, minute);
                        SimpleDateFormat endTime = new SimpleDateFormat("HH:mm");
                        endTimeString = endTime.format(endTimeCalendar.getTime());
                        endString.append(endTimeString);
                        btn_endtime.setText(endString);
                    }
                }, 23, 59, true);
                tp_startEndTimeInterval.setButton(DialogInterface.BUTTON_NEGATIVE, null, tp_startEndTimeInterval);
                showTimeDialog(getResources().getString(R.string.title_chooseEndTime));
            }
        });
        dialogStartEndTime.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                intervalbuilder.append(" ").append(getResources().getString(R.string.from)).append(" ").append(startTimeString).append(" ")
                        .append(getResources().getString(R.string.till)).append(" ").append(endTimeString);
                txt_reminder.setText(intervalbuilder);
            }
        });

        Dialog d = dialogStartEndTime.create();
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

    private Calendar getStartTime(){
        return startTimeCalendar;
    }

    private Calendar getEndTime(){
        return endTimeCalendar;
    }

    private long getPeriodOfTime(){
        return startTimeCalendar.getTimeInMillis() - endTimeCalendar.getTimeInMillis();
    }

    private Collection<ConsumeInterval> getReminderInterval(){
        ConsumeInterval consumeInterval = new ConsumeInterval();
        consumeInterval.setStartTime(getStartTime());
        consumeInterval.setEndTime(getEndTime());
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
                StringBuilder durationString = new StringBuilder();
                txt_duration.setVisibility(View.VISIBLE);
                if (checkedId == R.id.rd_always) {
                    durationString.append(getResources().getString(R.string.txt_durationAlways));
                    txt_duration.setText(durationString);
                }
            }
        });
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

    private int getDuration(){
        int numberOfIntakes = -1;
        RadioButton rd_numberOfDays = (RadioButton)root.findViewById(R.id.rd_numberOfDays);
        RadioButton rd_always = (RadioButton)root.findViewById(R.id.rd_always);
        RadioButton rd_packageEnd = (RadioButton)root.findViewById(R.id.rd_durationPackageEnd);
        if (rd_numberOfDays.isChecked()){
            if (rd_reminderInterval.isChecked()){
                if (getInterval() ==0){
                    numberOfIntakes = (int) (getPeriodOfTime() * getIntervalValue());
                } else if (getInterval() == 1){
                    numberOfIntakes = numberOfDays() * getIntervalValue();
                } else if (getInterval() == 2){
                    numberOfIntakes = numberOfDays()/7 * getIntervalValue();
                }
            } else if (rd_reminderdaytime.isChecked()){
                numberOfIntakes = numberOfDays() * countDayTime();
            }
        } else if (rd_always.isChecked()){
            numberOfIntakes = -1;
        } else if (rd_packageEnd.isChecked()){
            int numberOFBlisters = np_numberofBlisters.getValue();
                numberOfIntakes = numberOFBlisters /  getDosage();
        }
        Log.i("Number Of Intakes", String.valueOf(numberOfIntakes));
        return numberOfIntakes;
    }

    public void setDuration(int checkedItem,int value,int year,int month, int day){
        rdg_duration.check(checkedItem);
        if (checkedItem==0){
            dateCalendarDuration.set(Calendar.YEAR, year);
            dateCalendarDuration.set(Calendar.MONTH,month);
            dateCalendarDuration.set(Calendar.DAY_OF_MONTH,day);
        } else if (checkedItem==2){
            np_numberofBlisters.setValue(value);
        }
    }

    public void setCurrentNumberOfBlistersValue(){
        np_numberofBlisters.setValue(numberOfBlisters);
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
                StringBuilder numDaysString = new StringBuilder();
                dateCalendarDuration.set(Calendar.YEAR, year);
                dateCalendarDuration.set(Calendar.MONTH, monthOfYear);
                dateCalendarDuration.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                endDate = new SimpleDateFormat("dd.MM.yyyy");
                String dateString = endDate.format(dateCalendarDuration.getTime());
                monthOfYear = monthOfYear+1;
                numDaysString.append(getResources().getString(R.string.taking)).append(" ")
                        .append(getResources().getString(R.string.till)).append(" ").append(dateString);
                txt_duration.setText(numDaysString);
                selectedYearDuration = year;
                selectedMonthDuration = monthOfYear-1;
                selectedDayDuration = dayOfMonth;
            }
        }, selectedYearDuration, selectedMonthDuration, selectedDayDuration);
        dpd.setButton(DialogInterface.BUTTON_NEGATIVE, null, dpd);
        dpd.show();
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
        np_numberofBlisters.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                numberOfBlisters = newVal;
            }
        });
    }

    public void showNumberOfBlistersNumberPickerDialog(){
        AlertDialog.Builder npb_numberofBlisters = new AlertDialog.Builder(getActivity(),R.style.DialogTheme);
        numberOfBlisterString = new StringBuilder();
        npb_numberofBlisters.setCancelable(false);
        np_numberofBlisters.setMaxValue(20);
        np_numberofBlisters.setMinValue(1);
        final FrameLayout parent = new FrameLayout(getActivity());
        parent.addView(np_numberofBlisters, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        npb_numberofBlisters.setView(parent);
        npb_numberofBlisters.setTitle(getResources().getString(R.string.d_packagEnd));
        npb_numberofBlisters.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                numberOfBlisterString.append(getResources().getString(R.string.taking)).append(" ");
                numberOfBlisterString.append(numberOfBlisters);
                txt_duration.setText(numberOfBlisterString);
            }
        });
        Dialog dialog = npb_numberofBlisters.create();
        dialog.show();
    }

    private void setDosage(){
        final ViewGroup ln_dosage = (ViewGroup) root.findViewById(R.id.ln_dosage);
        txt_dosage = new TextView(getActivity());
        txt_dosage.setText(getResources().getString(R.string.txt_dosageInitial));
        np_dosage.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                final StringBuilder dosageString = new StringBuilder();
                dosageString.append(getResources().getString(R.string.txt_dosage)).append(" ");
                dosageString.append(newVal).append(" ");
                if (newVal == 1) {
                    dosageString.append(getResources().getString(R.string.txt_dosageOneTab));
                } else {
                    dosageString.append(getResources().getString(R.string.txt_dosageMoreTab));
                }
                txt_dosage.setText(dosageString);
            }
        });
        showInfoTextField(txt_dosage, ln_dosage);
    }

    private int getDosage(){
        return np_dosage.getValue();
    }

    private void setDosage(int value){
        np_dosage.setValue(value);
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

    private void setNotes(String notes){
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
        final CardView cv_dosage = (CardView)root.findViewById(R.id.cv_dosage);
        final CardView cv_foodInstruction = (CardView)root.findViewById(R.id.cv_foodInstruction);
        final CardView cv_notes = (CardView)root.findViewById(R.id.cv_notes);
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
                            ((MainActivity) getActivity()).getNavigationView().setCheckedItem(R.id.nav_list);
                            ((MainActivity) getActivity()).getNavigationView().getMenu().getItem(0).setChecked(true);
                            ((MainActivity) getActivity()).setTitle("Übersicht");
                            //NavigationView nvDrawer = (NavigationView) root.findViewById(R.id.nav_view);
                            //nvDrawer.setCheckedItem(R.id.nav_list);
                            //nvDrawer.getMenu().getItem(0).setChecked(true);
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
                final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity(),R.style.DialogTheme);
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
                alertBuilder.show();
            }
        });
    }

    private void saveDataToDB(){
        RadioButton rd_reminderDayTime = (RadioButton)root.findViewById(R.id.rd_daytime);
        Data data = new Data();
        data.setDescription(getName());
        data.setPicture(getPicture());
        if (rd_reminderInterval.isChecked()){
            data.setAllConsumeInterval(getReminderInterval());
        } else if (rd_reminderDayTime.isChecked()) {
            data.setAllConsumeIndividual(getReminderDayTime());
        }
        data.setDuration(getDuration());
        data.setAmount(getDosage());
        data.setNote(getNotes());
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        data.setCreateDate(cal);
        try {
            CreateMediService.addNewMedi(data, dbAdapter);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }


}
