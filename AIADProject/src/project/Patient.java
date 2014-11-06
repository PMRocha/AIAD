package project;

import java.io.IOException;

import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class Patient extends Agent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String speciality;
	private TimeTable timetable;

	public TimeTable getTimetable() {
		return timetable;
	}

	public void setTimetable(TimeTable timetable) {
		this.timetable = timetable;
	}

	public String getSpeciality() {
		return speciality;
	}

	public void setSpeciality(String speciality) {
		this.speciality = speciality;
	}

	class PatientBehaviour extends SimpleBehaviour {
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;
		private int n = 0;

		// construtor do behaviour
		public PatientBehaviour(Agent a) {
			super(a);
		}

		// método action
		public void action() {
			ACLMessage msg = blockingReceive();
			ACLMessage reply = msg.createReply();
			if (msg.getPerformative() == ACLMessage.INFORM) {

				if (msg.getContent().equals("Aberto para servico")) {
				
					System.out.println(++n + " " + getLocalName() + ": recebi "
							+ msg.getContent());
					reply.setContent("Marcar-" + speciality
							+ TimeClock.timeEpooch);
					System.out.println("Enviei" + reply.getContent());
					send(reply);
				}

				else {
					System.out.println(++n + " " + getLocalName() + ": recebi "
							+ msg.getContent());
				}

				// cria resposta
				/*
				 * ACLMessage reply = msg.createReply(); String received =
				 * msg.getContent(); System.out.println(received);
				 * reply.setContent("yes");
				 */
			}
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return false;
		}
	}

	// método setup
	protected void setup() {
		// obtém argumentos
		Object[] args = getArguments();
		if (args != null && args.length > 0 && args.length < 3) {
			speciality = (String) args[0];

		
		try {
			TimeTable timetablehelp = new TimeTable("TimeTable.xlsx", (int) args[1]);
			setTimetable(timetablehelp);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	

		} else {
			System.out.println("Não especificou o tipo");

		}

		// regista agente no DF
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setName(getName());
		sd.setType("Patient");
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
