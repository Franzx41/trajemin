package com.robotica;

public class Segmento {
	
	public Ponto a;
	public Ponto b;

	public Segmento(Ponto a, Ponto b) {
		this.a = a;
		this.b = b;
	}
	
	@Override
	public boolean equals(Object obj) {
		Segmento s = (Segmento) obj;
		return ((s.a.equals(this.a) && s.b.equals(this.b)));// ||
				//(s.a.equals(this.b) && s.b.equals(this.a)));
	}



	public Double tamanho() {
		return Math.sqrt(Math.pow((a.x - b.x), 2) + Math.pow((a.y - b.y), 2));
	}
	    
	@Override
	public String toString() {
		return "a: " + a.toString() + " | b: " + b.toString();
	}
	
	
}
