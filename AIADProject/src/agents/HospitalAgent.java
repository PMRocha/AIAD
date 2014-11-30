package agents;

import algorithmHospitalSide.AppointmentAlgorithm0H;
import algorithmHospitalSide.AppointmentAlgorithm1H;
import algorithmHospitalSide.ReappointmentAlgorithmH;
import jade.core.*;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import resources.Hospital;

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

				switch (parts[0]) {
				case "Marcacao0": {
					reply = AppointmentAlgorithm0H.makeAppointment(parts[1],
							parts[2], reply, hospital);
					send(reply);
					break;
				}
				case "Marcacao1": {
					AppointmentAlgorithm1H.makeMergingAppointment(parts[1],
							parts[2], reply, hospital);
					send(reply);
					break;
				}
				case "Urgencia": {
					reply = AppointmentAlgorithm0H.makeAppointment(parts[1],
							parts[2], reply, hospital);
					send(reply);
					break;
				}
				case "RemarcacaoAproximacao": {
					// parts[1]->especialidade/parts[2]->hora/parts[3]->nova
					// hora
					long time = Long.valueOf(parts[2]).longValue();
					ReappointmentAlgorithmH.setTime(time);
					ReappointmentAlgorithmH.resetPatients();
					String patient = reply.getReplyWith();
					String[] patientParts = patient.split("@");
					ReappointmentAlgorithmH.addPatients(patientParts[0]);
					hospital.clearAppointment(parts[1], time);
					reply = AppointmentAlgorithm0H.makeAppointment(parts[1],
							parts[3], reply, hospital);
					send(reply);
					String nextPatient = ReappointmentAlgorithmH.getNextPatient(
							parts[1], hospital);
					if (!nextPatient.equals("")) {
						// próximo patient=>"patient-horaDaConsulta"
						ACLMessage forwardMessage = new ACLMessage(AP_INITIATED);

						String nextPatientparts[] = nextPatient.split("-");

						long nextPatientAppointmentTime = Long.valueOf(
								nextPatientparts[1]).longValue();
						forwardMessage = ReappointmentAlgorithmH
								.forwardAppointment(nextPatientparts[0],
										parts[1], time,
										nextPatientAppointmentTime,
										forwardMessage);
						System.out.println("forwardMessage:"
								+ forwardMessage.getContent());
						send(forwardMessage);
					}
					break;
				}
				case "ConfirmadoAproximacao": {
					System.out.println("Confirmada consulta");
					break;
				}
				case "AproximacaoMarcada": {
					System.out.println("Confirmada marcacao consulta");
					break;
				}
				case "AdiantamentoNegado": {
					String nextPatient = ReappointmentAlgorithmH.getNextPatient(
							parts[1], hospital);
					if (!nextPatient.equals("")) {
						// próximo patient=>"patient-horaDaConsulta"
						ACLMessage forwardMessage = new ACLMessage(AP_INITIATED);

						String nextPatientparts[] = nextPatient.split("-");

						long nextPatientAppointmentTime = Long.valueOf(
								nextPatientparts[1]).longValue();
						long actualAppointmentTime = Long.valueOf(
								nextPatientparts[2]).longValue();
						forwardMessage = ReappointmentAlgorithmH
								.forwardAppointment(nextPatientparts[0],
										parts[1], actualAppointmentTime,
										nextPatientAppointmentTime,
										forwardMessage);
						System.out.println("forwardMessage:"
								+ forwardMessage.getContent());
						send(forwardMessage);
					} else {
						System.out
								.println("No more parients in the twelve hours diference");
					}
					break;
				}
				case "AdiantamentoAceite": {
					hospital.clearAppointment(parts[1], Long.valueOf(parts[2])
							.longValue());
					String patient = reply.getReplyWith();
					String[] patientParts = patient.split("@");
					hospital.getTimetable().scheduleAppointment(Long.valueOf(parts[3])
							.longValue(),patientParts[0] , parts[1]);
					
					System.out.println("adiantado");
					break;
				}
				default: {
					System.out.println("mensagem não reconhecida:"
							+ msg.getContent());
					break;
				}
				}
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