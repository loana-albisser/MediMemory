package hslu.bda.medimemory.entity;

import android.content.Context;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import hslu.bda.medimemory.database.DbAdapter;

/**
 * Created by Andy on 18.03.2016.
 */
public class DayTest extends AndroidTestCase {
    private Day day;
    private DbAdapter dbAdapter;
    private Context context;
    private int id = 99;
    private String desc ="Test Eatpart";
    private String descUpdated = "Test Eatpart Updated";
    private int newID = 0;

    @Override
    protected void setUp(){
        context = new RenamingDelegatingContext(getContext(), "test_");
        dbAdapter = new DbAdapter(context);
        dbAdapter.open();
    }

    public void testCRUD(){

        //CRUD - TEST CREATED
        day = new Day();
        day.setId(id);
        day.setDescription(desc);
        newID = (int) dbAdapter.CreateDbObject(day);
        assertTrue(newID > 0);

        //CRUD - TEST READ
        day = null;
        day = Day.getDayById(String.valueOf(newID), dbAdapter);
        assertEquals(day.getDescription(),desc);


        //CRUD - TEST UPDATED
        day.setDescription(descUpdated);
        assertTrue(dbAdapter.updateDbObject(day));

        //CRUD - TEST DELETED
        day = Day.getDayById(String.valueOf(newID), dbAdapter);
        assertTrue(dbAdapter.deleteDbObject(day));
    }

    @Override
    protected void tearDown(){
        dbAdapter.close();
    }
}
