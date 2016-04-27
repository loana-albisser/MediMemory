package hslu.bda.medimemory.fragment.edit;


import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.Collection;

import hslu.bda.medimemory.R;
import hslu.bda.medimemory.database.DbAdapter;
import hslu.bda.medimemory.entity.Data;
import hslu.bda.medimemory.entity.Day;
import hslu.bda.medimemory.fragment.registration.FragmentRegistration;
import hslu.bda.medimemory.fragment.MainActivity;

/**
 * Created by Loana on 01.03.2016.
 */
public class FragmentEdit extends Fragment {
    private DbAdapter dbAdapter;
    private ListView listView;
    private ViewGroup root;
    private FragmentRegistration fragmentRegistration;
    private Context context;
    private Collection<Data> allPills;
    private ArrayList<String> pillNames;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.fragment_edit, container, false);
        allPills = new ArrayList<>();
        dbAdapter= new DbAdapter(getActivity().getApplicationContext());
        dbAdapter.open();

        showItems();
        showRegistrationFragment();
        return root;
    }

    /**
     * shows all registered pills in a listView
     */
    private void showItems() {
        listView = (ListView) root.findViewById(R.id.lv_edit);
        TextView txt_edit = (TextView)root.findViewById(R.id.txt_edit);
        allPills = Data.getAllDataFromTable(dbAdapter);
        pillNames = new ArrayList<>();

        for(Data pill: allPills){
            pillNames.add(pill.getDescription());
        }

        if (pillNames.size() == 0){
            listView.setVisibility(View.GONE);
            txt_edit.setVisibility(View.VISIBLE);
        } else {
            listView.setVisibility(View.VISIBLE);
            txt_edit.setVisibility(View.GONE);

            listView.setAdapter(new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, pillNames) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    TextView textView = (TextView) super.getView(position, convertView, parent);
                    textView.setTextColor(Color.BLACK);
                    return textView;
                }
            });

        }

    }

    /**
     * shows the RegistraionFragment from selected pill
     */
    private void showRegistrationFragment(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ((MainActivity) getActivity()).getFab().hide();
                fragmentRegistration = new FragmentRegistration();
                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.main, fragmentRegistration, "Fragment_Registration").commit();
            }
        });
    }

    private void setData(){
        //Data data = new Data();
        //fragmentRegistration.setName(data.getDescription());
    }
}
