package com.robotica;

import math.geom2d.Point2D;
import io.github.jdiemke.triangulation.Vector2D;

public class Ponto {

	final static double EPSILON = 0.0000001;

	public int x, y;


	public Ponto(int x, int y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public boolean equals(Object obj) {
		Ponto c = (Ponto) obj;
		return (this.x == c.x && this.y == c.y);//((Math.abs(x - c.x) <= EPSILON) && (Math.abs(y - c.y) <= EPSILON));
	}

	@Override
	public int hashCode() {
		return (smear(x) ^ smear(y));
	}

	public Vector2D paraVetor() {
		return new Vector2D(this.x, this.y);
	}
	
	
	public static Point2D paraPoint2D(Vector2D v) {
		return new Point2D(v.x, v.y);
	}
	
	public Point2D getPoint2D() {
		return new Point2D(this.x, this.y);
	}

	@Override
	public String toString() {
		return "(x: " + x + ", y: " + y  + ")";
	}

	/* Referência: https://stackoverflow.com/a/9625053
	 * 
	 * Gerador de hash
	 * 
	 * "This method was written by Doug Lea with assistance from members of JCP
	 * JSR-166 Expert Group and released to the public domain, as explained at
	 * http://creativecommons.org/licenses/publicdomain
	 * 
	 * As of 2010/06/11, this method is identical to the (package private) hash
	 * method in OpenJDK 7's java.util.HashMap class."
	 */
	static int smear(int hashCode) {
		hashCode ^= (hashCode >>> 20) ^ (hashCode >>> 12);
		return hashCode ^ (hashCode >>> 7) ^ (hashCode >>> 4);
	}
}

