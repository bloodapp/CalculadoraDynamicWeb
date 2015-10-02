package calculadora.p2p;

import java.awt.Color;
import java.awt.MediaTracker;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

public class GuiP2PLogin extends JFrame implements KeyListener {
	
	JTextField jtLogin = new JTextField();
	JTextField jtFormula = new JTextField();
	JButton jbLogin = new JButton();
	JButton jbFormula = new JButton();
	JLabel jLabelNick = new JLabel();
	JLabel jLabelLogoMack = new JLabel();
	TitledBorder titledBorder1;
	JLabel jLabel1 = new JLabel();
	JLabel jLabel2 = new JLabel();
	Border border1;
	JLabel jLabel3 = new JLabel();
	JLabel jLabel4 = new JLabel();
	JPanel jPanel1 = new JPanel();
	JLabel jLabel5 = new JLabel();
	private AppP2P app;

	public GuiP2PLogin() {
	}

	public GuiP2PLogin(AppP2P app) {
		this.app = app;
		try {
			jbInit();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void jbInit() throws Exception {
		this.setTitle("Implementação JXTA APP CALC P2P");
		this.setLocationRelativeTo(null); 
		border1 = BorderFactory.createLineBorder(Color.white, 1);
		jtLogin.setBounds(new Rectangle(5, 184, 171, 21));
		jtLogin.setText("alexander");
		jtFormula.setBounds(new Rectangle(5, 210, 171, 21));
		jtFormula.setText("(10+10)*(20+20)+(30*3.0)");
		this.getContentPane().setLayout(null);
		jbLogin.setBounds(new Rectangle(244, 182, 95, 23));
		jbLogin.setText("Entrar");
		jbFormula.setBounds(new Rectangle(244, 212, 95, 23));
		jbFormula.setText("Executar");
		this.setResizable(true);
		jLabelNick.setText("Digite abaixo o seu nick:");
		jLabelNick.setBounds(new Rectangle(6, 163, 141, 15));
		jLabel1.setFont(new java.awt.Font("SansSerif", 1, 12));
		jLabel1.setText("Implementação JXTA P2P");
		jLabel1.setBounds(new Rectangle(8, 0, 175, 23));
		jLabel2.setText("Alexander Campos");
		jLabel2.setBounds(new Rectangle(5, 12, 197, 17));
		jLabel3.setText("Rodrigo Castro");
		jLabel3.setBounds(new Rectangle(6, 36, 205, 20));
		jLabel4.setText("Elton Ricardo");
		jLabel4.setBounds(new Rectangle(7, 65, 155, 17));
		jPanel1.setBorder(BorderFactory.createEtchedBorder());
		jPanel1.setLayout(null);
		jPanel1.setBounds(new Rectangle(7, 52, 209, 99));
		jLabel5.setFont(new java.awt.Font("SansSerif", 1, 12));
		jLabel5.setText("Sistemas distribuidos");
		jLabel5.setBounds(new Rectangle(7, 25, 201, 20));
		jPanel1.add(jLabel4, null);
		jPanel1.add(jLabel3, null);
		jPanel1.add(jLabel2, null);
		jtLogin.addKeyListener(this);
		this.getContentPane().add(jtLogin, null);
		this.getContentPane().add(jtFormula, null);
		this.getContentPane().add(jLabelNick, null);
		this.getContentPane().add(jPanel1, null);
		this.getContentPane().add(jbLogin, null);
		this.getContentPane().add(jbFormula, null);
		this.getContentPane().add(jLabel5, null);
		this.getContentPane().add(jLabel1, null);
		this.getContentPane().add(jLabelLogoMack, null);
		try {
			MediaTracker tracker = new MediaTracker(this);
			ImageIcon img = new ImageIcon(getClass().getResource("logo.gif"));
			tracker.addImage(img.getImage(), 0);
			tracker.waitForAll();
			jLabelLogoMack.setIcon(img);
			jLabelLogoMack.setBounds(new Rectangle(230, 10,
					img.getIconWidth() + 8, img.getIconHeight() + 8));
		} catch (Exception e) {
			System.out.println("Erro ao carregar imagens:" + e.getMessage());
		}
		this.setSize(380, 280);
		this.setDefaultCloseOperation(3);
		jbLogin.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doLogin();
			}
		});
		jbFormula.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(ActionEvent e) {
				doExecutaFormula();
			}
		});
		show();
	}

	private void hideJtLogin() {
		this.jtLogin.setVisible(false);
	}

	private void doLogin() {
		String nick = jtLogin.getText();
		//hideJtLogin();
		int resultado = app.login(nick);
		if (resultado < 0) {
			JOptionPane.showMessageDialog(null, "Atenção, já existe um usuário com este nick no app, escolha outro!");
			System.out.println("Atenção, já existe um usuário com este nick no app, escolha outro!");
			System.exit(0);
		}
		jtLogin.setText("");
	}
	
	private void doExecutaFormula() {
		try {
			String resultado = app.executarFormula(jtFormula.getText());
			JOptionPane.showMessageDialog(null, "Resultado: " + resultado);
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, e.getMessage());
		}
	}

	public void keyPressed(KeyEvent arg0) {
		if (arg0.getKeyCode() == 10) {
			doLogin();
		}
	}

	public void keyReleased(KeyEvent arg0) {

	}

	public void keyTyped(KeyEvent arg0) {

	}

}