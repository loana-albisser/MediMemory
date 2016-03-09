package hslu.bda.medimemory.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
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

import hslu.bda.medimemory.R;

/**
 * Created by Loana on 29.02.2016.
 */
public class FragmentRegistration extends Fragment {
    private ViewGroup root;
    private final int REQUEST_CAMERA = 0;
    private final int SELECT_FILE = 1;
    private ImageView ivImage;
    private Button btn_select;

    private TextView txt_reminder;
    private View dialogView;
    private int selectedIntervalPosition;
    final boolean [] checkItems = {false,false,false,false};
    private AlertDialog.Builder reminderDaytimeDialog;
    private Spinner sp_reminderInterval;
    private TimePickerDialog tp_startEndTimeInterval;
    private int selectedHour;
    private int selectedMinute;
    private StringBuilder daytimebuilder;
    private List<String> daytimes;
    private NumberPicker np_reminderInterval;
    private StringBuilder intervalbuilder;
    private int selectedValue = 1;
    private String selectedInterval;

    private TextView txt_duration = null;
    private StringBuilder numDaysString;
    private StringBuilder numberOfBlisterString;
    final Calendar calendar;
    private int selectedYear;
    private int selectedMonth;
    private int selectedDay;
    private AlertDialog.Builder npb_numberofBlisters;
    private NumberPicker np_numberofBlisters;
    private int numberOfBlisters = 1;

    private TextView txt_dosage = null;
    private NumberPicker np_dosage;

    private TextView txt_foodInstruction = null;

    private FragmentRegistration mStartCommunicationListner;

