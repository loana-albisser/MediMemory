package hslu.bda.medimemory.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DecimalFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import hslu.bda.medimemory.R;


/**
 * Created by Loana on 05.03.2016.
 */
public class TimePickerPreference extends DialogPreference {
    private TimePicker timePicker;
    private int hourValue;
    private int minuteValue;
    private int DEFAULT_HOUR = 0;
    private int DEFAULT_MINUTE = 0;
    StringBuilder timeString;

    public TimePickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.dialogpreference_timepicker);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(null);
    }

    @Override
    public void onBindDialogView (View view){
        super.onBindDialogView(view);
        timePicker = (TimePicker) view.findViewById(R.id.tp_preference);
        timePicker.setIs24HourView(true);
        if (hourValue != 0) timePicker.setCurrentHour(hourValue);
        if (minuteValue !=0) timePicker.setCurrentMinute(minuteValue);

    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            if (callChangeListener(hourValue)||callChangeListener(minuteValue)) {
                setHour(timePicker.getCurrentHour());
                setMinute(timePicker.getCurrentMinute());
            }
            updateSummary();
        }
    }

    public void updateSummary(){
        timeString = new StringBuilder();
        timeString.append(new DecimalFormat("00").format(hourValue)).append(":").append(new DecimalFormat("00").format(minuteValue));
        setSummary(timeString);
    }

    public void setHour (int hourValue) {
        this.hourValue = hourValue;
        persistInt(this.hourValue);
    }

    public void setMinute (int minuteValue) {
        this.minuteValue = minuteValue;
        persistInt(this.minuteValue);
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setHour(restorePersistedValue ? getPersistedInt(DEFAULT_HOUR) : (Integer) defaultValue);
        setMinute(restorePersistedValue ? getPersistedInt(DEFAULT_MINUTE) : (Integer) defaultValue);

        updateSummary();
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInteger(index, 0);
    }
}
