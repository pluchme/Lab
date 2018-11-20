package aima.core.environment.wumpusworld;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.util.concurrent.ThreadLocalRandom;

public class SpeleologistAgent extends Agent {
    private AID navigation = new AID("navigator", AID.ISLOCALNAME);
    private AID environment = new AID("environment", AID.ISLOCALNAME);
    protected void setup(){
        System.out.println("Hello! "+getAID().getName()+" is ready.");
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
                    ACLMessage cfp = new ACLMessage(ACLMessage.REQUEST);
                    cfp.addReceiver(environment);
                    cfp.setContent("Give Me My Info");
                    cfp.setConversationId("First");
                    cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique value
                    System.out.println("Speleologist request: "+cfp.getContent());
                    myAgent.send(cfp);
                    // Prepare the template to get proposals
                    mt = MessageTemplate.and(MessageTemplate.MatchConversationId("First"),
                            MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
                    try {
                        Thread.sleep(122);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // Receive all proposals/refusals from seller agents
                    ACLMessage reply = myAgent.receive(mt);
                    if (reply != null) {
                        System.out.println("Speleologist received:" + reply.getContent());
                        // Reply received
                        if (reply.getPerformative() == ACLMessage.PROPOSE) {
                            // This is an offer
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
                    int randomNum = (int) (Math.random()*2);

                    String content = reply.getContent().replace("{", "").replace("}", "").replace(",", " and ");
                    if (content.equals(""))
                        content="Nothing";
                    switch (randomNum) {
                        case 0:
                            order.setContent("It's " + content +" here!");
                        case 2:
                            order.setContent("I feel " + content);
                        case 1:
                            order.setContent("There is " + content);
                    }
                            order.setConversationId("Navigation");
                    order.setReplyWith("Navigation" + System.currentTimeMillis()); // Unique value
                    System.out.println("Speleologist sent to Navigator: "+order.getContent());
                    myAgent.send(order);
                    // Prepare the template to get proposals
                    try {
                        Thread.sleep(10022);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mt = MessageTemplate.and(MessageTemplate.MatchConversationId("Navigation"),
                            MessageTemplate.MatchInReplyTo(order.getReplyWith()));



                    // Receive the purchase order reply
                    ACLMessage navigatorReply = myAgent.receive(mt);

                    if (navigatorReply != null) {
                        // Purchase order reply received
                        if (navigatorReply.getPerformative() == ACLMessage.PROPOSE) {
                            System.out.println("Speleologist get: "+navigatorReply.getContent());
                            ACLMessage path = new ACLMessage(ACLMessage.CFP);
                            path.addReceiver(environment);
                            path.setContent(navigatorReply.getContent());
                            path.setConversationId("Path");
                            path.setReplyWith("Time" + System.currentTimeMillis());
                            System.out.println("Speleologist sent to Environment: "+ path.getContent());
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
