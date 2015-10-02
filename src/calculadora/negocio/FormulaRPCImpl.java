package calculadora.negocio;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

import bsh.EvalError;

public class FormulaRPCImpl extends UnicastRemoteObject implements IFormulaRPC {  
  
	private FormulaNegocio formulaNegocio = null;
	
	protected FormulaRPCImpl() throws RemoteException {
		super();
	}

	private static final long serialVersionUID = -227594931446787840L;

	@Override
	public void ativar() throws RemoteException {
		System.out.println("FormulaRPCImpl.ativar()");
	}

	@Override
	public Object executarFormula(String formula) throws RemoteException {
		try {
			return getFormulaNegocio().executarFormula(formula);
		} catch (EvalError e) {
			throw new RemoteException("Erro ao executar fórmula", e);
		}
	}
	
	public FormulaNegocio getFormulaNegocio() {
		if(formulaNegocio == null)
			formulaNegocio = new FormulaNegocio();
		return formulaNegocio;
	}
	

}
