package hslu.bda.medimemory.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;

import hslu.bda.medimemory.R;


/**
 * Created by Loana on 05.03.2016.
 */
public class TimePickerPreference extends DialogPreference {
    private TimePicker timePicker;
    private Calendar calendar = Calendar.getInstance();
    private int hour = calendar.get(Calendar.HOUR_OF_DAY);
    private int minute = calendar.get(Calendar.MINUTE);
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
        setSummary(new StringBuilder().append(timePicker.getCurrentHour()).append(":").append(timePicker.getCurrentMinute()));
        super.onClick(dialogInterface, which);
    }
}
