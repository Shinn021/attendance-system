import javax.swing.JPanel;
import java.awt.Color;
import javax.swing.border.LineBorder;
import javax.swing.JLabel;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.Timer;
import javax.swing.ImageIcon;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class panelChangePassSetting extends JPanel {

	private static final long serialVersionUID = 1L;
	private int uid = Integer.valueOf(Login.pubUID), delay = 5000;
	private JPasswordField pwdCurrentPass;
	private JPasswordField pwdNewPass;
	private JPasswordField pwdConfirmNewPass;
	private JLabel lblStatus;
	Timer tick;
	private boolean passwordChecker = false;

	ActionListener lblStatusClearer = new ActionListener() {
		public void actionPerformed (ActionEvent e) {
			lblStatus.setText("");
			lblStatus.setForeground(Color.RED);
		}
	};
	
	public panelChangePassSetting() {
		setBounds(0,0, 539, 450);
		setBorder(new LineBorder(new Color(65, 105, 225)));
		setBackground(new Color(255, 255, 255));
		setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBorder(null);
		panel.setBackground(new Color(255, 255, 255));
		panel.setBounds(0, 0, 539, 450);
		add(panel);
		panel.setLayout(null);
		
		JPanel panelCurrentPass = new JPanel();
		panelCurrentPass.setBorder(new LineBorder(new Color(65, 105, 225)));
		panelCurrentPass.setBackground(new Color(255, 255, 255));
		panelCurrentPass.setBounds(100, 52, 335, 47);
		panel.add(panelCurrentPass);
		panelCurrentPass.setLayout(null);
		
		pwdCurrentPass = new JPasswordField();
		pwdCurrentPass.setFont(new Font("Tahoma", Font.PLAIN, 15));
		pwdCurrentPass.setForeground(new Color(65, 105, 225));
		pwdCurrentPass.setBorder(null);
		pwdCurrentPass.setBounds(10, 11, 286, 24);
		panelCurrentPass.add(pwdCurrentPass);
		
		JLabel logoShowCurrentPass = new JLabel("");
		logoShowCurrentPass.setBounds(301, 11, 24, 24);
		logoShowCurrentPass.setIcon(new ImageIcon(Images.showPass));
		logoShowCurrentPass.addMouseListener(new PasswordIcon(logoShowCurrentPass, pwdCurrentPass));
		panelCurrentPass.add(logoShowCurrentPass);
		
		JPanel panelNewPass = new JPanel();
		panelNewPass.setLayout(null);
		panelNewPass.setBorder(new LineBorder(new Color(65, 105, 225)));
		panelNewPass.setBackground(Color.WHITE);
		panelNewPass.setBounds(100, 157, 335, 47);
		panel.add(panelNewPass);
		
		pwdNewPass = new JPasswordField();
		pwdNewPass.setFont(new Font("Tahoma", Font.PLAIN, 15));
		pwdNewPass.setForeground(new Color(65, 105, 225));
		pwdNewPass.setBorder(null);
		pwdNewPass.setBounds(10, 11, 286, 24);
		panelNewPass.add(pwdNewPass);
		
		JLabel logoShowNewPass = new JLabel("");
		logoShowNewPass.setBounds(301, 11, 24, 24);
		logoShowNewPass.setIcon(new ImageIcon(Images.showPass));
		logoShowNewPass.addMouseListener(new PasswordIcon(logoShowNewPass, pwdNewPass));
		panelNewPass.add(logoShowNewPass);
		
		JPanel panelConfirmNewPass = new JPanel();
		panelConfirmNewPass.setLayout(null);
		panelConfirmNewPass.setBorder(new LineBorder(new Color(65, 105, 225)));
		panelConfirmNewPass.setBackground(Color.WHITE);
		panelConfirmNewPass.setBounds(100, 262, 335, 47);
		panel.add(panelConfirmNewPass);
		
		pwdConfirmNewPass = new JPasswordField();
		pwdConfirmNewPass.setFont(new Font("Tahoma", Font.PLAIN, 15));
		pwdConfirmNewPass.setForeground(new Color(65, 105, 225));
		pwdConfirmNewPass.setBorder(null);
		pwdConfirmNewPass.setBounds(10, 11, 286, 24);
		panelConfirmNewPass.add(pwdConfirmNewPass);
		
		JLabel logoShowConfirmNewPass = new JLabel("");
		logoShowConfirmNewPass.setBounds(301, 11, 24, 24);
		logoShowConfirmNewPass.setIcon(new ImageIcon(Images.showPass));
		logoShowConfirmNewPass.addMouseListener(new PasswordIcon(logoShowConfirmNewPass, pwdConfirmNewPass));
		panelConfirmNewPass.add(logoShowConfirmNewPass);
		
		tick = new Timer(delay, lblStatusClearer);
		tick.setRepeats(false);
		
		JButton changePassButton = new JButton("Change Password");
		changePassButton.setFont(new Font("Yu Gothic UI Light", Font.PLAIN, 15));
		changePassButton.setBorder(null);
		changePassButton.setForeground(new Color(255, 255, 255));
		changePassButton.setBackground(new Color(65, 105, 225));
		changePassButton.setBounds(180, 369, 180, 50);
		changePassButton.addMouseListener(new PropertiesListener(changePassButton));
		changePassButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				char[] getCurrentPass = pwdCurrentPass.getPassword(), getNewPass = pwdNewPass.getPassword(), getConfirmNewPass = pwdConfirmNewPass.getPassword();
				String currentPass = String.valueOf(getCurrentPass), newPass = String.valueOf(getNewPass), confirmNewPass = String.valueOf(getConfirmNewPass);
				try {
					Connection conn = DriverManager.getConnection("jdbc:mysql://sql6.freesqldatabase.com:3306/sql6476155","sql6476155","HHHLDqnNka");
					PreparedStatement checkCurrentPass = conn.prepareStatement("select pass from userInfo where pass='"+currentPass+"' and userid='"+uid+"'");
					ResultSet executeCheckCurrentPass = checkCurrentPass.executeQuery();
					if(!currentPass.isEmpty() && !newPass.isEmpty() && !confirmNewPass.isEmpty()) {
						if(executeCheckCurrentPass.next()) {
							if(newPass.equals(confirmNewPass)) {
								if(newPass.matches("(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[\\p{Punct}])(?=\\S+$).{8,}")){
									passwordChecker = true;
								}
								if(!passwordChecker) {
									lblStatus.setText("<html>Password must contain atleast 8 characters and requires a number, a lowercase letter, an uppercase letter, a special character and must not contain any spaces!</html>");
									int longerDelay = 7000;
									Timer longerTick = new Timer(longerDelay, lblStatusClearer);
									longerTick.setRepeats(false);
									longerTick.start();
								} else {
									PreparedStatement changePass = conn.prepareStatement("update userInfo set pass='"+newPass+"' where userID ='"+uid+"'");	
									ResultSet executeChangePass = changePass.executeQuery();
									if(executeChangePass.next()) {
										lblStatus.setForeground(Color.GREEN);
										lblStatus.setText("Changed password successfully!");
										tick.start();
									} else {
										lblStatus.setText("Changed password failed!");
										tick.start();
									}
									changePass.close();
								}
							} else {
								lblStatus.setText("Password does not match!");
								tick.start();
							}
						} else {
							lblStatus.setText("Error!");
							tick.start();
						}
					} else {
						lblStatus.setText("Enter all of the credentials!");
						tick.start();
					}
					conn.close();
					checkCurrentPass.close();
				} catch (SQLException sql) {
					sql.printStackTrace();
				}
			}
		});
		panel.add(changePassButton);
		
		lblStatus = new JLabel("");
		lblStatus.setForeground(new Color(255, 0, 0));
		lblStatus.setHorizontalAlignment(SwingConstants.CENTER);
		lblStatus.setBounds(100, 320, 334, 38);
		panel.add(lblStatus);
		
		JLabel lblCurrentPass = new JLabel("Current Password:");
		lblCurrentPass.setFont(new Font("Yu Gothic UI Light", Font.PLAIN, 15));
		lblCurrentPass.setBounds(100, 11, 154, 40);
		panel.add(lblCurrentPass);
		lblCurrentPass.setForeground(new Color(65, 105, 225));
		
		JLabel lblNewPass = new JLabel("New Password:");
		lblNewPass.setFont(new Font("Yu Gothic UI Light", Font.PLAIN, 15));
		lblNewPass.setBounds(100, 118, 154, 40);
		panel.add(lblNewPass);
		lblNewPass.setForeground(new Color(65, 105, 225));
		
		JLabel lblConfirmNewPass = new JLabel("Confirm New Password:");
		lblConfirmNewPass.setFont(new Font("Yu Gothic UI Light", Font.PLAIN, 15));
		lblConfirmNewPass.setBounds(100, 223, 154, 40);
		panel.add(lblConfirmNewPass);
		lblConfirmNewPass.setForeground(new Color(65, 105, 225));
		
		JLabel logoPasswordPolicy = new JLabel("");
		logoPasswordPolicy.setBounds(195, 128, 24, 24);
		panel.add(logoPasswordPolicy);
		logoPasswordPolicy.setIcon(new ImageIcon(Images.question));
		logoPasswordPolicy.setToolTipText("<html>The password must have at least: <br/>- Contains 8 characters or more. <br/>- Contains atleast 1 lowercase and 1 uppercase letter."
				+ "<br/>- Contains 1 special character. <br/>- Does not contain spaces. </html>");
	}
}
