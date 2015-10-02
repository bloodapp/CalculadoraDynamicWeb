package calculadora.visao;

import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import bsh.EvalError;
import calculadora.negocio.FormulaNegocio;

public class FormulaVisao {

	private Scanner scanner;
	private FormulaNegocio modelo;
	
	public void apresentar() throws Throwable {
		Integer opcao = 0;
		
		do {
			System.out.print("Enter com a opção desejada: \n");
			System.out.print("01 - Executar fórmula \n");
			System.out.print("02 - Ler arquivo \n");
			System.out.print("03 - Escrever arquivo \n");
			System.out.print("04 - Encerrar \n");
			opcao = Integer.parseInt(getScanner().nextLine());
			
			switch (opcao) {
			case 1:
				executarFormula();
				break;
			case 2:
				lerArquivo();
				break;
			case 3:
				executarFormula();
				break;
			case 4:
				encerrar();
				break;
			default:
				break;
			}
		} while(opcao != 4);
		
	}
	
	public void executarFormula() throws EvalError {
		System.out.print("Enter com a equação: ");
		String formula = getScanner().nextLine();
		Object resultado = getModelo().executarFormula(formula);
		System.out.println(resultado);
	}
	
	public void lerArquivo() throws EvalError, IOException {
		//System.out.print("Enter com o caminho do arquivo: ");
		//String caminho = getScanner().nextLine();
		String caminho = "C:\\Users/aluno/Documents/formulas.txt";
		List<String> lista = getModelo().lerArquivo(caminho);
		for(String formula : lista){
			try {
				Object resultado = getModelo().executarFormula(formula);
				System.out.println(formula + ": " + resultado);
			}catch (EvalError e) {
				new RuntimeException("Erro ao executar a fórmula + " + formula, e);
			}
		}
	}

	public void encerrar(){
		System.exit(0);
	}
	
	public Scanner getScanner() {
		if(scanner == null)
			scanner = new Scanner(System.in);
		return scanner;
	}

	public FormulaNegocio getModelo() {
		if(modelo == null)
			modelo = new FormulaNegocio();
		return modelo;
	}
	
}
