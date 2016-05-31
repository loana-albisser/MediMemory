package hslu.bda.medimemory.entity;

import android.content.ContentValues;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;

import hslu.bda.medimemory.contract.DbObject;
import hslu.bda.medimemory.database.DbAdapter;
import hslu.bda.medimemory.database.DbHelper;

/**
 * Created by manager on 07.03.2016.
 */
public class ConsumeInterval implements DbObject {

    private int id;
    private int mediid;
    private Calendar startTime;
    private Calendar endTime;
    private int interval;
    private int weekday;
    private boolean changed;
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

    public ConsumeInterval(){this.setId(-1);};

    public ConsumeInterval(int mediid, Calendar startTime, Calendar endTime, int interval, int weekday){
        this.setId(-1);
        this.setMediid(mediid);
        this.setStartTime(startTime);
        this.setEndTime(endTime);
        this.setInterval(interval);
        this.setWeekday(weekday);
        this.setChanged(true);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        this.setChanged(true);
    }

    public int getMediid() {
        return mediid;
    }

    public void setMediid(int mediid) {
        this.mediid = mediid;
        this.setChanged(true);
    }

    public Calendar getStartTime() {
        return startTime;
    }

    /**
     * Use GregorianCalendar
     * @param startTime
     */
    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
        this.setChanged(true);
    }

    /**
     * use GregorianCalendar
     * @return
     */
    public Calendar getEndTime() {
        return endTime;
    }

    public void setEndTime(Calendar endTime) {
        this.endTime = endTime;
        this.setChanged(true);
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
        this.setChanged(true);
    }

    /**
     * Returns Calendar.DAY_OF_WEEK id of the weekday
     * @return
     */
    public int getWeekday() {
        return weekday;
    }

    /**
     * set weekday over Calendar.DAY_OF_WEEK
     * @param weekday
     */
    public void setWeekday(int weekday) {
        this.weekday = weekday;
        this.setChanged(true);
    }

    public boolean isChanged() {
        return changed;
    }

    private void setChanged(boolean changed) {
        this.changed = changed;
    }

    @Override
    public ContentValues getContentValues() {
        final ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_ID,getId());
        values.put(DbHelper.COLUMN_MEDIID, getMediid());
        if(getStartTime()!=null) {
            values.put(DbHelper.COLUMN_STARTTIME, simpleDateFormat.format(getStartTime().getTime()));
        }
        else{
            values.put(DbHelper.COLUMN_STARTTIME, (String)null);
        }
        if(getEndTime()!=null) {
            values.put(DbHelper.COLUMN_ENDTIME, simpleDateFormat.format(getEndTime().getTime()));
        }
        else{
            values.put(DbHelper.COLUMN_ENDTIME, (String)null);
        }

        values.put(DbHelper.COLUMN_INTERVAL, getInterval());
        values.put(DbHelper.COLUMN_WEEKDAY, getWeekday());

        return values;
    }

    @Override
    public String getTableName() {
        return DbHelper.TABLE_MEDI_CONSINTER;
    }

    @Override
    public String getPrimaryFieldName() {
        return DbHelper.COLUMN_ID;
    }

    @Override
    public String getPrimaryFieldValue() {
        return String.valueOf(getId());
    }

    public static Collection<ConsumeInterval> getAllConsumeIntervalByMedid(int medid, DbAdapter dbAdapter){
        Collection<ConsumeInterval> allConsumeInterval = new ArrayList<ConsumeInterval>();
        Collection<ContentValues> allContentValues =
                dbAdapter.getAllByTable(DbHelper.TABLE_MEDI_CONSINTER,
                        new String[]{DbHelper.COLUMN_MEDIID},new String[]{String.valueOf(medid)});
        if(allContentValues!=null) {
            for(ContentValues contentValues:allContentValues){
                allConsumeInterval.add(copyContentValuesToObject(contentValues));
            }
        }

        return allConsumeInterval;
    }

    /**
     * only returns active Data
     * @param dbAdapter
     * @return
     */
    public static Collection<ConsumeInterval> getAllConsumeInterval(DbAdapter dbAdapter){
        Collection<ConsumeInterval> allConsumeInterval = new ArrayList<ConsumeInterval>();
        Collection<ContentValues> allContentValues =
                dbAdapter.getAllByTable(DbHelper.TABLE_MEDI_CONSINTER);
        if(allContentValues!=null) {
            for(ContentValues contentValues:allContentValues){
                ConsumeInterval consumeInterval =copyContentValuesToObject(contentValues);
                if(dbAdapter.isMediActive(consumeInterval.getMediid())) {
                    allConsumeInterval.add(consumeInterval);
                }
            }
        }

        return allConsumeInterval;
    }

    private static ConsumeInterval copyContentValuesToObject(ContentValues contentValues) {
        ConsumeInterval consumeInterval = new ConsumeInterval();
        consumeInterval.setId(contentValues.getAsInteger(DbHelper.COLUMN_ID));
        consumeInterval.setMediid(contentValues.getAsInteger(DbHelper.COLUMN_MEDIID));
        try{
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(simpleDateFormat.parse(contentValues.getAsString(DbHelper.COLUMN_STARTTIME)));
            consumeInterval.setStartTime(calendar);
        }catch(Exception e) {consumeInterval.setStartTime(null);}
        try{
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(simpleDateFormat.parse(contentValues.getAsString(DbHelper.COLUMN_ENDTIME)));
            consumeInterval.setEndTime(calendar);
        }catch(Exception e) {consumeInterval.setStartTime(null);}
        consumeInterval.setInterval(contentValues.getAsInteger(DbHelper.COLUMN_INTERVAL));
        consumeInterval.setWeekday(contentValues.getAsInteger(DbHelper.COLUMN_WEEKDAY));
        consumeInterval.setChanged(false);
        return  consumeInterval;
    }


}
