package project;

import project.HospitalPlanner.HospitalPlannerBehaviour;
import jade.core.Agent;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class Patient extends Agent {

	private String speciality;

	public String getSpeciality() {
		return speciality;
	}

	public void setSpeciality(String speciality) {
		this.speciality = speciality;
	}

	class PatientBehaviour extends SimpleBehaviour {
		private int n = 0;

		// construtor do behaviour
		public PatientBehaviour(Agent a) {
			super(a);
		}

		// método action
		public void action() {
			ACLMessage msg = blockingReceive();
			if (msg.getPerformative() == ACLMessage.INFORM) {
				System.out.println(++n + " " + getLocalName() + ": recebi "
						+ msg.getContent());
				// cria resposta
			/*	ACLMessage reply = msg.createReply();
				String received = msg.getContent();
				System.out.println(received);
				reply.setContent("yes");*/
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
	      if(args != null && args.length > 0) {
	         speciality = (String) args[0];
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
	      } catch(FIPAException e) {
	         e.printStackTrace();
	      }

	      // cria behaviour
	      //PatientBehaviour b = new PatientBehaviour(this);
	      //addBehaviour(b);
	      
	      
	      
	   // cria behaviour
	      PatientBehaviour b = new PatientBehaviour(this);
			addBehaviour(b);


			DFAgentDescription template = new DFAgentDescription();
			ServiceDescription sd1 = new ServiceDescription();
			
			sd1.setType("HospitalPlanner");
			template.addServices(sd1);
			try {
				DFAgentDescription[] result = DFService.search(this, template);
				// envia mensagem "pong" inicial a todos os agentes "ping"
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				for (int i = 0; i < result.length; ++i)
					msg.addReceiver(result[i].getName());
				msg.setContent("Marcar-"+speciality);
				send(msg);
			} catch (FIPAException e) {
				e.printStackTrace();
			}

	   }   // fim do metodo setup


}
