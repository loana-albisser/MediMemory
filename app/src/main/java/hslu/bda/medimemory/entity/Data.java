package hslu.bda.medimemory.entity;

import android.content.ContentValues;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import hslu.bda.medimemory.contract.DbObject;
import hslu.bda.medimemory.database.DbHelper;

/**
 * Created by manager on 07.03.2016.
 */
public class Data implements DbObject{

    private int id;
    private String description;
    private int duration;
    private int amount;
    private int width;
    private int length;
    private String picture;
    private Calendar createDate;
    private String note;
    private int active;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private List<Consumed> allConsumed = new ArrayList<Consumed>();
    private List<ConsumeIndividual> allConsumeIndividual = new ArrayList<ConsumeIndividual>();
    private List<ConsumeInterval> allConsumeInterval = new ArrayList<ConsumeInterval>();

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public Calendar getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Calendar createDate) {
        this.createDate = createDate;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public List<Consumed> getAllConsumed() {
        return allConsumed;
    }

    public void setAllConsumed(List<Consumed> allConsumed) {
        this.allConsumed = allConsumed;
    }

    public List<ConsumeIndividual> getAllConsumeIndividual() {
        return allConsumeIndividual;
    }

    public void setAllConsumeIndividual(List<ConsumeIndividual> allConsumeIndividual) {
        this.allConsumeIndividual = allConsumeIndividual;
    }

    public List<ConsumeInterval> getAllConsumeInterval() {
        return allConsumeInterval;
    }

    public void setAllConsumeInterval(List<ConsumeInterval> allConsumeInterval) {
        this.allConsumeInterval = allConsumeInterval;
    }


    @Override
    public ContentValues getContentValues() {
        final ContentValues values = new ContentValues();
        values.put(DbHelper.COLUMN_ID,getId());
        values.put(DbHelper.COLUMN_DESC, getDescription());
        values.put(DbHelper.COLUMN_DURATION, getDuration());
        values.put(DbHelper.COLUMN_AMOUNT, getAmount());
        values.put(DbHelper.COLUMN_WIDTH, getAmount());
        values.put(DbHelper.COLUMN_LENGTH, getLength());
        values.put(DbHelper.COLUMN_PICTURE, getPicture());
        values.put(DbHelper.COLUMN_CREATEDATE, simpleDateFormat.format(getCreateDate()));
        values.put(DbHelper.COLUMN_NOTE, getNote());
        values.put(DbHelper.COLUMN_ACTIVE, getActive());

        return values;
    }

    @Override
    public String getTableName() {
        return DbHelper.TABLE_MEDI_DATA;
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
