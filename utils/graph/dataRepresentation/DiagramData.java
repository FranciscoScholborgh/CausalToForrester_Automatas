/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils.graph.dataRepresentation;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author frank
 */
public class DiagramData {
    
    private final Map<Integer, String> nodes;
    
    private final int [][] matrix;

    public DiagramData(Map<Integer, String> nodes, int[][] matrix) {
        this.nodes = nodes;
        this.matrix = matrix;
    }

    public DiagramData(int size) {
        this.nodes = new HashMap<>();
        this.matrix = new int[size][size];
        for (int row = 0; row < size; row++) {
            for (int column = 0; column < size; column++) {
                this.matrix[row][column] = 0;
            }
            
        }
    }

    public Map<Integer, String> getNodes() {
        return nodes;
    }

    public int[][] getMatrix() {
        return matrix;
    }
    
    public boolean insert_node(Integer index, String value) {
        if(!this.nodes.containsKey(index)) {
            this.nodes.put(index, value);
            return true;
        } else {
            return false;
        }
    }
    
    public boolean insert_matrixRelation(int row, int column, int value) {
        if(value >= -1 || value <= 1) {
            this.matrix[row][column] = value;
            return true;
        } else {
            return false;
        }
    }
    
    
    
    
}
