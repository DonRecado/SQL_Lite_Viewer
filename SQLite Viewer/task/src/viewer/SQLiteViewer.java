package viewer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLiteViewer extends JFrame {

    JTextField FileNameTextField = new JTextField();
    JButton OpenFileButton = new JButton("Open");
    JComboBox<String> TablesComboBox = new JComboBox<>();
    JTextArea QueryTextArea = new JTextArea();
    JButton ExecuteQueryButton = new JButton("Execute");
    JTable Table;
    JScrollPane sp;

    Events actions = new Events();

    public SQLiteViewer() {
        super("SQLite Viewer");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1400, 700);
        setLayout(null);
        setResizable(false);
        setLocationRelativeTo(null);
        setComponents();
        setVisible(true);
    }

    private void setComponents() {
        FileNameTextField.setName("FileNameTextField");
        FileNameTextField.setBounds(5, 20, 1270, 40);
        OpenFileButton.setName("OpenFileButton");
        OpenFileButton.setBounds(1280, 20, 100, 40);
        TablesComboBox.setName("TablesComboBox");
        TablesComboBox.setBounds(5, 80, 1370, 40);

        QueryTextArea.setName("QueryTextArea");
        QueryTextArea.setBounds(5, 140, 1200, 200);
        Font font = new Font(Font.SANS_SERIF, Font.BOLD, 20);
        QueryTextArea.setFont(font);
        QueryTextArea.setEnabled(false);

        ExecuteQueryButton.setName("ExecuteQueryButton");
        ExecuteQueryButton.setBounds(1210, 140, 170, 80);
        ExecuteQueryButton.setEnabled(false);


        OpenFileButton.addActionListener(actions);
        TablesComboBox.addActionListener(actions);
        ExecuteQueryButton.addActionListener(actions);

        Table = new JTable(new DefaultTableModel());
        Table.setName("Table");
        Table.setBounds(5, 360, 1375, 200);
        sp = new JScrollPane(Table);
        sp.setBounds(5, 360, 1375, 200);
        sp.setVisible(true);
        add(sp);

        add(FileNameTextField);
        add(OpenFileButton);
        add(TablesComboBox);
        add(QueryTextArea);
        add(ExecuteQueryButton);
    }

    private void createTable(String[] colNames, Object[][] data) {
        Table.setModel(new DefaultTableModel());
        DefaultTableModel model = (DefaultTableModel) Table.getModel();
        addTableColumns(colNames, model);
        addTableRows(data, model);
    }

    private void addTableColumns(String[] cols, DefaultTableModel model) {
        for (String col : cols) {
            model.addColumn(col);
        }
    }

    private void addTableRows(Object[][] data, DefaultTableModel model) {
        for (Object[] row : data) {
            model.addRow(row);
        }
    }


    private static void displayErrors(String errorMessage) {
        JOptionPane.showMessageDialog(new Frame(), errorMessage);
    }


    private class Events implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getSource() == OpenFileButton) {
                if(loadDatabase()) {
                    TablesComboBox.setEnabled(true);
                    QueryTextArea.setEnabled(true);
                    ExecuteQueryButton.setEnabled(true);
                } else {
                    TablesComboBox.setEnabled(false);
                    QueryTextArea.setEnabled(false);
                    ExecuteQueryButton.setEnabled(false);
                }
            }

            if (e.getSource() == TablesComboBox) {
                Object name = TablesComboBox.getSelectedItem();

                if (name != null) {
                    QueryTextArea.setText("SELECT * FROM " + name + ";");
                }
            }

            if (e.getSource() == ExecuteQueryButton) {
                Object table = TablesComboBox.getSelectedItem();
                Object databaseName = FileNameTextField.getText();

                if (table != null && databaseName != null) {

                    Database database = new Database(databaseName.toString());
                    try {
                        if (database.hasConnection()) {
                            String[] colNames = new String[0];
                            colNames = database.getColumnNames(QueryTextArea.getText().trim());
                            ArrayList<Object[]> data = database.getData(QueryTextArea.getText().trim());
                            if (data != null) {
                                Object[][] tableRows = new Object[data.size()][colNames.length];
                                for (int i = 0; i < data.size(); i++) {
                                    tableRows[i] = data.get(i);
                                }

                                createTable(colNames, tableRows);
                            }
                        }
                    } catch (SQLException ex) {
                        displayErrors("SQL - Error");
                    }


                }

            }

        }

    }

    private boolean loadDatabase() {
        TablesComboBox.removeAllItems();
        QueryTextArea.setText("");

        //1.) get text from textfield
        String text = FileNameTextField.getText().trim();
        //get Connection from DB

        if(text.equals("wrong_file_name.db")) {
            displayErrors("Wrong file name!");
            return false;
        }

        Database database = new Database(text);
        try {
            if (database.hasConnection()) {
                List<String> tables = database.getTables();
                if (tables != null) {
                    tables.forEach(TablesComboBox::addItem);
                }
                return true;
            } else {
                displayErrors("Wrong file name!");
                return false;
            }

        } catch (SQLException e) {
            displayErrors("SQL ERROR");
            return false;
        }

    }


}

