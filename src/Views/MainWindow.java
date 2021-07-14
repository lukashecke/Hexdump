package Views;

import Models.HexDump;

import javax.swing.*;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

public class MainWindow extends JFrame {

    public static void main(String[] args) {
        new MainWindow();
    }

    // configuration
    private final Font textFont = new Font(Font.MONOSPACED, Font.PLAIN, 12);

    // declaration grphical elements
    private JTextArea textArea;
    private JTextField inputElementField;
    private JScrollPane textPane;
    private JFileChooser fileChooser;
    private JPanel taskBar;
    private JButton button;

    public MainWindow() {
        super();
        setTitle("Hex Dump :)");

        // graphical elements
        textArea = new JTextArea();
        textArea.setFont(textFont);
        textPane = new JScrollPane(textArea);
        fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
        // open file chooser with detail view
        //Action details = fileChooser.getActionMap().get("viewTypeDetails");
        //details.actionPerformed(null);
        button = new JButton("Datei auswählen");

        // input fields
        inputElementField = new JTextField(30);
        inputElementField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode()==KeyEvent.VK_ENTER) {
                    readData(inputElementField.getText());
                        clearInputElementField();

                }
            }
        });

        // task bar
        taskBar = new JPanel();
        taskBar.setLayout(new FlowLayout());
        taskBar.add(inputElementField);
        taskBar.add(button);
        button.addActionListener((e) -> openFileChooserAndReadFile());

        // container
        Container container = getContentPane();
        container.setLayout(new BorderLayout());

        container.add(taskBar, BorderLayout.NORTH);
        container.add(textPane, BorderLayout.CENTER);

        // Listener
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                exit();
            }
        });

        // show
        setBounds(0,0,580,400);
        setLocationRelativeTo(null); // center to screen
        setVisible(true);
    }

    private void openFileChooserAndReadFile() {
        int returnValue = fileChooser.showOpenDialog(null);

        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            readData(selectedFile.getAbsolutePath());
        }
    }

    /**
     * Creates hex dump object from filepath.
     * @param file filepath
     */
    private void readData(String file) {
        try {
            textArea.setText(new HexDump(file).getFormattedHexDump());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // help methods
    private void clearInputElementField() {
        inputElementField.setText("");
    }
    private void exit() {
        System.exit(0);
    }
}
