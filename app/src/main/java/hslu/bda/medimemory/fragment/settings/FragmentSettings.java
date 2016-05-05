package hslu.bda.medimemory.fragment.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.text.InputType;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;


import hslu.bda.medimemory.R;

import static android.content.SharedPreferences.*;

/**
 * Created by Loana on 03.03.2016.
 */
public class FragmentSettings extends PreferenceFragment implements OnSharedPreferenceChangeListener {

    private boolean showProtectionDialog;
    private SharedPreferences pref;
    private ViewGroup root;
    private EditText password;
    private CheckBoxPreference chk_protect;
    private AlertDialog.Builder passwordDialog;


    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        chk_protect = (CheckBoxPreference)findPreference("pref_key_showPassword");
        pref = PreferenceManager.getDefaultSharedPreferences(getActivity().getBaseContext());
        pref.registerOnSharedPreferenceChangeListener(listener);
        addPreferencesFromResource(R.xml.preferences);
    }



    @Override
    public void onResume() {
        super.onResume();
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }


    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }


    OnSharedPreferenceChangeListener listener = new OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals("pref_key_showPassword")) {
                chk_protect = (CheckBoxPreference)findPreference("pref_key_showPassword");
                if (isSetProtection()){
                    showProtectedDialog();
                    protetedDialogCheck();
                } else {
                    showProtectedDialog();
                    protetedDialogUncheck();
                }
            }
        }
    };

    public void showFalsePasswordDialog(){
        AlertDialog.Builder falsePasswordDialog = new AlertDialog.Builder(getActivity());
        falsePasswordDialog.setTitle("Falsches Passwort");
        falsePasswordDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showProtectedDialog();
                protetedDialogCheck();
            }
        });
        falsePasswordDialog.show();
    }

    public boolean isSetProtection(){

        showProtectionDialog = pref.getBoolean("pref_key_showPassword",false);
        return showProtectionDialog;
    }

    private void showProtectedDialog(){
        passwordDialog = new AlertDialog.Builder(getActivity());
        passwordDialog.setTitle(getResources().getString(R.string.enterPassword));
        LinearLayout layout = new LinearLayout(getActivity());
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        params.setMargins(16, 16, 16, 16);
        password = new EditText(getActivity());
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(password, params);
        passwordDialog.setView(layout);

    }

    private void protetedDialogCheck(){
        passwordDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pref.registerOnSharedPreferenceChangeListener(listener);
                setPassword();
            }
        });
        passwordDialog.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pref.unregisterOnSharedPreferenceChangeListener(listener);
                chk_protect.setChecked(false);
                /*pref.unregisterOnSharedPreferenceChangeListener(new OnSharedPreferenceChangeListener() {
                    @Override
                    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
                        chk_protect.setChecked(false);
                    }
                });*/
                dialog.dismiss();
                pref.registerOnSharedPreferenceChangeListener(listener);
            }
        });
        passwordDialog.show();
    }

    private void protetedDialogUncheck(){
        passwordDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String enteredPassword = password.getText().toString();
                if (enteredPassword.equals(getPassword())){
                    pref.unregisterOnSharedPreferenceChangeListener(listener);
                    chk_protect.setChecked(false);
                    dialog.dismiss();
                    pref.registerOnSharedPreferenceChangeListener(listener);
                } else {
                    pref.registerOnSharedPreferenceChangeListener(listener);
                    showFalsePasswordDialog();
                }
            }
        });
        passwordDialog.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                pref.unregisterOnSharedPreferenceChangeListener(listener);
                chk_protect.setChecked(true);
                dialog.dismiss();
                pref.registerOnSharedPreferenceChangeListener(listener);
            }
        });
        passwordDialog.show();
    }

    private void setPassword() {
        Editor editor = pref.edit();
        editor.putString("password", password.getText().toString());
        editor.commit();
    }

    public String getPassword(){
        String password = pref.getString("password",null);
        return password;
    }


}
