package algorithmPatientSide;

import jade.lang.acl.ACLMessage;
import resources.Patient;

public class UrgencyAlgorithm1P {

	public static ACLMessage schedule(ACLMessage msg, long time,
			Patient patient) {
		String content;

		content = "Horario-";

		for (long i = time; i < time + 12 * 3600; i += 3600) {
			content += i + "=" + patient.getTimetableTimetable().get(i) + ",";
		}
		msg.setContent(content);
		return msg;
	}

}
