# Trajemin - Planejador de Trajetória Miníma para Robôs Móveis

[![Build Status](https://travis-ci.org/joemccann/dillinger.svg?branch=master)](https://travis-ci.org/joemccann/dillinger)

![Trajemin](https://raw.githubusercontent.com/Franzx41/trajemin/master/img/trajemin_img.png)

# Descrição
Trajemin é um planejador offline de trajetória miníma para robôs móveis baseado em grafo de visibilidade. Ele dispõe de uma interface gráfica capaz de: 
- Adicionar pontos de origem e destino por cliques no espaço de configuração virtual;
- Adicionar obstáculos poligononais côncavos ou convexos por cliques, atribuindo seus pontos no espaço de configuração virtual;
- Configurar o raio do robô considerando que este esteja inscrito em uma circuferência que delimite suas dimensões;
- Visualizar todas as possíveis trajetórias;
- Buscar trajetória mínima por [algoritmo A*](https://en.wikipedia.org/wiki/A*_search_algorithm);

### Sumário

  - Instalação e Execução
  - Compilação (Opcional)
  - Manual de Uso
  - Descrição Teórica do Sistema
    1) Aquisição e Pré-processamento de Pontos
    2) Triangulação Delaunay
    2) Concatenação de Triângulos Sobrepostos por Mesmos Obstáculos
    3) Construção da Lista de Segmentos
    4) Construção do Grafo de Visibilidade
    5) Busca por Menor Trajetória

### Instalação e Execução

Requer apenas JavaSE 1.7 ou superior.

Execute os seguintes comandos em um terminal Linux(recomendado), Mac OSX ou Windows:

```sh
$ git clone https://github.com/Franzx41/trajemin.git
$ cd trajemin
$ java -jar trajemin.jar
```

### Compilação (Opcional)

Para compilar o projeto é necessário a IDE Eclipse 3.8.1 ou superior.
Abra o Eclipe e em File > Import. Selecione "Existing Projects into Workspace" e seguida clique em Browse para selecionar a pasta 'trajemin' do projeto. Por fim, execute "Run" para compilar e executar o projeto.

### Manual de Uso
![Opções](https://raw.githubusercontent.com/Franzx41/trajemin/master/img/trajemin_bts.png)
Utilize o formulário "Raio do Robô" para inserir um valor em ponto flutuante que represente o raio de um robô em pixels. 
Clique no botão "+ Origem" ou "+ Destino" e em seguida clique em algum lugar da janela para adicionar os pontos de origem e destino do robô.
Para adicionar um obstáculo, clique no botão "+ Obstáculos" e em seguida clique sequênciamente em um único sentido para determinar o formato e a posição do obstáculo desejado. Clique em "Salvar" para salvar e mostrar o obstáculo na tela.
Clicando em "Resultado", todas as trajetórias possíveis são gerados e a menor trajetória é encontrada.
Cliando em "Limpar Tudo", o sistema apaga tudo e permite um novo planejamento de trajetória
### Descrição Teórica do Sistema

### 1) Aquisição e Pré-processamento de Pontos
O Sistema [adquire e salva](https://github.com/Franzx41/trajemin/blob/ee22b862ca2c79125f744121b46a3549a8d06651/src/com/robotica/Main.java#L215) todos os pontos(em unidade de pixel) dos conjuntos de obstáculos aplicando uma [transformação escala](https://github.com/Franzx41/trajemin/blob/ee22b862ca2c79125f744121b46a3549a8d06651/src/com/robotica/Poligono.java#L26) utilizando o centro de cada obstáculo com seu ponto pivô. Tal transformação translada o centro do objeto para origem do sistema, escala e novamente translada para o seu ponto inicial.
Ele também adquire os pontos de origem, destino e os pontos que limitam o espaço de configuração. Dado isso, ele controi uma lista de pontos para a etapa seguinte. 
### 2) Triangulação Delaunay
Para descubrir todas as trajetórias possíveis, o sistema usa um algoritmo de [triangulação Delaunay](https://github.com/Franzx41/trajemin/blob/ee22b862ca2c79125f744121b46a3549a8d06651/src/com/robotica/Main.java#L240) em cima dos pontos gerados na etapa anterior. Tal implementação utilizou a bibliteca DelaunayTriangulator. 
Uma [lista de triângulos](https://github.com/Franzx41/trajemin/blob/ee22b862ca2c79125f744121b46a3549a8d06651/src/com/robotica/Main.java#L245) foi geradarada para etapa seguinte.
### 3) Concatenação de Triângulos Sobrepostos por Mesmos Obstáculos
Desejando que não tenha trajetórias passando pelo interior dos obstáculos, foi feita uma [busca por todos os grupos de triângulos que intersectam o mesmo polígono obstáculo](https://github.com/Franzx41/trajemin/blob/ee22b862ca2c79125f744121b46a3549a8d06651/src/com/robotica/Main.java#L255) e posteriormente a concatenação de cada grupo. A [concatenação dos triângulos](https://github.com/Franzx41/trajemin/blob/ee22b862ca2c79125f744121b46a3549a8d06651/src/com/robotica/Main.java#L272) é feita utilizando a lista de seus pontos e um algoritmo para encontrar o seu fecho convexo(polígono que contenha todos os pontos no seu interior).
### 4) Construção da Lista de Segmentos
É criado uma lista de segmentos totais concatenando os segmentos dos polígonos dos [fechos convexos](https://github.com/Franzx41/trajemin/blob/ee22b862ca2c79125f744121b46a3549a8d06651/src/com/robotica/Main.java#L268) encontrados juntamente com os triângulos que não intersectam nenhum obstáculo. Tal lista de segmentos é mostrada na tela descrevendo visualmente todos os possíveis trajetórias encontradas.
### 5) Construção do Grafo de Visibilidade
Um [grafo de visibilidade](https://github.com/Franzx41/trajemin/blob/ee22b862ca2c79125f744121b46a3549a8d06651/src/com/robotica/Main.java#L281) é então construido a partir da lista de segmentos gerados na etapa anterior. O grafo é não-dirigido, planar(conseguência da triangulação) e ponderado, tendo suas arestas com as distâncias euclidianas entre cada ponto de um segmento. Tal implementação utilizou a biblioteca JGraphT.
### 6) Busca por Menor Trajetória
Por fim, foi implementado a [busca por menor trajetória](https://github.com/Franzx41/trajemin/blob/ee22b862ca2c79125f744121b46a3549a8d06651/src/com/robotica/Main.java#L291) utilizando um [algoritmo A*](https://en.wikipedia.org/wiki/A*_search_algorithm) e um algoritmo de [Dijkstra](https://en.wikipedia.org/wiki/Dijkstra%27s_algorithm). Optou-se no fim pelo algoritmo A* por ser mais eficiente em tempo e pelo seu grande uso nesse tipo de problema. 

### TODO

 - Teste completo de interface de usuário

Licença
----

MIT
