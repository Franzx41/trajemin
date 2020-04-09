package com.robotica.util;

import io.github.jdiemke.triangulation.Triangle2D;

import java.util.ArrayList;
import java.util.List;

import com.robotica.Ponto;
import com.robotica.Segmento;

public class Geometria {
	public static List<Segmento> convertTrianguloParaSegmentos(Triangle2D triangulo) {
		List<Segmento> ret = new ArrayList<Segmento>();
		
		Segmento sa = new Segmento(new Ponto((int)triangulo.a.x, (int)triangulo.a.y), 
	  			   new Ponto((int)triangulo.b.x, (int)triangulo.b.y));

		Segmento sb = new Segmento(new Ponto((int)triangulo.b.x, (int)triangulo.b.y), 
		  				   new Ponto((int)triangulo.c.x, (int)triangulo.c.y));
		
		Segmento sc = new Segmento(new Ponto((int)triangulo.c.x, (int)triangulo.c.y), 
		  				   new Ponto((int)triangulo.a.x, (int)triangulo.a.y));
		
		ret.add(sa);
		ret.add(sb);
		ret.add(sc);
		
		return ret;
	}
}
