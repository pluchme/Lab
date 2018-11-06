import jade.core.AID;
import jade.core.Agent;

import java.util.ArrayList;
import java.util.Hashtable;

public class EnvironmentAgent extends Agent {

    private static int NUM_OF_ROWS = 4;
    private static int NUM_OF_COLUMNS = 4;
    private static int START_POS_X = 0;
    private static int START_POS_Y = 0;
    private Room[][] map = new Room[NUM_OF_ROWS][NUM_OF_COLUMNS];
    private Hashtable<Integer, String> envObjects = new Hashtable<Integer, String>() {{
        put(1, "Wumpus");
        put(2, "Gold");
        put(3, "Pit");
        put(4, "Breeze");
        put(5, "Stench");
    }};
    private int speleologistX;
    private int speleologistY;
    private Hashtable<Integer, String> directionTable = new Hashtable<>() {{
        put(1, "North");
        put(2, "South");
        put(3, "West");
        put(4, "East");
    }};
    private String direction;
    private AID id = new AID("Environment", AID.ISLOCALNAME);
    private AID Speleologist = new AID("Speleologist", AID.ISLOCALNAME);

    private void GenerateMap() {
        map[0][0] = new Room();
        map[0][1] = new Room(4);
        map[0][2] = new Room(3);
        map[0][3] = new Room(4);
        map[1][0] = new Room(5);
        map[1][1] = new Room();
        map[1][2] = new Room(4);
        map[1][3] = new Room();
        map[2][0] = new Room(1);
        map[2][1] = new Room(2, 4, 5);
        map[2][2] = new Room(3);
        map[2][3] = new Room(4);
        map[3][0] = new Room(5);
        map[3][1] = new Room();
        map[3][2] = new Room(4);
        map[3][3] = new Room(3);

    }

    protected void setup() {
        System.out.println("Hello! Environment-agent " + getAID().getName() + " is ready.");
        GenerateMap();
        speleologistX = START_POS_X;
        speleologistY = START_POS_Y;
        direction = directionTable.get(1);


    }

    class Room {
        ArrayList<String> info = new ArrayList<>();

        Room(int... code) {
            for (int i : code) {
                info.add(envObjects.get(i));
            }
        }
    }
}
