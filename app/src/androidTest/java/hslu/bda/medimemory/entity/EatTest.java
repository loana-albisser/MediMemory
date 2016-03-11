package hslu.bda.medimemory.entity;

import android.test.AndroidTestCase;

/**
 * Created by manager on 07.03.2016.
 */
public class EatTest extends AndroidTestCase {
    private int id;
    private String description;
    private Eat eat;



    @Override
    protected void setUp(){
        id = 0;
        description = "Test";
    }

    public void createTest(){
        eat = new Eat();
        assertTrue(eat !=null);
    }
}