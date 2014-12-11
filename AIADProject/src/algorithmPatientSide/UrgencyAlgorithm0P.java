package algorithmPatientSide;

import resources.Patient;
import jade.lang.acl.ACLMessage;

public class UrgencyAlgorithm0P {

	public static ACLMessage createReapointmentUrgenceMessage(ACLMessage reply,
			long time, String speciality,Patient patient) {
		patient.getTimetableTimetable().replace(time, "livre");
		
		reply.setContent("RemarcadoPorUrgencia-"+speciality+"-"+patient.getTimetable().firstAvailable(time));
		reply.setPerformative(ACLMessage.REQUEST);
		return reply;
	}

}
