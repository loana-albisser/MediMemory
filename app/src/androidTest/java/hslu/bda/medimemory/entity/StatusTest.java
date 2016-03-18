package hslu.bda.medimemory.entity;

import android.content.Context;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import hslu.bda.medimemory.database.DbAdapter;

/**
 * Created by Andy on 18.03.2016.
 */
public class StatusTest extends AndroidTestCase {

    private Status status;
    private DbAdapter dbAdapter;
    private Context context;
    private int id = 99;
    private String desc ="Test Status";
    private String descUpdated = "Test Status Updated";
    private int newID = 0;

    @Override
    protected void setUp(){
        context = new RenamingDelegatingContext(getContext(), "test_");
        dbAdapter = new DbAdapter(context);
        dbAdapter.open();
    }

    public void testCRUD(){

        //CRUD - TEST CREATED
        status = new Status();
        status.setId(id);
        status.setDescription(desc);
        newID = (int) dbAdapter.CreateDbObject(status);
        assertTrue(newID > 0);

        //CRUD - TEST READ
        status = null;
        status = Status.getStatusById(String.valueOf(newID), dbAdapter);
        assertEquals(status.getDescription(),desc);


        //CRUD - TEST UPDATED
        status.setDescription(descUpdated);
        assertTrue(dbAdapter.updateDbObject(status));

        //CRUD - TEST DELETED
        status = Status.getStatusById(String.valueOf(newID), dbAdapter);
        assertTrue(dbAdapter.deleteDbObject(status));
    }

    @Override
    protected void tearDown(){
        dbAdapter.close();
    }
}
