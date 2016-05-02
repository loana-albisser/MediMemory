package hslu.bda.medimemory.services;

import hslu.bda.medimemory.contract.DbObject;
import hslu.bda.medimemory.database.DbAdapter;
import hslu.bda.medimemory.entity.ConsumeIndividual;
import hslu.bda.medimemory.entity.ConsumeInterval;
import hslu.bda.medimemory.entity.Data;
import hslu.bda.medimemory.entity.PillCoords;

/**
 * Created by Andy on 21.03.2016.
 */
public class CreateMediService {

    private CreateMediService(){}

    public static int addNewMedi(Data data, DbAdapter dbAdapter) throws Throwable{
        int result = -1;

        try{
            dbAdapter.startTransaction();
            data = createDataEntry(data, dbAdapter);
            setAllDataSubobjectWithNewID(data);
            createSubObjects(data, dbAdapter);
            dbAdapter.setTransactionSuccessful();
        }catch (Exception e){
            result = -1;
            throw new RuntimeException(e);
        }finally {
            dbAdapter.endTransaction();
        }
        result = data.getId();
        return result;
    }

    private static Data createDataEntry(Data data, DbAdapter dbAdapter) throws Throwable{
        int dbID = -1;
        try{
            dbAdapter.startTransaction();
            if(data.getId()==-1) {
                dbID = dbAdapter.createDbObject(data);
                data.setId(dbID);
            }
            dbAdapter.setTransactionSuccessful();
        }catch (Exception e){
            throw new RuntimeException(e);
        }finally {
            dbAdapter.endTransaction();
        }

        return data;
    }

    private static void createSubObjects(Data data, DbAdapter dbAdapter) throws Throwable{
        try{
            dbAdapter.startTransaction();
            for(ConsumeIndividual consumeIndividual: data.getAllConsumeIndividual()){
                if(consumeIndividual.getId()==-1) {
                    consumeIndividual.setId(dbAdapter.createDbObject(consumeIndividual));
                }
            }

            for(ConsumeInterval consumeInterval : data.getAllConsumeInterval()){
                if(consumeInterval.getId()==-1) {
                    consumeInterval.setId(dbAdapter.createDbObject(consumeInterval));
                }
            }

            for(PillCoords pillCoords : data.getAllPillCoords()){
                if(pillCoords.getId()==-1) {
                    pillCoords.setId(dbAdapter.createDbObject(pillCoords));
                }
            }
            dbAdapter.setTransactionSuccessful();
        }catch (Exception e){
            throw new RuntimeException(e);
        }finally {
            dbAdapter.endTransaction();
        }
    }

    private static void setAllDataSubobjectWithNewID(Data data) throws Throwable{
        try {
            for (ConsumeIndividual consumeIndividual : data.getAllConsumeIndividual()) {
                consumeIndividual.setMediid(data.getId());
            }

            for (ConsumeInterval consumeInterval : data.getAllConsumeInterval()) {
                consumeInterval.setMediid(data.getId());
            }

            for(PillCoords pillCoords : data.getAllPillCoords()){
                pillCoords.setMediid(data.getId());
            }
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }


    public static DbObject createTableEntry(DbObject dbObject, DbAdapter dbAdapter) throws Throwable{

        try{
            dbAdapter.startTransaction();
            dbObject.setId(dbAdapter.createDbObject(dbObject));
            dbAdapter.setTransactionSuccessful();
        }catch (Exception e){
            dbObject.setId(-1);
            throw new RuntimeException(e);
        }finally {
            dbAdapter.endTransaction();
        }
        return dbObject;
    }
}
