/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils.graph;

import utils.graph.factories.CasualDiagramFactory;
import utils.graph.factories.CausalDiagramEditor;
import utils.graph.factories.DiagramFactory;

/**
 *
 * @author frank
 */
public class DiagramCreator {
    
    public static CausalDiagramEditor create_CausaDiagramEditor() {
        DiagramFactory factory = new CasualDiagramFactory();
        CausalDiagramEditor causalEditor = (CausalDiagramEditor) factory.createDiagram();
        return causalEditor;
    }
}
