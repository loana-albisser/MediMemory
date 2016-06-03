package hslu.bda.medimemory.notification;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import hslu.bda.medimemory.R;
import hslu.bda.medimemory.database.DbAdapter;
import hslu.bda.medimemory.entity.Data;
import hslu.bda.medimemory.fragment.edit.FragmentEdit;
import hslu.bda.medimemory.services.UpdateMediService;

/**
 * Created by Andy on 01.06.2016.
 */
public class NotifyReceiverActivityAdapter extends ArrayAdapter<Data> {
    private Holder holder;
    private List<Data> list;
    private Collection<Data> selData = new ArrayList<Data>();

    public NotifyReceiverActivityAdapter(Context context, int resource, List<Data> objects) {
        super(context, resource, objects);
        list = objects;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        holder = new Holder();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.fragment_notify_dialog_item, null);
            holder.textView = (TextView) convertView.findViewById(R.id.txt_editItem);
            holder.checkBox = (CheckBox) convertView.findViewById(R.id.chk_active);
            setFocus(false, holder.checkBox);
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
     * holds item of listview
     */
    private class Holder {
        private CheckBox checkBox;
        private TextView textView;

        private Holder(){

        }
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
                    setTextViewActive(textView);
                    if(!selData.contains(list.get((Integer)checkBox.getTag()))) {
                        selData.add(list.get((Integer) checkBox.getTag()));
                    }
                } else {
                    setTextViewInActive(textView);
                    if(selData.contains(list.get((Integer)checkBox.getTag()))) {
                        selData.remove(list.get((Integer) checkBox.getTag()));
                    }
                }
            }
        });
    }

    public Collection<Data> getSelData(){
        return selData;
    }
}
