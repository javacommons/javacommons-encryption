package com.github.javacommons.encryption;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.DatabaseConnection;
import static com.j256.ormlite.support.DatabaseConnection.DEFAULT_RESULT_FLAGS;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.concurrent.Callable;
import junit.framework.TestCase;
import org.junit.Test;

public class Test10_HSQLDB extends TestCase {

    @Override
    protected void setUp() throws Exception {
    }

    @Override
    protected void tearDown() throws Exception {
    }

    @Test
    public void test1() throws SQLException, IOException {
        String DATABASE_URL = "jdbc:h2:mem:test";
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(DATABASE_URL);

        doDbOper(connectionSource);

        DatabaseConnection conn = connectionSource.getReadWriteConnection(null);
        if (false) {
            File tempFile = File.createTempFile("prefix", ".sql");
            System.out.println(tempFile.getAbsolutePath());
            System.out.println(tempFile.exists());
            String sql = String.format("script to '%s'", tempFile.getAbsolutePath());
            conn.executeStatement(sql, DEFAULT_RESULT_FLAGS);
        }
        conn.executeStatement("script to 'target/漢字Unicode™.sql'", DEFAULT_RESULT_FLAGS);
        assertTrue(true);
    }

    @Test
    public void test2() throws SQLException, IOException {
        String DATABASE_URL = "jdbc:sqlite::memory:";
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(DATABASE_URL);
        
        doDbOper(connectionSource);

        DatabaseConnection conn = connectionSource.getReadWriteConnection(null);
        conn.executeStatement("backup to 'target/test.bak.db3'", DEFAULT_RESULT_FLAGS);

        /*
        File tempFile = File.createTempFile("prefix", ".sql");
        System.out.println(tempFile.getAbsolutePath());
        System.out.println(tempFile.exists());
        Connection conn = DriverManager.getConnection(DATABASE_URL);
        Statement stmt = conn.createStatement();*/
        // Do some updates
        //String sql = String.format("script to '%s'", tempFile.getAbsolutePath());
        //stmt.execute(sql);
        //stmt.execute("script to 'target/漢字Unicode™.sql'");
        //stmt.execute("backup to 'target/test.bak.db'");
        assertTrue(true);
    }

    public void doDbOper(JdbcConnectionSource connection) throws SQLException, IOException {
        // if you need to create the table
        TableUtils.createTableIfNotExists(connection, Account2.class);
        final Dao<Account2, Integer> accountDao = DaoManager.createDao(connection, Account2.class);
        // create an instance of Account
        String name = "Jim Coakley";
        final Account2 account = new Account2(name);

        // persist the account object to the database
        accountDao.create(account);
        int id = account.getId();

        // assign a password
        account.setPassword("_secret");
        // update the database after changing the object
        accountDao.update(account);

        // query for all items in the database
        List<Account2> accounts = accountDao.queryForAll();

        // construct a query using the QueryBuilder
        QueryBuilder<Account2, Integer> statementBuilder = accountDao.queryBuilder();
        // shouldn't find anything: name LIKE 'hello" does not match our account
        statementBuilder.where().like(Account2.NAME_FIELD_NAME, "hello");
        accounts = accountDao.query(statementBuilder.prepare());

        // should find our account: name LIKE 'Jim%' should match our account
        statementBuilder.where().like(Account2.NAME_FIELD_NAME, name.substring(0, 3) + "%");
        accounts = accountDao.query(statementBuilder.prepare());

        // delete the account since we are done with it
        //accountDao.delete(account);
        // we shouldn't find it now
        /*
        TransactionManager.callInTransaction(connectionSource, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                accountDao.delete(account);
                return null;
            }
        }
        );*/
    }

    @DatabaseTable(tableName = "accounts")
    public static class Account2 {

        // for QueryBuilder to be able to find the fields
        public static final String NAME_FIELD_NAME = "name";
        public static final String PASSWORD_FIELD_NAME = "passwd";

        @DatabaseField(generatedId = true)
        private int id;

        @DatabaseField(columnName = NAME_FIELD_NAME, canBeNull = false)
        private String name;

        @DatabaseField(columnName = PASSWORD_FIELD_NAME)
        private String password;

        @DatabaseField(dataType = DataType.BYTE_ARRAY)
        public byte[] byte_data;

        Account2() {
            // all persisted classes must define a no-arg constructor with at least package visibility
        }

        public Account2(String name) {
            this.name = name;
        }

        public Account2(String name, String password) {
            this.name = name;
            this.password = password;
        }

        public int getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public boolean equals(Object other) {
            if (other == null || other.getClass() != getClass()) {
                return false;
            }
            return name.equals(((Account2) other).name);
        }
    }
}
