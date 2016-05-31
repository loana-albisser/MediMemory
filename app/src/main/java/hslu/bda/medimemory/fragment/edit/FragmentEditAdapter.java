package hslu.bda.medimemory.fragment.edit;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
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
import hslu.bda.medimemory.services.UpdateMediService;

/**
 * Created by Loana on 12.05.2016.
 */
public class FragmentEditAdapter extends ArrayAdapter <Data> {
    private Collection<Data> allPills;
    private DbAdapter dbAdapter;
    private TextView pillname;
    private FragmentEdit fragmentEdit;
    private List<Data> list;
    private Holder holder;
    private Data activeElement;


    public FragmentEditAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        dbAdapter= new DbAdapter(getContext());
        dbAdapter.open();
        allPills = new ArrayList<>();
        allPills = Data.getAllDataFromTable(dbAdapter);
        fragmentEdit = new FragmentEdit();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        holder = new Holder();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.fragment_edit_item, null);
            holder.textView = (TextView) convertView.findViewById(R.id.txt_editItem);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.chk_active);
            setFocus(false, holder.checkBox);
            list = new ArrayList(allPills);
            Data data = list.get(position);
            setMediActive(data);
            holder.textView.setText(data.getDescription());
            holder.checkBox.setTag(position);
            setCheckBoxListener(holder.textView, holder.checkBox);
        }
        return convertView;
    }

    private void setFocus (boolean focus, View view){
        view.setFocusable(focus);
        view.setFocusable(focus);
    }

    /**
     * sets a medi active of inactive
     * @param textView the related textview
     * @param checkBox the selected checkbox
     */
   private void setCheckBoxListener(final TextView textView, final CheckBox checkBox){
       holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.toast_active), Toast.LENGTH_LONG).show();
                    setTextViewActive(textView);
                    Log.i("Checkbox.Tag", String.valueOf(checkBox.getTag()));
                    list.get((Integer)checkBox.getTag()).setActive(1);


                } else {
                    Toast.makeText(getContext(), getContext().getResources().getString(R.string.toast_inactive), Toast.LENGTH_LONG).show();
                    setTextViewInActive(textView);
                    Log.i("Checkbox.Tag", String.valueOf(checkBox.getTag()));
                    list.get((Integer)checkBox.getTag()).setActive(0);
                }
                try {
                    UpdateMediService.updateTableEntry(list.get((Integer)checkBox.getTag()),dbAdapter);
                } catch (Throwable throwable) {
                    throwable.printStackTrace();
                }
            }
        });
    }

    /**
     * sets the look of the textview if active
     * @param textView the related textview
     */
    private void setTextViewActive(TextView textView){
        textView.setTextColor(ContextCompat.getColor(getContext(), R.color.itemSelected));
        textView.setClickable(false);
    }

    /**
     * sets the look of the textview if active
     * @param textView the related textview
     */
    private void setTextViewInActive(TextView textView){
        textView.setTextColor(ContextCompat.getColor(getContext(), R.color.itemUnselected));
        textView.setClickable(true);
    }

    /**
     * sets medi active or inactive in relation to database entry
     * @param data
     */
    private void setMediActive(Data data){
        if (data.getActive()==1){
            holder.checkBox.setChecked(true);
            setTextViewActive(holder.textView);
        } else {
            holder.checkBox.setChecked(false);
            setTextViewInActive(holder.textView);
        }
    }


    /**
     * holds item of listview
     */
    private class Holder {
        private CheckBox checkBox;
        private TextView textView;

        private Holder(){

        }
    }



}
