package hslu.bda.medimemory.entity;

import android.content.ContentValues;

import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.Collection;

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
    private int height;
    private int width;

    /**
     * empty Constructor
     */
    public PillCoords(){this.setId(-1);}


    public PillCoords(int mediid, Point coords, int width, int height){
        this.setId(-1);
        this.setMediid(mediid);
        this.setCoords(coords);
        this.setWidth(width);
        this.setHeight(height);
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

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }
    @Override
    public ContentValues getContentValues() {
        final ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_ID,getId());
        values.put(DbHelper.COLUMN_MEDIID, getMediid());
        values.put(DbHelper.COLUMN_XCOORD, getCoords().x);
        values.put(DbHelper.COLUMN_YCOORD, getCoords().y);
        values.put(DbHelper.COLUMN_WIDTH, getWidth());
        values.put(DbHelper.COLUMN_HEIGHT, getHeight());


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
        pillCoords.setWidth(contentValues.getAsInteger(DbHelper.COLUMN_WIDTH));
        pillCoords.setHeight(contentValues.getAsInteger(DbHelper.COLUMN_HEIGHT));
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

    public static PillCoords getNextPillByMedid(int mediid, DbAdapter dbAdapter){
        PillCoords pillCoords = null;
        Collection<PillCoords> allPillCoords = getAllPillCoordsByMedid(mediid,dbAdapter);
        Collection<Consumed> allConsumedPills = Consumed.getAllConsumedByMedid(mediid, dbAdapter);

        for(PillCoords element:allPillCoords){
            boolean isConsumed = false;
            for(Consumed consumed :allConsumedPills){
                if(consumed.getPillCoord().getId()==element.getId()){
                    isConsumed = true;
                    break;
                }
            }
            if(!isConsumed){
                pillCoords=element;
                break;
            }
        }
        return pillCoords;
    }


}
