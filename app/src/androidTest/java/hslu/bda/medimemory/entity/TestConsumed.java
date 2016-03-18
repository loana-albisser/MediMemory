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
public class TestConsumed extends AndroidTestCase{
    private Data data;
    private DbAdapter dbAdapter;
    private Context context;
    private Consumed consumed;
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
        data.setId(dbAdapter.CreateDbObject(data));
        assertTrue(data.getId() > 0);
        consumed = new Consumed(0,data.getId(),cal,Status.getStatusById("0",dbAdapter));
        assertTrue(consumed!=null);
    }

    public void testCRUD(){

        //CRUD - TEST CREATED
        newID = (int) dbAdapter.CreateDbObject(consumed);
        assertTrue(newID > 0);

        //CRUD - TEST READ
        consumed = null;
        Collection<Consumed> allConsumedByMedid= Consumed.getAllConsumedByMedid(data.getId(),dbAdapter);
        consumed = Iterables.get(allConsumedByMedid,0);
        assertEquals(consumed.getMediid(), data.getId());

        //CRUD - TEST UPDATED
        consumed.setStatus(Status.getStatusById("1",dbAdapter));
        assertTrue(dbAdapter.updateDbObject(consumed));

        //CRUD - TEST DELETED
        assertTrue(dbAdapter.deleteDbObject(consumed));
    }

    @Override
    protected void tearDown(){
        dbAdapter.deleteDbObject(data);
        dbAdapter.close();
    }
}
