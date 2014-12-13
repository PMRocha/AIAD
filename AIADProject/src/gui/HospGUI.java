package gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import agents.HospitalAgent;

public class HospGUI extends JFrame implements ActionListener{
	private static final long serialVersionUID = 1L;

	HospitalAgent agent;
	private JTable table;
	private Object columnNames[] = { "TimeStamp", "Ocupation"};

	public HospGUI(HospitalAgent agent) {
		super(agent.getLocalName());
		getContentPane().setLayout(new BorderLayout(0, 0));
		
		this.agent = agent;

		table = new JTable((Object[][])agent.getHospital().getTimetable().getTimetableObject(),columnNames);

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

	public void refresh(){
		if(agent.getHospital()!=null){
			Object[][] temp = agent.getHospital().getTimetable().getTimetableObject();
			for(int i=0;i<100;i++){
				for(int j=0;j<2;j++){
					table.setValueAt(temp[i][j], i, j);
				}
			}
		}
	}
}
