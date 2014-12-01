package agents;

import algorithmHospitalSide.AppointmentAlgorithm0H;
import algorithmHospitalSide.AppointmentAlgorithm1H;
import algorithmHospitalSide.ReappointmentAlgorithmH;
import algorithmHospitalSide.UrgencyAlgorithm0H;
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
					appointment0Action(reply, parts);
					break;
				}
				case "Marcacao1": {
					appointment1Action(reply, parts);
					break;
				}
				case "Urgencia": {
					urgencyAction(reply, parts);
					break;
				}
				case "RemarcadoPorUrgencia": {
					long time = UrgencyAlgorithm0H.getFirstAvailableTime(Long
							.valueOf(parts[2]).longValue(), hospital);

					if (Long.valueOf(parts[2]).longValue() == time) {

						String toNotify = UrgencyAlgorithm0H.getPatient(time,
								hospital, parts[1]);

						reply = UrgencyAlgorithm0H.scheduleAppointment(time,
								parts[1], reply, hospital);

						System.out.println(Long.valueOf(parts[2]).longValue()
								+ "-" + parts[1] + "-" + toNotify);
						if (toNotify != null) {
							System.out.println("notify:" + toNotify);
							ACLMessage reappointmentMessage = UrgencyAlgorithm0H
									.createReappointmentMessage(time, parts[1],
											toNotify);
							send(reappointmentMessage);
						}

					}

					else {
						reply = UrgencyAlgorithm0H.createReappointmentMessage(
								time, parts[1]);

					}
					send(reply);
					break;
				}
				case "RemarcacaoAproximacao": {
					notifyReappointmentAction(reply, parts);
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
					notifyNegativeAction(parts);
					break;
				}
				case "AdiantamentoAceite": {
					notifyAffirmativeAction(reply, parts);
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

		private void notifyAffirmativeAction(ACLMessage reply, String[] parts) {
			hospital.clearAppointment(parts[1], Long.valueOf(parts[2])
					.longValue());
			String patient = reply.getReplyWith();
			String[] patientParts = patient.split("@");
			hospital.getTimetable().scheduleAppointment(
					Long.valueOf(parts[3]).longValue(), patientParts[0],
					parts[1]);

			System.out.println("adiantado");
		}

		private void notifyNegativeAction(String[] parts) {
			String nextPatient = ReappointmentAlgorithmH.getNextPatient(
					parts[1], hospital);
			if (!nextPatient.equals("")) {
				// próximo patient=>"patient-horaDaConsulta"
				ACLMessage forwardMessage = new ACLMessage(AP_INITIATED);

				String nextPatientparts[] = nextPatient.split("-");

				long nextPatientAppointmentTime = Long.valueOf(
						nextPatientparts[1]).longValue();
				long actualAppointmentTime = Long.valueOf(nextPatientparts[2])
						.longValue();
				forwardMessage = ReappointmentAlgorithmH.forwardAppointment(
						nextPatientparts[0], parts[1], actualAppointmentTime,
						nextPatientAppointmentTime, forwardMessage);
				System.out.println("forwardMessage:"
						+ forwardMessage.getContent());
				send(forwardMessage);
			} else {
				System.out
						.println("No more parients in the twelve hours diference");
			}
		}

		private void notifyReappointmentAction(ACLMessage reply, String[] parts) {
			// parts[1]->especialidade/parts[2]->hora/parts[3]->nova
			// hora
			long time = Long.valueOf(parts[2]).longValue();
			ReappointmentAlgorithmH.setTime(time);
			ReappointmentAlgorithmH.resetPatients();
			String patient = reply.getReplyWith();
			String[] patientParts = patient.split("@");
			ReappointmentAlgorithmH.addPatients(patientParts[0]);
			hospital.clearAppointment(parts[1], time);
			reply = AppointmentAlgorithm0H.makeAppointment(parts[1], parts[3],
					reply, hospital);
			send(reply);

			String nextPatient = ReappointmentAlgorithmH.getNextPatient(
					parts[1], hospital);
			if (!nextPatient.equals("")) {
				// próximo patient=>"patient-horaDaConsulta"
				ACLMessage forwardMessage = new ACLMessage(AP_INITIATED);
				String nextPatientparts[] = nextPatient.split("-");
				long nextPatientAppointmentTime = Long.valueOf(
						nextPatientparts[1]).longValue();
				forwardMessage = ReappointmentAlgorithmH.forwardAppointment(
						nextPatientparts[0], parts[1], time,
						nextPatientAppointmentTime, forwardMessage);
				System.out.println("forwardMessage:"
						+ forwardMessage.getContent());
				send(forwardMessage);
			}
		}

		private void appointment0Action(ACLMessage reply, String[] parts) {
			reply = AppointmentAlgorithm0H.makeAppointment(parts[1], parts[2],
					reply, hospital);
			send(reply);
		}

		private void appointment1Action(ACLMessage reply, String[] parts) {
			AppointmentAlgorithm1H.makeMergingAppointment(parts[1], parts[2],
					reply, hospital);
			send(reply);
		}

		private void urgencyAction(ACLMessage reply, String[] parts) {
			long time = UrgencyAlgorithm0H.getFirstAvailableTime(
					Long.valueOf(parts[2]).longValue(), hospital);
			String toNotify = UrgencyAlgorithm0H.getPatient(time, hospital,
					parts[1]);

			System.out.println(Long.valueOf(parts[2]).longValue() + "-"
					+ parts[1]);
			if (toNotify != null) {
				ACLMessage reappointmentMessage = UrgencyAlgorithm0H
						.createReappointmentMessage(time, parts[1], toNotify);
				send(reappointmentMessage);
			}
			reply = UrgencyAlgorithm0H.appointment(reply, hospital, parts[1],
					time);
			if (!reply.equals(null))
				send(reply);
			else
				System.out
						.println("Impossivel marcar urgencia(sai do limite do horário)");
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