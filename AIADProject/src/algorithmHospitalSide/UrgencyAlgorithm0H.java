package algorithmHospitalSide;

import resources.Hospital;
import jade.core.AID;
import jade.lang.acl.ACLMessage;

public class UrgencyAlgorithm0H {
	
	private static String patient;
	public static ACLMessage urgency()
	{
		return null;
		
	}

	public static ACLMessage appointment(ACLMessage reply, Hospital hospital,
			String speciality, long time) {
		
		String[] parts = reply.getReplyWith().split("@");
		System.out.println(time+"-"+parts[0]+"-"+speciality);
		if(hospital.getTimetable().slotTaken(speciality, time))
		{
			hospital.getTimetable().cancelConsultation(time, speciality);
		}
		hospital.getTimetable().scheduleAppointment(time,parts[0] ,speciality);
		reply.setContent("MarcadoUrgencia-" + time);
		return reply;
	}
	

	public static String getPatient(long time,Hospital hospital,String speciality) {

		String rsp=null;
		rsp=hospital.getTimetable().CheckConsultationsNow(time, speciality);
		
		return rsp;
	}

	public static long getFirstAvailableTime(long time, Hospital hospital) {
		while(hospital.getTimetable().timetable.get(time).equals("fechado"))
		{
			time+=3600;
			
			if(time>1420426800)
				return 0;
		}
		return time;
	}

	public static ACLMessage createReappointmentMessage(long time,
			String speciality, String toNotify) {
		ACLMessage reappointmentMessage=new ACLMessage(ACLMessage.INFORM);
		reappointmentMessage.setContent("DesmarcadaPorUrgencia-"+speciality+"-"+(time+3600));
		patient=toNotify;
		reappointmentMessage.addReceiver(new AID(toNotify, AID.ISLOCALNAME));
		return reappointmentMessage;
	}

	public static ACLMessage createReappointmentMessage(long time,
			String speciality) {
		ACLMessage reappointmentMessage=new ACLMessage(ACLMessage.INFORM);
		reappointmentMessage.setContent("DesmarcadaPorUrgencia-"+speciality+"-"+(time+3600));
		return reappointmentMessage;
	}
	
	public static ACLMessage scheduleAppointment(long time,String speciality, ACLMessage reply,Hospital hospital) {
		
		if(hospital.getTimetable().slotTaken(speciality, time))
		{
			hospital.getTimetable().cancelConsultation(time, speciality);
		}
		
		hospital.getTimetable().scheduleAppointment(time, patient, speciality);
		reply.setContent("Marcado-"+time);
		return reply;
	}

}
