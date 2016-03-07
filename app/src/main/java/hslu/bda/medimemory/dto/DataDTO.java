package hslu.bda.medimemory.dto;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by manager on 07.03.2016.
 */
public class DataDTO {

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

    private List<ConsumedDTO> allConsumed = new ArrayList<>();
    private List<ConsumeIndividualDTO> allConsumeIndividual = new ArrayList<>();
    private List<ConsumeIntervalDTO> allConsumeInterval = new ArrayList<>();

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

    public List<ConsumedDTO> getAllConsumed() {
        return allConsumed;
    }

    public void setAllConsumed(List<ConsumedDTO> allConsumed) {
        this.allConsumed = allConsumed;
    }

    public List<ConsumeIndividualDTO> getAllConsumeIndividual() {
        return allConsumeIndividual;
    }

    public void setAllConsumeIndividual(List<ConsumeIndividualDTO> allConsumeIndividual) {
        this.allConsumeIndividual = allConsumeIndividual;
    }

    public List<ConsumeIntervalDTO> getAllConsumeInterval() {
        return allConsumeInterval;
    }

    public void setAllConsumeInterval(List<ConsumeIntervalDTO> allConsumeInterval) {
        this.allConsumeInterval = allConsumeInterval;
    }
}
