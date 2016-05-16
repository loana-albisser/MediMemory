package hslu.bda.medimemory.fragment.edit;


import android.app.Fragment;
import android.app.FragmentManager;
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
import hslu.bda.medimemory.fragment.MainActivity;
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
    private List<Data> list;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.fragment_edit, container, false);
        dbAdapter= new DbAdapter(getActivity().getApplicationContext());
        dbAdapter.open();
        allPills = Data.getAllDataFromTable(dbAdapter);
        list = new ArrayList(allPills);
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

            listClick();
        }
    }

    /**
     * set selected listview position
     * @param pos position of clicked item
     */
    private void setPosition(int pos){
        this.position = pos;
    }

    /**
     * get clicked listview position
     * @return position
     */
    public int getPosition(){
        return position;
    }

    private void listClick(){
        listView = (ListView) root.findViewById(R.id.lv_edit);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                setPosition(position);
                showRegistrationFragment();
            }
        });
    }

    /**
     * shows the RegistrationFragment from selected pill
     */
    private void showRegistrationFragment(){
            ((MainActivity) getActivity()).getFab().hide();
            fragmentRegistration = new FragmentRegistration();
            setMediId(list.get(getPosition()).getId());
            FragmentManager fragmentManager = ((MainActivity) getActivity()).getFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.main, fragmentRegistration, "Fragment_Registration").commit();
    }

    /**
     * set selected mediId
     * @param id the id of the pill
     */
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

}
