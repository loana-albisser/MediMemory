package hslu.bda.medimemory.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collection;

import hslu.bda.medimemory.contract.DbObject;

/**
 * Created by tbeugste on 11.03.2016.
 */
public class DbAdapter {

    private DbHelper dbHelper;
    private SQLiteDatabase db;

    public DbAdapter(final Context context) {dbHelper = new DbHelper(context); }

    public void open() {
        if(db == null | !db.isOpen())
        {
            db = dbHelper.getWritableDatabase();
        }
    }

    public void close (){
        dbHelper.close();
        db.close();
        db = null;
    }

    public long CreateDbObject(DbObject dbObject) {

        long i=-1;
        if(db.isOpen() && !db.isReadOnly()) {
            db = dbHelper.getWritableDatabase();
            try {
                ContentValues contentValues = dbObject.getContentValues();
                contentValues.remove(DbHelper.COLUMN_ID);
                i = db.insert(dbObject.getTableName(), null, contentValues);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return i;
    }

    /**
     * Returns ContentValues of a dbObject's id
     * @param dbObject Object filled with at least the ID
     * @return All ColumnData's of supplied Object
     */
    public ContentValues getByObject(DbObject dbObject) {
        ContentValues contentValues = new ContentValues();

        if(dbObject.getPrimaryFieldValue()!=null) {
            String selection = dbObject.getPrimaryFieldName() + " =?";
            String [] args = new String[]{dbObject.getPrimaryFieldValue()};
            Cursor result = db.query(dbObject.getTableName(),null,selection,args,null,null
                    ,dbObject.getPrimaryFieldName());
            if(result.moveToFirst()) {
                for(int i =0; i<result.getColumnCount(); i++){
                    contentValues.put(result.getColumnName(i), result.getString(i));
                }
            }
        }

        return contentValues;
    }

    public Collection<ContentValues> getAllByTable(String table){
        Collection<ContentValues> allContentValues = new ArrayList<>();;
        try {
            Cursor result = db.query(table, null,null, null, null, null, DbHelper.COLUMN_ID);
            if (result.moveToFirst()) {

                do {
                    ContentValues contentValues = new ContentValues();
                    for (int i = 0; i < result.getColumnCount(); i++) {
                        contentValues.put(result.getColumnName(i), result.getString(i));
                    }
                    allContentValues.add(contentValues);
                } while (result.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return allContentValues;
    }

    public boolean updateDbObject(DbObject dbObject) {
        return db.update(dbObject.getTableName(), dbObject.getContentValues(),
                dbObject.getPrimaryFieldName() + "=" + dbObject.getPrimaryFieldValue(), null)>0;
    }

    public boolean deleteDbObject(DbObject dbObject) {
        return db.delete(dbObject.getTableName(), dbObject.getPrimaryFieldName() +
                "=" + dbObject.getPrimaryFieldValue(), null)>0;
    }

    public Collection<ContentValues> getAllByTable(String table, String[] selectionField,
                                                   String[] selectionValue){
        Collection<ContentValues> allContentValues = new ArrayList<>();
        try {
            String selection = null;
            if(selectionField !=null){
                selection = "";
                for (String field:selectionField){
                    if(selection.length()>0)
                    {
                        selection += " AND ";
                    }
                    selection +=selection+" =?";
                }
            }
            Cursor result =
                    db.query(table, null, selection + " =?", selectionValue, null, null,
                            DbHelper.COLUMN_ID);
            if (result.moveToFirst()) {

                do {
                    ContentValues contentValues = new ContentValues();
                    for (int i = 0; i < result.getColumnCount(); i++) {
                        contentValues.put(result.getColumnName(i), result.getString(i));
                    }
                    allContentValues.add(contentValues);
                } while (result.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return allContentValues;
    }
}