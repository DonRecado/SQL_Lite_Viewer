package viewer;

import org.sqlite.SQLiteDataSource;

import java.io.File;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public final class Database {
    private final SQLiteDataSource source;
    private final File file;


    public Database(String pathToDB) {
        this.source = new SQLiteDataSource();
        this.source.setUrl("jdbc:sqlite:" + pathToDB);
        this.file = new File(pathToDB);
    }

    public boolean hasConnection() throws SQLException {
        if(file.exists()) {

            Connection connection = source.getConnection();
            if (connection.isValid(5)) {
                return true;
            }
        }
        return false;
    }

    public List<String> getTables() {
        List<String> tables = new ArrayList<>();
        try (Connection connection = source.getConnection()) {
            DatabaseMetaData dmd = connection.getMetaData();
            String[] types = {"TABLE"};
            ResultSet rs = dmd.getTables(null, null, "%", types);
            while (rs.next()) {
                tables.add(rs.getString("TABLE_NAME"));
            }

        } catch (SQLException e) {
            System.out.println("Something went wrong");
            e.printStackTrace();
        } finally {
            if (tables.size() <= 0) {
                tables = null;
            }
        }
        return tables;
    }

    public String[] getColumnNames(String query) throws SQLException {
        String[] colNames = null;
        Connection connection = source.getConnection();
        if (query != null && !query.equals("")) {
            Statement statement = connection.createStatement();
            ResultSet rs = statement.executeQuery(query);
            ResultSetMetaData md = rs.getMetaData();
            int colCount = md.getColumnCount();
            colNames = new String[colCount];
            for (int i = 1; i <= colCount; i++) {
                colNames[i - 1] = md.getColumnName(i);
            }
        }
        return colNames;
    }

    public ArrayList<Object[]> getData(String query) {
        try (Connection connection = source.getConnection()) {
            if (query != null && !query.equals("")) {

                try (Statement statement = connection.createStatement()) {
                    ResultSet rs = statement.executeQuery(query);
                    ResultSetMetaData resultSetMetaData = rs.getMetaData();
                    ArrayList<Object[]> data = new ArrayList<>();
                    while (rs.next()) {
                        Object[] temp = new Object[resultSetMetaData.getColumnCount()];
                        for (int i = 1; i <= resultSetMetaData.getColumnCount(); i++) {
                            temp[i - 1] = rs.getString(i);
                        }
                        data.add(temp);
                    }
                    return data;
                } catch (SQLException e) {

                }
            }

        } catch (SQLException e) {
            System.out.println("sql connection");
        }
        return null;
    }
}
