package com.robotica.collection;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.PriorityQueue;
import java.util.Queue;

import com.robotica.collection.*;

/**
 * Essa classe implementa um grafo acíclico ponderado dirigido. <br>
 * A implementação desse grafo garante que o acesso a todos os vértices e arestas tenham tempo de execução O(1) no pior caso <br>
 * Notas: essa implementação supõem-se que os pesos das arestas são não negativos. 
 * 
 * OBS: Classe reaproveitada de algum PBL
 *
 * @author Francisco A. A. Ferreira Jr.
 * @param <V>
 */
public class Digrafo<V> implements IDigrafo<V> {
	
	private class Vertice {
		public Vertice(V chave) {
			this.chave = chave;
		}
		
		public void addAresta(V verticeDestino, Double peso) {
			arestasAdjacentes.put(verticeDestino, new ArestaDirigida(this.chave, verticeDestino, peso));
			Vertice v = (Vertice) grafo.get(verticeDestino);
			v.contadorIncidencia++;
		}
		
		@Override
		public boolean equals(Object vertice) {
			Vertice v  = (Vertice) vertice;
			return chave.equals(v.chave);
		}
	
		public V chave;
		public Double d;
		public Double pinc;
		public Vertice parent; 
		public Map<V, ArestaDirigida> arestasAdjacentes = new HashMap<V, ArestaDirigida>();
		public int contadorIncidencia = 0;
		public Enum flag;
	}
	
	public enum Flag { VISITADO, NVISITADO };
	
	private class VerticeComparator implements Comparator<Vertice> {
		@Override
		public int compare(Vertice o1, Vertice o2) {
			return (o1.d != o2.d ? (o1.d < o2.d ? - 1 : 1) : 0);
		}
	}
		
	/**
	 * Tabela de espalhamento de vértices 
	 */
	private Map<V, Vertice> grafo = new HashMap<V, Digrafo<V>.Vertice>();
	
	private int totalVertices = 0, totalArestas = 0;

	/**
	 * Checa se está vazio
	 * 
	 * @return <i>true</i> se está vazio, caso constrário <i>false</i> 
	 */
	@Override
	public boolean estaVazio() {
		return totalVertices == 0;
	}

	/**
	 * Retorna o total de vértices
	 * @return Totol de vértices
	 */
	@Override
	public int obterTotalVertices() {
		return totalVertices;
	}

	/**
	 * Retorna o total de arestas
	 * @return Total de arestas
	 */	
	@Override
	public int obterTotalArestas() {
		return totalArestas;
	}

	/**
	 * Adiciona um vértice
	 * 
	 * @param vertice Vértice
	 * @return <i>true</i> se foi adicionado com sucesso, ou seja, se não há a dada aresta já adicionada. Caso contrário, <i>false</i>
	 */
	@Override
	public boolean addVertice(V vertice) {
		if (!grafo.containsKey(vertice)) {
			grafo.put(vertice, new Vertice(vertice));
			totalVertices++;
			return true;
			
		} else {
			return false;
		}
	}

	/**
	 * Dado um vértice de origem, vértice de destino e um peso, adiciona uma aresta
	 * @param origem Vértice de origem
	 * @param destino Vértice de destino
	 * @return <i>true</i> se a aresta foi adicionada com sucesso sendo que ela não existe. Caso constrário, <i>false</i>
	 */
	@Override
	public boolean addAresta(V origem, V destino, Double peso) {
		if (grafo.containsKey(origem) && grafo.containsKey(destino)) {
			Vertice v = grafo.get(origem);
			v.addAresta(destino, peso);
			totalArestas++;
			return true;
			
		} else {
			return false;
		}
	}

	/**
	 * Dado uma chave, remove um vértice
	 * 
	 * @param vertice Vértice chave
	 * @return Vértice removido, se existir. Caso contrário, retorna <i>null</i>
	 */
	@Override
	public V removerVertice(V vertice) {
		if (!grafo.containsKey(vertice)) {
			return null;
			
		} else {
			// Remove sua(s) aresta(s) de incidência
			Iterator<V> it = iteradorVertices();
			while (it.hasNext()) {
				V v = it.next();
				removerAresta(v, vertice);
				removerAresta(vertice, v);
			}
			// Finalmente remove o vértice solicitado e suas arestas de adjacência
			Vertice ret = grafo.remove(vertice);
			totalVertices--;
			return ret.chave;
		}
	}

	/**
	 * Dado um vértice de origem e destino, remove a aresta representada
	 * @param origem Vértice de origem
	 * @param destino Vértice de destino
	 * @return Retorna a aresta removida
	 */
	@Override
	public ArestaDirigida removerAresta(V origem, V destino) {
		if (grafo.containsKey(origem) && grafo.containsKey(destino)) {
			ArestaDirigida ret = grafo.get(origem).arestasAdjacentes.remove(destino);
			if (ret != null) {
				--totalArestas; 
			}
			return ret;
			
		} else {
			return null;
		}
	}

