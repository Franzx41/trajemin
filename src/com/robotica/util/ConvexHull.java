package com.robotica.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

import com.robotica.Ponto;
import com.robotica.Segmento;

/**
 * Calcula o fecho convexo entre um conjunto de pontos
 *  
 * Referência: https://rosettacode.org/wiki/Convex_hull#Java
 */
public class ConvexHull {
 
    public static List<Segmento> convexHull(List<Ponto> p) {
        List<Ponto> h = new ArrayList<>();
        List<Segmento> ret = new ArrayList<Segmento>();
        if (p.isEmpty()) return ret;
        //p.sort(new Ponto(0, 0));
        Collections.sort(p, new PontoComparador());
        // lower hull
        for (Ponto pt : p) {
            while (h.size() >= 2 && !ccw(h.get(h.size() - 2), h.get(h.size() - 1), pt)) {
                h.remove(h.size() - 1);
            }
            h.add(pt);
        }
 
        // upper hull
        int t = h.size() + 1;
        for (int i = p.size() - 1; i >= 0; i--) {
            Ponto pt = p.get(i);
            while (h.size() >= t && !ccw(h.get(h.size() - 2), h.get(h.size() - 1), pt)) {
                h.remove(h.size() - 1);
            }
            h.add(pt);
        }
 
        h.remove(h.size() - 1);
        
        return getSegmentos(h);
    }
 
    // ccw returns true if the three points make a counter-clockwise turn
    private static boolean ccw(Ponto a, Ponto b, Ponto c) {
        return ((b.x - a.x) * (c.y - a.y)) > ((b.y - a.y) * (c.x - a.x));
    }
    
    // ---------------------------------
    
	private static List<Segmento> getSegmentos(Vector<Ponto> pontos) {
		List<Segmento> segmentos = new ArrayList<>();
		if (pontos.size() < 2) {
			return segmentos;
		}
		
		Ponto anterior = pontos.get(0);
		for (int i = 1; i < pontos.size(); i++) {
			segmentos.add(new Segmento(anterior, pontos.get(i)));
			anterior = pontos.get(i);
		}
		segmentos.add(new Segmento(pontos.get(pontos.size() - 1), pontos.get(0)));
		return segmentos;
	}
	
	private static List<Segmento> getSegmentos(List<Ponto> pontos) {
		List<Segmento> segmentos = new ArrayList<>();
		if (pontos.size() < 2) {
			return segmentos;
		}
		
		Ponto anterior = pontos.get(0);
		for (int i = 1; i < pontos.size(); i++) {
			segmentos.add(new Segmento(anterior, pontos.get(i)));
			anterior = pontos.get(i);
		}
		segmentos.add(new Segmento(pontos.get(pontos.size() - 1), pontos.get(0)));
		return segmentos;
	}
}
