package hslu.bda.medimemory.fragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import hslu.bda.medimemory.R;

/**
 * Created by Loana on 09.04.2016.
 */
public class FragmentHelpAdapter extends BaseExpandableListAdapter {
    private Context context;

    FragmentHelpAdapter(Context context){
        this.context = context;
    }


    private String[] header = { "Was ist MediMemory?", "Wie kann ich ein Medikament hinzufügen?", "Wozu muss ein Foto aufgenommen werden?", "Wie kann ich ine Medikament löschen?" };

    private String[][] children = {
            { "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. " },
            { "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum. Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet." },
            { "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam" },
            { "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam" }
    };

    @Override
    public int getGroupCount() {
        return header.length;
    }

    @Override
    public int getChildrenCount(int i) {
        return children[i].length;
    }

    @Override
    public Object getGroup(int i) {
        return header[i];
    }

    @Override
    public Object getChild(int i, int i1) {
        return children[i][i1];
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(R.layout.fragment_help_header, null);
        }
        TextView textView = (TextView)view.findViewById(R.id.txt_expListHeader);
        textView.setText(getGroup(i).toString());
        return view;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater infalInflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = infalInflater.inflate(R.layout.fragment_help_child, null);
        }
        TextView textView = (TextView)view.findViewById(R.id.txt_expListItem);
        textView.setText(getChild(i, i1).toString());
        return view;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }

}
