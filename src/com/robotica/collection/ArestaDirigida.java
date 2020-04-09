package com.robotica.collection;

/**
 * Essa classe serve de model para a classe {@link br.uefs.ecomp.brt.model.collection.ArestaDirigida ArestaDirigida }. <br>
 * Sua função consiste em conter o peso da aresta e seu vértice de destino
 * 
 *
 * @author Francisco A. A. Ferreira Jr.
 *
 * @param <E>
 */
public class ArestaDirigida<E> {
	private Double peso;
	private E origem, destino;

	public ArestaDirigida(E origem, E destino) {
		this.origem = origem;
		this.destino = destino;
	}
	
	public ArestaDirigida(E origem, E destino, Double peso) {
		this.origem = origem;
		this.destino = destino;
		this.peso = peso;
	}

	/**
	 * Retorna seu peso
	 * @return Peso da aresta
	 */
	public Double getPeso() {
		return peso;
	}

	/**
	 * Retorna seu vértie de origem
	 * 
	 * @return Vértice de origem
	 */
	public E getOrigem() {
		return origem;
	}
	
	/**
	 * Retorna seu vértie de destino
	 * 
	 * @return Vértice de destino
	 */
	public E getDestino() {
		return destino;
	}

	@Override
	public boolean equals(Object obj) {
		ArestaDirigida<E> a = (ArestaDirigida<E>) obj;
		return (this.origem.equals(a.origem) && this.destino.equals(a.destino));
	}

	@Override
	public String toString() {
		return "ArestaDirigida [peso=" + peso + ", origem=" + origem
				+ ", destino=" + destino + "]";
	}
}
