package calculadora.negocio;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface IFormulaRPC extends Remote {

	public void ativar() throws RemoteException;
	public Object executarFormula(String formula) throws RemoteException;

}
