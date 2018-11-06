import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.ArrayList;
import java.util.Random;

public class NavigationAgent extends Agent {
    protected void setup() {
        System.out.println("Hello! " + getAID().getName() + " is ready.");
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
                System.out.println(title.split(":")[1]);

                //Integer price = (Integer) catalogue.get(title);
                if (title != null) {
                    reply.setPerformative(ACLMessage.PROPOSE);
                    reply.setContent(processSenses(title.split(":")[1]).toString());
                } else {
                    // The requested book is NOT available for sale.
                    reply.setPerformative(ACLMessage.REFUSE);
                    reply.setContent("not-available");
                }
                myAgent.send(reply);

            } else {
                block();
            }
        }
    }
    private AgentPosition.Orientation processSenses(String receivedSenses) {
        System.out.println("[NavigationProcessor]: received senses: " + receivedSenses);

        if ((receivedSenses).equals("{}")) { //the first entrance case // next move is safe, move default
            ArrayList<AgentPosition.Orientation> list = new ArrayList<>();
            list.add(AgentPosition.Orientation.FACING_NORTH);
            list.add(AgentPosition.Orientation.FACING_EAST);
            list.add(AgentPosition.Orientation.FACING_SOUTH);
            list.add(AgentPosition.Orientation.FACING_WEST);
            Random random = new Random();
            int index = random.nextInt(list.size());
            AgentPosition.Orientation randomAvailableDirection = list.get(index);
            return randomAvailableDirection;
        }
        else {
            return AgentPosition.Orientation.FACING_NORTH;
        }
        }
        // update the map





}
