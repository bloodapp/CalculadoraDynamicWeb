package calculadora.negocio;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;

public class FormulaRPCCliente {

	public static void main( String args[] ) {  
        try {  
            IFormulaRPC m = (IFormulaRPC) Naming.lookup("rmi://localhost/FormulaRPCImpl");  
            m.executarFormula("1+10");  
        }  
        catch( MalformedURLException e ) {  
            System.out.println();  
            System.out.println( "MalformedURLException: " + e.toString() );  
        }  
        catch( RemoteException e ) {  
            System.out.println();  
            System.out.println( "RemoteException: " + e.toString() );  
        }  
        catch( NotBoundException e ) {  
            System.out.println();  
            System.out.println( "NotBoundException: " + e.toString() );  
        }  
        catch( Exception e ) {  
            System.out.println();  
            System.out.println( "Exception: " + e.toString() );  
        }  
    }  
	
}
