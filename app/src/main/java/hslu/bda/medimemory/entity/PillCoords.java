package hslu.bda.medimemory.entity;

import android.content.ContentValues;

import org.opencv.core.Point;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;

import hslu.bda.medimemory.contract.DbObject;
import hslu.bda.medimemory.database.DbAdapter;
import hslu.bda.medimemory.database.DbHelper;

/**
 * Created by Andy on 21.04.2016.
 */
public class PillCoords implements DbObject{
    private int id;
    private int mediid;
    private Point coords;
    /**
     * empty Constructor
     */
    public PillCoords(){}


    public PillCoords(int id, int mediid, Point coords){
        this.setId(id);
        this.setMediid(mediid);
        this.setCoords(coords);
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

    public Point getCoords() {
        return coords;
    }

    public void setCoords(Point coords) {
        this.coords = coords;
    }

    @Override
    public ContentValues getContentValues() {
        final ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_ID,getId());
        values.put(DbHelper.COLUMN_MEDIID, getMediid());
        values.put(DbHelper.COLUMN_XCOORD, getCoords().x);
        values.put(DbHelper.COLUMN_YCOORD, getCoords().y);


        return values;
    }

    @Override
    public String getTableName() {
        return DbHelper.TABLE_MEDI_PILL_LOC;
    }

    @Override
    public String getPrimaryFieldName() {
        return DbHelper.COLUMN_ID;
    }

    @Override
    public String getPrimaryFieldValue() {
        return String.valueOf(getId());
    }

    public static Collection<PillCoords> getAllPillCoordsByMedid(int medid, DbAdapter dbAdapter){
        Collection<PillCoords> allPillCoords = new ArrayList<PillCoords>();
        Collection<ContentValues> allContentValues =
                dbAdapter.getAllByTable(DbHelper.TABLE_MEDI_PILL_LOC,
                        new String[]{DbHelper.COLUMN_MEDIID},new String[]{String.valueOf(medid)});
        if(allContentValues!=null) {
            for(ContentValues contentValues:allContentValues){
                allPillCoords.add(copyContentValuesToObject(contentValues));
            }
        }

        return allPillCoords;
    }

    private static PillCoords copyContentValuesToObject(ContentValues contentValues) {
        PillCoords pillCoords = new PillCoords();
        pillCoords.setId(contentValues.getAsInteger(DbHelper.COLUMN_ID));
        pillCoords.setMediid(contentValues.getAsInteger(DbHelper.COLUMN_MEDIID));
        Point point = new Point();
        point.x = contentValues.getAsDouble(DbHelper.COLUMN_XCOORD);
        point.y = contentValues.getAsDouble(DbHelper.COLUMN_YCOORD);
        pillCoords.setCoords(point);
        return  pillCoords;
    }

    public static PillCoords getPillCoordById(String id, DbAdapter dbAdapter) {
        PillCoords pillCoords = new PillCoords();
        pillCoords.setId(Integer.parseInt(id));
        ContentValues contentValues = dbAdapter.getByObject(pillCoords);
        if(contentValues!= null) {
            pillCoords = copyContentValuesToObject(contentValues);
        }else{pillCoords=null;}

        return pillCoords;
    }


}
