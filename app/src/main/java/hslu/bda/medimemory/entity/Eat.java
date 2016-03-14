package hslu.bda.medimemory.entity;

import android.content.ContentValues;

import hslu.bda.medimemory.contract.DbObject;
import hslu.bda.medimemory.database.DbAdapter;
import hslu.bda.medimemory.database.DbHelper;

/**
 * Created by tbeugste on 07.03.2016.
 */
public class Eat implements DbObject{

    private int id;
    private String description;

    /**
     * Empty Constructor
     */
    public Eat(){}

    /**
     * Constructor to return Object with supplied fields
     * @param id of the eatpart
     * @param description of the eatpart
     */
    public Eat(int id, String description){
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
     * @param id of the eatpart
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * getter-Method of the description
     * @return description of the eatpart
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter-Method of the description
     * @param description of the eatpart
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
        return DbHelper.TABLE_MEDI_EAT;
    }

    @Override
    public String getPrimaryFieldName() {
        return DbHelper.COLUMN_ID;
    }

    @Override
    public String getPrimaryFieldValue() {
        return String.valueOf(getId());
    }

    public static Eat getEatById(String id, DbAdapter dbAdapter) {
        Eat eat = new Eat();
        eat.setId(Integer.parseInt(id));
        ContentValues contentValues = dbAdapter.getByObject(eat);
        if(contentValues!= null) {
            eat = copyContentValuesToObject(contentValues, dbAdapter);
        }else{eat=null;}

        return eat;
    }

    private static Eat copyContentValuesToObject(ContentValues contentValues, DbAdapter dbAdapter) {
        Eat eat = new Eat();
        eat.setId(contentValues.getAsInteger(DbHelper.COLUMN_ID));
        eat.setDescription(contentValues.getAsString(DbHelper.COLUMN_MEDIID));
        return  eat;
    }
}
