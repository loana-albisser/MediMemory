package hslu.bda.medimemory.alert;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Collection;

import hslu.bda.medimemory.entity.Data;
import hslu.bda.medimemory.notification.NotificationHandler;

/**
 * Created by Andy on 17.05.2016.
 */
public class AlarmReceiver extends BroadcastReceiver{
    private Context context;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction() == "RepeatedAlert") {
            this.context = context.getApplicationContext();
            AlarmDataHandler dataHandler = new AlarmDataHandler(context);
            Collection<Data> data = dataHandler.getAllDataByTime(Calendar.getInstance());
            if(data.size()>0) {
                NotificationHandler notificationHandler = new NotificationHandler(context);
                notificationHandler.createNotification(data.toArray(new Data[data.size()]));
            }
            Intent alarmIntent = new Intent(this.context, AlarmReceiver.class);
            alarmIntent.setAction("RepeatedAlert");
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this.context, 0, alarmIntent, 0);

            AlarmManager manager = (AlarmManager) this.context.getSystemService(Context.ALARM_SERVICE);
            AlarmDataHandler alarmDataHandler = new AlarmDataHandler(this.context);

            Calendar calendar = alarmDataHandler.getNextAlarmTime();
        /*
        calendar.set(Calendar.HOUR_OF_DAY, 18);
        calendar.set(Calendar.MINUTE, 6);
        */
            if (calendar != null && calendar.getTimeInMillis()>0) {
                manager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
            Toast.makeText(this.context, "Alarm Set- AlarmReceiver", Toast.LENGTH_SHORT).show();
        }

    }
}
