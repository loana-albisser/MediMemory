package hslu.bda.medimemory.notification;

import android.app.Activity;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;

import hslu.bda.medimemory.R;
import hslu.bda.medimemory.database.DbAdapter;
import hslu.bda.medimemory.database.DbHelper;
import hslu.bda.medimemory.entity.Consumed;
import hslu.bda.medimemory.entity.Data;
import hslu.bda.medimemory.entity.PillCoords;
import hslu.bda.medimemory.entity.Status;
import hslu.bda.medimemory.fragment.edit.FragmentEditAdapter;
import hslu.bda.medimemory.services.PillService;

/**
 * Created by Andy on 22.05.2016.
 */
public class NotifyReceiverActivity extends Activity{

    private DbAdapter dbAdapter;
    private int[] allId;
    private CharSequence[] message;
    private ArrayList<Data> messageData;
    private ListView listView;
    private TextView txt_edit;
    private NotifyReceiverActivityAdapter activityAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        this.setFinishOnTouchOutside(false);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_notify_dialog);

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

        showItems();
    }

    /**
     * shows all registered pills in a listView
     */
    private void showItems() {
        listView = (ListView)findViewById(R.id.lv_notify);
        listView.setItemsCanFocus(false);
        if (messageData.size() == 0){
            listView.setVisibility(View.GONE);
        } else {
            listView.setVisibility(View.VISIBLE);
            activityAdapter = new NotifyReceiverActivityAdapter(this, R.layout.fragment_notify_dialog, messageData);
            listView.setAdapter(activityAdapter);
            listView.setVisibility(View.VISIBLE);
        }
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

    public void consumeSelectedPills(View view) {
        Collection<Consumed> allConsumed = new ArrayList<Consumed>();
        for(Data data:messageData){
            Status status;
            if(activityAdapter.getSelData().contains(data)) {
                status = Status.getStatusById(Status.STATUS_EINGENOMMEN, dbAdapter);
            }else{
                status = Status.getStatusById(Status.STATUS_AUSSTEHEND, dbAdapter);
            }
            PillCoords pillCoords = PillCoords.getNextPillByMedid(data.getId(), dbAdapter);
            Consumed consumed = new Consumed();
            consumed.setMediid(data.getId());
            consumed.setStatus(status);
            consumed.setPillCoord(pillCoords);
            consumed.setPointInTime(Calendar.getInstance());
            allConsumed.add(consumed);
        }
        try {
            PillService.addConsumedPill(allConsumed, dbAdapter);
        }catch (Throwable e){
            Log.i("NotifyRecAbortSelection",e.getMessage());
        }
        super.onBackPressed();
    }

    public void abortSelection(View view) {
        Collection<Consumed> allConsumed = new ArrayList<Consumed>();
        for(Data data:messageData){
            PillCoords pillCoords = PillCoords.getNextPillByMedid(data.getId(),dbAdapter);
            Status status = Status.getStatusById(Status.STATUS_AUSSTEHEND, dbAdapter);

            Consumed consumed = new Consumed();
            consumed.setMediid(data.getId());
            consumed.setStatus(status);
            consumed.setPillCoord(pillCoords);
            consumed.setPointInTime(Calendar.getInstance());
            allConsumed.add(consumed);
        }
        try {
            PillService.addConsumedPill(allConsumed, dbAdapter);
        }catch (Throwable e){
            Log.i("NotifyRecAbortSelection",e.getMessage());
        }
        super.onBackPressed();
    }
}
