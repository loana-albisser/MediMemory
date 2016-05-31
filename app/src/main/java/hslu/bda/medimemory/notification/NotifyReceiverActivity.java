package hslu.bda.medimemory.notification;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Collection;

import hslu.bda.medimemory.database.DbAdapter;
import hslu.bda.medimemory.database.DbHelper;
import hslu.bda.medimemory.entity.Data;

/**
 * Created by Andy on 22.05.2016.
 */
public class NotifyReceiverActivity extends Activity{

    private DbAdapter dbAdapter;
    private int[] allId;
    CharSequence[] message;
    ArrayList<Data> messageData;
    Collection<Data> selList = new ArrayList<Data>();


    DialogInterface.OnMultiChoiceClickListener pillPickListener = new DialogInterface.OnMultiChoiceClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which, boolean isChecked) {
            if (isChecked) {
                selList.add(messageData.get(which));
            } else if (selList.contains(which)) {
                selList.remove(messageData.get(which));
            }
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        dbAdapter = new DbAdapter(getApplicationContext());
        dbAdapter.open();

        allId = getIntent().getIntArrayExtra(DbHelper.TABLE_MEDI_DATA);
        message = new CharSequence[allId.length];
        messageData = new ArrayList<Data>();
        for(int i=0;i<allId.length;i++){
            Data data = Data.getDataById(String.valueOf(allId[i]),dbAdapter);
            message[i]=data.getDescription()+"\n";
            messageData.add(data);
        }
        new AlertDialog.Builder(NotifyReceiverActivity.this)
                .setTitle("Wähle die eingenommenen Medikamente")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setOnCancelListener(new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel(DialogInterface dialog) {
                        finish();
                    }
                })
                .setMultiChoiceItems(message, null, pillPickListener)
                .show();
    }


    @Override
    protected void onResume() {
        if (dbAdapter == null) {
            dbAdapter = new DbAdapter(getApplicationContext());
            dbAdapter.open();
        }
        super.onResume();
    }

    @Override
    protected void onStop(){
        if(dbAdapter!=null) {
            dbAdapter.close();
            dbAdapter = null;
        }
        super.onStop();
    }
}
