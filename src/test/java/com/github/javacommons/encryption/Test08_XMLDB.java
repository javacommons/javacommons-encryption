package com.github.javacommons.encryption;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import junit.framework.TestCase;
import org.junit.Test;

public class Test08_XMLDB  extends TestCase {

    @Override
    protected void setUp() throws Exception {
    }

    @Override
    protected void tearDown() throws Exception {
    }

    @Test
    public void test1() throws JsonProcessingException {
        Hoge hoge = new Hoge();
        hoge.id = 10;
        hoge.value = "hoge";
        hoge.bytes = "abc漢字".getBytes();
        hoge.dbl = 1.23456789;
        hoge.friends.add("tom");
        hoge.friends.add("mary");
        List<String> list = new ArrayList<>();
        list.add("tom");
        list.add("mary");
        //hoge.friends = list.toArray(new String[0]);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        JAXB.marshal(hoge, baos);
        byte[] bytes = baos.toByteArray();
        System.out.println(new String(bytes));
        
        ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
        //Test08_Hoge hoge = JAXB.unmarshal(bais, Test08_Hoge.class);
        Hoge hoge2 = JAXB.unmarshal(bais, Hoge.class);
        System.out.println(hoge2.value);
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        String json = mapper.writeValueAsString(hoge);
        System.out.println(json);

        assertTrue(true);
     }
    
    //@XmlRootElement(name = "Hoge")
    @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS)
    static class Hoge {

        @XmlAttribute
        public int id;
        public String value;
        public byte[] bytes;
        public double dbl;
        //@XmlElementWrapper
        //@XmlElement(name = "name")
        public List<String> friends = new ArrayList<>();
        public Date date = new Date();
        public Map<String, String> map = new HashMap<String, String>() {{put("foo", "fooVal");}};
        public String getMessage()
        {
            return "this is a message";
        }
        public void setMessage(String message)
        {
            
        }
        
    }


}
