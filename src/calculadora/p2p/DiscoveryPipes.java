package calculadora.p2p;

import net.jxta.discovery.DiscoveryService;

/**
 * Esta classe implementa a interface Runnable, e inicia uma thread que procura
 * por novos advertisements na rede a cada 60 segundos.
 */
public class DiscoveryPipes implements Runnable {
	private DiscoveryService discoverySvc;

	/**
	 * Construtor da classe que recebe o serviço de discovery (DiscoveryService)
	 * da classe chat.ChatApp, e inicia uma nova thread para a procura de
	 * Advertisements.
	 * 
	 * @see AppP2P.ChatApp
	 */
	public DiscoveryPipes(DiscoveryService discoverySvc) {
		this.discoverySvc = discoverySvc;
		Thread t = new Thread(this);
		t.start();
		System.out.println("Thread para Discovery de pipes criada e iniciada....");
	}

	public void run() {
		while (true) {
			try {
				System.out.println("Tentando descobrir novos pipes remotamente...");
				discoverySvc.getRemoteAdvertisements(null,
						DiscoveryService.ADV, null, null, 100);
				Thread.sleep(60 * 1000);
			} catch (InterruptedException e) {
			}
		}
	}
}