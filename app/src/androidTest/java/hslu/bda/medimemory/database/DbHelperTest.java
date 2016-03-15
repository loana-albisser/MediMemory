package hslu.bda.medimemory.database;

import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;
import android.test.RenamingDelegatingContext;

/**
 * Created by manager on 05.03.2016.
 */
public class DbHelperTest extends AndroidTestCase{
    private DbHelper dbHelper;
    private RenamingDelegatingContext context;

    @Override
    protected void setUp() throws Exception{
        super.setUp();
        context = new RenamingDelegatingContext(getContext(), "test_");
    }

    public void testCreateTest(){
        dbHelper = new DbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        assertTrue(db.isOpen());
        db.close();
    }
}
