package com.jinpei.id.generator;

import cn.hutool.core.net.NetUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class IDGeneratorTest {

    private IDGenerator idGenerator;

    @Before
    public void setUp(){
        long ip = IDGenerator.ipv4ToLong(NetUtil.getLocalhostStr());
        System.out.println("ip long:"+ip+" ip bit:"+Long.toBinaryString(ip));
        int mId = (int)ip%7;
        idGenerator =  new IDGenerator(mId);
    }

    @Test
    public void test_generator(){
        long id = idGenerator.generate();
        assertEquals(10,String.valueOf(id).length());
        Assert.assertTrue(idGenerator.validate(id));
        Assert.assertFalse(idGenerator.validate(++id));
        Assert.assertFalse(idGenerator.validate(++id));
        id--;
//        id--;
//        Assert.assertFalse(idGenerator.validate(--id));
        Assert.assertFalse(idGenerator.validate(2061889807));
        Assert.assertFalse(idGenerator.validate(2061889806));
        Assert.assertFalse(idGenerator.validate(2061889805));
        Assert.assertFalse(idGenerator.validate(2061889804));
        Assert.assertFalse(idGenerator.validate(2061889803));

    }

    @Test
    public void performance() {
        long num = 100;
        for (int i = 0; i < num; i++) {
            Long id = idGenerator.generate();
            System.out.println(id);
        }
    }

    @Test
    public void parse() throws InterruptedException {
//        long id = 2061735203L;
        long id = idGenerator.generate();
//        TimeUnit.SECONDS.sleep(10);
        Long[] results = idGenerator.parse(id);
        long timestamp = results[0];
        LocalDateTime dateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
        System.out.println("Time: " + DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:dd").format(dateTime));
        System.out.println("Machine id: " + results[1]);
        System.out.println("Sequence: " + results[2]);
    }

    @Test
    public void crash() {
        long num = 10000000;
        long passNum = 0;
        Random random = new Random();
        for (int i = 0; i < num; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < 16; j++) {
                sb.append(random.nextInt(100) / 10);
            }

            long id = Long.parseLong(sb.toString());
            if (idGenerator.validate(id)) {
                passNum++;
            }
        }

        System.out.println(passNum);
    }

    @Test
    public void crash1() {
        long num = 1000000;
        long passNum = 0;
        long id = idGenerator.generate();
        for (int i = 0; i < num; i++) {
            if (idGenerator.validate(++id)) {
                passNum++;
            }
        }

        System.out.println(passNum);
    }

    @Test
    public void b(){
//        String s="11111111111111111111111111111";536870911
        String s="1111111111111111111111";
        System.out.println(Long.parseLong(s,2));

        System.out.println(Long.parseLong("1111111111111111111111111111111111",2));
    }
}