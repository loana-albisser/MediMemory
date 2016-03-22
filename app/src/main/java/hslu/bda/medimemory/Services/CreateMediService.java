package hslu.bda.medimemory.services;

import hslu.bda.medimemory.database.DbAdapter;
import hslu.bda.medimemory.entity.ConsumeIndividual;
import hslu.bda.medimemory.entity.ConsumeInterval;
import hslu.bda.medimemory.entity.Data;

/**
 * Created by Andy on 21.03.2016.
 */
public class CreateMediService {

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
            dbID = dbAdapter.createDbObject(data);
            data.setId(dbID);
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
                consumeIndividual.setId(dbAdapter.createDbObject(consumeIndividual));
            }

            for(ConsumeInterval consumeInterval : data.getAllConsumeInterval()){
                consumeInterval.setId(dbAdapter.createDbObject(consumeInterval));
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
        }catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
