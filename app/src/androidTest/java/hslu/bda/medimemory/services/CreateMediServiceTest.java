package hslu.bda.medimemory.services;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

import hslu.bda.medimemory.R;
import hslu.bda.medimemory.database.DbAdapter;
import hslu.bda.medimemory.entity.ConsumeIndividual;
import hslu.bda.medimemory.entity.ConsumeInterval;
import hslu.bda.medimemory.entity.Data;
import hslu.bda.medimemory.entity.Day;
import hslu.bda.medimemory.entity.Eat;


/**
 * Created by Andy on 21.03.2016.
 */
public class CreateMediServiceTest extends AndroidTestCase {
    private Data data;
    private DbAdapter dbAdapter;
    private Context context;
    private ConsumeIndividual consumeIndividual;
    private ConsumeInterval consumeInterval;
    private Collection<ConsumeIndividual> allConsumeIndividuals = new ArrayList<ConsumeIndividual>();
    private Collection<ConsumeInterval> allConsumeIntervals = new ArrayList<ConsumeInterval>();
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
        Calendar calStart = new GregorianCalendar(2016,03,21, 17,0);
        Calendar calEnd = new GregorianCalendar(2016,03,21, 7,0);
        consumeInterval = new ConsumeInterval(data.getId(),calStart,calEnd,5,0);
        assertTrue(consumeInterval!=null);
        consumeIndividual = new ConsumeIndividual(data.getId(),cal, Day.getDayById("0", dbAdapter), Eat.getEatById("0", dbAdapter));
        assertTrue(consumeIndividual != null);
        allConsumeIndividuals.add(consumeIndividual);
        allConsumeIntervals.add(consumeInterval);
        data.setAllConsumeInterval(allConsumeIntervals);
        data.setAllConsumeIndividual(allConsumeIndividuals);
        assertTrue(data.getAllConsumeInterval().size() > 0);
        assertTrue(data.getAllConsumeIndividual().size()>0);
    }

    public void testMediCreateSucceed(){
        try {
            newID = CreateMediService.addNewMedi(data, dbAdapter);
        }catch (Throwable e){
            e.printStackTrace();
            assertTrue(false);
        }
        assertTrue(newID>0);
    }
/*
    public void testMediCreateFails(){
        try {
            newID = CreateMediService.addNewMedi(data, dbAdapter);
        }catch (Throwable e){
            e.printStackTrace();
            assertTrue(false);
        }
        assertFalse(newID>0);
    }
*/
    @Override
    protected void tearDown(){
        dbAdapter.deleteDbObject(data);
        dbAdapter.close();
    }
}
