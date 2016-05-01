package hslu.bda.medimemory.entity;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.support.test.espresso.core.deps.guava.collect.Iterables;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

import hslu.bda.medimemory.R;
import hslu.bda.medimemory.database.DbAdapter;

/**
 * Created by Andy on 21.03.2016.
 */
public class ConsumeIndividualTest extends AndroidTestCase{
    private Data data;
    private DbAdapter dbAdapter;
    private Context context;
    private ConsumeIndividual consumeIndividual;
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
        consumeIndividual = new ConsumeIndividual(data.getId(),cal,Day.getDayById("0",dbAdapter),Eat.getEatById("0", dbAdapter));
        assertTrue(consumeIndividual != null);
    }

    public void testCRUD(){

        //CRUD - TEST CREATED
        newID = (int) dbAdapter.createDbObject(consumeIndividual);
        assertTrue(newID > 0);

        //CRUD - TEST READ
        consumeIndividual = null;
        Collection<ConsumeIndividual> allConsumedByMedid= ConsumeIndividual.getAllConsumeIndividualByMedid(data.getId(), dbAdapter);
        consumeIndividual = Iterables.get(allConsumedByMedid, 0);
        assertEquals(consumeIndividual.getMediid(), data.getId());

        //CRUD - TEST UPDATED
        consumeIndividual.setDaypart(Day.getDayById("1",dbAdapter));
        assertTrue(dbAdapter.updateDbObject(consumeIndividual));

        //CRUD - TEST DELETED
        assertTrue(dbAdapter.deleteDbObject(consumeIndividual));
    }

    @Override
    protected void tearDown(){
        dbAdapter.deleteDbObject(data);
        dbAdapter.close();
    }
}
