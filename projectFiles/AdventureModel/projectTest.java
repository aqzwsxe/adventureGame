package AdventureModel;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class projectTest {
    /*
    Test if only one object of the class Clock will be created
     */
    @Test
    public void testClock(){
        Clock instanceClock = Clock.getInstanceClock();
        Clock instanceClock1 = Clock.getInstanceClock();
        Clock instanceClock2 = Clock.getInstanceClock();

        assertEquals(instanceClock.hashCode(),instanceClock1.hashCode());
        assertEquals(instanceClock.hashCode(),instanceClock2.hashCode());
        assertEquals(instanceClock1.hashCode(),instanceClock2.hashCode());
    }

    @Test
    public void testClock1(){
        Clock instanceClock = Clock.getInstanceClock();
        Clock instanceClock1 = Clock.getInstanceClock();
        assertEquals(instanceClock,instanceClock1);

    }
}
