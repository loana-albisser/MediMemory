package hslu.bda.medimemory.entity;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import hslu.bda.medimemory.R;
import hslu.bda.medimemory.database.DbAdapter;

/**
 * Created by Andy on 18.03.2016.
 */
public class DataTest extends AndroidTestCase {


    private Data data;
    private DbAdapter dbAdapter;
    private Context context;
    private int newID = 0;
    private String desc = "Dafalgan 500";
    private String descUpdated = "Dafalgan 500 Updated";
    @Override
    protected void setUp(){
        context = new RenamingDelegatingContext(getContext(), "test_");
        dbAdapter = new DbAdapter(context);
        dbAdapter.open();
        data = new Data();
        data.setId(0);
        data.setDescription(desc);
        data.setDuration(5);
        data.setAmount(1);
        data.setWidth(2);
        data.setLength(4);
        data.setPicture(BitmapFactory.decodeResource(getContext().getResources(), R.drawable.example_pill));
        Calendar cal = new GregorianCalendar();
        cal.setTime(new Date());
        data.setCreateDate(cal);
        data.setEndDate(cal);
        data.setNote("Test Note");
        data.setActive(1);

    }

    public void testCRUD(){

        //CRUD - TEST CREATED
        newID = (int) dbAdapter.createDbObject(data);
        assertTrue(newID > 0);

        //CRUD - TEST READ
        data = null;
        data = Data.getDataById(String.valueOf(newID), dbAdapter);
        assertEquals(data.getDescription(), desc);
        assertTrue(data.getAllConsumed() !=null);
        assertTrue(data.getAllConsumeIndividual()!=null);
        assertTrue(data.getAllConsumeInterval()!=null);

        //CRUD - TEST UPDATED
        data.setDescription(descUpdated);
        assertTrue(dbAdapter.updateDbObject(data));

        //CRUD - TEST DELETED
        data = Data.getDataById(String.valueOf(newID), dbAdapter);
        assertTrue(dbAdapter.deleteDbObject(data));
    }

    @Override
    protected void tearDown(){
        dbAdapter.close();
    }
}
