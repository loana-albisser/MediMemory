package hslu.bda.medimemory.fragment.edit;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import hslu.bda.medimemory.R;
import hslu.bda.medimemory.database.DbAdapter;
import hslu.bda.medimemory.entity.Data;
import hslu.bda.medimemory.fragment.overview.FragmentOverview;
import hslu.bda.medimemory.fragment.overview.FragmentOverviewChild;
import hslu.bda.medimemory.fragment.registration.FragmentRegistration;
import hslu.bda.medimemory.fragment.settings.FragmentSettings;

/**
 * Created by Loana on 01.03.2016.
 */
public class FragmentEdit extends Fragment {
    private DbAdapter dbAdapter;
    private ListView listView;
    private ViewGroup root;
    private ViewGroup registerView;
    private int id;
    private FragmentRegistration fragmentRegistration;
    private FragmentEditAdapter editAdapter;
    private int position;
    private FragmentOverview fragmentOverview;
    private FragmentSettings fragmentSettings;
    private Context context;
    private Collection<Data> allPills;
    private ArrayList<Data>pills;
    private Data pillData;
    private TextView txt_edit;
    private TextView pillname;
    private CheckBox chk_active;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.fragment_edit, container, false);
        dbAdapter= new DbAdapter(getActivity().getApplicationContext());
        dbAdapter.open();
        allPills = Data.getAllDataFromTable(dbAdapter);
        showItems();
        return root;
    }


    /**
     * shows all registered pills in a listView
     */
    private void showItems() {
        listView = (ListView) root.findViewById(R.id.lv_edit);
        listView.setItemsCanFocus(false);
        listView.setClickable(true);

        txt_edit = (TextView)root.findViewById(R.id.txt_edit);

        if (allPills.size() == 0){
            listView.setVisibility(View.GONE);
            txt_edit.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.VISIBLE);
            txt_edit.setVisibility(View.GONE);
            editAdapter = new FragmentEditAdapter(getActivity(), R.layout.fragment_edit /*,(<Data>) allPills*/);
            // Populate the list, through the adapter
            for (Data data : getEntries()){
                editAdapter.add(data);
            }
            listView.setAdapter(editAdapter);
            listView.setVisibility(View.VISIBLE);
            getPosition();
        }
    }



    private void setPosition(int pos){
        this.position = pos;
    }

    public int getPosition(){
        listView = (ListView) root.findViewById(R.id.lv_edit);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setPosition(position);
                Toast.makeText(getActivity(),getPosition(),Toast.LENGTH_LONG).show();
                editAdapter.setCheckBoxListener();

            }
        });
        return position;
    }

    private void setMediId(int id){
        Bundle bundle = new Bundle();
        bundle.putInt("mediId", id);
        fragmentRegistration.setArguments(bundle);
    }

    private List<Data> getEntries() {
        final List<Data> entries = new ArrayList<Data>();
        for(Data data: allPills) {
            entries.add(data);
        }
        return entries;
    }




            // Populate the list, through the adapter

            /*listView.setAdapter(new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_multiple_choice, pillNames) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    if (convertView == null) {
                        LayoutInflater layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                        convertView = layoutInflater.inflate(R.layout.fragment_edit_item, null);
                        pillname = (TextView) convertView.findViewById(R.id.txt_editItem);
                        pillname.setClickable(true);
                        pillname.setText(pillNames.get(position));
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
            });*/



    /**
     * shows the RegistrationFragment from selected pill
     */




}
