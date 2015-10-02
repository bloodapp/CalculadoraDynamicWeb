package calculadora.p2p;

import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import calculadora.negocio.FormulaNegocio;
import calculadora.socket.ClienteSocket;
import bsh.EvalError;
import net.jxta.discovery.DiscoveryEvent;
import net.jxta.discovery.DiscoveryListener;
import net.jxta.discovery.DiscoveryService;
import net.jxta.document.AdvertisementFactory;
import net.jxta.endpoint.Message;
import net.jxta.endpoint.StringMessageElement;
import net.jxta.id.IDFactory;
import net.jxta.impl.protocol.ModuleImplAdv;
import net.jxta.peergroup.PeerGroup;
import net.jxta.peergroup.PeerGroupFactory;
import net.jxta.pipe.InputPipe;
import net.jxta.pipe.OutputPipe;
import net.jxta.pipe.PipeMsgEvent;
import net.jxta.pipe.PipeMsgListener;
import net.jxta.pipe.PipeService;
import net.jxta.protocol.DiscoveryResponseMsg;
import net.jxta.protocol.PeerAdvertisement;
import net.jxta.protocol.PipeAdvertisement;
import net.jxta.rendezvous.RendezVousService;

/**
 * Classe principal para aplicação do app P2P, implementa as interfaces
 * PipeMsgListener para recebimento de eventos de pipes e DiscoveryListener para
 * eventos de Discovery de novos peers.
 */
public class AppP2P implements PipeMsgListener, DiscoveryListener {
	/**
	 * Nome do elemento que definirá qual advertisement de pipe pertence a este
	 * app.
	 */
	public static final String CALC_NAME_TAG = "Calc";
	/**
	 * Tempo de vida do advertisement de pipe na rede. Após isso o app parará
	 * de funcionar.
	 */
	private static final long ADV_LIFETIME = 210 * 1000;
	/**
	 * 80 Frame responsável por exibir e enviar as mensagens para outros
	 * usuários.
	 */
	private static GuiP2P guiP2P;
	/**
	 * Frame responsável por recuperar o nick escolhido.
	 */
	private static GuiP2PLogin guiLogin;

	/**
	 * Nome do elemento que conterá mensagem enviada por um usuário.
	 */
	private static final String MESSAGE_TAG = "AppSenderMessage";
	/**
	 * Nome do elemento que conterá o nome do enviador da mensagem
	 */
	private static final String SENDER_NAME = "AppSenderName";
	/**
	 * Tempo de espera antes de tentar recuperar após um advertisement após um
	 * publish.
	 */
	private static final int WAITING_TIME = 5 * 1000;
	private DiscoveryService discovery;
	private PeerGroup group;
	/**
	 * Indica se o flush de advertisements já foi realizado.
	 */
	private boolean isFlushed;
	/**
	 * Indica se o usuário atual já esta logado.
	 */
	private boolean loggedIn;
	/**
	 * Nick escolhido pelo usuário.
	 */
	private String nick;
	private PipeService pipeSvc;
	private RendezVousService rdv;
	/**
	 * Irá procurar remotamente novos advertisements.
	 */
	private Thread thread;
	/**
	 * 81 Utilizado para recebimento das mensagens enviadas para este usuário.
	 */
	private PipeAdvertisement userAdv;

