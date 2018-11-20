package aima.core.environment.wumpusworld;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.states.MsgReceiver;

public class NavigationAgent extends Agent {
    private WumpusCave cave = new WumpusCave(4, 4);
    private HybridWumpusAgent agent = new HybridWumpusAgent();
    private WumpusEnvironment env;
    protected void setup(){
            System.out.println("Hello! "+getAID().getName()+" is ready.");
        Room gold_room = new Room(3, 2);
        cave.setGold(gold_room);
        Room Wumpus_room = new Room(3, 1);
        cave.setWumpus(Wumpus_room);
        cave.setPit(new Room(1, 3), true);
        cave.setPit(new Room(3, 3), true);
        cave.setPit(new Room(4, 4), true);
        env = new WumpusEnvironment(cave);

        env.addAgent(agent);
            addBehaviour(new OfferRequestsServer());
        }
    private class OfferRequestsServer extends CyclicBehaviour {
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage msg = myAgent.receive(mt);

            if (msg != null) {
                ACLMessage reply = msg.createReply();

                // Message received. Process it
                String title = msg.getContent();

                //Integer price = (Integer) catalogue.get(title);
                if (title != null) {
                    System.out.println("Navigator get: "+title);

                    WumpusPercept percept = new WumpusPercept();
                    if (title.contains("Stench"))
                        percept.setStench();
                    if (title.contains("Breeze"))
                        percept.setBreeze();
                    if (title.contains("Glitter"))
                        percept.setGlitter();
                    if (title.contains("Bump"))
                        percept.setBump();
                    if (title.contains("Scream"))
                        percept.setScream();
                    System.out.println("Percept "+percept);
                    var action = agent.execute(percept);
                    env.executeAction(agent, action);
                    reply.setPerformative(ACLMessage.PROPOSE);
                    reply.setContent(action.toString());
                } else {
                    // The requested book is NOT available for sale.
                    reply.setPerformative(ACLMessage.REFUSE);
                    reply.setContent("not-available");
                }
                System.out.println("Navigator sent: "+reply.getContent());
                myAgent.send(reply);
            } else {
                block();
            }
        }
    }


}
