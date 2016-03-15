package hslu.bda.medimemory.database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.Nullable;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;
import junit.framework.Assert;
import java.util.Collection;

import hslu.bda.medimemory.entity.Day;

/**
 * Created by Andy on 15.03.2016.
 */
public class DbAdapterTest extends AndroidTestCase {
    private RenamingDelegatingContext context;
    private SQLiteDatabase db;
    private DbAdapter dbAdapter;
    private Day day;

    @Override
    protected void setUp() throws Exception{
        super.setUp();
        context = new RenamingDelegatingContext(getContext(), "test_");
        dbAdapter = new DbAdapter(context);
        dbAdapter.open();
    }

    public void testReadDayByTable(){
        Collection<ContentValues> contentValues =dbAdapter.getAllByTable(DbHelper.TABLE_MEDI_DAY);
        assertTrue(contentValues.size()>0);
    }


    public void testReadDayByObject() throws Exception{
        day = new Day();
        day.setId(0);
        ContentValues contentValues =dbAdapter.getByObject(day);

        Assert.assertTrue(contentValues.getAsInteger(DbHelper.COLUMN_ID)==0);
    }

    @Override
    protected void tearDown() throws Exception{
        dbAdapter.close();

    }

}
