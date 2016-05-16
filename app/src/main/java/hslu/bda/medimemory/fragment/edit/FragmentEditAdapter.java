package hslu.bda.medimemory.fragment.edit;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import hslu.bda.medimemory.fragment.overview.FragmentOverviewChild;
import hslu.bda.medimemory.fragment.registration.FragmentRegistration;

/**
 * Created by Loana on 12.05.2016.
 */
public class FragmentEditAdapter extends ArrayAdapter <Data> {
    private Collection<Data> allPills;
    private DbAdapter dbAdapter;
    private TextView pillname;
    private ListView listView;
    private CheckBox chk_active;
    private List<Data> list;


    public FragmentEditAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        dbAdapter= new DbAdapter(getContext());
        dbAdapter.open();
        allPills = new ArrayList<>();
        allPills = Data.getAllDataFromTable(dbAdapter);
    }

    public FragmentEditAdapter(Context context, int textViewResourceId, ArrayList<Data> allPills) {
        super(context, textViewResourceId, allPills);
        dbAdapter= new DbAdapter(getContext());
        dbAdapter.open();
        this.allPills = allPills;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.fragment_edit,null);
        listView = (ListView)view.findViewById(R.id.lv_edit);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.fragment_edit_item, null);
            pillname = (TextView) convertView.findViewById(R.id.txt_editItem);
            chk_active = (CheckBox) convertView.findViewById(R.id.chk_active);
            setFocus(false, chk_active);
            list = new ArrayList(allPills);
            Data data = list.get(position);
            pillname.setText(data.getDescription());
            setCheckBoxListener();
        }
        return convertView;
    }

    private void setFocus (boolean focus, View view){
        view.setFocusable(focus);
        view.setFocusable(focus);
    }

   public void setCheckBoxListener(){
        chk_active.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.toast_active), Toast.LENGTH_LONG).show();
                    /*pillname.setTextColor(ContextCompat.getColor(getContext(), R.color.itemSelected));
                    pillname.setClickable(false);
                    list.get(listView.getSelectedItemPosition()).setActive(1);*/
                } else {
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.toast_inactive), Toast.LENGTH_LONG).show();
                    /*pillname.setTextColor(ContextCompat.getColor(getContext(), R.color.itemUnselected));
                    pillname.setClickable(true);
                    list.get(listView.getSelectedItemPosition()).setActive(0);*/
                }
            }
        });
    }

}
