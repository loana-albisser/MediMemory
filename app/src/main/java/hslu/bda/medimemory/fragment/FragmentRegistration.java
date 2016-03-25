package hslu.bda.medimemory.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
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
import java.util.List;

import android.app.Activity;
import android.provider.MediaStore.MediaColumns;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
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
import hslu.bda.medimemory.services.CreateMediService;

/**
 * Created by Loana on 29.02.2016.
 */
public class FragmentRegistration extends Fragment {
    private ViewGroup root;
    private EditText edit_name;
    private final int REQUEST_CAMERA = 0;
    private final int SELECT_FILE = 1;
    private ImageView iv_image;
    private String imagePath;

    private TextView txt_reminder;
    private View dialogView;
    private int selectedIntervalPosition;
    private final boolean [] checkItems = {false,false,false,false,false};
    private ArrayList<Integer> selList = new ArrayList<Integer>();
    private Spinner sp_reminderInterval;
    private int numberofCheckedItems;
    private RadioButton rd_reminderInterval;
    private TimePickerDialog tp_startEndTimeInterval;
    private StringBuilder daytimebuilder;
    private CharSequence[] daytimes;
    private SimpleDateFormat startTime;
    private Date startTimeDate;
    private Calendar startTimeCalendar;
    private Calendar endTimeCalendar;
    private List<String> weekdays;
    private int weekday;
    private String weekdayString;
    private NumberPicker np_reminderInterval;
    private StringBuilder intervalbuilder;
    private int selectedValue = 1;
    private String selectedInterval;
    private CardView cv_foodInstruction;
    private RadioGroup rdg_duration;
    private TextView txt_duration = null;
    private StringBuilder numberOfBlisterString;
    private final Calendar dateCalendar;
    private int selectedYear;
    private int selectedMonth;
    private int selectedDay;
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
    //private RadioButton rd_foodInstruction;
    private int selectedFoodInstruction;
    private int foodid;


