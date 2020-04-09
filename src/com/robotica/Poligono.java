package com.robotica;

import io.github.jdiemke.triangulation.Triangle2D;
import io.github.jdiemke.triangulation.Vector2D;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import math.geom2d.Point2D;
import math.geom2d.polygon.Polygons2D;
import math.geom2d.polygon.SimplePolygon2D;

public class Poligono {
	private List<Ponto> pontos = new ArrayList<>();

	public Poligono() {
		
	}
	
	public Poligono(List<Ponto> p) {
		this.pontos = p;
	}
	
	public Poligono copiaComTransformEscala(float fatorX, float fatorY) {
		List<Ponto> pts = new ArrayList<Ponto>();
		int cx = getCentroX(), cy = getCentroY();
		Ponto pn;
		for(Ponto p : pontos) {
			pn = new Ponto(p.x, p.y);
			// Subtrai centro do poligono
			transformTranslacao(pn, -cx, -cy);
			// Aplica escala
			transformEscala(pn, fatorX, fatorY);
			// Soma centro de volta
			transformTranslacao(pn, cx, cy);
			pts.add(pn);
		}
		return new Poligono(pts);
	}
	
	private int getCentroX() {
		int xMenor = Integer.MAX_VALUE, xMaior = Integer.MIN_VALUE;
		for(Ponto p : pontos) {
			if (p.x <= xMenor) {
				xMenor = p.x;
			}
			if (p.x >= xMaior) {
				xMaior = p.x;
			}
		}
		return (xMenor + xMaior) / 2;
		
	}
	
	public int getMeiaLarguraX() {
		int xMenor = Integer.MAX_VALUE, xMaior = Integer.MIN_VALUE;
		for(Ponto p : pontos) {
			if (p.x <= xMenor) {
				xMenor = p.x;
			}
			if (p.x >= xMaior) {
				xMaior = p.x;
			}
		}
		return (xMaior - xMenor) / 2;
	}
	
	public int getMeiaAlturaY() {
		int yMenor = Integer.MAX_VALUE, yMaior = Integer.MIN_VALUE;
		for(Ponto p : pontos) {
			if (p.y <= yMenor) {
				yMenor = p.y;
			}
			if (p.y >= yMaior) {
				yMaior = p.y;
			}
		}
		return (yMaior - yMenor) / 2;
	}
	
	private int getCentroY() {
		int yMenor = Integer.MAX_VALUE, yMaior = Integer.MIN_VALUE;
		for(Ponto p : pontos) {
			if (p.y <= yMenor) {
				yMenor = p.y;
			}
			if (p.y >= yMaior) {
				yMaior = p.y;
			}
		}
		return (yMenor + yMaior) / 2;
	}
	
	private void transformTranslacao(Ponto p, int dx, int dy) {
		p.x = p.x + dx;
		p.y = p.y + dy;
	}
	
	private void transformEscala(Ponto p, float fatorX, float fatorY) {
		p.x = Math.round(p.x * fatorX);
		p.y = Math.round(p.y * fatorY);
	}
	
	public void addPonto(Ponto p) {
		pontos.add(p);
	}
	
	public void parseTriangulo(Triangle2D t) {
		pontos.add(new Ponto((int)t.a.x, (int)t.a.y));
		pontos.add(new Ponto((int)t.b.x, (int)t.b.y));
		pontos.add(new Ponto((int)t.c.x, (int)t.c.y));
	}

	public Vector<Vector2D> getVetores() {
		Vector<Vector2D> vetores = new Vector<>();
		for(Ponto p : pontos) {
			vetores.add(p.paraVetor());
		}
		return vetores;
	}
	
	private List<Point2D> getPolygon2D() {
		List<Point2D> pts = new ArrayList<Point2D>();
		for(Ponto p : pontos) {
			pts.add(p.getPoint2D());
		}
		return pts;
	}

	public List<Ponto> getPontos() {
		return pontos;
	}
	
	public List<Segmento> getSegmentos() {
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
	
	/**
	 * Testa a interseção de um polígono com um triângulo
	 * 
	 * @param triangulo
	 * @return
	 */
	public boolean intersec(Triangle2D triangulo) {
	    SimplePolygon2D triPol = new SimplePolygon2D(Arrays.asList(
	    		Ponto.paraPoint2D(triangulo.a), 
	    		Ponto.paraPoint2D(triangulo.b),
	    		Ponto.paraPoint2D(triangulo.c)));
	    
	    SimplePolygon2D pol = new SimplePolygon2D(getPolygon2D());
	    
	    return !Polygons2D.intersection(pol, triPol).isEmpty();
	}
}
