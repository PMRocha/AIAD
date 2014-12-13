package gui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JFrame;

import agents.PatientAgent;

import java.awt.BorderLayout;
import java.util.Date;

import javax.swing.Box;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.JTable;
import javax.swing.JButton;
import javax.swing.JToolBar;
import javax.swing.JSeparator;

public class PatientGUI extends JFrame implements ActionListener{
	private static final long serialVersionUID = 1L;

	PatientAgent agent;
	private JTable table;
	private Object columnNames[] = { "TimeStamp", "Ocupation"};
	JComboBox<String> currentTimeFieldCancel;
	JComboBox<String> currentTimeFieldReschedule;

	public PatientGUI(PatientAgent agent) {

		super(agent.getLocalName());
		getContentPane().setLayout(new BorderLayout(0, 0));

		table = new JTable((Object[][])agent.getPatient().getTimetable().getTimetableObject(),columnNames);

		JComboBox<Long> currentTimeFieldAddConsultation = new JComboBox(agent.getPatient().getTimetable().availableTime());

		JPanel myPanelAddConsultation = new JPanel();
		myPanelAddConsultation.add(new JLabel("Current Time:"));
		myPanelAddConsultation.add(currentTimeFieldAddConsultation);

		JLabel lblNewLabel = new JLabel(agent.getLocalName());
		getContentPane().add(lblNewLabel, BorderLayout.NORTH);

		JToolBar toolBar = new JToolBar();
		toolBar.setOrientation(SwingConstants.VERTICAL);
		getContentPane().add(toolBar, BorderLayout.WEST);

		JButton addConsultationButton = new JButton("Add Consultation");
		addConsultationButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				int result = JOptionPane.showConfirmDialog(new JFrame(), myPanelAddConsultation, 
						"Please Enter The New Consultation", JOptionPane.OK_CANCEL_OPTION);
				if (result == JOptionPane.OK_OPTION) {
					if(((long) new Date().parse(currentTimeFieldAddConsultation.getSelectedItem().toString()))/1000<agent.getPatient().getTimeEpooch()){
						JOptionPane.showMessageDialog(new JPanel(), "Timestamp selected is Invalid (time has passed)");
					}
					else
					{
						agent.appointment0(((long) new Date().parse(currentTimeFieldAddConsultation.getSelectedItem().toString()))/1000);
					}
				}
			}	
		});

		JSeparator separator_3 = new JSeparator();
		toolBar.add(separator_3);
		toolBar.add(addConsultationButton);

		JSeparator separator = new JSeparator();
		separator.setOrientation(SwingConstants.VERTICAL);
		toolBar.add(separator);

		currentTimeFieldCancel = new JComboBox(agent.getPatient().getTimetable().consultationTime());

		JPanel myPanelCancel = new JPanel();
		myPanelCancel.add(new JLabel("Current Time:"));
		myPanelCancel.add(currentTimeFieldCancel); 

		JButton cancelConsultationButton = new JButton("Cancel Consultation");
		cancelConsultationButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				int result = JOptionPane.showConfirmDialog(new JFrame(), myPanelCancel, 
						"Please Enter The Current Timestamps", JOptionPane.OK_CANCEL_OPTION);
				if (result == JOptionPane.OK_OPTION && currentTimeFieldCancel.getSelectedItem()!=null) {
					if(((long) new Date().parse(currentTimeFieldCancel.getSelectedItem().toString()))/1000<agent.getPatient().getTimeEpooch()){
						JOptionPane.showMessageDialog(new JPanel(), "Timestamp selected is Invalid (time has passed)");
					}
					else
					{
						agent.cancelAppointment(((long) new Date().parse(currentTimeFieldCancel.getSelectedItem().toString()))/1000);
					}
				}
			}	
		});
		toolBar.add(cancelConsultationButton);

		currentTimeFieldReschedule = new JComboBox(agent.getPatient().getTimetable().consultationTime());
		JComboBox<String> newTimeFieldReschedule = new JComboBox(agent.getPatient().getTimetable().availableTime());

		JPanel myPanelReschedule = new JPanel();
		myPanelReschedule.add(new JLabel("Current Time:"));
		myPanelReschedule.add(currentTimeFieldReschedule);
		myPanelReschedule.add(Box.createHorizontalStrut(15));
		myPanelReschedule.add(new JLabel("New Time:"));
		myPanelReschedule.add(newTimeFieldReschedule); 

		JButton rescheduleConsultationButton = new JButton("Reschedule Consultation");
		rescheduleConsultationButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				int result = JOptionPane.showConfirmDialog(new JFrame(), myPanelReschedule, 
						"Please Enter The New Timestamps", JOptionPane.OK_CANCEL_OPTION);
				if (result == JOptionPane.OK_OPTION && currentTimeFieldReschedule.getSelectedItem()!=null) {
					if((Long)currentTimeFieldReschedule.getSelectedItem()<agent.getPatient().getTimeEpooch()||(Long)newTimeFieldReschedule.getSelectedItem()<agent.getPatient().getTimeEpooch()){
						JOptionPane.showMessageDialog(new JPanel(), "Timestamp selected is Invalid (time has passed)");
					}
				}
			}	
		});

		JSeparator separator_1 = new JSeparator();
		separator_1.setOrientation(SwingConstants.VERTICAL);
		toolBar.add(separator_1);
		toolBar.add(rescheduleConsultationButton);

		JSeparator separator_2 = new JSeparator();
		toolBar.add(separator_2);


		table.setBorder(null);
		table.setEnabled(false);

		JScrollPane scrollPane = new JScrollPane(table);

		getContentPane().add(scrollPane, BorderLayout.CENTER);


		initComponents();
	}
	private void initComponents() {
	}

	public void showGui() {
		pack();
		super.setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if ("debug".equals(e.getActionCommand())) {
			System.out.println(agent.toString());
		} 		
	}

	public void refreshPosition() {
		refreshProduct();
		refreshLoad();

	}

	public void refreshProduct(){

	}
	public void refreshLoad() {

	}	

	public void refresh(String content,long time){
		table.setValueAt(content, (int) ((time-1420070400)/3600), 1);
		currentTimeFieldCancel.addItem(new Date(time*1000).toString());
		currentTimeFieldReschedule.addItem(new Date(time*1000).toString());
	}
	
	public void removeTime(long time){
		table.setValueAt("livre", (int) ((time-1420070400)/3600), 1);
		currentTimeFieldCancel.removeItem(new Date(time*1000).toString());
		currentTimeFieldReschedule.removeItem(new Date(time*1000).toString());
	}
}