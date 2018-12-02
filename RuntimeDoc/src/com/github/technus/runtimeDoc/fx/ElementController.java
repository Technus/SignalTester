package com.github.technus.runtimeDoc.fx;

import com.github.technus.runtimeDoc.AnnotatedElementDocumentation;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

import java.net.URL;
import java.util.ResourceBundle;

public class ElementController implements Initializable {
    public SimpleObjectProperty<AnnotatedElementDocumentation> annotatedElementProperty =new SimpleObjectProperty<>();
    public TextArea textArea;

    public void initialize(URL location, ResourceBundle resources) {
        annotatedElementProperty.addListener((observable, oldValue, newValue) -> {
            String stringBuilder = newValue.getName() + '\n' +
                    newValue.getType() + '\n' +
                    newValue.getDocumentationType() + '\n' +
                    newValue.getDeclaration() + '\n' +
                    newValue.getDescription() + '\n';
            textArea.setText(stringBuilder);
        });
    }
}
