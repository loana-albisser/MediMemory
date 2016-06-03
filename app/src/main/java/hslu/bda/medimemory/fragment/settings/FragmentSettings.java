package hslu.bda.medimemory.fragment.settings;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;


import hslu.bda.medimemory.R;

import static android.content.SharedPreferences.*;

/**
 * Created by Loana on 03.03.2016.
 */
public class FragmentSettings extends PreferenceFragment{

    private boolean showProtectionDialog;
    private SharedPreferences pref;
    private EditText password;
    private CheckBoxPreference chk_protect;
    private AlertDialog.Builder passwordDialog;
    private Context mActivity;
    private OnSharedPreferenceChangeListener listener;


    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        chk_protect = (CheckBoxPreference)findPreference("pref_key_showPassword");
        pref = PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext());
        pref.registerOnSharedPreferenceChangeListener(listener);
        listener = new OnSharedPreferenceChangeListener() {
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                if (key.equals("pref_key_showPassword")) {
                    chk_protect = (CheckBoxPreference)findPreference("pref_key_showPassword");
                    if (!getFirstRun()){
                        createProtectedDialog();
                    } else {
                        setFirstRun(false);
                    }
                }
            }
        };
        addPreferencesFromResource(R.xml.preferences);
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(listener);
    }

    @TargetApi(23)
    @Override public void onAttach(Context context) {
        //This method avoid to call super.onAttach(context) if I'm not using api 23 or more
        //if (Build.VERSION.SDK_INT >= 23) {
        super.onAttach(context);
        onAttachToContext(context);
        //}
    }

    /*
     * Deprecated on API 23
     * Use onAttachToContext instead
     */
    @SuppressWarnings("deprecation")
    @Override public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < 23) {
            onAttachToContext(activity);
        }
    }

    /*
     * This method will be called from one of the two previous method
     */
    protected void onAttachToContext(Context context) {
        this.mActivity=context;
    }

    public void onStop(){
        super.onStop();
    }

    public void onDestroy(){
        super.onDestroy();

    }

    /**
     * dialog shown if a false password is entered
     */
    public void showFalsePasswordDialog(){
        AlertDialog.Builder falsePasswordDialog = new AlertDialog.Builder(mActivity);
        falsePasswordDialog.setTitle(mActivity.getString(R.string.wrongPW));
        falsePasswordDialog.setPositiveButton(mActivity.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                dialog.dismiss();
                createProtectedDialog();
            }
        });
    }

    /**
     * checks whether the save mode is checked or not
     * @return true if checked
     */
    public boolean isSetProtection(){
        showProtectionDialog = pref.getBoolean("pref_key_showPassword",false);
        return showProtectionDialog;
    }

    private void createProtectedDialog(){
        passwordDialog = new AlertDialog.Builder(mActivity,R.style.DialogTheme);
        passwordDialog.setTitle(mActivity.getString(R.string.enterPassword));
        LinearLayout layout = new LinearLayout(mActivity);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        params.setMargins(16, 16, 16, 16);
        password = new EditText(mActivity);
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(password, params);
        passwordDialog.setView(layout);
        passwordDialog.setPositiveButton(mActivity.getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isSetProtection()) {
                    setPassword(password.getText().toString());
                    Log.i("setPW", getPassword());
                } else {
                    final String enteredPassword = password.getText().toString();
                    Log.i("savedPW",getPassword());
                    Log.i("enteredPW",enteredPassword);
                    if (enteredPassword.equals(getPassword())) {
                        dialog.dismiss();
                        pref.unregisterOnSharedPreferenceChangeListener(listener);
                        chk_protect.setChecked(false);
                        pref.registerOnSharedPreferenceChangeListener(listener);
                    } else {
                        showFalsePasswordDialog();
                    }
                }

            }
        });
        passwordDialog.setNegativeButton(mActivity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isSetProtection()) {
                    dialog.dismiss();
                    pref.unregisterOnSharedPreferenceChangeListener(listener);
                    chk_protect.setChecked(false);
                    pref.registerOnSharedPreferenceChangeListener(listener);
                } else {
                    dialog.dismiss();
                    pref.unregisterOnSharedPreferenceChangeListener(listener);
                    chk_protect.setChecked(true);
                    pref.registerOnSharedPreferenceChangeListener(listener);
                }

            }
        });
        final Dialog dialog = passwordDialog.create();
        dialog.show();
    }

   private void setPassword(String password) {
        Editor editor = pref.edit();
        editor.putString("password", password);
        editor.commit();
    }

    public String getPassword(){
        String password = pref.getString("password", null);
        return password;
    }

    /**
     * sets whether the fragment is run for the first time
     * @param firstRun
     */
    private void setFirstRun(boolean firstRun) {
        Editor editor = pref.edit();
        editor.putBoolean("firstRun", firstRun);
        editor.commit();
    }

    public boolean getFirstRun(){
        boolean firstRun = pref.getBoolean("firstRun",true);
        return firstRun;
    }
}
