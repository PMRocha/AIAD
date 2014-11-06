package project;

import java.io.IOException;

import jade.core.*;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class HospitalPlanner extends Agent {

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
			ACLMessage reply = msg.createReply();

			System.out.println(" " + getLocalName() + ": recebi "
					+ msg.getContent());

			if (msg.getPerformative() == ACLMessage.INFORM) {

				// cria resposta
				reply.setContent("Confirmado " + msg.getContent()
						+ TimeClock.timeEpooch);
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
		try {
			new TimeTable("TimeTable.xlsx", 0);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
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

		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd1 = new ServiceDescription();

		sd1.setType("Patient");
		template.addServices(sd1);
		try {
			DFAgentDescription[] result = DFService.search(this, template);
			// envia mensagem "pong" inicial a todos os agentes "ping"
			ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
			for (int i = 0; i < result.length; ++i)
				msg.addReceiver(result[i].getName());

			msg.setContent("Aberto para servico");
			send(msg);
			System.out.println();
		} catch (FIPAException e) {
			e.printStackTrace();
		}

	} // fim do metodo setup

}
