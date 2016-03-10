package hslu.bda.medimemory.fragment;

import android.app.Fragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;

import hslu.bda.medimemory.R;

/**
 * Created by Loana on 05.03.2016.
 */
public class NumberPickerPreference extends DialogPreference {
    private static final int MIN_VALUE = 0;
    private NumberPicker numberPicker;
    private StringBuilder numberString;
    private int value = 15;
    private int DEFAULT_VALUE = 15;

    public NumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.dialogpreference_numberpicker);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(null);
    }

    @Override
    public void onBindDialogView (View view){
        super.onBindDialogView(view);
        numberPicker = (NumberPicker) view.findViewById(R.id.np_preference);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(120);
        if (value != 0){
            numberPicker.setValue(value);
        }
    }

    public void updateSummary(){
        numberString = new StringBuilder();
        numberString.append(value).append(" ").append(getContext().getResources().getString(R.string.minutes));
        setSummary(numberString);
    }


    @Override
    protected void onDialogClosed(boolean positiveResult){
        numberString = new StringBuilder();
        if(positiveResult){
            if (callChangeListener(value)) {
                setValue(numberPicker.getValue());
            }
        }
        updateSummary();
    }

    public void setValue(int value) {
        this.value = value;
        persistInt(this.value);
    }


    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        setValue(restorePersistedValue ? getPersistedInt(DEFAULT_VALUE) : (Integer) defaultValue);
        updateSummary();
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInteger(index, DEFAULT_VALUE);
    }

}
