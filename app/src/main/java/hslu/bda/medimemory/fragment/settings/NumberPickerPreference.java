package hslu.bda.medimemory.fragment.settings;

import android.content.Context;
import android.content.res.TypedArray;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.NumberPicker;

import hslu.bda.medimemory.R;

/**
 * Created by Loana on 05.03.2016.
 */
public class NumberPickerPreference extends DialogPreference {
    private NumberPicker numberPicker;
    private StringBuilder numberString;
    private int value = 30;
    private final int DEFAULT_VALUE = 30;

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

    /**
     * updates the summary text
     */
    private void updateSummary(){
        numberString = new StringBuilder();
        numberString.append(value).append(" ").append(getContext().getResources().getString(R.string.minutes));
        setSummary(numberString);
    }

    private int getValue(){
        return value;
    }


    @Override
    protected void onDialogClosed(boolean positiveResult){
        if(positiveResult){
            if (callChangeListener(value)) {
                setValue(numberPicker.getValue());
            }
        }
        updateSummary();
    }

    private void setValue(int value) {
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
