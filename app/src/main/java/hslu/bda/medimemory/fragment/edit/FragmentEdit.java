package hslu.bda.medimemory.fragment.edit;


import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Bundle;

import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;

import hslu.bda.medimemory.R;
import hslu.bda.medimemory.database.DbAdapter;
import hslu.bda.medimemory.entity.Data;
import hslu.bda.medimemory.fragment.overview.FragmentOverview;
import hslu.bda.medimemory.fragment.registration.FragmentRegistration;
import hslu.bda.medimemory.fragment.MainActivity;
import hslu.bda.medimemory.fragment.settings.FragmentSettings;

/**
 * Created by Loana on 01.03.2016.
 */
public class FragmentEdit extends Fragment {
    private DbAdapter dbAdapter;
    private ListView listView;
    private ViewGroup root;
    private ViewGroup itemView;
    private int id;
    private FragmentRegistration fragmentRegistration;
    private FragmentOverview fragmentOverview;
    private FragmentSettings fragmentSettings;
    private Context context;
    private Collection<Data> allPills;
    private Data allPillsByID;
    private ArrayList<String> pillNames;
    private TextView txt_edit;
    private TextView pillname;
    private CheckBox chk_active;
    //test

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.fragment_edit, container, false);
        itemView = (ViewGroup) inflater.inflate(R.layout.fragment_edit_item, container, false);
        allPills = new ArrayList<>();
        dbAdapter= new DbAdapter(getActivity().getApplicationContext());
        dbAdapter.open();
        showItems();
        return root;
    }

    /**
     * shows all registered pills in a listView
     */
    private void showItems() {
        listView = (ListView) root.findViewById(R.id.lv_edit);
        txt_edit = (TextView)root.findViewById(R.id.txt_edit);
        allPills = Data.getAllDataFromTable(dbAdapter);
        pillNames = new ArrayList<>();
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listView.setItemsCanFocus(false);
        listView.setClickable(true);

        for(Data pill: allPills){
            pillNames.add(pill.getDescription());
        }

        if (pillNames.size() == 0){
            listView.setVisibility(View.GONE);
            txt_edit.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.VISIBLE);
            txt_edit.setVisibility(View.GONE);
            listView.setAdapter(new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_multiple_choice, pillNames) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    /*TextView textView = (TextView) super.getView(position, convertView, parent);
                    textView.setTextColor(Color.BLACK);
                    long[] selectedIds = listView.getCheckItemIds();
                    return textView;*/
                    if (convertView == null) {
                        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        convertView = layoutInflater.inflate(R.layout.fragment_edit_item, null);
                        pillname = (TextView) convertView.findViewById(R.id.txt_editItem);
                        pillname.setClickable(true);
                        pillname.setText(pillNames.get(position));
                        //pillname.setTextColor(Color.BLACK);
                        chk_active = (CheckBox) convertView.findViewById(R.id.chk_active);
                        setCheckBoxListener();
                        convertView.setTag(pillname);
                        convertView.setTag(chk_active);

                    } else {
                        convertView.getTag();
                    }
                    showRegistrationFragment();
                    return convertView;
                }
            });

        }
    }

    private void setCheckBoxListener(){
        chk_active.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    Toast.makeText(getActivity(), getResources().getString(R.string.toast_active), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), getResources().getString(R.string.toast_inactive), Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    /**
     * shows the RegistraionFragment from selected pill
     */
    private void showRegistrationFragment(){
        pillname.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) getActivity()).getFab().hide();
                fragmentRegistration = new FragmentRegistration();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.main, fragmentRegistration, "Fragment_Registration").commit();
            }
        });
    }





    private void setData(){

    }


}
