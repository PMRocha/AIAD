package project;

import jade.core.*;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class HospitalPlanner extends Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// classe do behaviour
	class HospitalPlannerBehaviour extends SimpleBehaviour {

		private static final long serialVersionUID = 1L;

		// construtor do behaviour
		public HospitalPlannerBehaviour(Agent a) {
			super(a);
		}

		// método action
		public void action() {
			ACLMessage msg = blockingReceive();
			if (msg.getPerformative() == ACLMessage.INFORM) {
				System.out.println(" " + getLocalName() + ": recebi "
						+ msg.getContent());
				
				// cria resposta
				ACLMessage reply = msg.createReply();
				TimeClock.inc();
				reply.setContent("Confirmado "+msg.getContent()+TimeClock.timeEpooch);
				// envia mensagem
				send(reply);
			}
		}

		// método done
		public boolean done() {
			return false;
		}
	}

	protected void setup() {

		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setName(getName());
		sd.setType("HospitalPlanner");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			e.printStackTrace();
		}

		// cria behaviour
		HospitalPlannerBehaviour b = new HospitalPlannerBehaviour(this);
		addBehaviour(b);

	} // fim do metodo setup

}
