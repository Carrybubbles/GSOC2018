package ru.nsu.fedin.app;


import org.jgrapht.Graph;
import org.jgrapht.alg.CycleDetector;
import org.jgrapht.alg.NaiveLcaFinder;
import org.jgrapht.graph.*;
import org.jgrapht.io.*;

import java.io.File;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class App
{

    static class MyEdge extends DefaultEdge {
        MyEdge(){}

    }

    static class MyVertex {
        String s;
        Map<String,Attribute> map;

        MyVertex(String s, Map<String, Attribute> map) {
            this.s = s;
            this.map = map;
        }

        String getVertexName(){
            if(map.containsKey("label")) {
                return map.get("label").toString();
            }else{
                return s;
            }
        }

    }

    public static void main(String[] args ) throws Exception {
        if(args.length != 3){
            throw new IllegalArgumentException("Need 3 arguments: file vertex1 vertex2");
        }else{
            String pathToFile = args[0];
            String inputVerA = args[1];
            String inputVerB = args[2];

            Graph<MyVertex,MyEdge> graph = new DirectedPseudograph<>(MyEdge.class);

            VertexProvider<MyVertex> vp = MyVertex::new;
            EdgeProvider<MyVertex,MyEdge> ep = (node, v1, s, map) -> new MyEdge();
            GraphImporter<MyVertex,MyEdge> importer  = new DOTImporter<>(vp,ep);
            importer.importGraph(graph,new File(pathToFile));

            CycleDetector<MyVertex, MyEdge> cycleDetector = new CycleDetector<>(graph);
            if(cycleDetector.detectCycles()){
                throw new Exception("This graph contains cycle");
            }

            MyVertex vertexA = graph.vertexSet().stream().
                    filter(n -> n.getVertexName().equals(inputVerA)).
                    findFirst().
                    orElseThrow(()-> new IllegalArgumentException(String.format("Graph doesn't contain %s vertex",inputVerA)));

            MyVertex vertexB = graph.vertexSet().stream().
                    filter(n -> n.getVertexName().equals(inputVerB)).
                    findFirst().
                    orElseThrow(()-> new IllegalArgumentException(String.format("Graph doesn't contain %s vertex",inputVerB)));

            NaiveLcaFinder<MyVertex,MyEdge> lcaFinder = new NaiveLcaFinder<>(graph);
            Set<MyVertex> vertices = lcaFinder.findLcas(vertexA,vertexB);
            if(vertices.isEmpty()){
                throw new Exception("There are't any lowest common ancestors in graph");
            }else{
                String result = vertices.stream().map(MyVertex::getVertexName).collect(Collectors.joining(","));
                System.out.println("LCA :" + result);
            }
        }


    }

}
