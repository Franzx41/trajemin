package com.robotica;

import io.github.jdiemke.triangulation.DelaunayTriangulator;
import io.github.jdiemke.triangulation.NotEnoughPointsException;
import io.github.jdiemke.triangulation.Triangle2D;
import io.github.jdiemke.triangulation.Vector2D;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Label;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.jgrapht.Graph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.interfaces.AStarAdmissibleHeuristic;
import org.jgrapht.alg.shortestpath.AStarShortestPath;
import org.jgrapht.alg.shortestpath.DijkstraShortestPath;
import org.jgrapht.graph.WeightedPseudograph;

import com.robotica.ui.EspacoConfiguracaoComponente;
import com.robotica.ui.EspacoConfiguracaoComponente.MouseClickListener;
import com.robotica.util.ConvexHull;
import com.robotica.util.Geometria;

public class Main {

	public static class EuclideanDistance implements AStarAdmissibleHeuristic<Ponto> {
	    @Override
	    public double getCostEstimate(Ponto sourceVertex, Ponto targetVertex) {
	        return Math.sqrt(
	            Math.pow(sourceVertex.x - targetVertex.x, 2)
	                + Math.pow(sourceVertex.y - targetVertex.y, 2));
	    }
	}
	
    private static EspacoConfiguracaoComponente espacoConfig;
    
    private static JButton addObstBtn = new JButton("+ Obstáculo");
    private static JButton apagarBtn = new JButton("Limpar Tudo");
    private static JButton addOrigemBtn = new JButton("+ Origem");
    private static JButton addDestinoBtn = new JButton("+ Destino");
    private static JButton resultadoBtn = new JButton("= Resultado");
    private static TextField tfRaioRobo = new TextField(5);

    private static boolean habilitarSetOrigem = false, habilitarSetDestino = false;
    private static Color defaultBtntCor;

    private static Ponto ptOrigem, ptDestino;
    private static Poligono correntePolig;
    private static List<Poligono> obstaculosLista = new ArrayList<Poligono>();

