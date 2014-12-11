package agents;

import algorithmPatientSide.AppointmentAlgorithm0P;
import algorithmPatientSide.AppointmentAlgorithm1P;
import algorithmPatientSide.NotifyAppointmentAlgorithmP;
import algorithmPatientSide.ReappointmentAlgorithmP;
import algorithmPatientSide.UrgencyAlgorithm0P;
import algorithmPatientSide.UrgencyAlgorithm1P;
import resources.Patient;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class PatientAgent extends Agent {

	private static final long serialVersionUID = 1L;
	private Patient patient;
	private String name;

	class PatientBehaviour extends SimpleBehaviour {

		private static final long serialVersionUID = 1L;

		// construtor do behaviour
		public PatientBehaviour(Agent a) {
			super(a);
		}

		// método action
		public void action() {
			ACLMessage msg = blockingReceive();
			ACLMessage reply = msg.createReply();
			String content = msg.getContent();

				if (content.equals("Aberto para servico")) {
					patient.runTime();
				}

				else {
					String[] parts = content.split("-");

					switch (parts[0]) {
					case "Remarcacao": {
						System.out.println(getLocalName() + ": recebi "
								+ msg.getContent());
						reply = AppointmentAlgorithm0P.reappointment(Long
								.valueOf(parts[1]).longValue(), msg, reply,
								patient);
						System.out.println(name + ":enviei->"
								+ reply.getContent());
						if (!reply.equals(null))
							send(reply);
						break;
					}
					case "Aproximacao": {
						System.out.println(name + ":recebi->Confirmacao:"
								+ parts[1] + "," + parts[2]);
						reply = NotifyAppointmentAlgorithmP.doNotification(
								reply, patient, parts[2]);
						System.out.println(name + ":mandei->"
								+ reply.getContent());
						send(reply);
						break;
					}
					case "Marcado": {
						System.out.println(name + ":recebi->consulta marcada:"
								+ parts[1]);
						patient.setAppointment(Long.valueOf(parts[1])
								.longValue());
						break;
					}
					case "MarcadoUrgencia": {
						System.out.println(name + ":recebi->urgencia marcada:"
								+ parts[1]);
						patient.setUrgentAppointment(Long.valueOf(parts[1])
								.longValue());
						break;
					}
					case "DesmarcadaPorUrgencia": {
						System.out.println(name
								+ ":recebi->consulta desmarcada " + parts[1]);
						reply=UrgencyAlgorithm0P.createReapointmentUrgenceMessage(reply,Long.valueOf(parts[2]).longValue(),parts[1], patient);
						send(reply);
						break;
					}
					case "RemarcadaConsulta": {
						System.out.println(name
								+ ":recebi->consulta adiantada marcada:"
								+ parts[1]);
						reply = ReappointmentAlgorithmP.reappointment(reply,
								parts, patient);
						send(reply);
					}
						break;
					case "Horario": {
						System.out.println(name
								+ ":recebi->Horário:"
								+ parts[1]);
						reply = UrgencyAlgorithm1P.schedule(reply,
								Long.valueOf(parts[1]).longValue(), patient);
						send(reply);
					}
						break;
					default: {
						System.out.println(name
								+ ":recebi->Mensagem não reconhecida:"
								+ msg.getContent());
					}
					
				}
			}
		}

		@Override
		public boolean done() {
			return patient.isDone();
		}
	}

	// called by timer in patient

	public void appointment0(long appTime) {

		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg = AppointmentAlgorithm0P.appointment(msg, msg, appTime, patient);
		if (!msg.equals(null)) {
			msg = setReceiverHospital(msg);
			send(msg);
		}
	}

	// marcacao do algoritmo 1
	public void appointment1(String schedule) {

		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg = AppointmentAlgorithm1P.appointment(msg, patient, schedule);
		msg = setReceiverHospital(msg);
		send(msg);
	}

	// marcacao de urgencia

	public void appointmentUrg(long nextHour) {
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setContent("Urgencia-" + patient.getSpeciality() + "-"
				+ patient.getTimetable().firstAvailable(nextHour));
		msg = setReceiverHospital(msg);
		send(msg);
	}

	public ACLMessage setReceiverHospital(ACLMessage msg) {
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd1 = new ServiceDescription();

		sd1.setType("HospitalAgent");
		template.addServices(sd1);
		try {
			DFAgentDescription[] result = DFService.search(this, template);
			msg.addReceiver(result[0].getName());
		} catch (FIPAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return msg;
	}

	// método setup
	protected void setup() {
		// obtém argumentos
		Object[] args = getArguments();
		if (args != null && args.length == 5) {
			patient = new Patient((String) args[0], (int) args[1],
					(long) args[2], (int) args[3], (int) args[4], this);

		} else {
			System.out.println(name
					+ "->Os parametros do agente paciente estao errados");
			System.exit(1);// temina programa mas lança exepção
		}

		// regista agente no DF
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setName(getName());
		sd.setType("PatientAgent");
		name = sd.getName();
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException e) {
			e.printStackTrace();
		}

		// cria behaviour
		PatientBehaviour b = new PatientBehaviour(this);
		addBehaviour(b);

	} // fim do metodo setup

	public Patient getPatient() {
		return patient;
	}

}
