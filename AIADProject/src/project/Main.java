package project;

import agents.HospitalAgent;
import jade.core.Profile;
import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;

public class Main {

	public static void main(String arg[]) {
		// Get a hold on JADE runtime
		Runtime rt = Runtime.instance();
		// Create a default profile
		Profile p = new ProfileImpl();
		// Create a new non-main container, connecting to the default
		// main container (i.e. on this host, port 1099)
		ContainerController cc = rt.createMainContainer(p);
		
		HospitalAgent hosp=new HospitalAgent();
		
		 Object[] arguments = new Object[4];
		 arguments[0]="pediatria";
		 arguments[1]=1;
		 arguments[2]=(long)1420106400;
		 arguments[3]=0;
		
		try {
			AgentController rma = cc.createNewAgent("rma", "jade.tools.rma.rma", null);
			
			
			AgentController p1 = cc.createNewAgent("Patient1", "agents.PatientAgent",arguments);
			rma.start();
			
			p1.start();
			
			 Object[] arguments1 = new Object[4];
			arguments1[0]="oncologia";
			arguments1[1]=2;
			arguments1[2]=(long)1420113600;
			arguments1[3]=0;
			
			AgentController p2 = cc.createNewAgent("Patient2", "agents.PatientAgent",arguments);
			p2.start();
			
			AgentController p3 = cc.createNewAgent("Patient3", "agents.PatientAgent",arguments1);
			p3.start();
			
			AgentController h1 = cc.acceptNewAgent("hosp", hosp);
			h1.start();
			
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
