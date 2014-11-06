package project;

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
		
		HospitalPlanner hosp=new HospitalPlanner();
		
		 Object[] arguments = new Object[2];
		 arguments[0]="pediatria";
		 arguments[1]=1;
		
		try {
			AgentController rma = cc.createNewAgent("rma", "jade.tools.rma.rma", null);
			
			
			AgentController p1 = cc.createNewAgent("pat1", "project.Patient",arguments);
			rma.start();
			
			p1.start();
			
			 Object[] arguments1 = new Object[2];
			arguments1[0]="oncologia";
			arguments1[1]=2;
			
			AgentController p2 = cc.createNewAgent("pat2", "project.Patient",arguments1);
			p2.start();
			
			AgentController h1 = cc.acceptNewAgent("hosp", hosp);
			h1.start();
			
		} catch (StaleProxyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
