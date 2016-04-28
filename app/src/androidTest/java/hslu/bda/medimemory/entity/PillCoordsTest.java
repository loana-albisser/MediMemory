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
 * Created by Andy on 22.04.2016.
 */
public class PillCoordsTest extends AndroidTestCase{
    private Data data;
    private DbAdapter dbAdapter;
    private Context context;
    private PillCoords pillCoords;
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
        data.setPicture(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.example_pill));
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        data.setCreateDate(cal);
        data.setNote("Test Note");
        data.setActive(1);
        data.setId(dbAdapter.createDbObject(data));
        assertTrue(data.getId() > 0);
        Point point = new Point();
        point.x = 200;
        point.y = 200;
        pillCoords = new PillCoords(0,data.getId(),point, 200,300);
        assertTrue(pillCoords != null);
    }

    public void testCRUD(){

        //CRUD - TEST CREATED
        newID = (int) dbAdapter.createDbObject(pillCoords);
        assertTrue(newID > 0);

        //CRUD - TEST READ
        pillCoords = null;
        pillCoords = PillCoords.getPillCoordById(String.valueOf(newID), dbAdapter);
        assertEquals(pillCoords.getMediid(), data.getId());

        //CRUD - TEST UPDATED
        pillCoords.setCoords(new Point(200,300));
        assertTrue(dbAdapter.updateDbObject(pillCoords));

        //CRUD - TEST DELETED
        assertTrue(dbAdapter.deleteDbObject(pillCoords));
    }

    @Override
    protected void tearDown(){
        dbAdapter.deleteDbObject(data);
        dbAdapter.close();
    }
}
