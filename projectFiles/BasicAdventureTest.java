
import java.io.IOException;

import AdventureModel.AdventureGame;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BasicAdventureTest {
    @Test
    void getCommandsTest() throws IOException {
        AdventureGame game = new AdventureGame("TinyGame");
        String commands = game.player.getCurrentRoom().getCommands();
        assertEquals("WEST, UP, NORTH, IN, SOUTH, DOWN", commands); // I revisited it myself
//        assertEquals("DOWN,NORTH,IN,WEST,UP,SOUTH", commands);
    }

    @Test
    void getCommandsTest1() throws IOException {
        AdventureGame game = new AdventureGame("TinyGame");
        game.movePlayer("UP");
        String commands = game.player.getCurrentRoom().getCommands();
        assertEquals("EAST, WEST, DOWN", commands); // I revisited it myself
//        assertEquals("DOWN,NORTH,IN,WEST,UP,SOUTH", commands);
    }

    @Test
    void getObjectString() throws IOException {
        AdventureGame game = new AdventureGame("TinyGame");
        String objects = game.player.getCurrentRoom().getObjectString();
        assertEquals("a water bird", objects);
    }
    @Test
    void getObjectString1() throws IOException {
        AdventureGame game = new AdventureGame("TinyGame");
        game.movePlayer("UP");
        String objects = game.player.getCurrentRoom().getObjectString();
        assertEquals("a pirate chest", objects);
    }


}
