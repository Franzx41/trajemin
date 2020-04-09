package com.robotica.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.util.LinkedList;

import javax.swing.JComponent;

import com.robotica.Poligono;
import com.robotica.Ponto;
import com.robotica.Segmento;

public class EspacoConfiguracaoComponente extends JComponent implements MouseListener {
	
	private class Linha {
	    final int x1, y1;
	    final int x2, y2;   
	    final Color color;
	    final int espessura;

	    public Linha(int x1, int y1, int x2, int y2, Color color, int espessura) {
	        this.x1 = x1;
	        this.y1 = y1;
	        this.x2 = x2;
	        this.y2 = y2;
	        this.color = color;
	        this.espessura = espessura;
	    }               
	}
	
	public interface MouseClickListener {
		public void onMouseClick(int x, int y);
	};

    private final int ARR_SIZE = 8;

	private final LinkedList<Linha> linhas = new LinkedList<Linha>();
	private final LinkedList<Polygon> poligonos = new LinkedList<Polygon>();
	private final LinkedList<Ponto> marcadores = new LinkedList<Ponto>();
	private final LinkedList<Ponto> pontos = new LinkedList<Ponto>();
	
	private MouseClickListener clickListener;
	
	public EspacoConfiguracaoComponente() { }

	public void setListener(MouseClickListener clickListener) {
		this.clickListener = clickListener;
		this.addMouseListener(this);
	}
	
	public void limpar() {
		linhas.clear();
		poligonos.clear();
		marcadores.clear();
		pontos.clear();
		repaint();
	}

	public void addObstaculo(Poligono obst) {
		Polygon pol = new Polygon();
		for (Ponto p : obst.getPontos()) {
			pol.addPoint(p.x, p.y);
		}
		poligonos.add(pol);
		repaint();
	}
	
	public void addMarcador(Ponto marcador) {
		marcadores.add(marcador);
		repaint();
	}
	
	public void addPonto(Ponto pt) {
		pontos.add(pt);
		repaint();
	}
	
	/*public void apagarMarcadores() {
		marcadores.clear();
		repaint();
	}*/
	
	public void addSegmento(Segmento seg, Color color, int espessura) {
	    linhas.add(new Linha(seg.a.x,seg.a.y, seg.b.x, seg.b.y, color, espessura));        
	    repaint();
	}

	/*public void apagarSegmentos() {
	    linhas.clear();
	    repaint();
	}*/

	@Override
	protected void paintComponent(Graphics g) {
	    super.paintComponent(g);
	    Graphics2D g2 = (Graphics2D) g;
	    
	    g2.clearRect(0, 0, getWidth(), getHeight());
	    
	    g2.setColor(Color.BLACK);	
	    for (Ponto pt: marcadores) {
	    	g2.drawOval(pt.x - 3, pt.y - 3, 6, 6);
	    }
	        
	    g2.setColor(Color.MAGENTA);
	    for (Ponto pt: pontos) {
	    	g2.setStroke(new BasicStroke(2));
	    	g2.drawOval(pt.x - 6, pt.y - 6, 12, 12);
	    	//g2.fillOval(pt.x - 6, pt.y - 6, 12, 12);
	    }
  
	    g2.setColor(Color.DARK_GRAY);
	    for (Polygon p: poligonos) {
	        g2.fillPolygon(p);
	    }
	    
    
	    for (Linha l : linhas) {
	    	g2.setColor(l.color);
	    	g2.setStroke(new BasicStroke(l.espessura));
	        g2.drawLine(l.x1, l.y1, l.x2, l.y2);
	    }
	}
	
    private void drawArrow(Graphics g1, int x1, int y1, int x2, int y2) {
        Graphics2D g = (Graphics2D) g1.create();

        double dx = x2 - x1, dy = y2 - y1;
        double angle = Math.atan2(dy, dx);
        int len = (int) Math.sqrt(dx*dx + dy*dy);
        int offset = 5;
        AffineTransform at = AffineTransform.getTranslateInstance(x1, y1);
        at.concatenate(AffineTransform.getRotateInstance(angle));
        g.transform(at);

        // Draw horizontal arrow starting in (0, 0)
        g.drawLine(0, 0, len, 0);
        g.fillPolygon(new int[] {len - offset, len - offset - ARR_SIZE, len - offset - ARR_SIZE, len - offset},
                      new int[] {0, -ARR_SIZE, ARR_SIZE, 0}, 4);
    }

	@Override
	public void mouseClicked(MouseEvent e) { 

	}

	@Override
	public void mousePressed(MouseEvent e) { 
		clickListener.onMouseClick(e.getX(), e.getY());
	}

	@Override
	public void mouseReleased(MouseEvent e) { }

	@Override
	public void mouseEntered(MouseEvent e) { }

	@Override
	public void mouseExited(MouseEvent e) { }
}
