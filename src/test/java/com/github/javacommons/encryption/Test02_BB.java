package com.github.javacommons.encryption;

import com.github.javacommons.encryption.CryptoUtils;
import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.TableUtils;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Callable;
import org.junit.Test;
import static org.junit.Assert.*;

public class Test02_BB {

    @Test
    public void test1() {

        ByteBuffer byteBuf = ByteBuffer.allocate(4);
        byteBuf.order(ByteOrder.LITTLE_ENDIAN);
        byteBuf.putShort((short) 12345);
        byteBuf.putShort((short) 4321);
        byte[] byteArray = byteBuf.array();
        ByteBuffer bb = ByteBuffer.wrap(byteArray);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        while (bb.hasRemaining()) {
            short v = bb.getShort();
            System.out.println(v);
        }
        assertTrue(true);
    }

    @Test
    public void test2() {

        ByteArrayDataOutput out = ByteStreams.newDataOutput();
        out.writeUTF("abc");
        out.writeInt(0xffffffff);
        out.writeInt(543210);
        byte[] result = out.toByteArray();
        System.out.println(CryptoUtils.bytes2Hex(result));
        ByteArrayDataInput in = ByteStreams.newDataInput(result);
        System.out.println(in.readUTF());
        System.out.println(in.readInt());
        System.out.println(in.readInt());
        
        assertTrue(true);
    }

    @Test
    public void ormliteTest() throws SQLException {
        //String DATABASE_URL = "jdbc:sqlite:/home/javagame/store/sample.db";
        String DATABASE_URL = "jdbc:sqlite:target/sample.db";
        JdbcConnectionSource connectionSource = new JdbcConnectionSource(DATABASE_URL);
        // if you need to create the table
        TableUtils.createTableIfNotExists(connectionSource, Account.class);
        final Dao<Account, Integer> accountDao = DaoManager.createDao(connectionSource, Account.class);
        // create an instance of Account
        String name = "Jim Coakley";
        final Account account = new Account(name);

        // persist the account object to the database
        accountDao.create(account);
        int id = account.getId();

        // assign a password
        account.setPassword("_secret");
        // update the database after changing the object
        accountDao.update(account);

        // query for all items in the database
        List<Account> accounts = accountDao.queryForAll();

        // construct a query using the QueryBuilder
        QueryBuilder<Account, Integer> statementBuilder = accountDao.queryBuilder();
        // shouldn't find anything: name LIKE 'hello" does not match our account
        statementBuilder.where().like(Account.NAME_FIELD_NAME, "hello");
        accounts = accountDao.query(statementBuilder.prepare());

        // should find our account: name LIKE 'Jim%' should match our account
        statementBuilder.where().like(Account.NAME_FIELD_NAME, name.substring(0, 3) + "%");
        accounts = accountDao.query(statementBuilder.prepare());

        // delete the account since we are done with it
        //accountDao.delete(account);
        // we shouldn't find it now
        TransactionManager.callInTransaction(connectionSource, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                accountDao.delete(account);
                return null;
            }
        }
        );
    }
}
