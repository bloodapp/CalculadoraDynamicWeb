package calculadora.ws;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import bsh.EvalError;
import calculadora.negocio.FormulaNegocio;

@Path("Calc")
public class CalculadoraWS {

	private FormulaNegocio formulaNegocio;

	@GET
	@Path("/calc/{formula}")
	public String calc(@PathParam("formula") String formula) {
		return executarFormula(formula);
	}

	@GET
	@Path("/calcAsJSON/{formula}")
	@Produces(MediaType.APPLICATION_JSON)
	public String calcAsXml(@PathParam("formula") String formula) {
		return executarFormula(formula);
	}

	@GET
	@Path("/{formula}")
	public Response calculadora(@PathParam("formula") String formula) {
		String resultado = executarFormula(formula);
		return Response.status(200).entity(resultado).build();
	}
	
	@POST
	@Path("/post")
	@Consumes(MediaType.APPLICATION_JSON)
	public Response post(@PathParam("formula") String formula) {
		String resultado = executarFormula(formula);
		return Response.status(201).entity(resultado).build();
	}
	
	protected String executarFormula(String formula){
		try {
			return getFormulaNegocio().executarFormula(formula).toString();
		} catch (EvalError e) {
			e.printStackTrace();
		}
		return null;
	}

	public FormulaNegocio getFormulaNegocio() {
		if(formulaNegocio == null)
			formulaNegocio = new FormulaNegocio();
		return formulaNegocio;
	}

}
