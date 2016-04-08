package hslu.bda.medimemory.fragment;


import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;

import hslu.bda.medimemory.R;
import hslu.bda.medimemory.database.DbAdapter;
import hslu.bda.medimemory.entity.Data;

/**
 * Created by Loana on 01.03.2016.
 */
public class FragmentEdit extends Fragment {
    private DbAdapter dbAdapter;
    private ListView listView;
    private ViewGroup root;
    private FragmentRegistration fragmentRegistration;
    private Context context;
    private Activity mActivity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        root = (ViewGroup) inflater.inflate(R.layout.fragment_edit, container, false);
        dbAdapter= new DbAdapter(getActivity().getApplicationContext());
        dbAdapter.open();
        showItems();
        showRegistrationFragment();
        return root;
    }

    @TargetApi(23)
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity)context;
    }
    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            mActivity = activity;
        }
    }


    private void showItems() {
        listView = (ListView) root.findViewById(R.id.lv_edit);
        String[] testlist = new String[]{"Item1","Item2","Item3","Item4","Item5"};
        // // TODO: 23.03.2016 no Items! 
        /*ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity().getApplicationContext(),getAllPills.getName());
        listView.setAdapter(adapter);*/
        listView.setAdapter(new ArrayAdapter<String>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, testlist) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                textView.setTextColor(Color.BLACK);
                return textView;
            }
        });
    }



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
        Data data = new Data();
        fragmentRegistration.setName(data.getDescription());
    }
}
