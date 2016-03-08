package hslu.bda.medimemory.dto;

import java.util.Calendar;

/**
 * Created by manager on 07.03.2016.
 */
public class ConsumedDTO {
    private int id;
    private int mediid;
    private Calendar pointInTime;
    private StatusDTO status;

    public ConsumedDTO(){}

    public ConsumedDTO(int id, int mediid, Calendar pointInTime, StatusDTO status){
        this.setId(id);
        this.setMediid(mediid);
        this.setPointInTime(pointInTime);
        this.setStatus(status);
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

    /**
     * Use GregorianCalendar
     * @return
     */
    public Calendar getPointInTime() {
        return pointInTime;
    }

    /**
     * Use GregorianCalendar
     * @param pointInTime
     */
    public void setPointInTime(Calendar pointInTime) {
        this.pointInTime = pointInTime;
    }

    public StatusDTO getStatus() {
        return status;
    }

    public void setStatus(StatusDTO status) {
        this.status = status;
    }
}
