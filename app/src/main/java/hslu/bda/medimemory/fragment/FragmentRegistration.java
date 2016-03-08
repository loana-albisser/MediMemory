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

    private TextView txt_duration = null;
    private TextView txt_foodInstruction = null;
    private NumberPicker np_durationPackageEnd;
    private AlertDialog.Builder numberPickerBuilder;
    private AlertDialog.Builder reminderDaytimeDialog;
    private TextView txt_dosage = null;
    private NumberPicker np_reminderInterval;
    private TextView txt_reminder;
    private StringBuilder daytimebuilder;
    private List<String> daytimes;
    private StringBuilder intervalbuilder;
    private int selectedValue ;
    private String selectedInterval;
    private View dialogView;
    private Spinner sp_reminderInterval;
    private TimePickerDialog tpd;
    private int selectedHour;
    private int selectedMinute;
    private int selectedIntervalPosition;
    final boolean [] checkItems = {false,false,false,false};
    private int numberOfBlisters;
    final Calendar calendar;
    private int selectedYear;
    private int selectedMonth;
    private int selectedDay;
    private ArrayList <String> selectedItems ;
    private StringBuilder numberOfBlisterString;
    private StringBuilder numDaysString;

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
        //setupReminderIntervalNumberPicker();
        save();
        return root;
    }

    private void setupShowImage(){
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

    private void showReminderDaytimeDialog(){
        daytimes = Arrays.asList(getResources().getStringArray(R.array.array_daytime));
        selectedItems = new ArrayList<>();
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
                for (int i = 0; i < checkItems.length; i++) {
                    if (checkItems[i] == true) {
                        daytimebuilder.append(daytimes.get(i)).append(" ");
                    }
                }
                txt_reminder.setText(daytimebuilder);
            }
        });
        reminderDaytimeDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        Dialog dialog = reminderDaytimeDialog.create();
        dialog.show();
    }

    private void showReminderIntervalDialog(){
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog_reminderinterval, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle(getResources().getString(R.string.title_intervalDialog));
        getReminderIntervalTimes();
        intervalbuilder = new StringBuilder();
        intervalbuilder.append(getResources().getString(R.string.reminderInterval)).append(" ");
        dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                intervalbuilder.append(selectedValue).append(" ").append(selectedInterval);
                txt_reminder.setText(intervalbuilder);
                if (sp_reminderInterval.getSelectedItemPosition() == 0) {
                    setStartEndTime();
                }

            }
        });
        np_reminderInterval.setValue(selectedValue);
        sp_reminderInterval.setSelection(selectedIntervalPosition);
        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }

    private void setStartEndTime(){
        final AlertDialog.Builder dialogStartEndTime = new AlertDialog.Builder(getActivity());
        LayoutInflater inflaterStartEnd = getActivity().getLayoutInflater();
        View dialogViewStartEnd = inflaterStartEnd.inflate(R.layout.dialog_reminderstartendtime, null);
        final Button btn_starttime = (Button)dialogViewStartEnd.findViewById(R.id.btn_starttime);
        final Button btn_endtime = (Button)dialogViewStartEnd.findViewById(R.id.btn_endtime);
        Button btn_startendAlways = (Button)dialogViewStartEnd.findViewById(R.id.btn_startEndAlways);
        dialogStartEndTime.setView(dialogViewStartEnd);
        dialogStartEndTime.setTitle("Start/ Endzeit festlegen");
        btn_starttime.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                final StringBuilder startString = new StringBuilder();
                tpd = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        startString.append(hourOfDay).append(":").append(minute);
                        btn_starttime.setText(startString);
                    }
                }, selectedHour, selectedMinute, true);
                showTimeDialog(getResources().getString(R.string.title_chooseStartTime));
            }
        });
        btn_endtime.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                final StringBuilder endString = new StringBuilder();
                tpd = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        endString.append(hourOfDay).append(":").append(minute);
                        btn_endtime.setText(endString);
                    }
                }, selectedHour, selectedMinute, true);
                showTimeDialog(getResources().getString(R.string.title_chooseEndTime));
            }
        });
        btn_startendAlways.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        dialogStartEndTime.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialogStartEndTime.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        Dialog d = dialogStartEndTime.create();
        d.show();
    }

    private void getReminderIntervalTimes(){
        np_reminderInterval = (NumberPicker)dialogView.findViewById(R.id.np_reminderInterval);
        np_reminderInterval.setValue(1);
        sp_reminderInterval = (Spinner)dialogView.findViewById(R.id.sp_reminderInterval);
        np_reminderInterval.setMaxValue(30);
        np_reminderInterval.setMinValue(1);
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
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedInterval = getResources().getString(R.string.hour);
            }
        });
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
                if (checkedId == R.id.rd_daytime) {
                    showReminderDaytimeDialog();
                } else if (checkedId == R.id.rd_interval) {
                    showReminderIntervalDialog();
                }
            }
        });
    }

    private void showInfoTextField(TextView textView, ViewGroup viewGroup){
        textView.setPadding(10, 10, 10, 10);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        viewGroup.addView(textView);
    }

    private void setChangeListener(){
        RadioButton button = (RadioButton)root.findViewById(R.id.rd_numberOfDays);
        //button.setonch
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "hallo", Toast.LENGTH_SHORT).show();
            }
        });

    }

    private void setDuration() {
        //setChangeListener();
        RadioGroup radioGroup = (RadioGroup)root.findViewById(R.id.rdg_duration);
        final LinearLayout ln_duration = (LinearLayout)root.findViewById(R.id.ln_duration);
        txt_duration = new TextView(getActivity());
        showInfoTextField(txt_duration, ln_duration);
        txt_duration.setVisibility(View.GONE);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                StringBuilder durationString = new StringBuilder();
                txt_duration.setVisibility(View.VISIBLE);
                if (checkedId == R.id.rd_numberOfDays) {
                    showDateDialog();
                } else if (checkedId == R.id.rd_always) {
                    durationString.append(getResources().getString(R.string.txt_durationAlways));
                    txt_duration.setText(durationString);
                } else if (checkedId == R.id.rd_packageEnd) {
                    changeNumberOfBlisterTextField();
                    showNumberPickerDialog();
                    np_durationPackageEnd.setValue(numberOfBlisters);

                }
            }
        });
    }

    private void changeNumberOfBlisterTextField() {
        np_durationPackageEnd = new NumberPicker(getActivity());
        np_durationPackageEnd.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                numberOfBlisterString = new StringBuilder();
                numberOfBlisterString.append(getResources().getString(R.string.taking)).append(" ");
                numberOfBlisterString.append(newVal);
                numberOfBlisters = newVal;
            }
        });
    }

    private void showNumberPickerDialog(){
        numberPickerBuilder = new AlertDialog.Builder(getActivity());
        numberPickerBuilder.setCancelable(false);        //
        np_durationPackageEnd.setMaxValue(24);
        np_durationPackageEnd.setMinValue(1);
        final FrameLayout parent = new FrameLayout(getActivity());
        parent.addView(np_durationPackageEnd, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        numberPickerBuilder.setView(parent);
        numberPickerBuilder.setTitle(getResources().getString(R.string.d_packagEnd));
        numberPickerBuilder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                np_durationPackageEnd.setValue(numberOfBlisters);
                txt_duration.setText(numberOfBlisterString);

            }
        });
        numberPickerBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        Dialog dialog = numberPickerBuilder.create();
        dialog.show();
    }

    private void showDateDialog(){
        numDaysString = new StringBuilder();
        DatePickerDialog dpd = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                   @Override
                   public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                       selectedYear = year;
                       selectedMonth = monthOfYear;
                       selectedDay = dayOfMonth;
                   }
               }, selectedYear, selectedMonth, selectedDay);
       dpd.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int which) {
               numDaysString.append(getResources().getString(R.string.taking)).append(" ");
               numDaysString.append(getResources().getString(R.string.till)).append(" ").append(selectedDay).append(".").append(selectedMonth + 1).append(".").append(selectedYear);
               txt_duration.setText(numDaysString);
           }
       });
        dpd.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
       dpd.show();
   }

    private void showTimeDialog(String title){
        final Calendar c = Calendar.getInstance();
        selectedHour = c.get(Calendar.HOUR_OF_DAY);
        selectedMinute = c.get(Calendar.MINUTE);
        tpd.setTitle(title);
        tpd.show();
    }

    private void setDosage (){
        NumberPicker np_dosage = (NumberPicker)root.findViewById(R.id.np_dosage);
        final ViewGroup ln_dosage = (ViewGroup)root.findViewById(R.id.ln_dosage);
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

    private void setupDosageNumberPicker(){
        NumberPicker numberPicker = (NumberPicker)root.findViewById(R.id.np_dosage);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(20);
        numberPicker.setValue(1);
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
