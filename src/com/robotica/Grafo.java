package com.robotica;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Grafo {
	HashMap<String, ArrayList<Ponto>> listaAdj = new HashMap<>();
	ArrayList<Ponto> listaPontos = new ArrayList<>();
	
	public void addAresta(Ponto origem, Ponto destino) {
		ArrayList<Ponto> l = listaAdj.get(geraChaveUnica(origem));
		if (l != null) {
			l.add(destino);
		} else {
			listaAdj.put(geraChaveUnica(origem), new ArrayList<>(Arrays.asList(destino)));
			listaPontos.add(origem);
		}
	}
	
	/**
	 * Deriva uma chave única através da concatenação dos hash de cada coordenada 
	 * @param p Ponto
	 * @return Retorna uma chave única
	 */
	private String geraChaveUnica(Ponto p) {
		String s1, s2, ret = "";
		try {
			s1 = makeSHA1Hash(String.valueOf(p.x));
			s2 = makeSHA1Hash(String.valueOf(p.y));
			ret = makeSHA1Hash(s1 + s2);
		} catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return ret;
	}
	/**
	 * Gera um hash único SHA1 a partir de uma string
	 * Referência: https://stackoverflow.com/a/6120698
	 * @param input
	 * @return hash único SHA1
	 * @throws NoSuchAlgorithmException
	 * @throws UnsupportedEncodingException
	 */
    private String makeSHA1Hash(String input) throws NoSuchAlgorithmException, UnsupportedEncodingException{
	    MessageDigest md = MessageDigest.getInstance("SHA1");
	    md.reset();
	    byte[] buffer = input.getBytes("UTF-8");
	    md.update(buffer);
	    byte[] digest = md.digest();
	
	    String hexStr = "";
	    for (int i = 0; i < digest.length; i++) {
	        hexStr +=  Integer.toString( ( digest[i] & 0xff ) + 0x100, 16).substring( 1 );
	    }
	    return hexStr;
   }
}
