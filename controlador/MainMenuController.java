/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.SwingUtilities;
import modelo.Conversion;
import modelo.ValidacionCausal;
import utils.graph.DiagramCreator;
import utils.graph.dataRepresentation.DiagramData;
import utils.graph.factories.CausalDiagramEditor;
import vista.ForresterViewer;
import vista.VariablesView;

/**
 *
 * @author frank
 */
public class MainMenuController {
             
    private final JButton edit_btn;
    
    private final JButton delete_btn;

    private final JButton reset_btn;

    private final JToggleButton varopt_btn;

    private final JToggleButton addopt_btn;

    private final JToggleButton minusopt_btn;
  
    private final JScrollPane editor_viewer;
    
    private CausalDiagramEditor causalEditor;
    
    private final Map<String, Boolean> active_utils;
    
    private final VariablesViewController variablesViewer;
    
    private Conversion causalConvertion;

    public MainMenuController(JButton edit_btn, JButton delete_btn, JButton reset_btn, JToggleButton varopt_btn, JToggleButton addopt_btn, JToggleButton minusopt_btn, JScrollPane editor_viewer) {
        this.edit_btn = edit_btn;
        this.delete_btn = delete_btn;
        this.reset_btn = reset_btn;
        this.varopt_btn = varopt_btn;
        this.addopt_btn = addopt_btn;
        this.minusopt_btn = minusopt_btn;
        this.editor_viewer = editor_viewer;
        
        this.active_utils = new HashMap<>();
        this.active_utils.put("BIRD_VIEW", Boolean.FALSE);
        this.active_utils.put("ZOOM", Boolean.FALSE);
        
        this.variablesViewer = new VariablesView().getController();
        this.variablesViewer.setParent(this);
                
        this.create_newDiagramEditor();
    }
    
    private void create_newDiagramEditor() {
        this.causalEditor = DiagramCreator.create_CausaDiagramEditor();
        SwingUtilities.invokeLater(() -> {
            JComponent sceneView = this.causalEditor.createView();
            int length = this.editor_viewer.getViewport().getComponents().length;
            if(length != 0) {
                this.editor_viewer.getViewport().removeAll();
            }
            this.editor_viewer.getViewport().add(sceneView);
            this.editor_viewer.getHorizontalScrollBar().setUnitIncrement (32);
            this.editor_viewer.getHorizontalScrollBar().setBlockIncrement (256);
            this.editor_viewer.getVerticalScrollBar().setUnitIncrement (32);
            this.editor_viewer.getVerticalScrollBar().setBlockIncrement (256);
        });
    }
    
    private void unlocked_elements() {
        this.varopt_btn.setEnabled(true);
        this.addopt_btn.setEnabled(true);
        this.minusopt_btn.setEnabled(true);
        this.edit_btn.setEnabled(true);
        this.delete_btn.setEnabled(true);
    }
    
    public void create_variable() {
        this.unlocked_elements();
        this.varopt_btn.setEnabled(false);
        this.causalEditor.activate_insertVariable();
    }
    
    public void add_linktovariable() {
        this.unlocked_elements();
        this.addopt_btn.setEnabled(false);  
        this.causalEditor.activate_relationVariables("+");
    }
    
    public void minus_linktovariable() {
        this.unlocked_elements();
        this.minusopt_btn.setEnabled(false);
        this.causalEditor.activate_relationVariables("-");
    }
    
    public void edit_diagram() {
        this.unlocked_elements();
        this.edit_btn.setEnabled(false);
        this.causalEditor.deactivate_all();
    }
    
    public void birdView_diagram() {
        if(this.active_utils.get("BIRD_VIEW")) {
            this.causalEditor.enable_birdview(false);
            this.active_utils.replace("BIRD_VIEW", Boolean.FALSE);
        } else {
            this.causalEditor.enable_birdview(true);
            this.active_utils.replace("BIRD_VIEW", Boolean.TRUE);
        }
    }
    
    public void delete_nodeFromDiagram() {
        this.unlocked_elements();
        this.delete_btn.setEnabled(false);
        this.causalEditor.enable_deleteMode(true);
    }
    
    public void reset_diagram() {
        int answer = JOptionPane.showConfirmDialog(null, "¿Desea borrar los cambios realizados en el diagrama?", 
                    "Eliminar elemento", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        if (answer == JOptionPane.YES_OPTION) {
            this.create_newDiagramEditor();
            this.unlocked_elements();
        }
    }
    
    public void casual_toForrester() {
        DiagramData data =  this.causalEditor.getDiagramData();
        Map<Integer, String> nodes = data.getNodes();
        int[][] matrix = data.getMatrix();
        ValidacionCausal casualValidator = new ValidacionCausal(matrix.length);
        casualValidator.rellenar(matrix);
        
        boolean escausal = casualValidator.esCausal();
        if (escausal && nodes.size() > 0) {
            causalConvertion = new Conversion(matrix.length, matrix);
            causalConvertion.identificarParams();
            causalConvertion.identificarConexion("I");
            causalConvertion.identificarCiclosMinimos();
            causalConvertion.identificarConexion("F");
            causalConvertion.rellenarIFaltantes();
            causalConvertion.establecerRX();
            
            if(causalConvertion.estaCompleta()){
                //borrar en modo produccion
                causalConvertion.mostrarVariablesForrester();
                causalConvertion.calcularEcuaciones();
                this.show_ForresterDiagram(nodes);
                
            } else {
                ArrayList<String> variables = new ArrayList<>();
                String[][] tipos = causalConvertion.matrizTipos;    
                for (int i = 0; i < matrix.length; i++) {
                    if(tipos[i][i].equals(" ")) {
                        String var = nodes.get(i);
                        variables.add(var);
                    }
                }
                this.variablesViewer.loadViariables(variables, nodes);
                this.variablesViewer.show();
            }
        } else {
            System.out.println("Mensaje de pendejo tu usuario xD");
        }
        
        /*
        //Recibir la informacion para el procesamiento
        ArrayList<String> variables = new ArrayList<>();
        variables.add("Mortal Kombat X");
        variables.add("Mortal Kombat 11");
        this.variablesViewer.loadViariables(variables);
        this.variablesViewer.show();*/
    }
    
    protected void convertion_userPreset(ArrayList<String> preset, Map<Integer, String> nodes) {
        this.variablesViewer.hide();
        ArrayList<Integer> index = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>(nodes.values());
        System.out.println("map" + nodes.get(0));
        System.out.println("Array" + values.get(0));
        preset.forEach((var) -> {
            index.add(values.indexOf(var));
        });
        causalConvertion.procesarInformacion(index);
        causalConvertion.rellenarIFaltantes();
        causalConvertion.establecerRX();
        causalConvertion.calcularEcuaciones();
        
        this.show_ForresterDiagram(nodes);

    }
    
    private void show_ForresterDiagram(Map<Integer, String> nodes) {
        new ForresterViewer().getController().showDiagram(causalConvertion.matrizTipos, nodes);
    }
}
