package hslu.bda.medimemory.alert;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.Calendar;

/**
 * Created by Andy on 17.05.2016.
 */
public class DeviceBootReceiver extends BroadcastReceiver{
    private Activity activity;
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            /* Setting the alarm here */
            //TODO: RESET ALERT


            Intent alarmIntent = new Intent(activity, AlarmReceiver.class);
            alarmIntent.setAction("RepeatedAlert");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(activity, 0, alarmIntent, 0);

            AlarmManager manager = (AlarmManager) activity.getSystemService(Context.ALARM_SERVICE);
            AlarmDataHandler alarmDataHandler = new AlarmDataHandler(activity);

            //Calendar calendar = alarmDataHandler.getNextAlarmTime();
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 19);
            calendar.set(Calendar.MINUTE, 55);


            manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

            /*
            Intent alarmIntent = new Intent(context, AlarmReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, alarmIntent, 0);

            AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            int interval = 8000;
            manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
            */
            Toast.makeText(context, "Alarm Set", Toast.LENGTH_SHORT).show();

        }
    }
}
