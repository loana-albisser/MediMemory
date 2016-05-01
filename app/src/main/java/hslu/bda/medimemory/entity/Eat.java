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
public class Eat implements DbObject{

    private int id;
    private String description;

    /**
     * Empty Constructor
     */
    public Eat(){this.setId(-1);}

    /**
     * Constructor to return Object with supplied fields
     * @param description of the eatpart
     */
    public Eat(String description){
        this.setId(-1);
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
            eat = copyContentValuesToObject(contentValues);
        }else{eat=null;}

        return eat;
    }

    public static Collection<Eat> getAllEatValues(DbAdapter dbAdapter){
        Collection<Eat> allEatValues = new ArrayList<Eat>();
        try{
            Collection<ContentValues> allContentValues = dbAdapter.getAllByTable(DbHelper.TABLE_MEDI_EAT);
            for(ContentValues contentValues : allContentValues){
                Eat eat = copyContentValuesToObject(contentValues);
                allEatValues.add(eat);
            }
        }catch (Exception e){
            System.console().printf(e.getMessage());
        }

        return allEatValues;
    }

    private static Eat copyContentValuesToObject(ContentValues contentValues) {
        Eat eat = new Eat();
        eat.setId(contentValues.getAsInteger(DbHelper.COLUMN_ID));
        eat.setDescription(contentValues.getAsString(DbHelper.COLUMN_DESC));
        return  eat;
    }
}
