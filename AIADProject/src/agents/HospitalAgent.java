package agents;

import jade.core.*;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import resources.Hospital;
import resources.TimeClock;
import resources.TimeTable;

public class HospitalAgent extends Agent {

	private static final long serialVersionUID = 1L;
	private Hospital hospital;

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

			String[] parts = msg.getContent().split("-");

			if (msg.getPerformative() == ACLMessage.INFORM) {

				if (parts[0].equals("Marcacao")) {
					makeAppointment(parts[1], parts[2], reply);
					send(reply);
				} else {

				}

			}
		}

		private void makeAppointment(String speciality, String timeStamp,
				ACLMessage reply) {

			long ts = Long.valueOf(timeStamp).longValue();
			TimeTable timetable=hospital.getTimetable();
			if (timetable.slotTaken(speciality, ts)) {
				reply.setContent("Remarcacao-" + (ts + 3600));
				System.out.println("nao marcado");

			} else {

				String[] parts = reply.getReplyWith().split("@");

				timetable.scheduleAppointment(ts, parts[0], speciality);
				reply.setContent("confirmado");
				System.out.println("marcado-" + timetable.timetable.get(ts));

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
			hospital=new Hospital(0,this);
		
		sd.setType("HospitalAgent");
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

		sd1.setType("PatientAgent");
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
	}

	// sends notication
	public void sendNotification(String string, String key, long timeEpooch) {

		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.addReceiver(new AID(string, AID.ISLOCALNAME));
		msg.setContent("Aproximacao-" + key + "-" + timeEpooch);
		send(msg);

		System.out.println(string);
	}

	// fim do metodo setup

	
}