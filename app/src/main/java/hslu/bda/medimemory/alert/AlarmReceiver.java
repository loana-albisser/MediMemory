package hslu.bda.medimemory.alert;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

/**
 * Created by Andy on 17.05.2016.
 */
public class AlarmReceiver extends BroadcastReceiver{


    @Override
    public void onReceive(Context context, Intent intent) {
        //TODO DO stuff on AlarmReceive
        Toast.makeText(context, "i'm running", Toast.LENGTH_SHORT).show();
    }
}
