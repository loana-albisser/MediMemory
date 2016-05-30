package hslu.bda.medimemory.services;

import hslu.bda.medimemory.contract.DbObject;
import hslu.bda.medimemory.database.DbAdapter;
import hslu.bda.medimemory.entity.ConsumeIndividual;
import hslu.bda.medimemory.entity.ConsumeInterval;
import hslu.bda.medimemory.entity.Data;
import hslu.bda.medimemory.entity.PillCoords;

/**
 * Created by Andy on 01.05.2016.
 */
public class UpdateMediService {
    private UpdateMediService(){};

    public static boolean updateTableEntry(DbObject dbObject, DbAdapter dbAdapter) throws Throwable{
        boolean result = false;
        try{
            dbAdapter.startTransaction();
            if(dbObject.isChanged()) {
                result = dbAdapter.updateDbObject(dbObject);
            }
            dbAdapter.setTransactionSuccessful();
        }catch (Exception e){
            result = false;
            throw new RuntimeException(e);
        }finally {
            dbAdapter.endTransaction();
        }
        return result;
    }

    public static boolean updateDataObject(Data data, DbAdapter dbAdapter) throws Throwable{
        boolean result = false;
        try{
            dbAdapter.startTransaction();
            if(data.isChanged()) {
                dbAdapter.updateDbObject(data);
            }
            updateSubObjects(data, dbAdapter);
            dbAdapter.setTransactionSuccessful();
        }catch (Exception e){
            result = false;
            throw new RuntimeException(e);
        }finally {
            dbAdapter.endTransaction();
        }
        return result;
    }

    private static void updateSubObjects(Data data, DbAdapter dbAdapter) {
        try{
            dbAdapter.startTransaction();
            for(ConsumeIndividual consumeIndividual: data.getAllConsumeIndividual()){
                if(consumeIndividual.getId()!=-1 && consumeIndividual.isChanged()) {
                    dbAdapter.updateDbObject(consumeIndividual);
                }
            }

            for(ConsumeInterval consumeInterval : data.getAllConsumeInterval()){
                if(consumeInterval.getId()!=-1 && consumeInterval.isChanged()) {
                    dbAdapter.updateDbObject(consumeInterval);
                }
            }

            for(PillCoords pillCoords : data.getAllPillCoords()){
                if(pillCoords.getId()!=-1 && pillCoords.isChanged()) {
                    dbAdapter.updateDbObject(pillCoords);
                }
            }

            dbAdapter.setTransactionSuccessful();
        }catch (Exception e){
            throw new RuntimeException(e);
        }finally {
            dbAdapter.endTransaction();
        }
    }
}
