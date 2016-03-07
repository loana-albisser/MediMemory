package hslu.bda.medimemory.dto;

/**
 * Created by manager on 07.03.2016.
 */
public class StatusDTO {

    private int id;
    private String description;

    /**
     * Empty Constructor
     */
    public StatusDTO (){}

    /**
     * Constructor to return Object with supplied fields
     * @param id of the status
     * @param description of the status
     */
    public StatusDTO (int id, String description){
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
     * @param id of the status
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * getter-Method of the description
     * @return description of the status
     */
    public String getDescription() {
        return description;
    }

    /**
     * Setter-Method of the description
     * @param description of the status
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
