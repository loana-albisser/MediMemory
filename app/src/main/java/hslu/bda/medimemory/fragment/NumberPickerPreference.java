package hslu.bda.medimemory.fragment;

import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
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
    private NumberPickerPreference myView;
    private NumberPicker numberPicker;
    private int value = 15;

    public NumberPickerPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDialogLayoutResource(R.layout.dialogpreference_numberpicker);
        setPositiveButtonText(android.R.string.ok);
        setNegativeButtonText(android.R.string.cancel);
        setPersistent(false);
    }

    @Override
    public void onBindDialogView (View view){
        super.onBindDialogView(view);
        numberPicker = (NumberPicker) view.findViewById(R.id.np_preference);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(120);
        numberPicker.setValue(value);
    }

    @Override
    protected void onDialogClosed(boolean positiveResult){
        super.onDialogClosed(positiveResult);
        if(positiveResult){
            //persistInt(numberPicker.getValue());
            SharedPreferences.Editor editor = getEditor();
            editor.putInt("key1",numberPicker.getValue());
            editor.apply();
            editor.commit();
        }
    }

    @Override
    public void onClick(DialogInterface dialogInterface, int which){
        setSummary(String.valueOf(numberPicker.getValue()));
        super.onClick(dialogInterface, which);
    }

}
