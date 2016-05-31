package hslu.bda.medimemory.entity;

import android.content.ContentValues;

import org.opencv.core.Point;

import java.io.Serializable;
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
    private boolean changed = false;

    public Consumed(){this.setId(-1);}

    public Consumed(int mediid, Calendar pointInTime, Status status, PillCoords pillCoords){
        this.setId(-1);
        this.setMediid(mediid);
        this.setPointInTime(pointInTime);
        this.setStatus(status);
        this.setPillCoord(pillCoords);
        this.setChanged(true);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
        this.changed=true;
    }

    public int getMediid() {
        return mediid;
    }

    public void setMediid(int mediid) {
        this.mediid = mediid;
        this.changed=true;
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
        this.changed=true;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
        this.changed=true;
    }

    public boolean isChanged() {
        return changed;
    }

    private void setChanged(boolean changed) {
        this.changed = changed;
    }

    public PillCoords getPillCoord() {
        return pillCoord;
    }

    public void setPillCoord(PillCoords pillCoord) {
        this.pillCoord = pillCoord;
        this.changed=true;
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

    public static Consumed getConsumedByPillCoord(PillCoords pillCoords, DbAdapter dbAdapter){
        Consumed allConsumed = null;
        Collection<ContentValues> allContentValues =
                dbAdapter.getAllByTable(DbHelper.TABLE_MEDI_CONSUMED,
                        new String[]{DbHelper.COLUMN_POINT},new String[]{String.valueOf(pillCoords.getId())});
        if(allContentValues!=null) {
            for(ContentValues contentValues:allContentValues){
                allConsumed = copyContentValuesToObject(contentValues, dbAdapter);
                break;
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
        consumed.setPillCoord(PillCoords.getPillCoordById(contentValues.getAsString(DbHelper.COLUMN_POINT), dbAdapter));
        consumed.setChanged(false);
        return  consumed;
    }



}
