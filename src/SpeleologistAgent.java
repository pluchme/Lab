import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class SpeleologistAgent extends Agent {
    private AID navigation = new AID("Navigation", AID.ISLOCALNAME);
    private AID environment = new AID("Environment", AID.ISLOCALNAME);
    protected void setup() {
        System.out.println("Hello! " + getAID().getName() + " is ready.");
        addBehaviour(new TickerBehaviour(this, 7000) {
            protected void onTick() {
                myAgent.addBehaviour(new RequestPerformer());
            }
        });
    }

    private class RequestPerformer extends Behaviour {
        private MessageTemplate mt; // The template to receive replies
        private int step = 0;

        public void action() {
            switch (step) {
                case 0:
                    // Send the cfp to all sellers
                    ACLMessage cfp = new ACLMessage(ACLMessage.REQUEST);
                    cfp.addReceiver(environment);
                    cfp.setContent("Give Me My Info");
                    cfp.setConversationId("Hmm");
                    cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique value
                    myAgent.send(cfp);
                    // Prepare the template to get proposals
                    mt = MessageTemplate.and(MessageTemplate.MatchConversationId("Hmm"),
                            MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
                    try {
                        Thread.sleep(122);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Receive all proposals/refusals from seller agents
                    ACLMessage reply = myAgent.receive(mt);
                    if (reply != null) {

                        // Reply received
                        if (reply.getPerformative() == ACLMessage.PROPOSE) {
                            // This is an offer
                            System.out.println(reply.getContent());
                            if (reply.getContent().contains("dead"))
                                doDelete();
                        }
                        }
                     else {
                        block();
                    }
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // Send the purchase order to the seller that provided the best offer
                    ACLMessage order = new ACLMessage(ACLMessage.REQUEST);
                    order.addReceiver(navigation);
                    order.setContent(reply.getContent());
                    order.setConversationId("Navigation");
                    order.setReplyWith("order" + System.currentTimeMillis());
                    myAgent.send(order);

                    // Prepare the template to get the purchase order reply
                    mt = MessageTemplate.and(MessageTemplate.MatchConversationId("Navigation"),
                            MessageTemplate.MatchInReplyTo(order.getReplyWith()));
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    // Receive the purchase order reply
                    reply = myAgent.receive(mt);
                    System.out.println(reply);
                    if (reply != null) {
                        // Purchase order reply received
                        if (reply.getPerformative() == ACLMessage.PROPOSE) {
                            System.out.println(reply.getContent());

                            ACLMessage path = new ACLMessage(ACLMessage.CFP);
                            path.addReceiver(environment);
                            path.setContent(reply.getContent());
                            path.setConversationId("Path");
                            path.setReplyWith("Time" + System.currentTimeMillis());
                            myAgent.send(path);
                            mt = MessageTemplate.and(MessageTemplate.MatchConversationId("Path"),
                                    MessageTemplate.MatchInReplyTo(path.getReplyWith()));

                            // Purchase successful. We can terminate
                            //System.out.println(targetBookTitle + " successfully purchased.");
                            //System.out.println("Price = " + bestPrice);

                        }
                    } else {
                        block();
                    }
                    break;
            }
        }

        public boolean done() {
            return  true;//((step == 2 && bestSeller == null) || step == 4);
        }
    }
}
