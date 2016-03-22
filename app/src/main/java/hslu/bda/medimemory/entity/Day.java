package hslu.bda.medimemory.entity;

import android.content.ContentValues;

import java.util.ArrayList;
import java.util.Collection;

import hslu.bda.medimemory.contract.DbObject;
import hslu.bda.medimemory.database.DbAdapter;
import hslu.bda.medimemory.database.DbHelper;

/**
 * Created by tbeugste on 07.03.2016.
 */
public class Day implements DbObject{

    private int id;
    private String description;

    /**
     * Empty Constructor
     */
    public Day(){}

    /**
     * Constructor to return Object with supplied fields
     * @param id of the daypart
     * @param description of the daypart
     */
    public Day(int id, String description){
        this.setId(id);
        this.setDescription(description);
    }

    /**
     * getter-Method of the ID
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * setter-Method of the id
     * @param id of the daypart
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * getter-Method of the description
     * @return description of the daypart
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter-Method of the description
     * @param description of the daypart
     */
    public void setDescription(String description) {
        this.description = description;
    }


    @Override
    public ContentValues getContentValues() {
        final ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_ID,getId());
        values.put(DbHelper.COLUMN_DESC, getDescription());

        return values;
    }

    @Override
    public String getTableName() {
        return DbHelper.TABLE_MEDI_DAY;
    }

    @Override
    public String getPrimaryFieldName() {
        return DbHelper.COLUMN_ID;
    }

    @Override
    public String getPrimaryFieldValue() {
        return String.valueOf(getId());
    }

    public static Day getDayById(String id, DbAdapter dbAdapter) {
        Day day = new Day();
        day.setId(Integer.parseInt(id));
        ContentValues contentValues = dbAdapter.getByObject(day);
        if(contentValues!= null) {
            day = copyContentValuesToObject(contentValues);
        }else{day=null;}

        return day;
    }

    public static Collection<Day> getAllDayValues(DbAdapter dbAdapter){
        Collection<Day> allDayValues =null;
        try{
            Collection<ContentValues> allContentValues = dbAdapter.getAllByTable(DbHelper.TABLE_MEDI_DAY);
            for(ContentValues contentValues : allContentValues){
                Day day = copyContentValuesToObject(contentValues);
                allDayValues.add(day);
            }
        }catch (Exception e){
            System.console().printf(e.getMessage());
        }

        return allDayValues;
    }

    private static Day copyContentValuesToObject(ContentValues contentValues) {
        Day day = new Day();
        day.setId(contentValues.getAsInteger(DbHelper.COLUMN_ID));
        day.setDescription(contentValues.getAsString(DbHelper.COLUMN_DESC));
        return day;
    }
}
