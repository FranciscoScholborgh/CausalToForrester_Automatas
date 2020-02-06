/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils.graph.factories;

/**
 *
 * @author frank
 */
public class ForresterDiagramFactory implements DiagramFactory{

    @Override
    public DiagramViewer createDiagram() {
        return new ForresterDiagramViewer();
    }
    
    
}
