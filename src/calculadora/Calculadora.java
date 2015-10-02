package calculadora;

import calculadora.visao.FormulaVisao;

public class Calculadora {

	public static void main(String[] args) {
		try {
			FormulaVisao formulaVisao = new FormulaVisao();
			formulaVisao.apresentar();
		} catch (Throwable ex) {
			ex.printStackTrace();
		}
	}

}
