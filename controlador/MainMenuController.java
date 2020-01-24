/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controlador;

import com.jfoenix.controls.JFXButton;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Menu;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import utils.graph.DiagramCreator;
import utils.graph.factories.CausalDiagramEditor;

/**
 *
 * @author frank
 */
public class MainMenuController implements Initializable {
    
    
    @FXML
    private AnchorPane pane_stage;
            
    @FXML
    private Menu design_btn;
    
    @FXML
    private Menu import_btn;

    @FXML
    private JFXButton edit_btn;

    @FXML
    private JFXButton reset_btn;

    @FXML
    private JFXButton convert_btn;
    
    @FXML
    private JFXButton varopt_btn;

    @FXML
    private JFXButton addopt_btn;

    @FXML
    private JFXButton minusopt_btn;

    @FXML
    private JFXButton addloop_btn;

    @FXML
    private JFXButton minusloop_btn;
    
    @FXML
    private AnchorPane editor_viewer;
    
    private CausalDiagramEditor causalEditor;
    
     
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        this.causalEditor = DiagramCreator.create_CausaDiagramEditor();
        SwingUtilities.invokeLater(() -> {
            SwingNode swingNode = new SwingNode();
            JComponent sceneView = this.causalEditor.createView();
            JScrollPane panel = new JScrollPane (sceneView);
            panel.getHorizontalScrollBar().setUnitIncrement (32);
            panel.getHorizontalScrollBar().setBlockIncrement (256);
            panel.getVerticalScrollBar().setUnitIncrement (32);
            panel.getVerticalScrollBar().setBlockIncrement (256);
            swingNode.setContent(panel);
            Platform.runLater(() -> {
                editor_viewer.getChildren().add(swingNode);
                AnchorPane.setBottomAnchor(swingNode, 0.0);
                AnchorPane.setLeftAnchor(swingNode, 0.0);
                AnchorPane.setRightAnchor(swingNode, 0.0);
                AnchorPane.setTopAnchor(swingNode, 0.0);
            });
        });
        
        /*
        this.causalEditor = DiagramCreator.create_CausaDiagramEditor();
        SceneNode sceneView =  this.causalEditor.createView();
        ScrollPane scrollPane = new javafx.scene.control.ScrollPane(sceneView);
        scrollPane.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
	scrollPane.setStyle("-fx-focus-color: transparent;");
        
        scrollPane.widthProperty().addListener(e->{
            Platform.runLater(()->{
                sceneView.setWidth(scrollPane.getWidth());
                sceneView.draw();
            });
        });
        
        scrollPane.heightProperty().addListener(e->{
            Platform.runLater(()->{
                sceneView.setHeight(scrollPane.getHeight());
                sceneView.draw();
            });
        });
        
        editor_viewer.getChildren().add(scrollPane);
        AnchorPane.setBottomAnchor(scrollPane, 0.0);
        AnchorPane.setLeftAnchor(scrollPane, 0.0);
        AnchorPane.setRightAnchor(scrollPane, 0.0);
        AnchorPane.setTopAnchor(scrollPane, 0.0);*/
    }
    
    @FXML
    void casual_toForrester(ActionEvent event) {
        try {
            System.out.println("Convertir diagrama causal");
            Parent root = FXMLLoader.load(getClass().getResource("/vista/ForresterViewer.fxml"));
           
            Scene scene = new Scene(root);
            
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();      
        } catch (IOException ex) {
            Logger.getLogger(MainMenuController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    private void unlocked_elements() {
        this.varopt_btn.setDisable(false);
        this.addopt_btn.setDisable(false);
        this.minusopt_btn.setDisable(false);
        this.addloop_btn.setDisable(false);
        this.minusloop_btn.setDisable(false);
        this.edit_btn.setDisable(false);
    }
    
    @FXML
    void create_variable(ActionEvent event) {
        this.unlocked_elements();
        this.varopt_btn.setDisable(true);
        
    }
    
    @FXML
    void add_linktovariable(ActionEvent event) {
        this.unlocked_elements();
        this.addopt_btn.setDisable(true);  
    }
    
    @FXML
    void minus_linktovariable(ActionEvent event) {
        this.unlocked_elements();
        this.minusopt_btn.setDisable(true);
    }
    
    @FXML
    void create_addLoop(ActionEvent event) {
        this.unlocked_elements();
        this.addloop_btn.setDisable(true);
    }

    @FXML
    void create_minusLoop(ActionEvent event) {
        this.unlocked_elements();
        this.minusloop_btn.setDisable(true);
    }

    @FXML
    void delete_diagram(ActionEvent event) {
        Alert alert = new Alert(AlertType.CONFIRMATION, "¿Desea borrar los cambios realizados en el diagrama?", ButtonType.YES, ButtonType.NO); 
        alert.setHeaderText(null);
        alert.showAndWait();

        if (alert.getResult() == ButtonType.YES) {
            System.out.println("Llama funcion para limpiar pantalla");
        } else {
            System.out.println("No pasa nah xD");
        }
    }
    
    @FXML
    void edit_diagram(ActionEvent event) {
        this.unlocked_elements();
        this.edit_btn.setDisable(true);
    }

    @FXML
    void import_diagram(ActionEvent event) {
        System.out.println("Habemus pedum?");
        Stage window = (Stage) this.pane_stage.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importar diagrama causal");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("CSL", "*.csl")
            );
        fileChooser.showOpenDialog(window);
    }
    
}
