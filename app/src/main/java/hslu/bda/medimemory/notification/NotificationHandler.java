package hslu.bda.medimemory.notification;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.Collection;

import hslu.bda.medimemory.R;
import hslu.bda.medimemory.database.DbAdapter;
import hslu.bda.medimemory.database.DbHelper;
import hslu.bda.medimemory.entity.Data;

/**
 * Created by Andy on 22.05.2016.
 */
public class NotificationHandler{
    private Context context;

    public NotificationHandler(Context context){
        this.context = context;
    }

    @SuppressWarnings({"NewApi","deprecation"})
    public void createNotification(Data[] medis){
        Intent notifyIntent = new Intent(context, NotifyReceiverActivity.class);
        notifyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        notifyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int[] allIds = new int[medis.length];
        String contentText = "";
        for(int i = 0; i<medis.length; i++){
            allIds[i] = medis[i].getId();
            contentText+=medis[i].getDescription() +" Anzahl:"+medis[i].getAmount()+"\n";
        }
        notifyIntent.putExtra(DbHelper.TABLE_MEDI_DATA,allIds);
        PendingIntent pIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(),
                notifyIntent, 0);
        Notification noti;
        if(Build.VERSION.SDK_INT>=16) {
            noti = new Notification.Builder(context)
                    .setContentTitle("MediAlert")
                    .setContentText(contentText)
                    .setSmallIcon(R.drawable.example_pill)
                    .setContentIntent(pIntent)
                    .addAction(R.drawable.check_mark, "alles akzeptieren", pIntent)
                    .addAction(R.drawable.circle, "alles verwerfen", pIntent)
                    .addAction(R.drawable.question_mark, "öffnen", pIntent)
                    .setAutoCancel(true)
                    .build();
        }
        else{
             noti = new Notification.Builder(context)
                    .setContentTitle("Titel")
                    .setContentText("Subject").setSmallIcon(R.drawable.example_pill)
                    .setContentIntent(pIntent)
                     .setAutoCancel(true)
                    .getNotification();
        }

        NotificationManager notificationManager =
                (NotificationManager)context.getSystemService(Service.NOTIFICATION_SERVICE);
        notificationManager.notify(0,noti);
    }
}
