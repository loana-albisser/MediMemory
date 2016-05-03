package hslu.bda.medimemory.services;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.test.espresso.core.deps.guava.collect.Iterables;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import org.opencv.core.Point;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

import hslu.bda.medimemory.R;
import hslu.bda.medimemory.database.DbAdapter;
import hslu.bda.medimemory.entity.ConsumeIndividual;
import hslu.bda.medimemory.entity.ConsumeInterval;
import hslu.bda.medimemory.entity.Consumed;
import hslu.bda.medimemory.entity.Data;
import hslu.bda.medimemory.entity.Day;
import hslu.bda.medimemory.entity.Eat;
import hslu.bda.medimemory.entity.PillCoords;
import hslu.bda.medimemory.entity.Status;

/**
 * Created by Andy on 04.05.2016.
 */
public class DeleteMediServiceTest extends AndroidTestCase{
    private Data data;
    private DbAdapter dbAdapter;
    private Context context;
    private Consumed consumed;
    private int newID = 0;
    private PillCoords pillCoords;
    private boolean result = false;
    private ConsumeIndividual consumeIndividual;
    private ConsumeInterval consumeInterval;

    @Override
    protected void setUp(){
        context = new RenamingDelegatingContext(getContext(), "test_");
        dbAdapter = new DbAdapter(context);
        dbAdapter.open();
        data = new Data();
        data.setId(0);
        data.setDescription("Dafalgan 500");
        data.setDuration(5);
        data.setAmount(1);
        data.setWidth(2);
        data.setLength(4);
        data.setPicture(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.example_pill));
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        data.setCreateDate(cal);
        data.setNote("Test Note");
        data.setActive(1);
        data.setId(dbAdapter.createDbObject(data));
        assertTrue(data.getId() > 0);
        pillCoords = new PillCoords(data.getId(),new Point(200,200),100,200);
        pillCoords.setId(dbAdapter.createDbObject(pillCoords));
        assertTrue(pillCoords.getId() > 0);
        consumed = new Consumed(data.getId(),cal, Status.getStatusById("0", dbAdapter), pillCoords);
        consumeIndividual = new ConsumeIndividual(data.getId(),cal, Day.getDayById("0", dbAdapter), Eat.getEatById("0", dbAdapter));
        Calendar calStart = new GregorianCalendar(2016,03,21, 17,0);
        Calendar calEnd = new GregorianCalendar(2016,03,21, 7,0);
        consumeInterval = new ConsumeInterval(data.getId(),calStart,calEnd,5,0);
        assertTrue(consumed!=null);
        consumed.setId(dbAdapter.createDbObject(consumed));
        consumeIndividual.setId(dbAdapter.createDbObject(consumeIndividual));
        consumeInterval.setId(dbAdapter.createDbObject(consumeInterval));
        assertTrue(consumed.getId()>0);
    }

    public void testDeleteDataObject(){
        int count = pillCoords.getAllPillCoordsByMedid(data.getId(), dbAdapter).size();
        assertTrue(count >0);
        try {
            result = DeleteMediService.deleteDbObject(data, dbAdapter);
        }catch (Throwable e){
            assert(false);
        }

        count = pillCoords.getAllPillCoordsByMedid(data.getId(), dbAdapter).size();
        assertTrue(Consumed.getAllConsumedByMedid(data.getId(),dbAdapter).size()==0);
        assertTrue(ConsumeInterval.getAllConsumeIntervalByMedid(data.getId(), dbAdapter).size()==0);
        assertTrue(ConsumeIndividual.getAllConsumeIndividualByMedid(data.getId(), dbAdapter).size()==0);
        assertTrue(count ==0);

    }

    @Override
    protected void tearDown(){

    }
}
