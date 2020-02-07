/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import vista.LoopEdgeTest;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import org.netbeans.api.visual.action.ActionFactory;
import org.netbeans.api.visual.action.WidgetAction;
import org.netbeans.api.visual.anchor.Anchor;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.api.visual.router.Router;
import org.netbeans.api.visual.router.RouterFactory;
import org.netbeans.api.visual.vmd.VMDNodeAnchor;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.LayerWidget;
import org.netbeans.api.visual.widget.Widget;
import org.netbeans.modules.visual.border.EmptyBorder;
import utils.graph.factories.ForresterFactory;
import vista.ForresterViewer;

/**
 *
 * @author frank
 */
public class ForresterViewerController {
    
    private final ForresterViewer viewer;

    public ForresterViewerController(ForresterViewer viewer) {
        this.viewer = viewer;
    }
    
    public void showEcuations(Map<Integer, String> variables, String[] ecuations) {
        this.viewer.write_ecuations(variables, ecuations);
    }
    
    public void showDiagram(String [][] matrix, Map<Integer, String> variables) {

        ForresterFactory factory = new ForresterFactory();
        //Scene scene = new Scene ();
        
        LoopEdgeTest scene = new LoopEdgeTest ();

        LayerWidget mainLayer = scene.getMainLayer();
        
        LayerWidget connectionLayer = scene.getConnectionLayer();
        
        LayerWidget interractionLayer = new LayerWidget (scene);
        scene.addChild(interractionLayer);
        
        ArrayList<String> elementos = new ArrayList<>(variables.values());
        
         SwingUtilities.invokeLater(() -> {
            JComponent sceneView = scene.createView();
            int length = viewer.getViewer().getViewport().getComponents().length;
            if(length != 0) {
                viewer.getViewer().getViewport().removeAll();
            }
            viewer.getViewer().getViewport().add(sceneView);
            viewer.getViewer().getHorizontalScrollBar().setUnitIncrement (32);
            viewer.getViewer().getHorizontalScrollBar().setBlockIncrement (256);
            viewer.getViewer().getVerticalScrollBar().setUnitIncrement (32);
            viewer.getViewer().getVerticalScrollBar().setBlockIncrement (256);
        });
         viewer.setVisible(true);
         
        Map<Integer, Widget> elements = new HashMap<>();
        HashMap<Integer, Anchor> anchors  = new HashMap<>();
        
        WidgetAction moveAction = ActionFactory.createAlignWithMoveAction (mainLayer, interractionLayer, null);
        for (int i = 0; i < matrix.length; i++) {
            String type = matrix[i][i];
            int pos = i*100;
            if(type.contains("P") || type.contains("V")) {
                LabelWidget widget = factory.create_variable(scene, elementos.get(i), new Point(pos, pos));
                widget.getActions().addAction(moveAction);
                elements.put(i, widget);
                anchors.put(i, new VMDNodeAnchor (widget));
                mainLayer.addChild (widget);
            } else if(type.contains("R")) {/*  //crea elementos
                Widget iconNode = new Widget (scene);
                iconNode.addChild (new ImageWidget (scene, Utilities.loadImage ("/icon/reloj-de-arena.png")));
                iconNode.addChild (new LabelWidget (scene, elementos.get(i)));
                elements.put(i, iconNode);
                mainLayer.addChild (iconNode);*/

            } else if (type.contains("X")) {
                LabelWidget widget = factory.create_levelVar(scene, elementos.get(i), new Point(pos, pos));
                widget.getActions().addAction(moveAction);
                elements.put(i, widget);
                anchors.put(i, new VMDNodeAnchor (widget));
                mainLayer.addChild (widget);
            }
        }
        
        ArrayList<Widget> flow = new ArrayList<>();
        for (int diag = 0; diag < matrix.length; diag++) {
            String cell = matrix[diag][diag];
            if(cell.contains("R")) {
                ArrayList<Integer> inComing = new ArrayList<>();
                ArrayList<Integer> outComing = new ArrayList<>();
                for (int column = 0; column < matrix.length; column++) {
                    if(matrix[diag][column].equals("-F")) {
                        inComing.add(column);
                    } else if(matrix[diag][column].equals("F")) {
                        outComing.add(column);
                    }
                }
                int inSize = inComing.size();
                int outSize = outComing.size();
                System.out.println("in: " + inSize + " out: " + outSize );
                if(!(inSize > 1 ||  outSize> 1)) {
                    Point coordinates;
                    Widget source;
                    Widget target;
                    if(inSize != 0) {
                        source = elements.get(inComing.get(0));
                        coordinates = source.getPreferredLocation();
                        if(outSize == 0) {
                            Point point = new Point((int) (coordinates.getX() + 350), (int) coordinates.getY());
                            target = factory.create_suplly(scene, coordinates);
                            target.setPreferredLocation(point);
                            target.getActions().addAction(moveAction);
                            mainLayer.addChild (target);
                        } else {
                            target = elements.get(outComing.get(0));
                        }
                        ConnectionWidget connect = factory.flow_conecction(scene, elementos.get(diag), source, target);
                        connectionLayer.addChild(connect);
                        elements.put(diag, connect.getChildren().get(0));
                        anchors.put(diag, new VMDNodeAnchor (connect.getChildren().get(0).getChildren().get(0)));
                        flow.add(connect.getChildren().get(0));
                                
                    } else {
                        target = elements.get(outComing.get(0));
                        coordinates = target.getPreferredLocation();
                        
                        if(inSize == 0) {
                            Point point = new Point((int) (coordinates.getX() - 350 ), (int) coordinates.getY() );
                            source = factory.create_suplly(scene, coordinates);
                            source.setPreferredLocation(point);
                            source.getActions().addAction(moveAction);
                            mainLayer.addChild (source);
                        } else {
                            source = elements.get(inComing.get(0));
                        }
                        ConnectionWidget connect = factory.flow_conecction(scene, elementos.get(diag), source, target);
                        connectionLayer.addChild(connect);
                        elements.put(diag, connect.getChildren().get(0));
                        anchors.put(diag, new VMDNodeAnchor (connect.getChildren().get(0).getChildren().get(0)));
                        flow.add(connect.getChildren().get(0));
                    }
                }
            }
        }
        
        Router router = RouterFactory.createOrthogonalSearchRouter (mainLayer, connectionLayer);
        for (int row = 0; row < matrix.length; row++) {
            Widget sourceWidget = elements.get(row);
            for (int column = 0; column < matrix.length; column++) {
                String cell = matrix[row][column];
                if(row != column && !cell.equals("")){
                    if(cell.contains("I")) {
                        Widget targetWidget = elements.get(column);
                        ConnectionWidget connection;
                        if(!(sourceWidget.getBorder() instanceof EmptyBorder) && flow.contains(targetWidget)){
                            connection = new ConnectionWidget (scene);
                            connection.setRouter (router);
                            connection.setTargetAnchorShape (AnchorShape.TRIANGLE_FILLED);
                            //connection.setRouter(RouterFactory.createDirectRouter());
                            connection.setSourceAnchor(anchors.get (row));
                            connection.setTargetAnchor(anchors.get (column));
                            
                        } else {
                            connection = factory.regular_conecction(scene, sourceWidget, targetWidget);
                            //connection.setRouter(RouterFactory.createOrthogonalSearchRouter (mainLayer, connectionLayer));
                        }
                        connectionLayer.addChild(connection);
                    } 
                }
            }
        }
    }
}
