package hslu.bda.medimemory.entity;

import android.content.ContentValues;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import hslu.bda.medimemory.contract.DbObject;
import hslu.bda.medimemory.database.DbAdapter;
import hslu.bda.medimemory.database.DbHelper;

/**
 * Created by tbeugste on 07.03.2016.
 */
public class Day implements DbObject {

    private int id;
    private String description;
    private boolean changed;

    /**
     * Empty Constructor
     */
    public Day(){
        this.setId(-1);
        this.setChanged(true);
    }

    /**
     * Constructor to return Object with supplied fields
     * @param description of the daypart
     */
    public Day(int id, String description){
        this.setId(id);
        this.setDescription(description);
        this.setChanged(false);
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
        this.setChanged(true);
    }

    /**
     * getter-Method of the description
     * @return description of the daypart
     */
    public String getDescription() {
        return description;
    }

    @Override
    public String toString(){
        return this.getDescription();
    }

    /**
     * Setter-Method of the description
     * @param description of the daypart
     */
    public void setDescription(String description) {
        this.description = description;
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
        Collection<Day> allDayValues = new ArrayList<Day>();
        try{
            Collection<ContentValues> allContentValues = dbAdapter.getAllByTable(DbHelper.TABLE_MEDI_DAY);
            for(ContentValues contentValues : allContentValues){
                Day day = copyContentValuesToObject(contentValues);
                allDayValues.add(day);
            }
        }catch (Exception e){
            System.out.printf(e.getMessage());
        }

        return allDayValues;
    }

    private static Day copyContentValuesToObject(ContentValues contentValues) {
        Day day = new Day();
        day.setId(contentValues.getAsInteger(DbHelper.COLUMN_ID));
        day.setDescription(contentValues.getAsString(DbHelper.COLUMN_DESC));
        day.setChanged(false);
        return day;
    }



}
