package com.github.javacommons.encryption;

import com.github.javacommons.encryption.CryptoUtils;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.DatabaseTable;
import com.j256.ormlite.table.TableUtils;
import java.io.File;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import junit.framework.TestCase;
import org.apache.commons.lang.ArrayUtils;
import org.junit.Test;

@DatabaseTable()
class TBL_UUID_TEST {

    @DatabaseField(generatedId = true)
    public int id;

    @DatabaseField(canBeNull = false)
    public String name;

    @DatabaseField(dataType = DataType.BYTE_ARRAY)
    public byte[] byte_data;

    @DatabaseField(dataType = DataType.UUID_NATIVE, index = true)
    public UUID uuid;

    @DatabaseField(index = true)
    public UUID uuid2;

    TBL_UUID_TEST() {
        // all persisted classes must define a no-arg constructor with at least package visibility
    }

}

public class Test06_UUID extends TestCase {

    @Override
    protected void setUp() throws Exception {
        new File("target/UUID_TEST.db").delete();
    }

    @Override
    protected void tearDown() throws Exception {
    }

    @Test
    public void test0() throws SQLException {
        Logger.getGlobal().log(Level.INFO, "test0()");
        UUID uuid = UUID.randomUUID();
        System.out.println(uuid.toString());
        byte[] uuidBytes = UuidUtils.serializeUuid(uuid);
        System.out.println("highBytes="+CryptoUtils.bytes2Hex(uuidBytes));
        UUID uuid2 = UuidUtils.deserializeUuid(uuidBytes);
        System.out.println("uuid2="+uuid2.toString());
        assertTrue(uuid2.equals(uuid));
        
        String name = CryptoUtils.randomJapaneseString(20);
        UUID uuid3 = UuidUtils.generateUuidForName(name);
        uuidBytes = UuidUtils.serializeUuid(uuid3);
        assertTrue(ArrayUtils.isEquals(uuidBytes, CryptoUtils.md5(name.getBytes())));
    }

    @Test
    public void test1() throws SQLException {
        String DATABASE_URL = "jdbc:sqlite:target/UUID_TEST.db";
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(DATABASE_URL);
        // if you need to create the table
        TableUtils.createTableIfNotExists(connectionSource, TBL_UUID_TEST.class);
        final Dao<TBL_UUID_TEST, Integer> uuidDao = DaoManager.createDao(connectionSource, TBL_UUID_TEST.class);

        TransactionManager.callInTransaction(connectionSource, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                for (int i = 0; i < 50; i++) {
                    TBL_UUID_TEST account = new TBL_UUID_TEST();
                    account.name = CryptoUtils.randomJapaneseString(50);
                    account.byte_data = account.name.getBytes();
                    account.uuid = UUID.randomUUID();
                    account.uuid2 = UUID.nameUUIDFromBytes(account.name.getBytes());
                    uuidDao.create(account);
                }
                return null;
            }
        });

        assertNotNull(uuidDao.queryForId(1));

        List<TBL_UUID_TEST> accounts = uuidDao.queryForAll();

        // construct a query using the QueryBuilder
        QueryBuilder<TBL_UUID_TEST, Integer> statementBuilder = uuidDao.queryBuilder();
        // shouldn't find anything: name LIKE 'hello" does not match our account
        statementBuilder.where().eq("uuid", UUID.randomUUID());
        accounts = uuidDao.query(statementBuilder.prepare());

        /*
        for (int i = 0; i < 50; i++) {
            TBL_UUID_TEST account = new TBL_UUID_TEST();
            account.name = CryptoUtils.randomJapaneseString(50);
            account.byte_data = account.name.getBytes();
            account.uuid = UUID.randomUUID();
            account.uuid2 = UUID.nameUUIDFromBytes(account.name.getBytes());
            uuidDao.create(account);
        }*/
 /*
        // create an instance of Account
        String name = "Jim Coakley";
        final Account account = new Account(name);

        // persist the account object to the database
        uuidDao.create(account);
        int id = account.getId();

        // assign a password
        account.setPassword("_secret");
        // update the database after changing the object
        uuidDao.update(account);

        // query for all items in the database
        List<Account> accounts = uuidDao.queryForAll();

        // construct a query using the QueryBuilder
        QueryBuilder<Account, Integer> statementBuilder = uuidDao.queryBuilder();
        // shouldn't find anything: name LIKE 'hello" does not match our account
        statementBuilder.where().like(Account.NAME_FIELD_NAME, "hello");
        accounts = uuidDao.query(statementBuilder.prepare());

        // should find our account: name LIKE 'Jim%' should match our account
        statementBuilder.where().like(Account.NAME_FIELD_NAME, name.substring(0, 3) + "%");
        accounts = uuidDao.query(statementBuilder.prepare());

        // delete the account since we are done with it
        //accountDao.delete(account);
        // we shouldn't find it now
        TransactionManager.callInTransaction(connectionSource, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                uuidDao.delete(account);
                return null;
            }
        }
        );
         */
        assertTrue(true);
    }

}
