package com.github.javacommons.encryption.test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import static org.hamcrest.core.Is.is;
import org.junit.Test;
import static org.junit.Assert.*;

class Person implements Serializable {
    public String firstName;
    public String lastName;
    public int age;
    public Hobby hobby;
}

class Hobby 
{
    public String hobby;
}

public class TestSerialize {

    @Test
    public void test1() {
        // シリアライズ
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("target/person.txt"))) {
            Hobby hobby = new Hobby();
            hobby.hobby = "tennis";
            Person person = new Person();
            person.firstName = "John";
            person.lastName = "Williams";
            person.age = 55;
            person.hobby = hobby;
            oos.writeObject(person);
            fail();
        } catch (IOException e) {
            //e.printStackTrace();
        }

        // デシリアライズ
        /*try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("target/person.txt"))) {
            Person dePerson = (Person) ois.readObject();

            assertThat(dePerson.firstName, is("John"));
            assertThat(dePerson.lastName, is("Williams"));
            assertThat(dePerson.age, is(55));
        } catch (IOException | ClassNotFoundException e) {
            fail(e.getMessage());
        }*/
     }

}
