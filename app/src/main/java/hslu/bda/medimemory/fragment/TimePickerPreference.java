package hslu.bda.medimemory.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import hslu.bda.medimemory.R;


/**
 * Created by Loana on 05.03.2016.
 */
public class TimePickerPreference extends DialogPreference {
    private TimePicker timePicker;
    private Calendar calendar = Calendar.getInstance();
    public TimePickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.dialogpreference_timepicker);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);

    }

    public void onBindDialogView (View view){
        timePicker = (TimePicker) view.findViewById(R.id.tp_preference);
        timePicker.setIs24HourView(true);
        super.onBindDialogView(view);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult){
        if(positiveResult){
            persistInt(timePicker.getCurrentHour());
            persistInt(timePicker.getCurrentMinute());
        }
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int which){
        StringBuilder timeString = new StringBuilder();
        if (timePicker.getCurrentHour()<10){
            timeString.append("0").append(timePicker.getCurrentHour());
        } else {
            timeString.append(timePicker.getCurrentHour());
        }
        timeString.append(":");
        if (timePicker.getCurrentMinute()<10){
            timeString.append("0").append(timePicker.getCurrentMinute());
        } else {
            timeString.append(timePicker.getCurrentMinute());
        }
        setSummary(timeString);
        super.onClick(dialogInterface, which);
    }
}
