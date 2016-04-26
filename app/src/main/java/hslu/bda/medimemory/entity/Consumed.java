package hslu.bda.medimemory.entity;

import android.content.ContentValues;

import org.opencv.core.Point;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.GregorianCalendar;

import hslu.bda.medimemory.contract.DbObject;
import hslu.bda.medimemory.database.DbAdapter;
import hslu.bda.medimemory.database.DbHelper;

/**
 * Created by manager on 07.03.2016.
 */
public class Consumed implements DbObject {
    private int id;
    private PillCoords pillCoord;
    private int mediid;
    private Calendar pointInTime;
    private Status status;
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Consumed(){}

    public Consumed(int id, int mediid, Calendar pointInTime, Status status, PillCoords pillCoords){
        this.setId(id);
        this.setMediid(mediid);
        this.setPointInTime(pointInTime);
        this.setStatus(status);
        this.setPillCoord(pillCoords);
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

    public PillCoords getPillCoord() {
        return pillCoord;
    }

    public void setPillCoord(PillCoords pillCoord) {
        this.pillCoord = pillCoord;
    }

    @Override
    public ContentValues getContentValues() {
        final ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_ID,getId());
        values.put(DbHelper.COLUMN_MEDIID, getMediid());
        values.put(DbHelper.COLUMN_POINTINTIME, simpleDateFormat.format(getPointInTime().getTime()));
        values.put(DbHelper.COLUMN_STATUS, getStatus().getId());
        values.put(DbHelper.COLUMN_POINT, getPillCoord().getId());
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

    public static Collection<Consumed> getAllConsumedByMedid(int medid, DbAdapter dbAdapter){
        Collection<Consumed> allConsumed = new ArrayList<Consumed>();
        Collection<ContentValues> allContentValues =
                dbAdapter.getAllByTable(DbHelper.TABLE_MEDI_CONSUMED,
                        new String[]{DbHelper.COLUMN_MEDIID},new String[]{String.valueOf(medid)});
        if(allContentValues!=null) {
            for(ContentValues contentValues:allContentValues){
                allConsumed.add(copyContentValuesToObject(contentValues, dbAdapter));
            }
        }

        return allConsumed;
    }

    private static Consumed copyContentValuesToObject(ContentValues contentValues, DbAdapter dbAdapter) {
        Consumed consumed = new Consumed();
        consumed.setId(contentValues.getAsInteger(DbHelper.COLUMN_ID));
        consumed.setMediid(contentValues.getAsInteger(DbHelper.COLUMN_MEDIID));
        try{
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(simpleDateFormat.parse(contentValues.getAsString(DbHelper.COLUMN_POINTINTIME)));
            consumed.setPointInTime(calendar);
        }catch(Exception e) {consumed.setPointInTime(null);}
        consumed.setStatus(Status.getStatusById(contentValues.getAsString(DbHelper.COLUMN_STATUS),dbAdapter));
        consumed.setPillCoord(PillCoords.getPillCoordById(contentValues.getAsString(DbHelper.COLUMN_POINT),dbAdapter));
        return  consumed;
    }


}
