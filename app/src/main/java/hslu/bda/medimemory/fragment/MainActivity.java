package hslu.bda.medimemory.fragment;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;

import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.LinearLayout;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;

import hslu.bda.medimemory.R;
import hslu.bda.medimemory.fragment.edit.FragmentEdit;
import hslu.bda.medimemory.fragment.help.FragmentHelp;
import hslu.bda.medimemory.fragment.overview.FragmentOverview;
import hslu.bda.medimemory.fragment.registration.FragmentRegistration;
import hslu.bda.medimemory.fragment.settings.FragmentSettings;

public class MainActivity extends AppCompatActivity{
    private DrawerLayout drawer;
    private Fragment fragment;
    private Class fragmentClass;
    private FragmentRegistration fragmentRegistration;
    private FloatingActionButton fab;
    private NavigationView nvDrawer;
    private int currentMenuItem;
    private AlertDialog.Builder passwordDialog;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        fab =(FloatingActionButton)findViewById(R.id.fab);
        setSupportActionBar(toolbar);
        //setup Fragment
        fragmentClass = FragmentOverview.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        changeFragment(fragment, "Fragment_Overview");
        fab.show();

        drawer = (DrawerLayout) findViewById(R.id.activity_main);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        nvDrawer = (NavigationView) findViewById(R.id.nav_view);
        nvDrawer.setCheckedItem(R.id.nav_list);
        nvDrawer.getMenu().getItem(0).setChecked(true);
        currentMenuItem = R.id.nav_list;
        onFloatingButtonPressed();
        // Setup drawer view
        setupDrawerContent(nvDrawer);
    }


    public void onResume(){
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_1_0, this, mLoaderCallback);
        super.onResume();
    }
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        /**
         * This is the callback method called once the OpenCV //manager is connected
         * @param status
         */
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS: {
                    Log.i("Example Loaded", "OpenCV loaded successfully");
                    //mOpenCvCameraView.enableView();
                } break;
                default: {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };


    private void onFloatingButtonPressed(){
        fragmentClass = FragmentRegistration.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isProtected()){
                    fragment = new FragmentRegistration();
                    currentMenuItem = R.id.nav_registration;
                    showProtectedDialog(fragment, "Fragment_Registration");
                } else {
                    try {
                        fragment = (Fragment) fragmentClass.newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    changeFragment(fragment, "Fragment_Registration");
                    nvDrawer.setCheckedItem(R.id.nav_registration);
                    currentMenuItem = R.id.nav_registration;
                    nvDrawer.getMenu().getItem(1).setChecked(true);
                    setTitle(getResources().getString(R.string.nav_registration));
                    fab.hide();

                }

            }
        });
    }

    public NavigationView getNavigationView() { return nvDrawer;}
    public FloatingActionButton getFab(){
        return fab;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.activity_main);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    private void changeFragment(Fragment targetFragment, String fragmentString){
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.main, targetFragment, fragmentString)
                .commit();
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }


    public int getCurrentMenuItem(){
        return currentMenuItem;
    }

    private void selectDrawerItem(MenuItem menuItem) {
        fragment = null;
        FragmentManager fragmentManager = getFragmentManager();
        switch (menuItem.getItemId()) {
            case R.id.nav_list:
                fragment = new FragmentOverview();
                changeFragment(fragment, "Fragment_Overview");
                fab.show();
                currentMenuItem = R.id.nav_list;
                break;
            case R.id.nav_registration:
                fragment = new FragmentRegistration();
                if (isProtected()){
                    showProtectedDialog(fragment, "Fragment_Registration");
                } else {
                    changeFragment(fragment, "Fragment_Registration");
                    fab.hide();
                    currentMenuItem = R.id.nav_registration;
                }
                fab.hide();
                break;
            case R.id.nav_edit:
                fragment = new FragmentEdit();
                if (isProtected()){
                    showProtectedDialog(fragment, "Fragment_Edit");

                } else {
                    fragment = new FragmentEdit();
                    changeFragment(fragment, "Fragment_Edit");
                    fab.show();
                    currentMenuItem = R.id.nav_edit;
                }
                break;
            case R.id.nav_settings:
                fragment = new FragmentSettings();
                changeFragment(fragment,"Fragment_Settings");
                fab.show();
                currentMenuItem = R.id.nav_settings;
                break;
            case R.id.nav_help:
                fragment = new FragmentHelp();
                changeFragment(fragment, "Fragment_Help");
                fab.show();
                currentMenuItem = R.id.nav_help;
                break;
        }

        // Highlight the selected item, update the title, and close the drawer
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        drawer.closeDrawer(GravityCompat.START);
    }

    public int getTabHeigth(){
        TabLayout tabLayout = (TabLayout)findViewById(R.id.my_tab_layout);
        Log.i("tablayout", String.valueOf(tabLayout.getHeight()));
        return tabLayout.getHeight();
        /*Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        Log.i("toolbar", String.valueOf(toolbar.getHeight()));
        return toolbar.getHeight();*/

    }

    public boolean isProtected(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean showProtectionDialog = pref.getBoolean("pref_key_showPassword",false);
        return showProtectionDialog;
    }

    public void showProtectedDialog(final Fragment fragment, final String stringFragment){
        passwordDialog = new AlertDialog.Builder(this,R.style.DialogTheme);
        passwordDialog.setCancelable(false);
        passwordDialog.setTitle(getResources().getString(R.string.enterPassword));
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        params.setMargins(16, 16, 16, 16);
        password = new EditText(this);
        password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        layout.addView(password, params);
        passwordDialog.setView(layout);
        passwordDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                final String enteredPassword = password.getText().toString();
                if (enteredPassword.equals(getPassword())) {
                    changeFragment(fragment, stringFragment);
                    if (currentMenuItem == R.id.nav_registration) {
                        changeFragment(fragment, "Fragment_Registration");
                        nvDrawer.setCheckedItem(R.id.nav_registration);
                        currentMenuItem = R.id.nav_registration;
                        nvDrawer.getMenu().getItem(1).setChecked(true);
                        setTitle(getResources().getString(R.string.nav_registration));
                        fab.hide();
                    }
                } else {
                    showFalsePasswordDialog(fragment, stringFragment);
                }
            }
        });
        passwordDialog.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FragmentOverview fragmentOverview = new FragmentOverview();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.main, fragmentOverview, "Fragment_Overview").commit();
                currentMenuItem = R.id.nav_view;
                nvDrawer.getMenu().getItem(0).setChecked(true);
                setTitle(getResources().getString(R.string.nav_list));
                fab.show();
                dialog.dismiss();
            }
        });
        passwordDialog.show();
    }


    public void showFalsePasswordDialog(final Fragment fragment, final String stringFragment){
        AlertDialog.Builder falsePasswordDialog = new AlertDialog.Builder(this);
        falsePasswordDialog.setTitle("Falsches Passwort");
        falsePasswordDialog.setPositiveButton(getResources().getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showProtectedDialog(fragment, stringFragment);
            }
        });
        falsePasswordDialog.show();
    }

    public String getPassword(){
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        String password = pref.getString("password", null);
        return password;
    }


}
