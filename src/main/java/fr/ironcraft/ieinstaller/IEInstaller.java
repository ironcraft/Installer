package fr.ironcraft.ieinstaller;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Locale;

public class IEInstaller extends JPanel {
	private static final long serialVersionUID = 1L;
	public static File ironDir = null;
	
	private Downloader dlThread;
	public JButton btn_installer;

	public static void main(String[] args) {
		String userHomeDir = System.getProperty("user.home", ".");
        String os = System.getProperty("os.name").toLowerCase(Locale.ENGLISH);
        String expoDir = ".ironexpo";
        if (os.contains("win") && System.getenv("APPDATA") != null)
        {
        	ironDir = new File(System.getenv("APPDATA"), expoDir);
        }
        else if (os.contains("mac"))
        {
        	ironDir = new File(userHomeDir, "Library" + File.separator + "Application Support" + File.separator + expoDir.substring(1));
        }
        else
        {
        	ironDir = new File(userHomeDir, expoDir);
        }
		
	    try {
	    	UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    } 
	    catch (Exception e) {
	    	e.printStackTrace();
	    }
		
		JFrame mainFrame = new JFrame();
		IEInstaller panel = new IEInstaller();
		
		mainFrame.setTitle("IronExpo Installer");
		mainFrame.setContentPane(panel);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		mainFrame.setResizable(false);
		mainFrame.setSize(300, 200);
		mainFrame.setLocationRelativeTo(null);
		
		mainFrame.setVisible(true);
	}
	
	public IEInstaller() {
		dlThread = new Downloader(this);
		this.setupLayout();
	}
	
	private void setupLayout() {
		this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		
		ImageIcon logo = new ImageIcon("logo.png");
		JLabel lbl_logo = new JLabel(logo);
		lbl_logo.setMaximumSize(new Dimension(400, 140));
		this.add(lbl_logo);
		
		btn_installer = new JButton("Installer");
		btn_installer.setMaximumSize(new Dimension(400, 80));
		btn_installer.setFocusable(false);
		btn_installer.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				btn_installer.setEnabled(false);
				IEInstaller.this.dlThread.start();
			}
		});
		this.add(btn_installer);
	}
}
