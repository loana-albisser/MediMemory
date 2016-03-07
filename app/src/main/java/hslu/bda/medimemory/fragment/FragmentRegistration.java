package hslu.bda.medimemory.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.Fragment;
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
import android.support.v7.widget.CardView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
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
    Button btnRemindTime;
    private ImageView ivImage;
    private Button btn_HourTime = null;
    private Button btn_dayTime = null;
    private Button btn_WeekTime = null;
    private TextView txt_durationAlways = null;
    private TextView txt_durationNumberOfDays = null;
    private TextView txt_durationPackageEnd = null;
    private TextView txt_eatBefore = null;
    private TextView txt_eatAfter = null;
    private TextView txt_eatDuring = null;
    private NumberPicker numberPicker;
    private AlertDialog.Builder numberPickerBuilder;
    private AlertDialog.Builder reminderDaytimeDialog;
    private AlertDialog.Builder reminderIntervalDialog;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private TextView txt_dosage = null;
    private AlertDialog d_day = null;
    private RadioButton radioButton;
    private int selectedId;
    private CheckBox chk_ReminderMorning;
    private CheckBox chk_ReminderNoon;
    private CheckBox chk_ReminderEvening;
    private CheckBox chk_ReminderNight;
    private NumberPicker numberPickerInterval;
    private Spinner spinner;
    private LinearLayout ln_reminderDetailsDaytime;
    private LinearLayout ln_reminderDetailsInterval;
    private TextView txt_reminderDayTime;
    private String daytime;
    private TextView txt_reminderInterval;
    private String interval = null;
    private String timeInterval = null;
    private StringBuilder builder;
    private int timeval = 0;
    private boolean morningChecked;
    private boolean noonChecked;
    private boolean eveningChecked;
    private boolean nightChecked;
    private StringBuilder daytimebuilder;
    private List<String> daytimes;
    private StringBuilder intervalbuilder;
    private int selectedValue ;
    private String selectedInterval;

    public FragmentRegistration() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.fragment_registration, container, false);

        Button btnSelect = (Button) root.findViewById(R.id.btn_SelectPhoto);
        btnSelect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        ivImage = (ImageView) root.findViewById(R.id.iv_Image);
        setupDosageNumberPicker();
        setDuration();
        setDosage();
        setFoodInstruction();
        //setFoodinstructionVisibility();
        showReminderDetails();
        save();
        return root;
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
        daytimebuilder = new StringBuilder();
        reminderDaytimeDialog = new AlertDialog.Builder(getActivity());
        reminderDaytimeDialog.setCancelable(false);
        reminderDaytimeDialog.setTitle("Um welche Tageszeit möchten sie das Medikament nehmen");
        reminderDaytimeDialog.setMultiChoiceItems(R.array.array_daytime, null, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    if (which == daytimes.indexOf(getString(R.string.morning))) {
                        daytimebuilder.append(getResources().getString(R.string.morning)).append(" ");
                    }
                    if (which == daytimes.indexOf(getString(R.string.noon))) {
                        daytimebuilder.append(getResources().getString(R.string.noon)).append(" ");
                    }
                    if (which == daytimes.indexOf(getString(R.string.evening))) {
                        daytimebuilder.append(getResources().getString(R.string.evening)).append(" ");
                    }
                    if (which == daytimes.indexOf(getString(R.string.night))) {
                        daytimebuilder.append(getResources().getString(R.string.night)).append(" ");
                    }
                }
                txt_reminderDayTime.setText(daytimebuilder);
            }
        });
        reminderDaytimeDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
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
        reminderIntervalDialog = new AlertDialog.Builder(getActivity());
        reminderIntervalDialog.setCancelable(false);
        intervalbuilder = new StringBuilder();
        LinearLayout layout = new LinearLayout(getActivity());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        layout.setOrientation(LinearLayout.HORIZONTAL);
        layout.setLayoutParams(params);
        //numberPickerInterval.setLayoutParams(params);
        setupReminderIntervalDialogContent();
        layout.addView(numberPickerInterval, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        layout.addView(spinner, new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        intervalbuilder.append(getResources().getString(R.string.reminderInterval)).append(" ");
        numberPickerInterval.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                selectedValue = newVal;
            }
        });
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedInterval = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                selectedInterval = getResources().getString(R.string.hour);
            }
        });
        reminderIntervalDialog.setTitle("Wie oft möchten Sie das Medikament einnnehmen");
        reminderIntervalDialog.setView(layout);
        reminderIntervalDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                intervalbuilder.append(selectedValue).append(" ").append(selectedInterval);
                txt_reminderInterval.setText(intervalbuilder);
            }
        });
        reminderIntervalDialog.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        Dialog dialog = reminderIntervalDialog.create();
        dialog.show();
    }

    private void setupReminderIntervalDialogContent(){
        numberPickerInterval = new NumberPicker(getActivity());
        spinner = new Spinner(getActivity());
        ArrayAdapter<String>stringArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.array_interval));
        stringArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(stringArrayAdapter);
        spinner.setGravity(Gravity.CENTER_VERTICAL);
        spinner.setGravity(Gravity.CENTER_HORIZONTAL);
        numberPickerInterval.setMinValue(0);
        numberPickerInterval.setMaxValue(30);
        numberPickerInterval.setValue(1);
    }

    private void showReminderDetails(){
        RadioGroup rdg_reminder = (RadioGroup) root.findViewById(R.id.rdg_reminder);
        final CardView cv_foodInstruction = (CardView)root.findViewById(R.id.cv_foodInstruction);
        final LinearLayout ln_reminder = (LinearLayout)root.findViewById(R.id.ln_reminder);
        rdg_reminder.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rd_daytime) {
                    showReminderDaytimeDialog();
                    if (txt_reminderDayTime == null) {
                        txt_reminderDayTime = new TextView(getActivity());
                        showInfoTextField(txt_reminderDayTime, ln_reminder);
                    } else {
                        txt_reminderDayTime.setVisibility(View.VISIBLE);
                    }
                    if (txt_reminderInterval != null) {
                        txt_reminderInterval.setVisibility(View.GONE);
                    }
                }
                if (checkedId == R.id.rd_interval) {
                    if (txt_reminderInterval == null) {
                        txt_reminderInterval = new TextView(getActivity());
                        showReminderIntervalDialog();
                        showInfoTextField(txt_reminderInterval, ln_reminder);
                        cv_foodInstruction.setVisibility(View.GONE);
                    } else {
                        txt_reminderInterval.setVisibility(View.VISIBLE);
                        showReminderIntervalDialog();
                    }
                    if (txt_reminderDayTime != null) {
                        txt_reminderDayTime.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void showInfoTextField(TextView textView, ViewGroup viewGroup){
        textView.setPadding(10, 10, 10, 10);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        viewGroup.addView(textView);
    }

    private void hideTextField (TextView textView){
        if (textView != null){
            textView.setVisibility(View.GONE);
        }
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
        //setChangeListener(getActivity());
        RadioGroup radioGroup = (RadioGroup)root.findViewById(R.id.rdg_duration);
        final LinearLayout ln_duration = (LinearLayout)root.findViewById(R.id.ln_duration);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rd_numberOfDays) {
                    showDateDialog();
                    if (txt_durationNumberOfDays == null) {
                        txt_durationNumberOfDays = new TextView(getActivity());
                        showInfoTextField(txt_durationNumberOfDays, ln_duration);
                    } else {
                        txt_durationNumberOfDays.setVisibility(View.VISIBLE);
                    }
                    hideTextField(txt_durationAlways);
                    hideTextField(txt_durationPackageEnd);
                } else if (checkedId == R.id.rd_always) {
                    if (txt_durationAlways == null) {
                        txt_durationAlways = new TextView(getActivity());
                        showInfoTextField(txt_durationAlways, ln_duration);
                        txt_durationAlways.setText(getResources().getString(R.string.txt_durationAlways));
                    } else {
                        txt_durationAlways.setVisibility(View.VISIBLE);
                    }
                    hideTextField(txt_durationNumberOfDays);
                    hideTextField(txt_durationPackageEnd);
                } else if (checkedId == R.id.rd_packageEnd) {
                    showNumberPickerDialog();
                    if (txt_durationPackageEnd == null) {
                        txt_durationPackageEnd = new TextView(getActivity());
                        changeNumberOfBlisterTextField();
                        showInfoTextField(txt_durationPackageEnd, ln_duration);
                        txt_durationPackageEnd.setText(getResources().getString(R.string.txt_durationPackageEnd));
                    } else {
                        changeNumberOfBlisterTextField();
                        txt_durationPackageEnd.setVisibility(View.VISIBLE);
                    }
                    hideTextField(txt_durationNumberOfDays);
                    hideTextField(txt_durationAlways);
                }
            }
        });
    }

    private void changeNumberOfBlisterTextField(){
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                txt_durationPackageEnd.setText(new StringBuilder().append(getResources().getString(R.string.txt_duration_numberOfPills)).append(" ").append(newVal).append(" ").append(getResources().getString(R.string.blister)));
            }
        });
    }

    private void showNumberPickerDialog(){
        numberPickerBuilder = new AlertDialog.Builder(getActivity());
        numberPickerBuilder.setCancelable(false);
        numberPicker = new NumberPicker(getActivity());
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(24);
        final FrameLayout parent = new FrameLayout(getActivity());
        parent.addView(numberPicker, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        numberPickerBuilder.setView(parent);
        numberPickerBuilder.setTitle(getResources().getString(R.string.d_packagEnd));
        numberPickerBuilder.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
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
       final Calendar c = Calendar.getInstance();
        int selectedYear = c.get(Calendar.YEAR);
        int selectedMonth = c.get(Calendar.MONTH);
        int selectedDay = c.get(Calendar.DAY_OF_MONTH);
       // Launch Date Picker Dialog
       DatePickerDialog dpd = new DatePickerDialog(getActivity(),
               new DatePickerDialog.OnDateSetListener() {

                   @Override
                   public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                       monthOfYear = monthOfYear +1;
                       txt_durationNumberOfDays.setText(new StringBuilder().append(getResources().getString(R.string.txt_duration_till)).append(" ").append(dayOfMonth).append(".").append(monthOfYear).append(".").append(year));
                   }
               }, selectedYear, selectedMonth, selectedDay);
       dpd.show();

   }

    private void setDosage (){
        NumberPicker numberPicker = (NumberPicker)root.findViewById(R.id.np_dosage);
        final ViewGroup ln_dosage = (ViewGroup)root.findViewById(R.id.ln_dosage);
        txt_dosage = new TextView(getActivity());
        txt_dosage.setText(getResources().getString(R.string.txt_dosageInitial));        
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if (newVal == 1) {
                    txt_dosage.setText(new StringBuilder().append(getResources().getString(R.string.txt_dosageOne)).append(" ").append(newVal).append(" ").append(getResources().getString(R.string.txt_dosageOneTab)));
                } else {
                    txt_dosage.setText(new StringBuilder().append(getResources().getString(R.string.txt_dosageMore)).append(" ").append(newVal).append(" ").append(getResources().getString(R.string.txt_dosageMoreTab)));
                }
            }
        });
        showInfoTextField(txt_dosage,ln_dosage);
    }

    private void setFoodInstruction(){
        RadioGroup radioGroup = (RadioGroup)root.findViewById(R.id.rdg_foodInstruction);
        final ViewGroup ln_foodInstruction = (ViewGroup)root.findViewById(R.id.ln_foodInstruction);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rd_eatBefore) {
                    if (txt_eatBefore == null) {
                        txt_eatBefore = new TextView(getActivity());
                        txt_eatBefore.setText(getResources().getString(R.string.txt_eatBefore));
                        showInfoTextField(txt_eatBefore, ln_foodInstruction);
                    } else {
                        txt_eatBefore.setVisibility(View.VISIBLE);
                    }
                    hideTextField(txt_eatAfter);
                    hideTextField(txt_eatDuring);
                } else if (checkedId == R.id.rd_eatDuring) {
                    if (txt_eatDuring == null) {
                        txt_eatDuring = new TextView(getActivity());
                        txt_eatDuring.setText(getResources().getString(R.string.txt_eatDuring));
                        showInfoTextField(txt_eatDuring,ln_foodInstruction);
                    } else {
                        txt_eatDuring.setVisibility(View.VISIBLE);
                    }
                    hideTextField(txt_eatBefore);
                    hideTextField(txt_eatAfter);
                } else if (checkedId == R.id.rd_eatAfter) {
                    if (txt_eatAfter == null) {
                        txt_eatAfter = new TextView(getActivity());
                        txt_eatAfter.setText(getResources().getString(R.string.txt_eatAfter));
                        showInfoTextField(txt_eatAfter,ln_foodInstruction);
                    } else {
                        txt_eatAfter.setVisibility(View.VISIBLE);
                    }
                    hideTextField(txt_eatBefore);
                    hideTextField(txt_eatDuring);
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
                Toast.makeText(getActivity()," Ihr Medikament wurde gespeichert",Toast.LENGTH_LONG).show();
            }
        });
    }
}
