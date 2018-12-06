package com.github.technus.runtimeDoc.fx;

import com.github.technus.runtimeDoc.annotatedElement.AnnotatedElementDocumentation;
import com.github.technus.runtimeDoc.annotatedElement.accessibleObject.executable.method.MethodAnnotation;
import com.github.technus.runtimeDoc.annotatedElement.parameter.ParameterAnnotation;
import com.github.technus.runtimeDoc.annotatedElement.type.ClassDocumentation;
import com.github.technus.runtimeDoc.annotatedElement.type.TypeAnnotation;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;

import java.io.IOError;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

@TypeAnnotation
public class DocumentationController implements Initializable {
    public TreeTableView<AnnotatedElementDocumentation> documentationTree;
    public TreeTableColumn<AnnotatedElementDocumentation,String> nameColumn;
    public TreeTableColumn<AnnotatedElementDocumentation,String> typeColumn;
    public ScrollPane elementScroll;
    private ElementController elementController;

    @Override
    @MethodAnnotation
    public void initialize(@ParameterAnnotation(name = "location") URL location, ResourceBundle resources) {
        documentationTree.setShowRoot(true);
        documentationTree.setRoot(new ClassDocumentation(getClass()).buildTreeRoot());
        nameColumn.setCellValueFactory(data-> data.getValue().getValue() != null ?
                new ReadOnlyStringWrapper(data.getValue().getValue().getName()) : null);
        typeColumn.setCellValueFactory(data-> data.getValue().getValue() != null ?
                new ReadOnlyStringWrapper(data.getValue().getValue().getDocumentationType()) : null);
        try {
            FXMLLoader loader = new FXMLLoader(ElementController.class.getResource("Element.fxml"));
            Parent parent = loader.load();
            elementController=loader.getController();
            elementScroll.setContent(parent);
            documentationTree.getSelectionModel().selectedItemProperty().addListener(
                    (observable, oldValue, newValue) -> elementController.annotatedElementProperty.set(newValue.getValue()));
        }catch (IOException e){
            throw new IOError(e);
        }
    }

    @SuppressWarnings("unchecked")
    public void setRoot(ArrayList<AnnotatedElementDocumentation> docs){
        TreeItem<AnnotatedElementDocumentation> root=new TreeItem<>();
        for(AnnotatedElementDocumentation documentation:docs){
            TreeItem<AnnotatedElementDocumentation> elementDoc=documentation.buildTreeRoot();
            if(elementDoc!=null) {
                root.getChildren().add(elementDoc);
            }
        }
    }
}
