package com.robotica.collection;

import java.util.Iterator;
import java.util.List;
import java.util.Queue;

import com.robotica.collection.*;

public interface IDigrafo<V> {
	public boolean estaVazio();
	public void apagar();
	public int obterTotalVertices();
	public int obterTotalArestas();
	public boolean addVertice(V vertice);
	public boolean addAresta(V origem, V destino, Double peso);
	public V removerVertice(V vertice);
	public ArestaDirigida removerAresta(V origem, V destino); 
	public V obterMaiorVerticeIncidente();
	public List<Queue<ArestaDirigida>> obterTodosCaminhos(V origem, V destino);
	public Queue<ArestaDirigida> obterMenorCaminho(V origem, V destino) throws IllegalArgumentException;
	public Iterator<V> iteradorVertices();
	public Iterator<ArestaDirigida> iteradorArestas(V vertice);
}

