package hslu.bda.medimemory.entity;

import android.content.Context;
import android.support.test.espresso.core.deps.guava.collect.Iterables;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

import hslu.bda.medimemory.database.DbAdapter;

/**
 * Created by Andy on 18.03.2016.
 */
public class ConsumeIntervalTest extends AndroidTestCase{
    private Data data;
    private DbAdapter dbAdapter;
    private Context context;
    private ConsumeInterval consumeInterval;
    private int newID = 0;




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
        data.setPicture("Test.jpg");
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        data.setCreateDate(cal);
        data.setNote("Test Note");
        data.setActive(1);
        data.setId(dbAdapter.createDbObject(data));
        assertTrue(data.getId() > 0);
        Calendar calStart = new GregorianCalendar(2016,03,21, 17,0);
        Calendar calEnd = new GregorianCalendar(2016,03,21, 7,0);
        consumeInterval = new ConsumeInterval(0,data.getId(),calStart,calEnd,5,0);
        assertTrue(consumeInterval != null);
    }

    public void testCRUD(){

        //CRUD - TEST CREATED
        newID = (int) dbAdapter.createDbObject(consumeInterval);
        assertTrue(newID > 0);

        //CRUD - TEST READ
        consumeInterval = null;
        Collection<ConsumeInterval> allConsumedByMedid= ConsumeInterval.getAllConsumeIntervalByMedid(data.getId(), dbAdapter);
        consumeInterval = Iterables.get(allConsumedByMedid, 0);
        assertEquals(consumeInterval.getMediid(), data.getId());

        //CRUD - TEST UPDATED
        consumeInterval.setInterval(5);
        assertTrue(dbAdapter.updateDbObject(consumeInterval));

        //CRUD - TEST DELETED
        assertTrue(dbAdapter.deleteDbObject(consumeInterval));
    }

    @Override
    protected void tearDown(){
        dbAdapter.deleteDbObject(data);
        dbAdapter.close();
    }
}
