package hslu.bda.medimemory.entity;

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

/**
 * Created by Andy on 18.03.2016.
 */
public class ConsumedTest extends AndroidTestCase{
    private Data data;
    private DbAdapter dbAdapter;
    private Context context;
    private Consumed consumed;
    private int newID = 0;
    private PillCoords pillCoords;



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
        data.setPicture(BitmapFactory.decodeResource(getContext().getResources(),R.drawable.example_pill));
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
        consumed = new Consumed(data.getId(),cal,Status.getStatusById(Status.STATUS_EINGENOMMEN,dbAdapter), pillCoords);
        assertTrue(consumed!=null);
    }

    public void testCRUD(){

        //CRUD - TEST CREATED
        newID = (int) dbAdapter.createDbObject(consumed);
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
        dbAdapter.deleteDbObject(pillCoords);
        dbAdapter.deleteDbObject(data);
        dbAdapter.close();
    }
}
