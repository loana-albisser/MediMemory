package hslu.bda.medimemory.entity;

import android.content.ContentValues;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import hslu.bda.medimemory.contract.DbObject;
import hslu.bda.medimemory.database.DbHelper;

/**
 * Created by manager on 07.03.2016.
 */
public class ConsumeInterval implements DbObject{

    private int id;
    private int mediid;
    private Calendar startTime;
    private Calendar endTime;
    private int interval;
    private int weekday;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

    public ConsumeInterval(){};

    public ConsumeInterval(int id, int mediid, Calendar startTime, Calendar endTime, int interval, int weekday){
        this.setId(id);
        this.setMediid(mediid);
        this.setStartTime(startTime);
        this.setEndTime(endTime);
        this.setInterval(interval);
        this.setWeekday(weekday);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMediid() {
        return mediid;
    }

    public void setMediid(int mediid) {
        this.mediid = mediid;
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
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
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
    }


    @Override
    public ContentValues getContentValues() {
        final ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_ID,getId());
        values.put(DbHelper.COLUMN_MEDIID, getMediid());
        values.put(DbHelper.COLUMN_STARTTIME, simpleDateFormat.format(getStartTime()));
        values.put(DbHelper.COLUMN_ENDTIME, simpleDateFormat.format(getEndTime()));
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
}
