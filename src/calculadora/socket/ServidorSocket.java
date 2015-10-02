package calculadora.socket;

import java.net.ServerSocket;
import java.net.Socket;

public class ServidorSocket {

	private static final int PORTA_SERVIDOR = 7010;

	public static void main(String[] args) {
		try {
			ServerSocket serverSocket = new ServerSocket(PORTA_SERVIDOR);

			System.out.println("Servidor iniciado, ouvindo a porta " + PORTA_SERVIDOR);

			while (true) {
				Socket socket = serverSocket.accept();
				new ThreadSocket(socket).start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
