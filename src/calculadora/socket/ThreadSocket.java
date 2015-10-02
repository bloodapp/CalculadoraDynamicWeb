package calculadora.socket;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import calculadora.negocio.FormulaNegocio;

public class ThreadSocket extends Thread {

	private Socket socket;
	private FormulaNegocio modelo;

	public ThreadSocket(Socket cliente) {
		setSocket(cliente);
	}

	public void run() {
		try {
			ObjectInputStream entrada = new ObjectInputStream(getSocket().getInputStream());
			ObjectOutputStream saida = new ObjectOutputStream(getSocket().getOutputStream());

			String formula = (String) entrada.readObject();
			Object resultado = getModelo().executarFormula(formula);
			saida.writeObject(resultado);
			
			System.out.println("Solicitação atendida: " + formula + " = " + resultado.toString());

			saida.flush();
			entrada.close();
			saida.close();
			getSocket().close();
		}

		catch (Exception e) {
			System.out.println("Excecao ocorrida na thread: " + e.getMessage());
			try {
				getSocket().close();
			} catch (Exception ex) {
			}
		}

	}
	
	public FormulaNegocio getModelo() {
		if(modelo == null)
			modelo = new FormulaNegocio();
		return modelo;
	}

	public void setSocket(Socket socket) {
		this.socket = socket;
	}
	
	public Socket getSocket() {
		return socket;
	}
	

}
