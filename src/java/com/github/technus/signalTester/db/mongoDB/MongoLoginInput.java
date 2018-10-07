package java.com.github.technus.signalTester.db.mongoDB;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MongoLoginInput extends JDialog {
    private JPanel contentPane;
    private JButton buttonOK;
    private JButton buttonCancel;
    private JTextField urlInput;
    private JTextField userInput;
    private JTextField passwordInput;
    private JTextField portInput;
    private JTextField databaseInput;
    private String[] strings;

    public MongoLoginInput() {
        setTitle("QPulse database login");
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(buttonOK);

        buttonOK.addActionListener(e -> onOK());

        buttonCancel.addActionListener(e -> onCancel());

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);

        pack();
        setVisible(true);
    }

    private void onOK() {
        strings=new String[]{urlInput.getText(),portInput.getText(),databaseInput.getText(),userInput.getText(),passwordInput.getText()};
        dispose();
    }

    private void onCancel() {
        dispose();
    }

    public String[] getStrings(){
        return strings;
    }
}
