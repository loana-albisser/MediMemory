package hslu.bda.medimemory.dto;

import java.text.DateFormatSymbols;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by manager on 07.03.2016.
 */
public class ConsumeIndividualDTO {

    private int id;
    private int mediid;
    private Calendar consumeTime;
    private DayDTO daypart;
    private EatDTO eatpart;

    /**
     * empty Constructor
     */
    public ConsumeIndividualDTO(){}


    public ConsumeIndividualDTO(int id, int mediid, Calendar consumeTime, DayDTO daypart, EatDTO eatpart){
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

    public DayDTO getDaypart() {
        return daypart;
    }

    public void setDaypart(DayDTO daypart) {
        this.daypart = daypart;
    }

    public EatDTO getEatpart() {
        return eatpart;
    }

    public void setEatpart(EatDTO eatpart) {
        this.eatpart = eatpart;
    }
}
