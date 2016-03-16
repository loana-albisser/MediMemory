package hslu.bda.medimemory.fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;

import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;

import hslu.bda.medimemory.R;

public class MainActivity extends AppCompatActivity{
    private DrawerLayout drawer;
    private Fragment fragment = null;
    private Class fragmentClass;
    private FragmentRegistration fragmentRegistration;
    private FloatingActionButton fab;
    private NavigationView nvDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        fab =(FloatingActionButton)findViewById(R.id.fab);
        fab.hide();
        setSupportActionBar(toolbar);
        //setup Fragment
        fragmentClass = FragmentOverview.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        getFragmentManager().beginTransaction().replace(R.id.main, fragment, "Fragment_Overview").commit();

        drawer = (DrawerLayout) findViewById(R.id.activity_main);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        nvDrawer = (NavigationView) findViewById(R.id.nav_view);
        nvDrawer.setCheckedItem(R.id.nav_list);
        nvDrawer.getMenu().getItem(0).setChecked(true);
        onFloatingButtonPressed();
        // Setup drawer view
        setupDrawerContent(nvDrawer);
    }


    private void onFloatingButtonPressed(){
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentClass = FragmentRegistration.class;
                try {
                    fragment = (Fragment) fragmentClass.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                changeRegistrationFragment(fragment);
                nvDrawer.setCheckedItem(R.id.nav_registration);
                nvDrawer.getMenu().getItem(1).setChecked(true);
                setTitle(getResources().getString(R.string.nav_registration));
                fab.hide();
            }
        });
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

    private void changeRegistrationFragment(Fragment targetFragment){
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.main, targetFragment, "Fragment_Registration")
                .commit();
    }

    public void onReminderDayTimeRadioButtonClick(View v){
        fragmentRegistration.showReminderDaytimeDialog();
    }

    public void onReminderIntervalRadioButtonClick(View v){
        fragmentRegistration.showReminderIntervalDialog();
    }

    public void onDurationNumOfBlistersRadioButtonClick(View v) {
        fragmentRegistration.changeNumberOfBlisterTextField();
        fragmentRegistration.showNumberOfBlistersNumberPickerDialog();
        fragmentRegistration.setCurrentNumberOfBlistersValue();
    }

    public void onDurationDateRadioButtonClick(View v) {
        fragmentRegistration.showDateDialog();
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

    private void selectDrawerItem(MenuItem menuItem) {
        Fragment fragment = null;
        FragmentManager fragmentManager = getFragmentManager();
        switch (menuItem.getItemId()) {
            case R.id.nav_list:
                fragment = new FragmentOverview();
                fragmentManager.beginTransaction().replace(R.id.main, fragment, "Fragment_Overview").commit();
                fab.show();
                break;
            case R.id.nav_registration:
                fragmentRegistration = new FragmentRegistration();
                fragmentManager.beginTransaction().replace(R.id.main, fragmentRegistration ,"Fragment_Registration").commit();
                fab.hide();
                break;
            case R.id.nav_edit:
                fragment = new FragmentEdit();
                fragmentManager.beginTransaction().replace(R.id.main, fragment, "Fragment_Edit").commit();
                fab.show();
                break;
            case R.id.nav_settings:
                fragment = new FragmentSettings();
                fragmentManager.beginTransaction().replace(R.id.main, fragment, "Fragment_Settings").commit();
                fab.show();
                break;
            case R.id.nav_help:
                fragment = new FragmentHelp();
                fragmentManager.beginTransaction().replace(R.id.main, fragment, "Fragment_Help").commit();
                fab.show();
                break;
        }

        //

        // Highlight the selected item, update the title, and close the drawer
        menuItem.setChecked(true);
        setTitle(menuItem.getTitle());
        drawer.closeDrawer(GravityCompat.START);
    }




}
