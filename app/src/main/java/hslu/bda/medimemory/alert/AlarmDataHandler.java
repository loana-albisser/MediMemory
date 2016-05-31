package hslu.bda.medimemory.alert;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

import hslu.bda.medimemory.database.DbAdapter;
import hslu.bda.medimemory.entity.ConsumeIndividual;
import hslu.bda.medimemory.entity.ConsumeInterval;
import hslu.bda.medimemory.entity.Data;

/**
 * Created by Andy on 20.05.2016.
 */
public class AlarmDataHandler {
    SharedPreferences sharedPreferences;
    private Activity activity;
    private DbAdapter dbAdapter;
    private long minuteInMillis = 60*1000;

    public AlarmDataHandler(Activity activity){
        this.activity = activity;
        this.dbAdapter = new DbAdapter(activity.getApplicationContext());
        this.sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity.getApplicationContext());

    }

    /**
     *
     * @param now calculating difference of next Item
     * @return diff in millis from next item and now
     */
    private long getNextIndividual(Calendar now){
        dbAdapter.open();
        ConsumeIndividual consumeIndividual = null;
        Calendar next = new GregorianCalendar();
        long diff =0;
        for(ConsumeIndividual item : ConsumeIndividual.getAllConsumeIndividual(dbAdapter)){
            diff = calcDiffByIndividualAndNow(consumeIndividual,now);
            if(consumeIndividual==null && diff >0){
                consumeIndividual = item;
                next.setTimeInMillis(diff+now.getTimeInMillis());
            }else{
                if((next.getTimeInMillis()-now.getTimeInMillis()) >diff){
                    consumeIndividual = item;
                    next.setTimeInMillis(diff+now.getTimeInMillis());
                }
            }
        }

        dbAdapter.close();
        return now.getTimeInMillis()-next.getTimeInMillis();
    }

    private long calcDiffByIndividualAndNow(ConsumeIndividual consumeIndividual, Calendar now) {
        long result = 0;
        long daytime = 0;
        long eattime = 0;

        switch (consumeIndividual.getDaypart().getId()){
            case 0:
                daytime = sharedPreferences.getLong("pref_key_morning_reminder",-1);
                break;
            case 1:
                daytime = sharedPreferences.getLong("pref_key_noon_reminder",-1);
                break;
            case 2:
                daytime =  sharedPreferences.getLong("pref_key_evening_reminder",-1);
                break;
            case 3:
                daytime = sharedPreferences.getLong("pref_key_night_reminder",-1);
                break;
        }

        switch (consumeIndividual.getEatpart().getId()){

            case 0:
                eattime = -1*sharedPreferences.getInt("pref_key_before_food",0)*60*1000;
                break;
            case 1:
                eattime = sharedPreferences.getInt("pref_key_after_food",0)*60*1000;
                break;
            case 2:
                eattime = 0;
                break;
        }

        result = (daytime+eattime)-now.getTimeInMillis();

        return result;
    }

    /**
     *
     * @param now calculating difference of next Item
     * @return diff in millis from next item and now
     */
    private long getNextInterval(Calendar now){
        dbAdapter.open();
        ConsumeInterval consumeInterval = null;
        Calendar temp = new GregorianCalendar();
        Calendar next = new GregorianCalendar();
        for(ConsumeInterval item : ConsumeInterval.getAllConsumeInterval(dbAdapter)){
            if(item.getWeekday()==-1 || item.getWeekday() == now.get(Calendar.DAY_OF_WEEK)) {
                if (item.getStartTime().getTimeInMillis() <= now.getTimeInMillis() && now.getTimeInMillis() < item.getEndTime().getTimeInMillis()) {
                    //calculate Inveraltime
                    long amountOfInterval = calcDiffByConsumeIntervalAndNow(item, now);
                    temp.setTimeInMillis(item.getStartTime().getTimeInMillis() + amountOfInterval * item.getInterval());
                    if (consumeInterval == null) {
                        //set first itemalways
                        consumeInterval = item;
                        next.setTimeInMillis(temp.getTimeInMillis());
                    } else {
                        //set closer Medi if one exists already
                        if (now.getTimeInMillis() - temp.getTimeInMillis() < now.getTimeInMillis() - next.getTimeInMillis()) {
                            next.setTimeInMillis(temp.getTimeInMillis());
                            consumeInterval = item;
                        }
                    }
                }
            }
        }
        dbAdapter.close();
        return (now.getTimeInMillis()-next.getTimeInMillis());
    }

    private long calcDiffByConsumeIntervalAndNow(ConsumeInterval item, Calendar now) {
        long duration = now.getTimeInMillis()-item.getStartTime().getTimeInMillis();
        long result = (long)Math.ceil(duration/(item.getInterval()*60*60*1000));
        return result;
    }

    public Calendar getNextAlarmTime(){
        Calendar now = new GregorianCalendar();
        now.setTime(new Date());
        Calendar next = new GregorianCalendar();
        long interval = getNextInterval(now);
        long individual = getNextIndividual(now);
        if(interval>individual){
            next.setTimeInMillis(now.getTimeInMillis()+individual);
        }else{
            next.setTimeInMillis(now.getTimeInMillis()+interval);
        }


        return next;
    }

    /**
     * Returns all Data-Object wich have a pill to take. +-1min of clocktime
     * @param alarmTime
     * @return Collection of Data-Objects
     */
    public Collection<Data> getAllDataByTime(Calendar alarmTime){
        Collection<Data> allData = new ArrayList<Data>();
        dbAdapter.open();
        boolean toConsume = false;
        for(Data item : Data.getAllDataFromTable(dbAdapter)){
            if(hasIntervalConsume(alarmTime, item.getAllConsumeInterval())){
                allData.add(item);
            } else if(hasIndividualConsume(alarmTime, item.getAllConsumeIndividual())){
                allData.add(item);
            }
        }

        dbAdapter.close();
        return allData;
    }

    /**
     * returns boolean if/if not a pill is to take in the ConsumeSettings
     * @param alarmTime
     * @param allConsumeIndividual
     * @return
     */
    private boolean hasIndividualConsume(Calendar alarmTime, Collection<ConsumeIndividual> allConsumeIndividual) {
        boolean result = false;

        for(ConsumeIndividual item:allConsumeIndividual){
            long diff = calcDiffByIndividualAndNow(item,alarmTime);
            if((diff >=0 && diff <= minuteInMillis) || (diff >= -1*minuteInMillis && diff<=0)){
                result = true;
                break;
            }
        }
        return result;
    }

    private boolean hasIntervalConsume(Calendar alarmTime, Collection<ConsumeInterval> allConsumeInterval) {
        boolean result = false;
        for(ConsumeInterval item: allConsumeInterval){
            long diff = calcDiffByConsumeIntervalAndNow(item, alarmTime);
            if((diff >=0 && diff <= minuteInMillis) || (diff >= -1*minuteInMillis && diff<=0)){
                result = true;
                break;
            }
        }
        return result;
    }
}