	/**
	 * Método para exibição formatada de data
	 * 
	 * @return String contendo data no formato dd/mm/aa hh:mm
	 * @see <code>java.text.DateFormat#getDateTimeInstance(int, int,
java.util.Locale)</code>
	 */
	public static String getShortDate() {
		DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT,
				DateFormat.SHORT, new Locale("pt", "BR"));
		return df.format(new Date(System.currentTimeMillis()));
	}

	public static void main(String args[]) {
		AppP2P app = new AppP2P();
		System.out.println("Aplicação iniciada...");
		app.startJxta();
	}

	/**
	 * Cria um input pipe e adiciona a própria classe como ouvinte de eventos
	 * recebido por este pipe
	 */
	private void createInputPipe() {
		InputPipe pipeIn = null;
		try {
			pipeIn = pipeSvc.createInputPipe(userAdv, this);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Impossível criar pipe de entrada:" + e.getMessage());
			//System.exit(-1);
		}
		System.out.println("Pipe de entrada criado com sucesso...");
	}

	/**
	 * Recebe os eventos de discovery para esta classe. Apenas registra no log
	 * os advertisements recebidos pertencente a este app, e mostra quais foram
	 * as respostas recebidas e por quem foram enviadas
	 * 
	 * @param ev
	 *            Evento de Discovery recebido
	 */
	public void discoveryEvent(DiscoveryEvent ev) {
		DiscoveryResponseMsg res = ev.getResponse();
		PeerAdvertisement peerAdv = res.getPeerAdvertisement();
		System.out.println(res.getResponseCount() + " resposta[s] de discovery recebida[s] ...");
		Enumeration e = res.getAdvertisements();
		if (e != null) {
			while (e.hasMoreElements()) {
				try {
					Object element = e.nextElement();
					if(element instanceof ModuleImplAdv){
						ModuleImplAdv module = (ModuleImplAdv) element;
						System.out.println("Pipe para máquina '" + module.getProvider()
								+ "' recuperado no evento dediscovery: " /* + module.toString()*/);
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}

	/**
	 * Procura por advertisements de pipe de um determinado usuário localmente e
	 * remotamente
	 * 
	 * @param name
	 *            Nick de qual usuário que desejamos procurar advs
	 * @return <code>PipeAdvertisement</code> contendo o advertisement
	 *         recuperado para este usuário ou null, caso nenhum tenha sido
	 *         encontrado.
	 * @see #findUserAdvLocal(String).
	 * @see #findUserAdvRemote(String).
	 */
	private PipeAdvertisement findUserAdv(String name) {
		PipeAdvertisement adv = findUserAdvLocal(name);
		if (adv == null) {
			adv = findUserAdvRemote(name);
			adv = findUserAdvLocal(name);
		}
		return adv;
	}

	/**
	 * Realiza o flush dos advertisements locais, e recupera os advertisements
	 * ainda validos que correspondam a esta aplicação de app, procurando por
	 * um usuário específico.
	 * 
	 * @param name
	 *            Nick do usuário desejado.
	 * @return null ou PipeAdvertiment já atribuído a este usuário
	 *         anteriormente.
	 */
	private PipeAdvertisement findUserAdvLocal(String name) {
		Enumeration e = null;
		try {
			if (isFlushed) {
				discovery.flushAdvertisements(null, DiscoveryService.ADV);
				System.out.println(" flush de advertisements executados com sucesso...");
			}
			e = discovery.getLocalAdvertisements(DiscoveryService.ADV, "Name", CALC_NAME_TAG + ":" + nick);
			if (e != null) {
				PipeAdvertisement adv = null;
				while (e.hasMoreElements()) {
					try {
						adv = (PipeAdvertisement) e.nextElement();
						if ((CALC_NAME_TAG + ":" + name).equals(adv.getName())) {
							return adv;
						}
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			}
		} catch (Exception ex) {
			System.out.println("Erro ao recuperar advertisements de cache:" + ex.getMessage());
		}
		return null;
	}

	/**
	 * Procura remotamente os advertisements de pipe desta aplicação e que
	 * correspondam a um determinado usuário informado.
	 * 
	 * @param name
	 *            Nick do usuário desejado
	 * @return null ou <code>PipeAdvertiment</code> deste usuário válido na rede
	 *         JXTA
	 */
	private PipeAdvertisement findUserAdvRemote(String name) {
		PipeAdvertisement adv = null;
		try {
			// ache todos os advertisiments locais de pipes publicados por esta
			// aplicação....
			discovery.getRemoteAdvertisements(null, DiscoveryService.ADV, "Name", "*" + CALC_NAME_TAG + ":*", 2);
			try {
				Thread.sleep(WAITING_TIME);
			} catch (Exception e) {

			}
		} catch (Exception e) {
			System.out.println("Erro ao recuperar advertisements remotos: " + e.getMessage());
		}
		return null;
	}

	/**
	 * Recupera todos os advertisements locais desta aplicação, verificando qual
	 * corresponde a PipeAdvertisement.
	 * 
	 * @return <code>Collection</code> contendo todos os PipeAdvertisement
	 *         encontrados.
	 */
	private Collection getAllPipeLocalAdvertisements() {
		Collection pipes = new ArrayList();
		try {
			Enumeration e = discovery.getLocalAdvertisements(
					DiscoveryService.ADV, "Name", "*" + CALC_NAME_TAG + ":*");
			if (e != null) {
				while (e.hasMoreElements()) {
					try {
						PipeAdvertisement adv = (PipeAdvertisement) e.nextElement();
						pipes.add(adv);
					} catch (Exception ex) {
						continue;
					}
				}
			}
		} catch (Exception e) {
			System.out.println("Erro ao recuperar cache de advertisements:"
					+ e.getMessage());
		}
		return pipes;
	}

	public String getNick() {
		return nick;
	}

	/**
	 * Efetua o login na aplicação P2P, criando um PipeAdvertisement para
	 * este usuário, e publicando este pipe localmente e remotamente na rede
	 * JXTA. Após realizada a publicação do advertisement,o frame que exibe e
	 * permite o envio de mensagens é exibido, uma mensagem para todos é enviada
	 * alertando a presença de um novo usuário, e uma thread é iniciada para
	 * descoberta de novos advertisements na rede JXTA. Caso haja algum problema
	 * antes de terminar a rotina, a aplicação é encerrada com código de erro
	 * -1.
	 * 
	 * @param name
	 *            Nick escolhido pelo usuário na tela de login
	 * @return <code>int</code> , -1 caso o usuário já tenha realizado o login
	 *         anteriormente, ou 1 caso tudo tenha sido realizado com sucesso.
	 *         85
	 */
	public int login(String name) {
		if (!loggedIn) {
			this.nick = name;
			discovery.addDiscoveryListener(this);
			PipeAdvertisement adv = findUserAdv(name);
			if (adv != null) {
				System.out.println("Erro, Advertisiment já existente para este usuário, saindo da aplicação...");
				System.exit(-1);
			}
			try {// criando um adv de pipe para este user...
				adv = (PipeAdvertisement) AdvertisementFactory.newAdvertisement(PipeAdvertisement.getAdvertisementType());
				adv.setPipeID(IDFactory.newPipeID(group.getPeerGroupID()));
				adv.setName(CALC_NAME_TAG + ":" + name);
				adv.setType(PipeService.PropagateType);
			} catch (Exception e) {
				System.out.println("Impossível criar advertisement de pipe:" + e.getMessage());
				System.exit(-1);
			}
			try {
				discovery.publish(adv, DiscoveryService.ADV, ADV_LIFETIME);
				discovery.remotePublish(adv, DiscoveryService.ADV);
			} catch (Exception ex) {
				System.out.println("Impossível publicar advertisement de pipe:" + ex.getMessage());
				System.exit(-1);
			}
			adv = findUserAdvLocal(name);
			if (adv == null) {
				System.out.println("Impossível recuperar advertisement de pipe para este usuário...");
				//System.exit(-1);
			}
			userAdv = adv;
			loggedIn = true;
			createInputPipe();
			guiP2P.setTitle(getNick() + " - JXTA APP CALC P2P ");
			guiP2P.show();
			
			//sendMessageToAll(" Entrei na aplicação....");
			
			// thread que vai recuperar novos pipes de tempos em tempos...
			System.out.println("Criando thread para Discovery de pipes...");
			DiscoveryPipes discoveryPipes = new DiscoveryPipes(discovery);
		} else {
			System.out.println("Atenção , você já esta logado na aplicação...");
			return -1;
		}
		return 1;
	}

	public void pipeMsgEvent(PipeMsgEvent pipeMsgEvent) {
		try {
			Message message = pipeMsgEvent.getMessage();
			String from = message.getMessageElement(SENDER_NAME).toString();
			String msg = message.getMessageElement(MESSAGE_TAG).toString();
			guiP2P.printMessage(msg, from);
		} catch (Exception e) {
			System.out.println("Erro ao receber evento de pipe:" + e.getMessage());
		}
	}

	public void sendMessageToAll(String texto) {
		Collection pipes = getAllPipeLocalAdvertisements();
		if (pipes != null) {
			Iterator it = pipes.iterator();
			while (it.hasNext()) {
				PipeAdvertisement adv = (PipeAdvertisement) it.next();
				OutputPipe pipeOut = null;
				try {
					pipeOut = pipeSvc.createOutputPipe(adv, 100);
				} catch (Exception e) {
					System.out.println("Erro ao criar OutputPipe para '" + adv.getName() + "':" + e.getMessage());
				}
				if (pipeOut != null) {
					Message msg = null;
					String userInput = null;
					InputStream inputStream = null;
					try {
						msg = pipeSvc.createMessage();
						// inputStream=new ByteArrayInputStream()
						StringMessageElement sme = new StringMessageElement(
								SENDER_NAME, nick, null);
						StringMessageElement sme2 = new StringMessageElement(
								MESSAGE_TAG, texto, null);
						msg.replaceMessageElement(sme);
						msg.replaceMessageElement(sme2);
						pipeOut.send(msg);
					} catch (Exception e) {
						System.out.println("Erro ao enviar mensagem para '"
								+ adv.getName() + "':" + e.getMessage());
					}
				}
			}
		}
	}

	public void startJxta() {
		try {
			group = PeerGroupFactory.newNetPeerGroup();
			System.out.println("NetPeerGroup iniciado com sucesso...");
		} catch (Exception e) {
			System.out.println("Erro ao iniciar netPeerGroup:" + e.getMessage());
			System.exit(-1);
		}
		discovery = group.getDiscoveryService();
		rdv = group.getRendezVousService();
		System.out.println("Rendezvous service recuperado...");
		while (rdv.isConnectedToRendezVous()) {
			try {
				System.out.println("Aguardando conexão com Rendezvous...");
				Thread.sleep(2000);
			} catch (InterruptedException e) {

			}
		}
		System.out.println("Conexão com Rendezvous estabelecida, recuperando serviço de pipe...");
		pipeSvc = group.getPipeService();
		guiLogin = new GuiP2PLogin(this);
		guiP2P = new GuiP2P(this);
	}
	
	public String executarFormula(String formula) throws Exception {
		Pattern p = Pattern.compile("\\((.*?)\\)",Pattern.DOTALL);
		Matcher matcher = p.matcher(formula);
		while(matcher.find()){
			String match = matcher.group(1);
		    System.out.println("found match:"+match);
		    Object resultado = delegarFormula(match);
		    System.out.println(formula);
		    formula = formula.replace("("+match+")", resultado.toString());
		    System.out.println(formula);
		}
		if(!formula.matches("[0-9]")){
		    Object resultado = delegarFormula(formula);
		    System.out.println(formula);
		    formula = resultado.toString();
		    System.out.println(formula);
		}
		return formula;
	}
	
	private Object delegarFormula(String formula) throws Exception {
		
		Socket socket = new Socket("172.16.5.53", 7000);

		ObjectOutputStream saida = new ObjectOutputStream(socket.getOutputStream());
		saida.writeObject(formula);

		ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());
		
		Object resultado = entrada.readObject();
		
		System.out.println(resultado.toString());
		
		entrada.close();
		
		return resultado.toString();
		
		//return new FormulaModelo().executarFormula(formula);
	}
	
}