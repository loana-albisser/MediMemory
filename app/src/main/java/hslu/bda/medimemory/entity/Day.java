package hslu.bda.medimemory.entity;

import android.content.ContentValues;

import hslu.bda.medimemory.contract.DbObject;
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
}
