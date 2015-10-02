package calculadora.p2p;

import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Date;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class GuiP2P extends JFrame implements KeyListener {

	private AppP2P appP2P;
	JButton jbEnviar = new JButton();
	JLabel jLabelLogoJxta = new JLabel();
	JLabel jLabelLogoMack = new JLabel();
	JScrollPane jScrollPane1 = new JScrollPane();
	JTextField jtMsg = new JTextField();
	JTextArea jtMsgAll = new JTextArea();

	public GuiP2P(AppP2P app) {
		try {
			jbInit();
			this.appP2P = app;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void jbInit() throws Exception {
		jtMsg.setBounds(new Rectangle(13, 272, 395, 19));
		this.setDefaultCloseOperation(3);
		this.getContentPane().setLayout(null);
		jbEnviar.setBounds(new Rectangle(439, 272, 77, 21));
		jbEnviar.setText("Enviar");
		// jScrollPane1.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
		jScrollPane1.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
		jScrollPane1.setAutoscrolls(true);
		jScrollPane1.setBounds(new Rectangle(13, 8, 402, 251));
		jtMsgAll.setWrapStyleWord(true);
		jtMsgAll.setBounds(new Rectangle(13, 8, 350, 200));
		jtMsgAll.setEditable(false);
		jScrollPane1.getViewport().add(jtMsgAll, null);
		try {
			MediaTracker tracker = new MediaTracker(this);
			ImageIcon img = new ImageIcon(getClass().getResource("logo.gif"));
			ImageIcon img2 = new ImageIcon(getClass().getResource("logo_jxta.gif"));
			tracker.addImage(img.getImage(), 0);
			tracker.addImage(img2.getImage(), 0);
			tracker.waitForAll();
			jLabelLogoMack.setIcon(img);
			jLabelLogoMack.setBounds(new Rectangle(430, 10,img.getIconWidth() + 8, img.getIconHeight() + 8));
			jLabelLogoJxta.setIcon(img2);
			jLabelLogoJxta.setBounds(new Rectangle(430, 120, img2.getIconWidth() + 8, img2.getIconHeight() + 8));
		} catch (Exception e) {
			System.out.println("Erro ao carregar imagens:" + e.getMessage());
		}
		jbEnviar.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendMessage(jtMsg.getText());
				jtMsg.setText("");
			}
		});
		this.getContentPane().add(jScrollPane1, null);
		this.getContentPane().add(jbEnviar, null);
		this.getContentPane().add(jtMsg, null);
		this.getContentPane().add(jLabelLogoJxta, null);
		this.getContentPane().add(jLabelLogoMack, null);
		setSize(580, 360);
		setResizable(false);
		jtMsg.requestFocusInWindow();
		jbEnviar.setFocusPainted(true);
		jtMsg.addKeyListener(this);
	}

	public void keyPressed(KeyEvent arg0) {
		// user apertou enter
		if (arg0.getKeyCode() == 10) {
			if ("".equals(jtMsg.getText())) {
				System.out.println("Mensagem inválida, digite algo...");
				JOptionPane.showMessageDialog(null, "Mensagem inválida!");
			} else {
				sendMessage(jtMsg.getText());
				jtMsg.setText("");
			}
		}
	}

	public void keyReleased(KeyEvent arg0) {
	}

	public void keyTyped(KeyEvent arg0) {
	}

	public void printMessage(String msg) {
		jtMsgAll.append("\n" + new Date() + " - " + msg);
	}

	public void printMessage(String msg, String from) {
		String msgFormatada = AppP2P.getShortDate() + " - " + from	+ " enviar calculo para todos: " + msg;
		jtMsgAll.append("\n" + msgFormatada);
	}

	public void sendMessage(String mensagem) {
		appP2P.sendMessageToAll(mensagem);
	}
}