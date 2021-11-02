package viewer;

import org.sqlite.SQLiteDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class ApplicationRunner {
    public static void main(String[] args) {
        new SQLiteViewer();
    }
}