	/**
	 * Retorna uma lista de todos os caminhos (representados por {@link br.uefs.ecomp.brt.model.collection.ArestaDirigida ArestaDirigida}) sendo estes distintos e <br>
	 *  não contendo vértices repetidos
	 *  
	 *  @param origem Vértice de origem
	 *  @param destino Vértice de destino
	 */
	@Override
	public List<Queue<ArestaDirigida>> obterTodosCaminhos(V origem, V destino) {
		if (!(grafo.containsKey(origem) && grafo.containsKey(destino))) {
			throw new IllegalArgumentException("Vértice de origem e/ou destino inexistente(s)");
		}
		
		List<Queue<ArestaDirigida>> lista = new ArrayList<Queue<ArestaDirigida>>();
		int queueIndex = 0;
		Vertice s = grafo.get(origem), d = grafo.get(destino);
		// Fila de visitados, usada para acumular caminhos anteriormente visitados
		LinkedList<ArestaDirigida> queueVisitado = new LinkedList<ArestaDirigida>();
		Iterator<Vertice> it = grafo.values().iterator();
		while (it.hasNext()) {
			Vertice n = it.next();
			n.flag = Flag.NVISITADO;
		}
		obterTodosCaminhos(lista, s, d, null, queueVisitado, queueIndex);
		return lista;
	}

	private void obterTodosCaminhos(List<Queue<ArestaDirigida>> lista, Vertice s, 
			Vertice d, ArestaDirigida aresta, LinkedList<ArestaDirigida> queueVisitado, int queueIndex) {
		s.flag = Flag.VISITADO;
		if (aresta != null) queueVisitado.add(queueIndex++, aresta);
		
		if (s.chave == d.chave) {			// Condição de parada da recursão
			
			LinkedList<ArestaDirigida> q = new LinkedList<>();
			Iterator<ArestaDirigida> it = queueVisitado.iterator();
			while (it.hasNext()) {
				ArestaDirigida z = it.next();
				q.add(z);
			}
			lista.add(new LinkedList<>(q));
			
		} else { 							// Base de recursão
			Iterator<ArestaDirigida> it = s.arestasAdjacentes.values().iterator();
			while (it.hasNext()) {
				ArestaDirigida a = it.next();
				Vertice v = grafo.get(a.getDestino());
				if (v.flag == Flag.NVISITADO) {
					obterTodosCaminhos(lista, v, d, a, queueVisitado, queueIndex);
				}
			}
		}
		s.flag = Flag.NVISITADO;
		if (aresta != null) queueVisitado.remove(--queueIndex);
	}
	
	/**
	 * Retorna um iterador de vértices
	 */
	@Override
	public Iterator<V> iteradorVertices() {
		return grafo.keySet().iterator();
	}

	/**
	 * Retorna um iterador de arestas de adjacência
	 * 
	 * @param vertice Vértice
	 */
	@Override
	public Iterator<ArestaDirigida> iteradorArestas(V vertice) {
		return new IteradorArestas(vertice);
	}

	/**
	 * @param origem Vértice de origem
	 * @param destino Vértice de destino
	 * @return Iterador de
	 */
	@Override
	public Queue<ArestaDirigida> obterMenorCaminho(V origem, V destino)
			throws IllegalArgumentException {
		if (!(grafo.containsKey(origem) && grafo.containsKey(destino))) {
			throw new IllegalArgumentException("Vértice de origem e/ou destino inexistente(s)");
		}
		
		PriorityQueue<Vertice> prQueue = new PriorityQueue<>(20, new VerticeComparator());
		Iterator<Vertice> it = grafo.values().iterator();
		// Inicialização das distâncias parciais, vértices pai,
		//	transposição dos vértices para uma e lista de prioridade mínima
		while (it.hasNext()) {
			Vertice n = it.next();
			n.d = Double.POSITIVE_INFINITY;
			n.parent = null;
			n.pinc = 0.0;
			prQueue.add(n);
		}
		
		Vertice u = grafo.get(origem); // Obtém vértice de origem
		u.d = 0.0; 					   // Garante o invariante de laço
		Vertice v = null;
		while (!prQueue.isEmpty()) {
			u = prQueue.poll();
			Iterator<V> itv = u.arestasAdjacentes.keySet().iterator();
			while (itv.hasNext()) {    // Itera sobre os vértices adjacentes ao vértice u 
				v = grafo.get(itv.next());
				ArestaDirigida a = u.arestasAdjacentes.get(v.chave); 
				if (u.d + a.getPeso() < v.d) {
					v.d = u.d + a.getPeso();
					v.parent = u;
					v.pinc = a.getPeso();
				}
			}
		}
		
		LinkedList<ArestaDirigida> ret = new LinkedList<>();
		u = grafo.get(destino);
		while (u != null) {
			if (u.parent != null) {
				ret.addFirst(new ArestaDirigida(u.parent.chave, u.chave, u.pinc));
			}
			u = u.parent;
		}
		return ret;
	}

	@Override
	public V obterMaiorVerticeIncidente() {
		Vertice ret = null;
		Iterator<Vertice> it = grafo.values().iterator();
		if (it.hasNext()) ret = it.next();
		while (it.hasNext()) {
			Vertice v = it.next();
			if (ret.contadorIncidencia < v.contadorIncidencia) {
				ret = v;
			}
		}
		return (ret != null) ? ret.chave : null;
	}
	
	private class IteradorArestas implements Iterator<ArestaDirigida> {
		
		private Iterator<ArestaDirigida> it = null;
		
		public IteradorArestas(V vertice) {
			if (grafo.containsKey(vertice)) {
				it = grafo.get(vertice).arestasAdjacentes.values().iterator();
			}	
		}

		@Override
		public boolean hasNext() {
			return (it != null && it.hasNext());
		}

		@Override
		public ArestaDirigida next() {
			return (it != null) ? it.next() : null;
		}

		@Override
		public void remove() { }
	}

	@Override
	public void apagar() {
		grafo.clear();
		totalVertices = 0;
		totalArestas = 0;
	} 
}
