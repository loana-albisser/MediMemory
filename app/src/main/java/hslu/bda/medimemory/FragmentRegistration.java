package hslu.bda.medimemory;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.NumberPicker;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.provider.MediaStore.MediaColumns;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by Loana on 29.02.2016.
 */
public class FragmentRegistration extends Fragment {
    private ViewGroup root;
    private final int REQUEST_CAMERA = 0;
    private final int SELECT_FILE = 1;
    private Button btnSelect;
    Button btnRemindTime;
    private ImageView ivImage;
    private Button btn_HourTime = null;
    private Button btn_dayTime = null;
    private Button btn_WeekTime = null;
    private TextView txt_durationAlways = null;
    private TextView txt_durationNumberOfDays = null;
    private NumberPicker numberPicker;
    private TextView txt_dosage = null;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.fragment_registration, container, false);

        btnSelect = (Button) root.findViewById(R.id.btn_SelectPhoto);
        btnSelect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                selectImage();
            }
        });
        ivImage = (ImageView) root.findViewById(R.id.iv_Image);
        setupNumberPicker();
        setReminderInterval();
        setDuration();
        setDosage();
        save();
        return root;
    }




    private void selectImage() {
            final CharSequence[] items = { "Take Photo", "Choose from Library",
                    "Cancel" };

            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
            builder.setTitle("Add Photo!");
            builder.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (items[item].equals("Take Photo")) {
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(intent, REQUEST_CAMERA);
                    } else if (items[item].equals("Choose from Library")) {
                        Intent intent = new Intent(
                                Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        intent.setType("image/*");
                        startActivityForResult(
                                Intent.createChooser(intent, "Select File"),
                                SELECT_FILE);
                    } else if (items[item].equals("Cancel")) {
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

        @SuppressWarnings("deprecation")
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


    private void showTimePicker(){
        TimePickerFragment timePickerFragment = new TimePickerFragment();
        timePickerFragment.show(getFragmentManager(), "timePicker");
     }

    private void setReminderTimeDay(){
        btn_dayTime.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });
    }

    private void showWeekSpinnerDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Wähle ein Wochentag aus");
        builder.setSingleChoiceItems(R.array.week_array,-1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getActivity()," Selected",
                        Toast.LENGTH_LONG).show();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void setReminderTimeWeek(){
        btn_WeekTime.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showWeekSpinnerDialog();
            }
        });
    }

    private void setReminderTimeHour(){
        btn_HourTime.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showNumberPickerDialog();
            }
        });
    }

    private void showNumberPickerDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(false);
        numberPicker = new NumberPicker(getActivity());
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(24);
        final FrameLayout parent = new FrameLayout(getActivity());
        parent.addView(numberPicker, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT, Gravity.CENTER));
        builder.setView(parent);

        builder.setPositiveButton("ok", null);
        builder.setNegativeButton("cancel", null);

        Dialog dialog = builder.create();
        dialog.show();

    }

    private void setReminderInterval(){
        RadioGroup radioGroup = (RadioGroup)root.findViewById(R.id.rdg_reminder);
        final ViewGroup ln_reminder = (ViewGroup)root.findViewById(R.id.ln_reminder);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rd_reminderDaily) {
                    if (btn_dayTime == null) {
                        btn_dayTime = new Button(getActivity());
                        btn_dayTime.setText(getResources().getString(R.string.btn_dayTime));
                        btn_dayTime.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        ln_reminder.addView(btn_dayTime);
                        setReminderTimeDay();
                    } else {
                        btn_dayTime.setVisibility(View.VISIBLE);
                    }
                    if (btn_HourTime != null) {
                        btn_HourTime.setVisibility(View.GONE);
                    }
                    if (btn_WeekTime != null) {
                        btn_WeekTime.setVisibility(View.GONE);
                    }
                } else if (checkedId == R.id.rd_reminderHour) {
                    if (btn_HourTime == null) {
                        btn_HourTime = new Button(getActivity());
                        btn_HourTime.setText(getResources().getString(R.string.btn_HourTime));
                        btn_HourTime.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        ln_reminder.addView(btn_HourTime);
                        setReminderTimeHour();
                    } else {
                        btn_HourTime.setVisibility(View.VISIBLE);
                    }
                    if (btn_dayTime != null) {
                        btn_dayTime.setVisibility(View.GONE);
                    }
                    if (btn_WeekTime != null) {
                        btn_WeekTime.setVisibility(View.GONE);
                    }
                } else if (checkedId == R.id.rd_reminderWeekly) {
                    if (btn_WeekTime == null) {
                        btn_WeekTime = new Button(getActivity());
                        btn_WeekTime.setText(getResources().getString(R.string.btn_weekTime));
                        btn_WeekTime.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        ln_reminder.addView(btn_WeekTime);
                        setReminderTimeWeek();
                    } else {
                        btn_WeekTime.setVisibility(View.VISIBLE);
                    }
                    if (btn_HourTime != null) {
                        btn_HourTime.setVisibility(View.GONE);
                    }
                    if (btn_dayTime != null) {
                        btn_dayTime.setVisibility(View.GONE);
                    }
                }
            }
        });
    }

    private void setDuration() {
        RadioGroup radioGroup = (RadioGroup)root.findViewById(R.id.rdg_duration);
        final ViewGroup ln_duration = (ViewGroup)root.findViewById(R.id.ln_duration);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rd_numberOfDays) {
                    showNumberPickerDialog();
                    if (txt_durationNumberOfDays == null){
                        txt_durationNumberOfDays = new TextView(getActivity());
                        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
                            @Override
                            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                                if (newVal == 1) {
                                    txt_durationNumberOfDays.setText(getResources().getString(R.string.duration)+" "+ newVal +" "+ getResources().getString(R.string.txt_durationOneDay));
                                } else {
                                    txt_durationNumberOfDays.setText(getResources().getString(R.string.duration)+" "+ newVal +" "+ getResources().getString(R.string.txt_durationMoreDays));
                                }
                            }
                        });
                        txt_durationNumberOfDays.setPadding(10,10,10,10);
                        txt_durationNumberOfDays.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                        ln_duration.addView(txt_durationNumberOfDays);
                    } else {
                        txt_durationNumberOfDays.setVisibility(View.VISIBLE);
                    } if (txt_durationAlways !=null){
                            txt_durationAlways.setVisibility(View.GONE);
                        }
                    } else if (checkedId == R.id.rd_always) {
                        if(txt_durationAlways == null){
                            txt_durationAlways = new TextView(getActivity());
                            txt_durationAlways.setText(getResources().getString(R.string.txt_durationAlways));
                            txt_durationAlways.setPadding(10, 10, 10, 10);
                            txt_durationAlways.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                            ln_duration.addView(txt_durationAlways);
                        } else {
                            txt_durationAlways.setVisibility(View.VISIBLE);
                        } if (txt_durationNumberOfDays != null){
                            txt_durationNumberOfDays.setVisibility(View.GONE);
                        }
                    }
            }
        });
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
                    txt_dosage.setText(getResources().getString(R.string.txt_dosageOne)+" "+ newVal +" "+ getResources().getString(R.string.txt_dosageOneTab));
                } else {
                    txt_dosage.setText((getResources().getString(R.string.txt_dosageMore)+" "+ newVal +" "+ getResources().getString(R.string.txt_dosageMoreTab)));
                }
            }
        });
        txt_dosage.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        txt_dosage.setPadding(10,10,10,10);
        ln_dosage.addView(txt_dosage);
    }

    private void setupNumberPicker(){
        NumberPicker numberPicker = (NumberPicker)root.findViewById(R.id.np_dosage);
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(50);
        numberPicker.setValue(1);
    }

    public void save(){
        Button btn_save = (Button)root.findViewById(R.id.btn_save);
        btn_save.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity()," Ihr Medikament wurde gespeichert",Toast.LENGTH_LONG).show();
            }
        });
    }
}
