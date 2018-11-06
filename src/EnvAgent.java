import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.states.MsgReceiver;

import java.util.Arrays;
import java.util.List;

public class EnvAgent extends Agent {
    private WumpusCave cave = new WumpusCave(4, 4);
    private boolean isWumpusAlive = true;
    private boolean isGoldGrabbed;
    //private AID speleologist = new AID("Speleologist", AID.ISLOCALNAME);
    private AgentPosition start = cave.getStart();

    public WumpusCave getCave() {
        return cave;
    }
    public boolean agentJustKillingWumpus = false;
    protected void setup() {
        System.out.println("Hello! " + getAID().getName() + " is ready.");
        System.out.println(cave.getStart());
        Room gold_room = new Room(3, 2);
        cave.setGold(gold_room);
        Room Wumpus_room = new Room(3, 1);
        cave.setWumpus(Wumpus_room);
        cave.setPit(new Room(1, 3), true);
        cave.setPit(new Room(3, 3), true);
        cave.setPit(new Room(4, 4), true);
        System.out.println();
        System.out.println("qq");
        addBehaviour(new OfferRequestsServer());
        addBehaviour(new movingListener());
    }
    private class movingListener extends CyclicBehaviour{


        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
            ACLMessage msg = myAgent.receive(mt);

            if (msg != null){
                System.out.println(msg);
                start = cave.go(start, msg.getContent());
        }}
    }
    private class OfferRequestsServer extends MsgReceiver {
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage msg = myAgent.receive(mt);
            System.out.println(msg);
            if (msg != null) {

                // Message received. Process it
                String title = msg.getContent();
                System.out.println(title);
                ACLMessage reply = msg.createReply();
                //Integer price = (Integer) catalogue.get(title);
                if (start != null) {

                    reply.setPerformative(ACLMessage.PROPOSE);
                    reply.setContent(start.toString()+":"+getPerceptSeenBy());
                } else {
                    // The requested book is NOT available for sale.
                    reply.setPerformative(ACLMessage.REFUSE);
                    reply.setContent("not-available");
                }
                System.out.println(reply.toString());
                myAgent.send(reply);

            } else {
                block();
            }
        }
    }

    public WumpusPercept getPerceptSeenBy() {
        WumpusPercept result = new WumpusPercept();
        AgentPosition pos = start;
        List<Room> adjacentRooms = Arrays.asList(
                new Room(pos.getX()-1, pos.getY()), new Room(pos.getX()+1, pos.getY()),
                new Room(pos.getX(), pos.getY()-1), new Room(pos.getX(), pos.getY()+1)
        );
        for (Room r : adjacentRooms) {
            if (r.equals(cave.getWumpus()))
                result.setStench();
            if (cave.isPit(r))
                result.setBreeze();
        }
        if (pos.getRoom().equals(cave.getGold()))
            result.setGlitter();
        if (pos.getX()==1 && pos.getOrientation().equals("FacingWest"))
            result.setBump();
        if (pos.getX()==4 && pos.getOrientation().equals("FacingEast"))
            result.setBump();
        if (pos.getY()==1 && pos.getOrientation().equals("FacingSouth"))
            result.setBump();
        if (pos.getX()==4 && pos.getOrientation().equals("FacingNorth"))
            result.setBump();
        if (agentJustKillingWumpus)
            result.setScream();
        return result;
    }

}
