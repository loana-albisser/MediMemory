package hslu.bda.medimemory.entity;

import android.content.Context;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import hslu.bda.medimemory.database.DbAdapter;

/**
 * Created by manager on 07.03.2016.
 */
public class EatTest extends AndroidTestCase {
    private Eat eat;
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
        eat = new Eat();
        eat.setId(id);
        eat.setDescription(desc);
        newID = (int) dbAdapter.createDbObject(eat);
        assertTrue(newID > 0);

        //CRUD - TEST READ
        eat = null;
        eat = Eat.getEatById(String.valueOf(newID), dbAdapter);
        assertEquals(eat.getDescription(),desc);


        //CRUD - TEST UPDATED
        eat.setDescription(descUpdated);
        assertTrue(dbAdapter.updateDbObject(eat));

        //CRUD - TEST DELETED
        eat = Eat.getEatById(String.valueOf(newID), dbAdapter);
        assertTrue(dbAdapter.deleteDbObject(eat));
    }

    @Override
    protected void tearDown(){
        dbAdapter.close();
    }
}