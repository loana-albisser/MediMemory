package hslu.bda.medimemory.dto;

import android.test.AndroidTestCase;

/**
 * Created by manager on 07.03.2016.
 */
public class EatDTOTest extends AndroidTestCase {
    private int id;
    private String description;
    private EatDTO eatDTO;



    @Override
    protected void setUp(){
        id = 0;
        description = "Test";
    }

    public void createTest(){
        eatDTO = new EatDTO();
        assertTrue(eatDTO!=null);
    }
}