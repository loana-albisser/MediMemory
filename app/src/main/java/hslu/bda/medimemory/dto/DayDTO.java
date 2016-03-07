package hslu.bda.medimemory.dto;

/**
 * Created by tbeugste on 07.03.2016.
 */
public class DayDTO {

    private int id;
    private String description;

    /**
     * Empty Constructor
     */
    public DayDTO (){}

    /**
     * Constructor to return Object with supplied fields
     * @param id of the daypart
     * @param description of the daypart
     */
    public DayDTO (int id, String description){
        this.setId(id);
        this.setDescription(description);
    }

    /**
     * getter-Method of the ID
     * @return id
     */
    public int getId() {
        return id;
    }

    /**
     * setter-Method of the id
     * @param id of the daypart
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * getter-Method of the description
     * @return description of the daypart
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter-Method of the description
     * @param description of the daypart
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
