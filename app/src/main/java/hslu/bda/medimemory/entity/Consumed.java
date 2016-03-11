package hslu.bda.medimemory.entity;

import android.content.ContentValues;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import hslu.bda.medimemory.contract.DbObject;
import hslu.bda.medimemory.database.DbHelper;

/**
 * Created by manager on 07.03.2016.
 */
public class Consumed implements DbObject {
    private int id;
    private int mediid;
    private Calendar pointInTime;
    private Status status;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Consumed(){}

    public Consumed(int id, int mediid, Calendar pointInTime, Status status){
        this.setId(id);
        this.setMediid(mediid);
        this.setPointInTime(pointInTime);
        this.setStatus(status);
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

    /**
     * Use GregorianCalendar
     * @return
     */
    public Calendar getPointInTime() {
        return pointInTime;
    }



    /**
     * Use GregorianCalendar
     * @param pointInTime
     */
    public void setPointInTime(Calendar pointInTime) {
        this.pointInTime = pointInTime;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public ContentValues getContentValues() {
        final ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_ID,getId());
        values.put(DbHelper.COLUMN_MEDIID, getMediid());
        values.put(DbHelper.COLUMN_POINTINTIME, simpleDateFormat.format(getPointInTime()));
        values.put(DbHelper.COLUMN_STATUS, getStatus().getId());

        return values;
    }

    @Override
    public String getTableName() {
        return DbHelper.TABLE_MEDI_CONSUMED;
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