	private static void setListerners() {
	    addObstBtn.addActionListener(new ActionListener() {

	        @Override
	        public void actionPerformed(ActionEvent e) {
	            if (correntePolig == null) {
	            	correntePolig = new Poligono();
	            	addObstBtn.setText("+ Salvar   ");
	            	addObstBtn.setBackground(Color.MAGENTA);
	            	
	            } else {
	            	// Add polígono
	            	float fatorX = 1, fatorY = 1;
	            	try {
	            		int raio = Integer.valueOf(tfRaioRobo.getText().toString().trim());
	            		float hw = correntePolig.getMeiaLarguraX();
	            		float hh = correntePolig.getMeiaAlturaY();
	            		fatorX = (hw + raio) / hw;
	            		fatorY = (hh + raio) / hh;
	            		
					} catch (Exception e2) {
						e2.printStackTrace();
					}
	            	
	            	//System.out.println("fatorX: " + fatorX + " fatorY: " + fatorY);
	            	obstaculosLista.add(correntePolig.copiaComTransformEscala(fatorX, fatorY)); // correntePolig.copiaComTransformEscala(1.1f)
	            	espacoConfig.addObstaculo(correntePolig);
	            	correntePolig = null;
	            	addObstBtn.setText("+ Obstáculo");
	            	addObstBtn.setBackground(defaultBtntCor);
	            }
	        }
	    });
	    
	    apagarBtn.addActionListener(new ActionListener() {
	        @Override
	        public void actionPerformed(ActionEvent e) {
	        	espacoConfig.limpar();
	            addDestinoBtn.setEnabled(true);
	            addOrigemBtn.setEnabled(true);
	            resultadoBtn.setEnabled(true);
	            
	            correntePolig = null;
	            
	            obstaculosLista.clear();
	            ptOrigem = null;
	            ptDestino = null;
	        }
	    });
	    
	    addOrigemBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				habilitarSetOrigem = true;
				addOrigemBtn.setBackground(Color.MAGENTA);
			}
		});
	    
	    addDestinoBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				habilitarSetDestino = true;
				addDestinoBtn.setBackground(Color.MAGENTA);
			}
		});
	    
	    resultadoBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					if (ptOrigem != null && ptDestino != null && obstaculosLista.size() > 0) {
						resultadoBtn.setEnabled(false);
						processaCaminhos();
					} else {
						System.out.println("INFO: Ação não foi possível (ptOrigem: " +
					ptOrigem + ", ptDestino: " + ptDestino + ", obstaculosLista.size(): " + obstaculosLista.size());
					}
				} catch (Exception e2) { 
					System.out.println("INFO: Caminho mínio não encontrado");
					JOptionPane.showMessageDialog(null, "Caminho mínimo não encontrado.");
				}
			}
		});
	}
	
	// https://github.com/jdiemke/delaunay-triangulator
	public static void main(String[] args) {
		
		try { 
	        UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel"); 
	    } catch(Exception ignored){}

		
	    JFrame frame = new JFrame();
	    frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    
	    espacoConfig = new EspacoConfiguracaoComponente();
	    espacoConfig.setListener(new MouseClickListener() {
			
			@Override
			public void onMouseClick(int x, int y) {
				//System.out.println("X: " + x + " Y: " + y);
				if (correntePolig != null) {
					correntePolig.addPonto(new Ponto(x, y));
					espacoConfig.addMarcador(new Ponto(x, y));
				}
				
	            if (habilitarSetDestino) {
	            	ptDestino = new Ponto(x, y);
	            	espacoConfig.addPonto(new Ponto(x, y));
	            	habilitarSetDestino = false;
	            	addDestinoBtn.setEnabled(false);
	            	addDestinoBtn.setBackground(defaultBtntCor);
	            }
	            
	            if (habilitarSetOrigem) {
	            	ptOrigem = new Ponto(x, y);
	            	espacoConfig.addPonto(new Ponto(x, y));
	            	habilitarSetOrigem = false;
	            	addOrigemBtn.setEnabled(false);
	            	addOrigemBtn.setBackground(defaultBtntCor);
	            }
			}
		});
		
	    espacoConfig.setPreferredSize(new Dimension(800, 400));
	    
	    frame.getContentPane().add(espacoConfig, BorderLayout.CENTER);
	    
	    frame.setTitle(":: Planejador de Trajetória Mínima - v1.0 (por Francisco e Rafael - 2020)");
	    
	    JPanel buttonsPanel = new JPanel();
	    buttonsPanel.add(new Label("Raio do Robô(px):"));
	    tfRaioRobo.setText("10");
	    buttonsPanel.add(tfRaioRobo);
	    buttonsPanel.add(addOrigemBtn);
	    buttonsPanel.add(addDestinoBtn);
	    buttonsPanel.add(addObstBtn);
	    buttonsPanel.add(resultadoBtn);
	    buttonsPanel.add(apagarBtn);
	    frame.getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
	    
    	defaultBtntCor = addObstBtn.getBackground();
	    
	    setListerners();

	    frame.pack();
	    frame.setVisible(true);
	    
	}
	
	private static void processaCaminhos() {
		Vector<Vector2D> pointSet = new Vector<>();
		for (Poligono obst: obstaculosLista) {
			pointSet.addAll(obst.getVetores());
		}
		
		// Add pontos de origem e destino para o processamento
		pointSet.add(ptOrigem.paraVetor());
		pointSet.add(ptDestino.paraVetor());
		
		// Add limites 
		pointSet.add(new Vector2D(0, 0));
		pointSet.add(new Vector2D(0, espacoConfig.getHeight() - 1));
		pointSet.add(new Vector2D(espacoConfig.getHeight(), espacoConfig.getHeight() - 1));
		pointSet.add(new Vector2D(espacoConfig.getHeight(), 0));
		
		//System.out.println("pointSet.size(): " + pointSet.size() + " [" + pointSet.toString());
		
		/**
		 * Triangulação Delaunay do espaço de configuração
		 * 
		 * Referência: https://github.com/jdiemke/delaunay-triangulator
		 */
		DelaunayTriangulator delaunayTriangulator = new DelaunayTriangulator(pointSet);
		try {
			delaunayTriangulator.triangulate();
		} catch (NotEnoughPointsException e) {
			e.printStackTrace();
		}
		
		List<Triangle2D> triangleSoup = delaunayTriangulator.getTriangles();
		List<Segmento> segs = getSegmentos(triangleSoup);
		/*for (Segmento s : segs) {
			espacoConfig.addSegmento(s, Color.RED, 1);
		}*/
		

		List<Segmento> segList = new ArrayList<>();
	    // Busca todos os triângulos pertecentes ao mesmo obstáculo e
		//	acha o fecho convexo de cada qual
	    for (Poligono obst: obstaculosLista) {
	    	List<Ponto> grupoTriang = new ArrayList<>();
		    for (int i = triangleSoup.size() - 1; i >= 0; i--) {
		    	if (obst.intersec(triangleSoup.get(i))) {
		    		//segList.addAll(Geometria.convertTrianguloParaSegmentos(triangleSoup.get(i)));
		    		
		    		Poligono p = new Poligono();
		    		p.parseTriangulo(triangleSoup.get(i));
		    		grupoTriang.addAll(p.getPontos());
		    		triangleSoup.remove(i);
		    	} 
		    }
		    //segList.addAll(ConvexHull.convexHull(grupoTriang.toArray(new Ponto[0]), grupoTriang.size()));
		    segList.addAll(ConvexHull.convexHull(grupoTriang));
    	}
	    
	    // Add os triangulos restantes para a lista de segmentos final
	    for (Triangle2D t : triangleSoup) {
	    	List<Segmento> s = Geometria.convertTrianguloParaSegmentos(t);
	    	for (Segmento segm : s) {
	    		//espacoConfig.addSegmento(segm, Color.RED);
	    	}
	    	segList.addAll(Geometria.convertTrianguloParaSegmentos(t));
	    }

	    try {
	    	Graph<Ponto, Double> g = new WeightedPseudograph<>(Double.class);
		    
		    // Constroi grafo de visibilidade e mostra todos os caminhos na tela
		    for (Segmento seg: segList) {
		    	espacoConfig.addSegmento(seg, Color.BLACK, 1);
		    	g.addVertex(seg.a);
		    	g.addVertex(seg.b);
		    	g.addEdge(seg.a, seg.b, seg.tamanho());
		    }
	        
	        List<Ponto> sp = getMenorCaminhoAStar(g, ptOrigem, ptDestino);
	        
	        if (sp.size() < 2) {
	        	System.out.println("Caminho não encontrado");
	        	return;
	        }
	        
	        Ponto pAnterior = sp.get(0);
	        for (int i = 1; i < sp.size(); i++) {
	        	espacoConfig.addSegmento(new Segmento(pAnterior, sp.get(i)), Color.MAGENTA, 3);
	        	pAnterior = sp.get(i);
			}
		} catch (Exception e) {
			JOptionPane.showMessageDialog(null, "Caminho mínimo não encontrado.");
		}
	}

	private static List<Ponto> getMenorCaminhoDijkstra(Graph<Ponto, Double> grafo, Ponto ori, Ponto dest) {
		GraphPath<Ponto, Double> sp = DijkstraShortestPath.findPathBetween(grafo, ori, dest);
		return sp.getVertexList();
	}
	
	private static List<Ponto> getMenorCaminhoAStar(Graph<Ponto, Double> grafo, Ponto ori, Ponto dest) {
	    
	    AStarShortestPath<Ponto, Double> aStarShortestPath2 = new AStarShortestPath<>(grafo, new EuclideanDistance());
        GraphPath<Ponto, Double> path2 = aStarShortestPath2.getPath(ori, dest);
        
        return path2.getVertexList();
	}
	
	private static List<Segmento> getSegmentos(List<Triangle2D> listaTriangulos) {
		List<Segmento> listSeg = new ArrayList<Segmento>();
		for (Triangle2D t : listaTriangulos) {
			listSeg.add(new Segmento(new Ponto((int) t.a.x, (int) t.a.y), new Ponto((int) t.b.x, (int) t.b.y)));
			listSeg.add(new Segmento(new Ponto((int) t.b.x, (int) t.b.y), new Ponto((int) t.c.x, (int) t.c.y)));
			listSeg.add(new Segmento(new Ponto((int) t.c.x, (int) t.c.y), new Ponto((int) t.a.x, (int) t.a.y)));
		}
		return listSeg;
	}
}
