package com.github.technus.runtimeDoc.fx;

import com.github.technus.runtimeDoc.AnnotatedElementDocumentation;
import com.github.technus.runtimeDoc.accessibleObject.executable.method.MethodAnnotation;
import com.github.technus.runtimeDoc.accessibleObject.field.FieldAnnotation;
import com.github.technus.runtimeDoc.parameter.ParameterAnnotation;
import com.github.technus.runtimeDoc.type.ClassDocumentation;
import com.github.technus.runtimeDoc.type.TypeAnnotation;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.AnchorPane;

import java.net.URL;
import java.util.ResourceBundle;

@TypeAnnotation
public class DocumentationController implements Initializable {
    @FieldAnnotation
    public AnchorPane elementAnchor;
    public TreeTableView<AnnotatedElementDocumentation> documentationTree;
    public TreeTableColumn<AnnotatedElementDocumentation,String> nameColumn;
    public TreeTableColumn<AnnotatedElementDocumentation,Class> typeColumn;


    @Override
    @MethodAnnotation
    @SuppressWarnings("unchecked")
    public void initialize(@ParameterAnnotation(name = "location") URL location, ResourceBundle resources) {
        TreeItem<AnnotatedElementDocumentation> root=new TreeItem<>();
        root.setExpanded(true);

        root.getChildren().setAll(new ClassDocumentation(getClass()).buildTreeRoot());

        nameColumn.setCellValueFactory(data-> new ReadOnlyStringWrapper(data.getValue().getValue().getName()));
        typeColumn.setCellValueFactory(data-> new ReadOnlyObjectWrapper<>(data.getValue().getValue().getClass()));

        documentationTree.setShowRoot(false);
        documentationTree.setRoot(root);
    }
}
