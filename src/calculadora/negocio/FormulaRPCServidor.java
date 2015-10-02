package calculadora.negocio;

import java.rmi.Naming;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class FormulaRPCServidor {

	public FormulaRPCServidor() {  
        try {  
        	//Registry registry = LocateRegistry.createRegistry(1099);
            IFormulaRPC formula = new FormulaRPCImpl();  
            Naming.rebind("rmi://localhost:1099/FormulaRPCImpl", formula);  
        }  
        catch( Exception e ) {  
            System.out.println("Trouble: " + e);  
        }  
    }  
  
    public static void main(String[] args) {  
        new FormulaRPCServidor();  
    }  	
}
