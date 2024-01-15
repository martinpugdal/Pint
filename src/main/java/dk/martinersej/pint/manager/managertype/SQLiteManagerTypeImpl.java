package dk.martinersej.pint.manager.managertype;

import dk.martinersej.pint.manager.DatabaseConnectionManager;
import dk.martinersej.pint.manager.ManagerType;

public abstract class SQLiteManagerTypeImpl implements ManagerType.SQLiteManagerType {

    private final DatabaseConnectionManager databaseConnectionManager;
    private String[] tables;

    public SQLiteManagerTypeImpl(DatabaseConnectionManager databaseConnectionManager) {
        this.databaseConnectionManager = databaseConnectionManager;
    }

    public DatabaseConnectionManager getDatabase() {
        return databaseConnectionManager;
    }

    public String[] getTables() {
        return tables;
    }

    public void setTables(String[] tables) {
        this.tables = tables;
    }
}
