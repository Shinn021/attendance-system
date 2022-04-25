package main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

public class attendanceSettings extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final JPanel contentPanel = new JPanel();
	panelAttendance panelAttendance;
	public String obtainedDept;
	int[] hr = new int[24];
	int[] num = new int[60];
	int selectedHour, selectedMinute, selectedSecond;
	int obtainedNum;
	int deptCount;
	int secCount;
	String month;
	public static String name;
	int day, year;
	public static int currentRecordCount = 0;
	public String obtainedSub, obtainedSec;
	String currentTime;
	public JComboBox<String> cbSub;
	public boolean isCancelled = false;
	public JLabel obtainedDeptName, obtainedSecName;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		try {
			attendanceSettings dialog = new attendanceSettings();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public attendanceSettings() {
		super(null, ModalityType.TOOLKIT_MODAL);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setIconImage(Toolkit.getDefaultToolkit().getImage(attendanceSettings.class.getResource("/res/attendance.png")));
		setTitle("Add Attendance");
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(null);
		
		for(int i = 0; i < num.length; i++) {
			num[i] = i;
		}
		
		for(int i = 0; i < hr.length; i++) {
			hr[i] = i;
		}
		
		panelAttendance = new panelAttendance();
		
		JLabel attendanceName = new JLabel("Attendance Name: ");
		attendanceName.setBounds(10, 98, 262, 14);
		contentPanel.add(attendanceName);
		
		JLabel sectionName = new JLabel("Section Name: ");
		sectionName.setBounds(10, 36, 72, 14);
		contentPanel.add(sectionName);
		
		JLabel departmentName = new JLabel("Department Name: ");
		departmentName.setBounds(10, 11, 97, 14);
		contentPanel.add(departmentName);
		
		JPanel panelSetTimer = new JPanel();
		panelSetTimer.setBounds(10, 123, 180, 68);
		contentPanel.add(panelSetTimer);
		panelSetTimer.setVisible(false);
		panelSetTimer.setLayout(null);
		
		JLabel lblHour = new JLabel("Hours:");
		lblHour.setBounds(10, 11, 46, 14);
		panelSetTimer.add(lblHour);
		
		JLabel lblMinute = new JLabel("Minutes:");
		lblMinute.setBounds(66, 11, 46, 14);
		panelSetTimer.add(lblMinute);
		
		JLabel lblSeconds = new JLabel("Seconds:");
		lblSeconds.setBounds(122, 11, 46, 14);
		panelSetTimer.add(lblSeconds);
		
		JComboBox<Integer> cbHour = new JComboBox<Integer>();
		cbHour.setName("Hour");
		cbHour.setBounds(10, 36, 50, 22);
		cbHour.setSelectedItem(hr[0]);
		addItemsHour(cbHour);
		panelSetTimer.add(cbHour);
		
		JComboBox<Integer> cbMinute = new JComboBox<Integer>();
		cbMinute.setName("Minute");
		cbMinute.setBounds(66, 36, 50, 22);
		cbMinute.setSelectedItem(num[0]);
		addItems(cbMinute);
		panelSetTimer.add(cbMinute);
		
		JComboBox<Integer> cbSecond = new JComboBox<Integer>();
		cbSecond.setName("Second");
		cbSecond.setBounds(122, 36, 50, 22);
		cbSecond.setSelectedItem(num[0]);
		addItems(cbSecond);
		panelSetTimer.add(cbSecond);
		
		cbHour.addItemListener(new selectedItem(cbHour));
		cbMinute.addItemListener(new selectedItem(cbMinute));
		cbSecond.addItemListener(new selectedItem(cbSecond));
		
		JCheckBox withTimer = new JCheckBox("With Time Limit");
		withTimer.setBounds(10, 198, 97, 23);
		contentPanel.add(withTimer);;
		
		cbSub = new JComboBox<String>();
		cbSub.setBounds(92, 69, 180, 22);
		cbSub.addItem("Select a department first");
		cbSub.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					if(cbSub.getSelectedIndex() == 0) {
						attendanceName.setText("Attendance Name: ");
					} else {
						obtainedSub = cbSub.getSelectedItem().toString();
						checkRecordCount();
						Date date = new Date();
						Calendar cal = Calendar.getInstance();
						cal.setTime(date);
						month = new SimpleDateFormat("MMM").format(cal.getTime());
						day = cal.get(Calendar.DAY_OF_MONTH);
						year = cal.get(Calendar.YEAR);
						name = obtainedDept+"-"+obtainedSec+"-"+obtainedSub+" | "+month+" "+day+", "+year + " - " + currentRecordCount;
						attendanceName.setText("Attendance Name: " + name); 
					}
				}
				revalidate();
				repaint();
			}
		});
		contentPanel.add(cbSub);
		
		JLabel warningLabel = new JLabel("");
		warningLabel.setBounds(295, 177, 129, 40);
		warningLabel.setForeground(Color.RED);
		contentPanel.add(warningLabel);
		
		obtainedDeptName = new JLabel();
		obtainedDeptName.setBounds(117, 11, 97, 14);
		contentPanel.add(obtainedDeptName);
		
		JLabel subjectName = new JLabel("Subject Name:");
		subjectName.setBounds(10, 73, 72, 14);
		contentPanel.add(subjectName);
		
		obtainedSecName = new JLabel();
		obtainedSecName.setBounds(93, 36, 97, 14);
		contentPanel.add(obtainedSecName);
		
		withTimer.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					panelSetTimer.setVisible(true);
				} else {
					panelSetTimer.setVisible(false);
					cbHour.setSelectedItem(hr[0]);
					cbMinute.setSelectedItem(num[0]);
					cbSecond.setSelectedItem(num[0]);
				}
				revalidate();
				repaint();
			}
		});

		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton createButton = new JButton("Create");
				createButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						if(cbSub.getSelectedIndex() == 0) {
							warningLabel.setText("Set the Subject!");
							Timer timer = new Timer(3000, new ActionListener() {
								public void actionPerformed(ActionEvent e) {
									warningLabel.setText("");
								}
							});
							timer.setRepeats(false);
							timer.start();
						} else {
							try (Connection conn = DriverManager.getConnection(MySQLConnectivity.URL, MySQLConnectivity.user ,MySQLConnectivity.pass)){	
								String obtainedRecordID = "";
								int totalMembers = 0;
								List<String> obtainedFN = new ArrayList<String>();
								List<String> obtainedMN = new ArrayList<String>();
								List<String> obtainedLN = new ArrayList<String>();
								PreparedStatement getStatement = conn.prepareStatement("insert into attendancerecords (record_name, subjectname, sectionname, departmentname, schoolname, timecreated, timelimit, currenttime, timeexpires) values (?,?,?,?,?,CURRENT_TIMESTAMP,TIME(\"" + selectedHour + ":" + selectedMinute + ":" + selectedSecond + "\"),CURRENT_TIMESTAMP, ADDTIME(attendancerecords.timecreated, attendancerecords.timelimit))");
								getStatement.setString(1, obtainedDept+"-"+obtainedSec+"-"+obtainedSub+" | "+month+" "+day+", "+year + " - " + currentRecordCount);
								getStatement.setString(2, obtainedSub);
								getStatement.setString(3, obtainedSec);
								getStatement.setString(4, obtainedDept);
								getStatement.setString(5, Login.pubSchoolName);
								int result = getStatement.executeUpdate();
								PreparedStatement getRecordID = conn.prepareStatement("select recordid from attendancerecords where record_name='"+obtainedDept+"-"+obtainedSec+"-"+obtainedSub+" | "+month+" "+day+", "+year + " - " + currentRecordCount+"' and schoolname='"+Login.pubSchoolName+"'");
								ResultSet resultID = getRecordID.executeQuery();
								if(resultID.next()) {
									obtainedRecordID = resultID.getString("recordid");
								}
								PreparedStatement getTotalMembers = conn.prepareStatement("select count(concat(firstname, ' ', middlename, ' ', lastname)) as fullname from userinfo where occupation='Student' and sectionname='"+obtainedSec+"' and departmentname='"+obtainedDept+"' and schoolname='"+Login.pubSchoolName+"'");
								ResultSet obtainedTotalMembers = getTotalMembers.executeQuery();
								if(obtainedTotalMembers.next()) {
									totalMembers = obtainedTotalMembers.getInt("fullname");
								}
								PreparedStatement getMemberNames = conn.prepareStatement("select firstname, middlename, lastname from userinfo where occupation ='Student' and sectionname='"+obtainedSec+"' and departmentname='"+obtainedDept+"' and schoolname='"+Login.pubSchoolName+"'");
								ResultSet obtainedMemberNames = getMemberNames.executeQuery();
								while(obtainedMemberNames.next()) {
									String FN = obtainedMemberNames.getString("firstname");
									String MN = obtainedMemberNames.getString("middlename");
									String LN = obtainedMemberNames.getString("lastname");
									obtainedFN.add(FN);
									obtainedMN.add(MN);
									obtainedLN.add(LN);
								}
								for(int i = 0; i < totalMembers; i++) {
									PreparedStatement getSecondStatement = conn.prepareStatement("insert into attendancestatus (recordid, record_name, firstname, middlename, lastname, subjectname, sectionname, departmentname, schoolname) values (?,?,?,?,?,?,?,?,?)");
									getSecondStatement.setString(1, obtainedRecordID);
									getSecondStatement.setString(2, obtainedDept+"-"+obtainedSec+"-"+obtainedSub+" | "+month+" "+day+", "+year + " - " + currentRecordCount);
									getSecondStatement.setString(3, obtainedFN.get(i));
									getSecondStatement.setString(4, obtainedMN.get(i));
									getSecondStatement.setString(5, obtainedLN.get(i));
									getSecondStatement.setString(6, obtainedSub);
									getSecondStatement.setString(7, obtainedSec);
									getSecondStatement.setString(8, obtainedDept);
									getSecondStatement.setString(9, Login.pubSchoolName);
									getSecondStatement.executeUpdate();
								}
								if(result == 1) {
									dispose();
									JOptionPane.showMessageDialog(null, "Successfully created!");
									revalidate();
									repaint();
								}
								panelAttendance.addingRecords = false;
							} catch (SQLException sql) {
								sql.printStackTrace();
							}
						}
					}
				});
				createButton.addMouseListener(new PropertiesListener(createButton));
				createButton.setActionCommand("Create");
				buttonPane.add(createButton);
				getRootPane().setDefaultButton(createButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						dispose();
						isCancelled = true;
						panelAttendance.addingRecords = false;
					}
				});
				cancelButton.addMouseListener(new PropertiesListener(cancelButton));
				cancelButton.setActionCommand("Cancel");
				buttonPane.add(cancelButton);
			}
		}
	}
	
	public void sub(JComboBox<String> cb) {
		try (Connection conn = DriverManager.getConnection(MySQLConnectivity.URL, MySQLConnectivity.user ,MySQLConnectivity.pass)){	
			PreparedStatement getStatement = conn.prepareStatement("select subjectname from subjectinfo where departmentname='"+obtainedDept+"' and schoolname='"+Login.pubSchoolName+"'");
			ResultSet result = getStatement.executeQuery();
			cb.removeAllItems();
			cb.addItem("Select a subject");
			while(result.next()) {
				String obtainedString = result.getString("subjectname");
				cb.addItem(obtainedString);
			}
		} catch (SQLException sql) {
			sql.printStackTrace();
		}
	}
	
	private void addItems(JComboBox<Integer> cb) {
		for(int i = 0; i < num.length; i++) {
			cb.addItem(num[i]);
		}
	}
	
	private void addItemsHour(JComboBox<Integer> cb) {
		for(int i = 0; i < hr.length; i++) {
			cb.addItem(hr[i]);
		}
	}
	
	private void checkRecordCount() {
		try (Connection conn = DriverManager.getConnection(MySQLConnectivity.URL, MySQLConnectivity.user ,MySQLConnectivity.pass)){
			PreparedStatement getStatement = conn.prepareStatement("select count(record_name) from attendancerecords where subjectname='"+obtainedSub+"' and departmentname='"+obtainedDept+"' and schoolname='"+Login.pubSchoolName+"'");
			ResultSet result = getStatement.executeQuery();
			if(result.next()) {
				currentRecordCount = result.getInt("count(record_name)");
			}
		} catch (SQLException sql) {
			sql.printStackTrace();
		}
	}
	
	private class selectedItem implements ItemListener {
		JComboBox<Integer> cb;
		
		private selectedItem (JComboBox<Integer> cb) {
			this.cb = cb;
		}
		
		public void itemStateChanged(ItemEvent e) {
			JComboBox<?> cbS = (JComboBox<?>) e.getSource();
			String obtainedName = cbS.getName();
			if(e.getStateChange() == ItemEvent.SELECTED) {
				int obtainedNum = Integer.valueOf(cb.getSelectedItem().toString());
				if(obtainedName.equals("Hour")) {
					selectedHour = obtainedNum;
				} else if (obtainedName.equals("Minute")) {
					selectedMinute = obtainedNum;
				} else if (obtainedName.equals("Second")) {
					selectedSecond = obtainedNum;
				}
			}
		}
	}
}
