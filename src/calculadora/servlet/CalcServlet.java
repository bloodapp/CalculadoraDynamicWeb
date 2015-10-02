package calculadora.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import bsh.EvalError;
import calculadora.negocio.FormulaNegocio;

public class CalcServlet extends HttpServlet {

	private static final long serialVersionUID = -4558411947384458570L;

	private FormulaNegocio negocio;
	
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
	
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		String formula = request.getParameter("formula");
		
		if(formula == null)
			return;
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		try {
			Object resultado = getNegocio().executarFormula(formula);
			out.println("<b><font color='blue'>Fórmula:</font></b>"
					+ "<b>" + formula + "</b>" + "<br>");
			out.println("<b><font color='blue'>Resultado:</font></b>"
					+ "<b>" + resultado + "</b>" + "<br>");
			out.println("<b><font color='black'>Execução:</font></b>"
					+ "<b>" + sdf.format(new Date()) + "</b>" + "<br>");
		} catch (EvalError e) {
			out.println("<b><font color='red'>Erro ao executar fórmula:</font></b>"
					+ "<b>" + e.getMessage() + "</b>" + "<br>");
			e.printStackTrace();
		}
		
	}

	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		doGet(request, response);
	}
	
	public FormulaNegocio getNegocio() {
		if(negocio == null)
			negocio = new FormulaNegocio();
		return negocio;
	}
	
}
