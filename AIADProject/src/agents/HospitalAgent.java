package agents;

import jade.core.*;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import resources.Hospital;
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

		// m�todo action
		public void action() {
			ACLMessage msg = blockingReceive();
			ACLMessage reply = msg.createReply();

			System.out.println(" " + getLocalName() + ": recebi "
					+ msg.getContent());

			String[] parts = msg.getContent().split("-");

			if (msg.getPerformative() == ACLMessage.INFORM) {

				switch (parts[0]) {
				case "Marcacao0": {
					makeAppointment(parts[1], parts[2], reply);
					send(reply);
					break;
				}
				case "Marcacao1": {
					makeMergingAppointment(parts[1], parts[2], reply);
					send(reply);
					break;
				}
				case "Urgencia": {
					makeAppointment(parts[1], parts[2], reply);
					send(reply);
					break;
				}
				case "RemarcacaoAproximacao": {
					hospital.clearAppointment(parts[1], Long.valueOf(parts[2])
							.longValue());
					hospital.getNextPatient(parts[1]);
					makeAppointment(parts[1], parts[3], reply);
					send(reply);
					break;
				}
				default: {
					System.out.println("mensagem n�o reconhecida");
					break;
				}
				}
			}

		}

		private void makeMergingAppointment(String speciality,
				String timestamp, ACLMessage reply) {
			TimeTable timetable = hospital.getTimetable();
			long time = hospital.mergingAppointment(speciality, timestamp);
			String[] parts = reply.getReplyWith().split("@");
			timetable.scheduleAppointment(time, parts[0], speciality);
			reply.setContent("Marcado-" + time);
			System.out.println("Marcado-" + timetable.timetable.get(time));
		}

		private void makeAppointment(String speciality, String timestamp,
				ACLMessage reply) {

			long ts = Long.valueOf(timestamp).longValue();
			TimeTable timetable = hospital.getTimetable();
			if (timetable.slotTaken(speciality, ts)) {
				reply.setContent("Remarcacao-" + (ts + 3600));
				System.out.println("nao marcado");

			} else {

				String[] parts = reply.getReplyWith().split("@");
				timetable.scheduleAppointment(ts, parts[0], speciality);
				reply.setContent("Marcado-" + ts);
				System.out.println("Marcado-" + timetable.timetable.get(ts));

			}

		}

		// m�todo done
		public boolean done() {
			return false;
		}
	}

	protected void setup() {

		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setName(getName());
		hospital = new Hospital(0, this);

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