package agents;

import resources.Patient;
import jade.core.AID;
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
			long halfDayDif;

			if (msg.getPerformative() == ACLMessage.INFORM) {

				if (content.equals("Aberto para servico")) {
					patient.runTime();
				}

				else {
					String[] parts = content.split("-");

					switch (parts[0]) {
					case "Remarcacao": {
						halfDayDif = Long.valueOf(parts[1]).longValue();
						appointment(msg, reply, halfDayDif);
						break;
					}
					case "Aproximacao": {
						System.out.println("Confirmacao:" + parts[1] + ","
								+ parts[2]);
						long time = Long.valueOf(parts[2]).longValue();
						if (patient.appointment(time)) {
							reply.setContent("ConfirmadoAproximacao-"
									+ patient.getSpeciality() + "-" + parts[2]);
							send(reply);
						} else {
							if (patient.freeTime(time)) {
								reply.setContent("AproximacaoMarcada-"
										+ patient.getSpeciality() + "-"
										+ parts[2]);
								send(reply);
							} else {
								reply.setContent("RemarcacaoAproximacao-"
										+ patient.getSpeciality()
										+ "-"
										+ parts[2]
										+ "-"
										+ patient.getTimetable()
												.firstAvailable(time + 3600));
								send(reply);
							}

						}
						break;
					}
					case "Marcado": {
						System.out.println("recebi->consulta marcada:" + parts[1]);
						patient.setAppointment(Long.valueOf(parts[1])
								.longValue());
						break;
					}
					default: {
						System.out.println("Mensagem não reconhecida");
						System.out.println("received " + msg.getContent());
					}
					}
				}
			}
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return false;
		}

		// does initial appointment when the program is started (the appointment
		// can be made with a 12 hours difference)
		private void appointment(ACLMessage msg, ACLMessage reply, long appTime) {
			System.out.println(getLocalName() + ": recebi " + msg.getContent());

			reply.setContent("Marcacao-" + patient.getSpeciality() + "-"
					+ patient.getTimetable().firstAvailable(appTime));
			System.out.println("Enviei" + reply.getContent());
			send(reply);
		}
	}

	// called by timer in patient

	public void appointment0(long appTime) {

		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setContent("Marcacao0-" + patient.getSpeciality() + "-"
				+ patient.getTimetable().firstAvailable(appTime));
		msg.addReceiver(new AID("hosp", AID.ISLOCALNAME));// ->atenção:
															// mudar o nome
															// de hosp para
															// outro
															// consoante o
															// nome do
															// agente
															// hospital
		send(msg);

	}

	// marcacao de urgencia

	public void appointmentUrg(long nextHour) {
		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setContent("Urgencia-" + patient.getSpeciality() + "-"
				+ patient.getTimetable().firstAvailable(nextHour));
		msg.addReceiver(new AID("hosp", AID.ISLOCALNAME));// ->atenção:
															// mudar o nome
															// de hosp para
															// outro
															// consoante o
															// nome do
															// agente
															// hospital
		send(msg);
		

	}

	public void appointment1(String schedule) {

		ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
		msg.setContent("Marcacao1-" + patient.getSpeciality() + "-" + schedule);
		msg.addReceiver(new AID("hosp", AID.ISLOCALNAME));// ->atenção:
															// mudar o nome
															// de hosp para
															// outro
															// consoante o
															// nome do
															// agente
															// hospital
		send(msg);

	}

	// método setup
	protected void setup() {
		// obtém argumentos
		Object[] args = getArguments();
		if (args != null && args.length == 5) {
			patient = new Patient((String) args[0], (int) args[1],
					(long) args[2], (int) args[3], (int) args[4], this);

		} else {
			System.out
					.println("Os parametros do agente paciente estao errados");
			System.exit(1);// temina programa mas lança exepção
		}

		// regista agente no DF
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setName(getName());
		sd.setType("PatientAgent");
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

}
