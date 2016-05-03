package hslu.bda.medimemory.services;

import hslu.bda.medimemory.contract.DbObject;
import hslu.bda.medimemory.database.DbAdapter;

/**
 * Created by Andy on 03.05.2016.
 */
public class DeleteMediService {

    private DeleteMediService(){}

    public static boolean deleteDbObject(DbObject dbObject, DbAdapter dbAdapter) throws Throwable{
        boolean result = false;
        try{
            dbAdapter.startTransaction();
            result = dbAdapter.deleteDbObject(dbObject);
            dbAdapter.setTransactionSuccessful();
        }catch (Exception e){
            result = false;
            throw new RuntimeException(e);
        }
        finally {
            dbAdapter.endTransaction();
        }
        return result;
    }

    public static boolean deleteAllEntryByTableAndMedId(String table, int mediid, DbAdapter dbAdapter) throws Throwable{
        boolean result = false;
        try{
            dbAdapter.startTransaction();
            result = dbAdapter.deleteTableEntryByMedID(table, mediid);
            dbAdapter.setTransactionSuccessful();
        }catch (Exception e){
            result = false;
            throw new RuntimeException(e);
        }
        finally {
            dbAdapter.endTransaction();
        }
        return result;
    }
}
