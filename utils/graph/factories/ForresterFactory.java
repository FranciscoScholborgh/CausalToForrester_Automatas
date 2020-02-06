/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils.graph.factories;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import org.netbeans.api.visual.anchor.AnchorFactory;
import org.netbeans.api.visual.anchor.AnchorShape;
import org.netbeans.api.visual.anchor.PointShape;
import org.netbeans.api.visual.border.BorderFactory;
import org.netbeans.api.visual.layout.LayoutFactory;
import org.netbeans.api.visual.widget.ConnectionWidget;
import org.netbeans.api.visual.widget.ImageWidget;
import org.netbeans.api.visual.widget.LabelWidget;
import org.netbeans.api.visual.widget.Scene;
import org.netbeans.api.visual.widget.Widget;
import org.openide.util.Utilities;

/**
 *
 * @author frank
 */
public class ForresterFactory {
    
    private final Map<String, Integer> point;
        
        public ForresterFactory() {
            this.point = new HashMap<>();
            this.point.put("x", 100);
            this.point.put("y", 100);
        }
                
        public LabelWidget create_variable(Scene scene, String variable, Point coordinates) {
            LabelWidget widget = new LabelWidget (scene, variable);
            int x = (int) coordinates.getX();
            int y = (int) coordinates.getY();
            //this.point.replace("x", y+140);
            widget.setPreferredLocation(new Point (x, y));
            return widget;
        }

        public LabelWidget create_levelVar(Scene scene, String variable, Point coordinates) {
            LabelWidget widget = new LabelWidget (scene, variable);
            widget.setBorder (BorderFactory.createLineBorder (4));
            int x = (int) coordinates.getX();
            int y = (int) coordinates.getY();
            //this.point.replace("x", y+140);
            widget.setPreferredLocation(new Point (x, y));
            return widget;
        }
        
        public Widget create_suplly(Scene scene, Point coordinates) {
            Widget iconNode = new Widget (scene);
            iconNode.addChild (new ImageWidget (scene, Utilities.loadImage ("/icons/nube.png")));
            iconNode.addChild (new LabelWidget (scene, ""));
            int x = (int) coordinates.getX();
            int y = (int) coordinates.getY();
            iconNode.setPreferredLocation(new Point (x, y));
            return iconNode;
        }
        
        public ConnectionWidget regular_conecction(Scene scene, Widget source, Widget target) {
            ConnectionWidget connection = new ConnectionWidget (scene);
            connection.setSourceAnchor (AnchorFactory.createRectangularAnchor (source));
            connection.setTargetAnchor (AnchorFactory.createRectangularAnchor (target));
            connection.setTargetAnchorShape (AnchorShape.TRIANGLE_FILLED);
            connection.setPaintControlPoints (true);
            connection.setControlPointShape (PointShape.SQUARE_FILLED_BIG);
            return connection;
        }
        
        public ConnectionWidget flow_conecction(Scene scene, String flujo, Widget source, Widget target) {
            ConnectionWidget connection = new ConnectionWidget (scene);
            connection.setSourceAnchor (AnchorFactory.createRectangularAnchor (source));
            connection.setTargetAnchor (AnchorFactory.createRectangularAnchor (target));
            connection.setTargetAnchorShape (AnchorShape.TRIANGLE_FILLED);
            connection.setPaintControlPoints (true);
            connection.setControlPointShape (PointShape.SQUARE_FILLED_BIG);
            
            Widget iconNode = new Widget (scene);
            //iconNode.setLayout (LayoutFactory.createVerticalFlowLayout (LayoutFactory.SerialAlignment.CENTER, 4));
            iconNode.addChild (new ImageWidget (scene, Utilities.loadImage ("/icons/reloj-de-arena.png")));
            iconNode.addChild (new LabelWidget (scene, flujo));
            connection.addChild (iconNode);
            //connection.setConstraint (iconNode, LayoutFactory.ConnectionWidgetLayoutAlignment.BOTTOM_LEFT, -25);
            connection.setConstraint (iconNode, LayoutFactory.ConnectionWidgetLayoutAlignment.CENTER, 0.5f);
            return connection;
        }

}
