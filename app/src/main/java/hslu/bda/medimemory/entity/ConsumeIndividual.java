package hslu.bda.medimemory.entity;

import android.content.ContentValues;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import hslu.bda.medimemory.contract.DbObject;
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
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
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
        values.put(DbHelper.COLUMN_CONSTIME, simpleDateFormat.format(getConsumeTime()));
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
}
