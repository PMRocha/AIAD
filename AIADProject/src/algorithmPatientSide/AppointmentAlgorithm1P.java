package algorithmPatientSide;

import jade.lang.acl.ACLMessage;
import resources.Patient;

public class AppointmentAlgorithm1P {

	public static ACLMessage appointment(ACLMessage msg,Patient patient, String schedule) {
		msg.setContent("Marcacao1-" + patient.getSpeciality() + "-" + schedule);
		return msg;
	}

}
