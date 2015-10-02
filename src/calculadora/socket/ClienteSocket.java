package calculadora.socket;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClienteSocket {

	private static final int PORTA_CLIENTE = 7010;
	
	public static void main(String[] args) {
		try {

			Socket socket = new Socket("172.16.5.26", PORTA_CLIENTE);

			ObjectOutputStream saida = new ObjectOutputStream(socket.getOutputStream());
			saida.writeObject("1 + 789 * 9");

			ObjectInputStream entrada = new ObjectInputStream(socket.getInputStream());
			
			Object resultado = entrada.readObject();
			
			System.out.println(resultado.toString());
			
			entrada.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
}
