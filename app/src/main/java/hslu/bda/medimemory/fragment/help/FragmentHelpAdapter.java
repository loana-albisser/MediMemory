package hslu.bda.medimemory.fragment.help;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hslu.bda.medimemory.R;

/**
 * Created by Loana on 09.04.2016.
 */
public class FragmentHelpAdapter extends BaseExpandableListAdapter {
    private Context context;
    private String [] header;
    private String [][]children;

    FragmentHelpAdapter(Context context){
        this.context = context;
        setQuestion();
        setAnswer();
    }


    /**
     * sets all shown questions
     */
    public void setQuestion(){
        header = new String[]{
                context.getString(R.string.question_1),
                context.getString(R.string.question_2),
                context.getString(R.string.question_3),
                context.getString(R.string.question_4),
                context.getString(R.string.question_5),
                context.getString(R.string.question_6),
                context.getString(R.string.question_7),
                context.getString(R.string.question_8),
                context.getString(R.string.question_9),
                context.getString(R.string.question_10),
                context.getString(R.string.question_11),
                context.getString(R.string.question_12),
                context.getString(R.string.question_13)

        };
    }

    /**
     * sets all shown answers
     */
    public void setAnswer(){
        children = new String[][]{
                {context.getString(R.string.answer_1)},
                {context.getString(R.string.answer_2)},
                {context.getString(R.string.answer_3)},
                {context.getString(R.string.answer_4)},
                {context.getString(R.string.answer_5)},
                {context.getString(R.string.answer_6)},
                {context.getString(R.string.answer_7)},
                {context.getString(R.string.answer_8)},
                {context.getString(R.string.answer_9)},
                {context.getString(R.string.answer_10)},
                {context.getString(R.string.answer_11)},
                {context.getString(R.string.answer_12)},
                {context.getString(R.string.answer_13)}
        };
    }


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
            LayoutInflater inflater = (LayoutInflater) context.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.fragment_help_child, null);
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