    public FragmentRegistration() {
        dateCalendar = Calendar.getInstance();
        selectedYear = dateCalendar.get(Calendar.YEAR);
        selectedMonth = dateCalendar.get(Calendar.MONTH);
        selectedDay = dateCalendar.get(Calendar.DAY_OF_MONTH);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.fragment_registration, container, false);
        dbAdapter= new DbAdapter(getActivity().getApplicationContext());
        dbAdapter.open();
        setupShowImage();
        showReminderDetails();
        showDuration();
        setupDosageNumberPicker();
        setDosage();
        showFoodInstruction();
        save();
        showDeleteButtonVisibility();
        return root;
    }

    @Override
    public void onStop(){
        dbAdapter.close();
        super.onStop();
    }




    public void showDeleteButtonVisibility(){
        Button btn_delete = (Button)root.findViewById(R.id.btn_delete);
        if (((MainActivity)getActivity()).getCurrentMenuItem() == R.id.nav_registration){
            btn_delete.setVisibility(View.GONE);
        } else if (((MainActivity)getActivity()).getCurrentMenuItem() == R.id.nav_edit){
            btn_delete.setVisibility(View.VISIBLE);
        }
    }

    private String getName(){
        edit_name = (EditText) root.findViewById(R.id.edit_name);
        Log.i("Name", String.valueOf(edit_name.getText()));
        return String.valueOf(edit_name.getText());
    }

    public void setName(String name){
        edit_name.setText(name);
    }

    private void setupShowImage() {
        Button btn_select = (Button) root.findViewById(R.id.btn_SelectPhoto);
        btn_select.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        iv_image = (ImageView) root.findViewById(R.id.iv_Image);

    }

    private void selectImage() {
        final CharSequence[] items = { getResources().getString(R.string.d_selectPhoto), getResources().getString(R.string.d_chooseLibrary),
                getResources().getString(R.string.cancel) };

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setTitle(getResources().getString(R.string.title_photo));
        builder.setItems(items, new DialogInterface.OnClickListener() {
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
        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE) {
                onSelectFromGalleryResult(data);
            } else if (requestCode == REQUEST_CAMERA){
                onCaptureImageResult(data);
            }
            getPicturePath();
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        assert thumbnail != null;
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(), System.currentTimeMillis() + ".jpg");
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
        iv_image.setImageBitmap(thumbnail);
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

        iv_image.setImageBitmap(bm);
    }

    private String getPicturePath(){
        return imagePath;
    }

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
        AlertDialog.Builder reminderDaytimeDialog = new AlertDialog.Builder(getActivity());
        daytimebuilder.append(getResources().getString(R.string.taking)).append(" ");
        reminderDaytimeDialog.setCancelable(false);
        reminderDaytimeDialog.setTitle(getResources().getString(R.string.title_reminderDaytime));
        reminderDaytimeDialog.setMultiChoiceItems(daytimes, checkItems, new DialogInterface.OnMultiChoiceClickListener() {
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
                }
        );
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

    private Collection<Day> getDaytimes(){
        return selectedDayTimes;
    }

    private int countDayTime(){
        return selectedDayTimes.size();
    }

    public void showReminderIntervalDialog(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
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
                    txt_reminder.setText(intervalbuilder);
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
                    np_reminderInterval.setMaxValue(20);
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

    private void showWeekdayDialog(){
        weekdayString = getResources().getString(R.string.monday);
        weekdays = Arrays.asList(getResources().getStringArray(R.array.array_weekday));
        AlertDialog.Builder dialogWeekday = new AlertDialog.Builder(getActivity());
        dialogWeekday.setCancelable(false);
        dialogWeekday.setTitle(getResources().getString(R.string.title_dialogWeekday));
        dialogWeekday.setSingleChoiceItems(R.array.array_weekday, 0, new DialogInterface.OnClickListener() {
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

        });
        dialogWeekday.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                intervalbuilder.append(" ").append(getResources().getString(R.string.on)).append(" ").append(weekdayString);
                txt_reminder.setText(intervalbuilder);
            }
        });
        Dialog d = dialogWeekday.create();
        d.show();
    }

    private int getWeekday(){
        return weekday;
    }

    private void setWeekday(){

    }

    private void showStartEndTimeDialog(){
        startTimeCalendar = Calendar.getInstance();
        endTimeCalendar = Calendar.getInstance();
        startTimeString = "00:00";
        endTimeString = "23:59";
        final AlertDialog.Builder dialogStartEndTime = new AlertDialog.Builder(getActivity());
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
                tp_startEndTimeInterval = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
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
                tp_startEndTimeInterval = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
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
        NumberPicker np_reminderInterval = (NumberPicker)root.findViewById(R.id.np_reminderInterval);
        return np_reminderInterval.getValue();
    }

    private int getInterval(){
        Spinner sp_reminderInterval = (Spinner)root.findViewById(R.id.sp_reminderInterval);
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
            consumeIndividual.setDaypart(Iterables.get(getDaytimes(), day.getId()));
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
        Log.i("Selected Date", String.valueOf(dateCalendar.getTimeInMillis()));
        return dateCalendar.getTimeInMillis();

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
        RadioButton rd_packageEnd = (RadioButton)root.findViewById(R.id.rd_packageEnd);
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

    public void setCurrentNumberOfBlistersValue(){
        np_numberofBlisters.setValue(numberOfBlisters);
    }

    public void showDateDialog(){
        DatePickerDialog dpd = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                StringBuilder numDaysString = new StringBuilder();
                dateCalendar.set(Calendar.YEAR, year);
                dateCalendar.set(Calendar.MONTH, monthOfYear);
                dateCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                endDate = new SimpleDateFormat("dd.MM.yyyy");
                String dateString = endDate.format(dateCalendar.getTime());
                monthOfYear = monthOfYear+1;
                numDaysString.append(getResources().getString(R.string.taking)).append(" ")
                        .append(getResources().getString(R.string.till)).append(" ").append(dateString);
                txt_duration.setText(numDaysString);
                selectedYear = year;
                selectedMonth = monthOfYear-1;
                selectedDay = dayOfMonth;
            }
        }, selectedYear, selectedMonth, selectedDay);
        dpd.setButton(DialogInterface.BUTTON_NEGATIVE, null, dpd);
        dpd.show();
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
        AlertDialog.Builder npb_numberofBlisters = new AlertDialog.Builder(getActivity());
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
        RadioGroup rdg_foodInstruction = new RadioGroup(getActivity().getApplicationContext());

       for (Eat eat : allFoodInstructions) {
           rd_foodinstruction[eat.getId()] = new RadioButton(getActivity().getApplicationContext());
           rdg_foodInstruction.addView(rd_foodinstruction[eat.getId()]);
           rd_foodinstruction[eat.getId()].setText(eat.getDescription());
           rd_foodinstruction[eat.getId()].setTextColor(Color.BLACK);
           //rd_foodinstruction[eat.getId()].setSupportButtonTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorAccent)));
        }
        ln_foodInstruction.addView(rdg_foodInstruction);
        showInfoTextField(txt_foodInstruction, ln_foodInstruction);
        rdg_foodInstruction.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                txt_foodInstruction.setVisibility(View.VISIBLE);
                eatString = new StringBuilder();
                selectedFoodInstruction = checkedId;
                if (checkedId == rd_foodinstruction[0].getId()) {
                    eatString.append(getResources().getString(R.string.txt_eatBefore));
                } else if (checkedId == rd_foodinstruction[1].getId()) {
                    eatString.append(getResources().getString(R.string.txt_eatAfter));
                } else if (checkedId == rd_foodinstruction[2].getId()) {
                    eatString.append(getResources().getString(R.string.txt_eatDuring));
                }
                txt_foodInstruction.setText(eatString);
            }
        });
    }

    private Eat getFoodInstruction(){
        return Iterables.get(allFoodInstructions, selectedFoodInstruction);
    }

    private void setFoodInstruction(){
        rd_foodinstruction[selectedFoodInstruction].isChecked();
    }

    private String getNotes(){
        edit_notes = (EditText)root.findViewById(R.id.edit_notes);
        return String.valueOf(edit_notes.getText());
    }

    private void setNotes(String text){
        edit_notes.setText(text);
    }

    private void save(){
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
        final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
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
                } else if (getPicturePath() ==null){
                    alertBuilder.setMessage(getResources().getString(R.string.dialog_photoMessage));
                    alertBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cv_photo.requestFocus();
                            cv_photo.setFocusableInTouchMode(true);
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
                } else if (rd_reminderdaytime.isChecked() && numberofCheckedItems == 0) {
                    alertBuilder.setMessage(getResources().getString(R.string.dialog_reminderDaytimeMessage));
                    alertBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cv_reminder.setFocusable(true);
                            cv_reminder.requestFocus();
                        }
                    });
                    alertBuilder.show();
                } else if (rd_reminderdaytime.isChecked() && rdg_foodInstruction.getCheckedRadioButtonId() == -1) {
                    alertBuilder.setMessage(getResources().getString(R.string.dialog_foodInstructionMessage));
                    alertBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            cv_foodInstruction.requestFocus();
                            cv_foodInstruction.setFocusableInTouchMode(true);
                        }
                    });
                    alertBuilder.show();
                } else {
                    saveDatatoDB();
                    Toast.makeText(getActivity(), getResources().getString(R.string.dialog_Medisave), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void saveDatatoDB(){
        RadioButton rd_reminderDayTime = (RadioButton)root.findViewById(R.id.rd_daytime);
        Data data = new Data();
        data.setDescription(getName());
        data.setPicture(getPicturePath());
        if (rd_reminderInterval.isChecked()){
            data.setAllConsumeIndividual(getReminderDayTime());

        } else if (rd_reminderDayTime.isChecked()){
            data.setAllConsumeInterval(getReminderInterval());
        }
        data.setDuration(getDuration());
        data.setAmount(getDosage());
        data.setNote(getNotes());
        try {
            CreateMediService.addNewMedi(data, dbAdapter);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }


}
