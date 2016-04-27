package hslu.bda.medimemory.entity;

import android.content.ContentValues;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;

import hslu.bda.medimemory.contract.DbObject;
import hslu.bda.medimemory.database.DbAdapter;
import hslu.bda.medimemory.database.DbHelper;

/**
 * Created by manager on 07.03.2016.
 */
public class ConsumeIndividual implements DbObject {

    private int id;
    private int mediid;
    private Calendar consumeTime;
    private Day daypart;
    private Eat eatpart;
    private static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
    /**
     * empty Constructor
     */
    public ConsumeIndividual(){}


    public ConsumeIndividual(int id, int mediid, Calendar consumeTime, Day daypart, Eat eatpart){
        this.setId(id);
        this.setMediid(mediid);
        this.setConsumeTime(consumeTime);
        this.setDaypart(daypart);
        this.setEatpart(eatpart);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMediid() {
        return mediid;
    }

    public void setMediid(int mediid) {
        this.mediid = mediid;
    }

    public Calendar getConsumeTime() {
        return consumeTime;
    }

    public void setConsumeTime(Calendar consumeTime) {
        this.consumeTime = consumeTime;
    }

    public Day getDaypart() {
        return daypart;
    }

    public void setDaypart(Day daypart) {
        this.daypart = daypart;
    }

    public Eat getEatpart() {
        return eatpart;
    }

    public void setEatpart(Eat eatpart) {
        this.eatpart = eatpart;
    }

    @Override
    public ContentValues getContentValues() {
        final ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_ID,getId());
        values.put(DbHelper.COLUMN_MEDIID, getMediid());
        if(getConsumeTime()!=null) {
            values.put(DbHelper.COLUMN_CONSTIME, simpleDateFormat.format(getConsumeTime().getTime()));
        }
        else{
            values.put(DbHelper.COLUMN_CONSTIME, (String)null);
        }
        values.put(DbHelper.COLUMN_DAYPART, getDaypart().getId());
        values.put(DbHelper.COLUMN_EATPART, getEatpart().getId());

        return values;
    }

    @Override
    public String getTableName() {
        return DbHelper.TABLE_MEDI_CONSINDIV;
    }

    @Override
    public String getPrimaryFieldName() {
        return DbHelper.COLUMN_ID;
    }

    @Override
    public String getPrimaryFieldValue() {
        return String.valueOf(getId());
    }

    public static Collection<ConsumeIndividual> getAllConsumeIndividualByMedid(int medid, DbAdapter dbAdapter){
        Collection<ConsumeIndividual> allConsumeIndividuals = new ArrayList<ConsumeIndividual>();
        Collection<ContentValues> allContentValues =
                dbAdapter.getAllByTable(DbHelper.TABLE_MEDI_CONSINDIV,
                        new String[]{DbHelper.COLUMN_MEDIID},new String[]{String.valueOf(medid)});
        if(allContentValues!=null) {
            for(ContentValues contentValues:allContentValues){
                allConsumeIndividuals.add(copyContentValuesToObject(contentValues, dbAdapter));
            }
        }

        return allConsumeIndividuals;
    }

    private static ConsumeIndividual copyContentValuesToObject(ContentValues contentValues, DbAdapter dbAdapter) {
        ConsumeIndividual consumeIndividual = new ConsumeIndividual();
        consumeIndividual.setId(contentValues.getAsInteger(DbHelper.COLUMN_ID));
        consumeIndividual.setMediid(contentValues.getAsInteger(DbHelper.COLUMN_MEDIID));
        try{
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(simpleDateFormat.parse(contentValues.getAsString(DbHelper.COLUMN_CONSTIME)));
            consumeIndividual.setConsumeTime(calendar);
        }catch(Exception e) {consumeIndividual.setConsumeTime(null);}
        consumeIndividual.setDaypart(Day.getDayById(contentValues.getAsString(DbHelper.COLUMN_DAYPART), dbAdapter));
        consumeIndividual.setEatpart(Eat.getEatById(contentValues.getAsString(DbHelper.COLUMN_EATPART), dbAdapter));
        return  consumeIndividual;
    }
}