    public FragmentRegistration() {
        calendar = Calendar.getInstance();
        selectedYear = calendar.get(Calendar.YEAR);
        selectedMonth = calendar.get(Calendar.MONTH);
        selectedDay = calendar.get(Calendar.DAY_OF_MONTH);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.fragment_registration, container, false);
        setupShowImage();
        setupDosageNumberPicker();
        setDuration();
        setDosage();
        setFoodInstruction();
        showReminderDetails();
        save();
        return root;
    }

    private void setupShowImage() {
        btn_select = (Button) root.findViewById(R.id.btn_SelectPhoto);
        btn_select.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        ivImage = (ImageView) root.findViewById(R.id.iv_Image);
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
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        assert thumbnail != null;
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");

        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        ivImage.setImageBitmap(thumbnail);
    }

    private void onSelectFromGalleryResult(Intent data) {
        Uri selectedImageUri = data.getData();
        String[] projection = { MediaColumns.DATA };
        Cursor cursor = getActivity().getContentResolver().query(selectedImageUri, projection, null, null, null);
        assert cursor != null;
        int column_index = cursor.getColumnIndexOrThrow(MediaColumns.DATA);
        cursor.moveToFirst();

        String selectedImagePath = cursor.getString(column_index);

        Bitmap bm;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(selectedImagePath, options);
        final int REQUIRED_SIZE = 200;
        int scale = 1;
        while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                && options.outHeight / scale / 2 >= REQUIRED_SIZE)
            scale *= 2;
        options.inSampleSize = scale;
        options.inJustDecodeBounds = false;
        bm = BitmapFactory.decodeFile(selectedImagePath, options);

        ivImage.setImageBitmap(bm);
    }

    public void showInfoTextField(TextView textView, ViewGroup viewGroup) {
        textView.setPadding(10, 10, 10, 10);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        viewGroup.addView(textView);
    }

    private void showReminderDetails(){
        RadioGroup rdg_reminder = (RadioGroup) root.findViewById(R.id.rdg_reminder);
        final LinearLayout ln_reminder = (LinearLayout)root.findViewById(R.id.ln_reminder);
        txt_reminder = new TextView(getActivity());
        txt_reminder.setVisibility(View.GONE);
        showInfoTextField(txt_reminder, ln_reminder);
        rdg_reminder.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                txt_reminder.setVisibility(View.VISIBLE);
            }
        });
    }

    public void showReminderDaytimeDialog(){
        daytimes = Arrays.asList(getResources().getStringArray(R.array.array_daytime));
        daytimebuilder = new StringBuilder();
        reminderDaytimeDialog = new AlertDialog.Builder(getActivity());
        daytimebuilder.append(getResources().getString(R.string.taking)).append(" ");
        reminderDaytimeDialog.setCancelable(false);
        reminderDaytimeDialog.setTitle("Um welche Tageszeit möchten sie das Medikament nehmen");
        reminderDaytimeDialog.setMultiChoiceItems(R.array.array_daytime, checkItems, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            if (which == daytimes.indexOf(getString(R.string.morning))) {
                                checkItems[which] = true;
                            }
                            if (which == daytimes.indexOf(getString(R.string.noon))) {
                                checkItems[which] = true;
                            }
                            if (which == daytimes.indexOf(getString(R.string.evening))) {
                                checkItems[which] = true;
                            }
                            if (which == daytimes.indexOf(getString(R.string.night))) {
                                checkItems[which] = true;
                            }
                        }
                    }
                }
        );
        reminderDaytimeDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int numberofCheckedItems = 0;
                for (int i = 0; i < checkItems.length; i++) {
                    if (checkItems[i] == true) {
                        numberofCheckedItems++;
                        daytimebuilder.append(daytimes.get(i)).append(" ");
                    }
                }
                if (numberofCheckedItems > 0) {
                    txt_reminder.setText(daytimebuilder);
                }
            }
        });
        Dialog dialog = reminderDaytimeDialog.create();
        dialog.show();
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
                    if (selectedInterval == getResources().getString(R.string.hour)) {
                        intervalbuilder.append(getResources().getString(R.string.everyHour));
                    } else if (selectedInterval == getResources().getString(R.string.day)) {
                        intervalbuilder.append(getResources().getString(R.string.day));
                    } else if (selectedInterval == getResources().getString(R.string.week)) {
                        intervalbuilder.append(getResources().getString(R.string.week));
                    }
                } else {
                    intervalbuilder.append(getResources().getString(R.string.every)).append(" ");
                    intervalbuilder.append(selectedValue).append(" ");
                    if (selectedInterval == getResources().getString(R.string.hour)) {
                        intervalbuilder.append(getResources().getString(R.string.hourMult));
                    } else if (selectedInterval == getResources().getString(R.string.day)) {
                        intervalbuilder.append(getResources().getString(R.string.dayMult));
                    } else if (selectedInterval == getResources().getString(R.string.week)) {
                        intervalbuilder.append(getResources().getString(R.string.weekMult));
                    }

                }
                txt_reminder.setText(intervalbuilder);
                if (sp_reminderInterval.getSelectedItemPosition() == 0) {
                    setStartEndTime();
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
                if (selectedInterval == getResources().getString(R.string.hour)) {
                    np_reminderInterval.setMaxValue(23);
                    np_reminderInterval.setValue(selectedValue);
                } else if (selectedInterval == getResources().getString(R.string.day)) {
                    np_reminderInterval.setMaxValue(20);
                    np_reminderInterval.setValue(selectedValue);
                } else if (selectedInterval == getResources().getString(R.string.week)) {
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

    private void setStartEndTime(){
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
                        startString.append(new DecimalFormat("00").format(hourOfDay)).append(":").append(new DecimalFormat("00").format(minute));
                        btn_starttime.setText(startString);
                    }
                }, 0, 0, true);
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
                        endString.append(new DecimalFormat("00").format(hourOfDay)).append(":").append(new DecimalFormat("00").format(minute));
                        btn_endtime.setText(endString);
                    }
                }, 23, 59, true);
                showTimeDialog(getResources().getString(R.string.title_chooseEndTime));
            }
        });
        dialogStartEndTime.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        Dialog d = dialogStartEndTime.create();
        d.show();
    }

    private void showTimeDialog(String title){
        final Calendar c = Calendar.getInstance();
        selectedHour = c.get(Calendar.HOUR_OF_DAY);
        selectedMinute = c.get(Calendar.MINUTE);
        tp_startEndTimeInterval.setTitle(title);
        tp_startEndTimeInterval.show();
    }

    public void setDuration() {
        RadioGroup radioGroup = (RadioGroup)root.findViewById(R.id.rdg_duration);
        final LinearLayout ln_duration = (LinearLayout) root.findViewById(R.id.ln_duration);
        txt_duration = new TextView(getActivity());
        showInfoTextField(txt_duration, ln_duration);
        txt_duration.setVisibility(View.GONE);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
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

    public void setCurrentNumberOfBlistersValue(){
        np_numberofBlisters.setValue(numberOfBlisters);
    }

    public void showDateDialog(){
        DatePickerDialog dpd = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                StringBuilder numDaysString = new StringBuilder();
                monthOfYear = monthOfYear+1;
                numDaysString.append(getResources().getString(R.string.taking)).append(" ");
                numDaysString.append(getResources().getString(R.string.till)).append(" ").append(dayOfMonth).append(".").append(monthOfYear).append(".").append(year);
                txt_duration.setText(numDaysString);
                selectedYear = year;
                selectedMonth = monthOfYear-1;
                selectedDay = dayOfMonth;
            }
        }, selectedYear, selectedMonth, selectedDay);

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
        npb_numberofBlisters = new AlertDialog.Builder(getActivity());
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



    public void setDosage(){
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

    private void setupDosageNumberPicker(){
        np_dosage = (NumberPicker)root.findViewById(R.id.np_dosage);
        np_dosage.setMinValue(1);
        np_dosage.setMaxValue(20);
        np_dosage.setValue(1);
    }

    private void setFoodInstruction(){
        RadioGroup radioGroup = (RadioGroup)root.findViewById(R.id.rdg_foodInstruction);
        final ViewGroup ln_foodInstruction = (ViewGroup)root.findViewById(R.id.ln_foodInstruction);
        txt_foodInstruction = new TextView(getActivity());
        txt_foodInstruction.setVisibility(View.GONE);
        showInfoTextField(txt_foodInstruction, ln_foodInstruction);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                StringBuilder eatString = new StringBuilder();
                txt_foodInstruction.setVisibility(View.VISIBLE);
                if (checkedId == R.id.rd_eatBefore) {
                    eatString.append(getResources().getString(R.string.txt_eatBefore));
                    txt_foodInstruction.setText(eatString);
                } else if (checkedId == R.id.rd_eatDuring) {
                    eatString.append(getResources().getString(R.string.txt_eatDuring));
                    txt_foodInstruction.setText(eatString);
                } else if (checkedId == R.id.rd_eatAfter) {
                    eatString.append(getResources().getString(R.string.txt_eatAfter));
                    txt_foodInstruction.setText(eatString);
                }
            }
        });
    }



    private void save(){
        Button btn_save = (Button)root.findViewById(R.id.btn_save);
            btn_save.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isEmpty(txt_reminder)){
                        Toast.makeText(getActivity(),"Einnahmezeit ausüllen", Toast.LENGTH_LONG).show();
                    } else if (isEmpty(txt_duration)){
                        Toast.makeText(getActivity(),"Einnahme ausüllen", Toast.LENGTH_LONG).show();
                    } else if (isEmpty(txt_dosage)){
                        Toast.makeText(getActivity(),"Dosierung ausüllen", Toast.LENGTH_LONG).show();
                    } else if (isEmpty(txt_foodInstruction)){
                        Toast.makeText(getActivity(),"Einnahme ausüllen", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity()," Ihr Medikament wurde gespeichert",Toast.LENGTH_LONG).show();
                    }

                }
            });
        }


    private boolean isEmpty(TextView textView){
        if (textView.getText().toString().trim().length()>0){
            return false;
        } else {
            return true;
        }
    }

}
