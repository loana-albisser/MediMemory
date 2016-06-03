package hslu.bda.medimemory.services;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

import hslu.bda.medimemory.database.DbAdapter;
import hslu.bda.medimemory.entity.Consumed;
import hslu.bda.medimemory.entity.PillCoords;
import hslu.bda.medimemory.entity.Status;

/**
 * Created by manager on 28.04.2016.
 */
public class PillService {

    private PillService(){}

    /**
     * Create or Update a Consumed Object and gives the Consumed-id back
     * @param status wich status should be set
     * @param pillCoords wich pill is consumed
     * @param dbAdapter for db-connection
     * @return id of consumed-object
     */
    public static long setConsumed(Status status, PillCoords pillCoords, DbAdapter dbAdapter) throws Throwable{
        long id = -1;
        Consumed consumed = Consumed.getConsumedByPillCoord(pillCoords, dbAdapter);
        if(consumed!=null){
            id = consumed.getId();
            consumed.setStatus(status);
            UpdateMediService.updateTableEntry(consumed, dbAdapter);
        }else{
            consumed = new Consumed();
            consumed.setStatus(status);
            consumed.setPillCoord(pillCoords);
            Calendar cal = new GregorianCalendar();
            cal.setTime(new Date());
            consumed.setPointInTime(cal);
            consumed.setMediid(pillCoords.getMediid());
            consumed = (Consumed)CreateMediService.createTableEntry(consumed,dbAdapter);
            id = consumed.getId();
        }
        return  id;
    }

    public static Collection<Consumed> addConsumedPill(Collection<Consumed> consumeds, DbAdapter dbAdapter) throws  Throwable{
        try {
            for (Consumed consumed : consumeds) {
                consumed=(Consumed)CreateMediService.createTableEntry(consumed, dbAdapter);
            }
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
        return consumeds;
    }
}
