package calculadora.persistencia;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class FormulaPersistencia {

	public List<String> lerArquivo(String caminho) throws IOException {
		List<String> lista = new ArrayList<String>();
		File file = new File(caminho);
		if(!file.exists())
			return lista;
		FileInputStream fstream = new FileInputStream(file);
		DataInputStream in = new DataInputStream(fstream);
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String strLine;
		while ((strLine = br.readLine()) != null) {
			lista.add(strLine);
		}
		in.close();
		return lista;
	}

	public void escreverArquivo(String texto, String caminho) {
		try {
			List<String> lista = lerArquivo(caminho);
			BufferedWriter buffWrite = new BufferedWriter(new FileWriter(caminho));
			for(String linha : lista){
				buffWrite.append(linha + "\n");
			}
			buffWrite.append(texto + "\n");
			buffWrite.close();
		}catch(IOException e){
			e.printStackTrace();
		}
	}

}
