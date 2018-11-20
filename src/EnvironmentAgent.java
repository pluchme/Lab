package aima.core.environment.wumpusworld;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.proto.states.MsgReceiver;

public class EnvironmentAgent extends Agent {
    private WumpusCave cave = new WumpusCave(4, 4);
    private HybridWumpusAgent agent = new HybridWumpusAgent();
    private WumpusEnvironment env;

    protected void setup(){
        System.out.println("Hello! "+getAID().getName()+" is ready.");

        Room gold_room = new Room(2, 3);
        cave.setGold(gold_room);
        Room Wumpus_room = new Room(1, 3);
        cave.setWumpus(Wumpus_room);
        cave.setPit(new Room(1, 3), true);
        cave.setPit(new Room(3, 3), true);
        cave.setPit(new Room(4, 4), true);
        env = new WumpusEnvironment(cave);

        env.addAgent(agent);

        addBehaviour(new OfferRequestsServer());
        addBehaviour(new ExecuteAction());
    }
    private class OfferRequestsServer extends MsgReceiver {
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                System.out.println("Environment received: " + msg.getContent());

                // Message received. Process it
                String title = msg.getContent();
                ACLMessage reply = msg.createReply();
                //Integer price = (Integer) catalogue.get(title);
                reply.setPerformative(ACLMessage.PROPOSE);
                reply.setContent(env.getPerceptSeenBy(agent).toString());
                System.out.println("Environment propose: "+reply.getContent());
                myAgent.send(reply);

            } else {
                block();
            }
        }
    }
    private class ExecuteAction extends CyclicBehaviour {
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.CFP);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                System.out.println("Environment get: "+msg.getContent());

                // Message received. Process it
                String title = msg.getContent();
                WumpusAction action = WumpusAction.valueOf(title);
                System.out.println("Action: "+action);
                env.executeAction(agent, action);
                System.out.println("Position: "+env.getAgentPosition(agent));
                ACLMessage reply = msg.createReply();
                //Integer price = (Integer) catalogue.get(title);
                reply.setPerformative(ACLMessage.ACCEPT_PROPOSAL);
                reply.setContent("OK");
                System.out.println(reply.getContent());
                myAgent.send(reply);

            } else {
                block();
            }
        }
    }

}
