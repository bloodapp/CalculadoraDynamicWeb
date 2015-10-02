package calculadora.negocio;

import java.io.File;
import java.io.IOException;
import java.util.List;

import calculadora.persistencia.FormulaPersistencia;
import bsh.EvalError;
import bsh.Interpreter;

public class FormulaNegocio {

	private FormulaPersistencia persistencia;
	
	public void receber(){
		
	}
	
	public void verificar(){
		
	}
	
	public Object executarFormula(String formula) throws EvalError {
		String caminhoArquivo = "home" + File.separator + "calc";
		//String caminhoArquivo = "c:\\temp\\calc.txt";
		getPersistencia().escreverArquivo(formula, caminhoArquivo);
		Interpreter inter = new Interpreter();
		return inter.eval(formula);
	}
	
	public List<String> lerArquivo(String caminho) throws IOException {
		return getPersistencia().lerArquivo(caminho);
	}
	
	public void persistir(){
		
	}

	public FormulaPersistencia getPersistencia() {
		if(persistencia == null)
			persistencia = new FormulaPersistencia();
		return persistencia;
	}
	
}
