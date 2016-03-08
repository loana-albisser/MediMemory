package hslu.bda.medimemory.dto;

import java.util.Calendar;

/**
 * Created by manager on 07.03.2016.
 */
public class ConsumeIntervalDTO {

    private int id;
    private int mediid;
    private Calendar startTime;
    private Calendar endTime;
    private int interval;
    private int weekday;

    public ConsumeIntervalDTO(){};

    public ConsumeIntervalDTO(int id, int mediid, Calendar startTime, Calendar endTime, int interval, int weekday){
        this.setId(id);
        this.setMediid(mediid);
        this.setStartTime(startTime);
        this.setEndTime(endTime);
        this.setInterval(interval);
        this.setWeekday(weekday);
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

    public Calendar getStartTime() {
        return startTime;
    }

    /**
     * Use GregorianCalendar
     * @param startTime
     */
    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
    }

    /**
     * use GregorianCalendar
     * @return
     */
    public Calendar getEndTime() {
        return endTime;
    }

    public void setEndTime(Calendar endTime) {
        this.endTime = endTime;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    /**
     * Returns Calendar.DAY_OF_WEEK id of the weekday
     * @return
     */
    public int getWeekday() {
        return weekday;
    }

    /**
     * set weekday over Calendar.DAY_OF_WEEK
     * @param weekday
     */
    public void setWeekday(int weekday) {
        this.weekday = weekday;
    }
}
