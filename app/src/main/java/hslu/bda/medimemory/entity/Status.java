package hslu.bda.medimemory.entity;

import android.content.ContentValues;

import hslu.bda.medimemory.contract.DbObject;
import hslu.bda.medimemory.database.DbAdapter;
import hslu.bda.medimemory.database.DbHelper;

/**
 * Created by manager on 07.03.2016.
 */
public class Status implements DbObject{

    private int id;
    private String description;

    /**
     * Empty Constructor
     */
    public Status(){}

    /**
     * Constructor to return Object with supplied fields
     * @param id of the status
     * @param description of the status
     */
    public Status(int id, String description){
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
     * @param id of the status
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * getter-Method of the description
     * @return description of the status
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter-Method of the description
     * @param description of the status
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
        return DbHelper.TABLE_MEDI_STATUS;
    }

    @Override
    public String getPrimaryFieldName() {
        return DbHelper.COLUMN_ID;
    }

    @Override
    public String getPrimaryFieldValue() {
        return String.valueOf(getId());
    }

    public static Status getStatusById(String id, DbAdapter dbAdapter) {
        Status status = new Status();
        status.setId(Integer.parseInt(id));
        ContentValues contentValues = dbAdapter.getByObject(status);
        if(contentValues!= null) {
            status = copyContentValuesToObject(contentValues, dbAdapter);
        }else{status=null;}

        return status;
    }

    private static Status copyContentValuesToObject(ContentValues contentValues, DbAdapter dbAdapter) {
        Status status = new Status();
        status.setId(contentValues.getAsInteger(DbHelper.COLUMN_ID));
        status.setDescription(contentValues.getAsString(DbHelper.COLUMN_MEDIID));
        return  status;
    }


}