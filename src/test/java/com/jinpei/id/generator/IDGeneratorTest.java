package com.jinpei.id.generator;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.RandomUtil;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class IDGeneratorTest {

    private IDGenerator idGenerator;

    private List<Long> list = Arrays.asList(5022021417571246896L,
            5022021417101796290L,
            5022021416193345713L,
            5022021415521258092L,
            5022021415452871970L,
            5022021414045982000L,
            5022021411074188633L,
            5022021115485798468L,
            5022021115483813642L,
            5022021111025624255L,
            5022021019384844981L,
            502202101723494237L,
            5022021017002259582L,
            5022021016502070068L,
            5022021016460272363L,
            5022021015211390255L,
            5022020918232796111L,
            5022020918160689419L,
            5022020917223743177L,
            5022020917194324827L,
            5022020917115979958L,
            5022020917113145826L,
            5022020917104882269L,
            5022020917075076630L,
            5022020917060372465L,
            502202091657049477L,
            502202091656478614L,
            5022020916564753033L,
            5022020916553079304L,
            5022020916531219042L,
            5022020916474433903L,
            5022020916421730068L,
            5022020916421755911L,
            5022020916381230271L,
            5022020916352548658L,
            5022020916332918868L,
            5022020916234310939L,
            5022020916192336623L,
            502202091613381838L,
            5022020916133833913L,
            5022020916110370246L,
            5022020916013844246L,
            5022020915551832622L,
            5022020915544523731L,
            5022020915434268026L,
            5022020915421242529L,
            5022020915373854255L,
            5022020915351260195L);
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
        Random random = new Random();
        long num = 100;
        HashSet<Long> set = new HashSet<>();
        int n=0;
        for (int i = 0; i < num; i++) {
            Long id = idGenerator.generate();
//            Long al = list.get(random.nextInt(list.size()));
            Long al = 502202091657049477L;
            String userid1 = "999b990853fc45b0a886d3473e5c06e5";
            String userid2 = "'ac3fb50c59944d60a0343266f333fb76'";
            String userid3 = "'9f1936c596344b2e9e14dd6d2014c9e3'";
//            System.out.println(id+" orderSeq "+al+" al "+ (id & al));
//            System.out.println(id+" hashcode "+userid1.hashCode()+" al "+ (id & userid1.hashCode()));
//            f(x) = a * x + b (mod 1,000,000,000,000)
//            long r = 7 * id+91312323423L%10000000000L;
            System.out.println(id);
            Assert.assertTrue(idGenerator.validate(id));
            if(set.contains(id)){
                n++;
            }else {
                set.add(id);
            }
        }
        System.out.println("冲突:"+n);
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
            for (int j = 0; j < 10; j++) {
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
        //115195 crash率:0.115195
        System.out.println(passNum+" crash率:"+ new BigDecimal(passNum).divide(new BigDecimal(num)));
    }

    @Test
    public void test_crash1() {
        long num = 10000000;
        long passNum = 0;
        Random random = new Random();
        for (int i = 0; i < num; i++) {
            StringBuilder sb = new StringBuilder();
            for (int j = 0; j < 10; j++) {
                sb.append(random.nextInt(100) / 10);
            }

            long id = Long.parseLong(sb.toString());
            if (vcg.validateCode(id)) {
                passNum++;
            }
        }

        System.out.println(passNum+" crash率:"+ new BigDecimal(passNum).divide(new BigDecimal(num)));
    }
    @Test
    public void test_crash(){
        long num = 1000000;
        long passNum = 0;
        long id = vcg.nextId();
        for (int i = 0; i < num; i++) {
            if (vcg.validateCode(++id)) {
                passNum++;
            }
        }

        //124999 crash率:0.124999
        System.out.println(passNum+" crash率:"+ new BigDecimal(passNum).divide(new BigDecimal(num)));
    }
    VerifyCodeGenerator vcg = new VerifyCodeGenerator();
    @Test
    public void testRandom(){
        HashMap<Integer, Integer> map = new HashMap<>();
        HashSet<Long> set = new HashSet<>();
        int n=0;
        int c = 1000000;//冲突:553938 冲突率:0.553938
        for (int i = 0; i < c; i++) {
            Long id = vcg.nextId();
//            System.out.println(id);
            String str = String.valueOf(id);
            Assert.assertTrue(vcg.validateCode(id));
//            if (!Objects.equals(str.length(),10)){
//                System.out.println("id:"+id+" ========== "+str.length());
                if (map.containsKey(str.length())){
                    Integer v = map.get(str.length())+1;
                    map.put(str.length(),v);
                }else {
                    map.put(str.length(),1);
                }
//            }
            if(set.contains(id)){
                n++;
            }else {
                set.add(id);
            }
        }
        System.out.println("冲突:"+n+" 冲突率:"+ new BigDecimal(n).divide(new BigDecimal(c)));
        map.forEach((k,v)-> System.out.println("length:"+k+" num:"+v));
        map.clear();
        LocalDate of = LocalDate.of(2032, 3, 31);
        int d=0;
        int d1=0;
        HashSet<Long> set1 = new HashSet<>();
        for (int i = 0; i < c; i++) {
            Long id = vcg.nextId(of);
            String str = String.valueOf(id);
            Assert.assertTrue(vcg.validateCode(id));
//            if (!Objects.equals(str.length(),10)){
//                System.out.println("id:"+id+" ========== "+str.length());
                if (map.containsKey(str.length())){
                    Integer v = map.get(str.length())+1;
                    map.put(str.length(),v);
                }else {
                    map.put(str.length(),1);
                }
//            }
            if (set.contains(id)){
                d1++;
            }
            if(set1.contains(id)){
                d++;
            }else {
                set1.add(id);
            }
        }
        System.out.println("冲突:"+d+" 冲突率:"+ new BigDecimal(d).divide(new BigDecimal(c)));
        System.out.println("day冲突:"+d1+" 冲突率:"+ new BigDecimal(d1).divide(new BigDecimal(c)));
        map.forEach((k,v)-> System.out.println("length:"+k+" num:"+v));
//        String number = RandomUtil.randomNumbers(10);
    }

    /**
     * 测试随机num
     * length:10 num:874738
     * 冲突:488 冲突率:0.000488
     * day冲突:929 冲突率:0.000929
     * length:10 num:875118
     */
    @Test
    public void testRandomNum(){
        HashMap<Integer, Integer> map = new HashMap<>();
        HashSet<Long> set = new HashSet<>();
        int n=0;
        int c = 1000000;//冲突:553938 冲突率:0.553938
        for (int i = 0; i < c; i++) {
            Long id = vcg.nextRandomNumberId();
//            System.out.println(id);
            String str = String.valueOf(id);
            Assert.assertTrue(vcg.validateCode(id));
//            if (!Objects.equals(str.length(),10)){
//                System.out.println("id:"+id+" ========== "+str.length());
            if (map.containsKey(str.length())){
                Integer v = map.get(str.length())+1;
                map.put(str.length(),v);
            }else {
                map.put(str.length(),1);
            }
//            }
            if(set.contains(id)){
                n++;
            }else {
                set.add(id);
            }
        }
        System.out.println("冲突:"+n+" 冲突率:"+ new BigDecimal(n).divide(new BigDecimal(c)));
        map.forEach((k,v)-> System.out.println("length:"+k+" num:"+v));
        map.clear();
        LocalDate of = LocalDate.of(2032, 3, 31);
        int d=0;
        int d1=0;
        HashSet<Long> set1 = new HashSet<>();
        for (int i = 0; i < c; i++) {
            Long id = vcg.nextRandomNumberId();
            String str = String.valueOf(id);
            Assert.assertTrue(vcg.validateCode(id));
            if (map.containsKey(str.length())){
                Integer v = map.get(str.length())+1;
                map.put(str.length(),v);
            }else {
                map.put(str.length(),1);
            }
            if (set.contains(id)){
                d1++;
            }
            if(set1.contains(id)){
                d++;
            }else {
                set1.add(id);
            }
        }
        System.out.println("冲突:"+d+" 冲突率:"+ new BigDecimal(d).divide(new BigDecimal(c)));
        System.out.println("day冲突:"+d1+" 冲突率:"+ new BigDecimal(d1).divide(new BigDecimal(c)));
        map.forEach((k,v)-> System.out.println("length:"+k+" num:"+v));
//        String number = RandomUtil.randomNumbers(10);
    }


    /**
     * 测试随机字符串num
     *     day冲突:98 冲突率:0.000098
    *      冲突:52 冲突率:0.000052
    *      冲突:44 冲突率:0.000044
     */
    @Test
    public void testRandomStringNum(){
        HashMap<Integer, Integer> map = new HashMap<>();
        HashSet<String> set = new HashSet<>();
        int n=0;
        int c = 1000000;//冲突:553938 冲突率:0.553938
        for (int i = 0; i < c; i++) {
            String id = vcg.nextRandomStringId();
            String str = String.valueOf(id);

            if (map.containsKey(str.length())){
                Integer v = map.get(str.length())+1;
                map.put(str.length(),v);
            }else {
                map.put(str.length(),1);
            }
            if(set.contains(id)){
                n++;
            }else {
                set.add(id);
            }
        }
        System.out.println("冲突:"+n+" 冲突率:"+ new BigDecimal(n).divide(new BigDecimal(c)));
        map.forEach((k,v)-> System.out.println("length:"+k+" num:"+v));
        map.clear();
        LocalDate of = LocalDate.of(2032, 3, 31);
        int d=0;
        int d1=0;
        HashSet<String> set1 = new HashSet<>();
        for (int i = 0; i < c; i++) {
            String id = vcg.nextRandomStringId();
            String str = String.valueOf(id);
            if (map.containsKey(str.length())){
                Integer v = map.get(str.length())+1;
                map.put(str.length(),v);
            }else {
                map.put(str.length(),1);
            }
            if (set.contains(id)){
                d1++;
            }
            if(set1.contains(id)){
                d++;
            }else {
                set1.add(id);
            }
        }
        System.out.println("冲突:"+d+" 冲突率:"+ new BigDecimal(d).divide(new BigDecimal(c)));
        System.out.println("day冲突:"+d1+" 冲突率:"+ new BigDecimal(d1).divide(new BigDecimal(c)));

        map.forEach((k,v)-> System.out.println("length:"+k+" num:"+v));
    }

    @Test
    public void test_crash_num(){
        long num = 1000000;
        long passNum = 0;
        long id = vcg.nextRandomNumberId();
        for (int i = 0; i < num; i++) {
            if (vcg.validateCode(++id)) {
                passNum++;
            }
        }

        //124999 crash率:0.124999
        System.out.println(passNum+" crash率:"+ new BigDecimal(passNum).divide(new BigDecimal(num)));
    }

    private final int validationBits = 3;
    private final int random1Bits = 7;
    private final int dayBits= 9;
    private final int random2Bits= 13;
    private final int yearBits = 4;

    private final int maxCode = ~(-1 << validationBits);
    private final int maxR1 = ~(-1 << random1Bits);
    private final int maxR2 = ~(-1 << random2Bits);
    @Test
    public void b(){
//        String s="11111111111111111111111111111";536870911
        String s="1111111111111111111111";
//        System.out.println(Long.parseLong(s,2));
//
//        System.out.println(Long.parseLong("1111111111111111111111111111111111",2));
        System.out.println("maxCode:"+maxCode);
        System.out.println("maxR1:"+maxR1);
        System.out.println("maxR2:"+maxR2);
        System.out.println(Long.parseLong("11111111111",2));
        System.out.println(Long.parseLong("11111111111111",2));
        System.out.println(Long.parseLong("111111111111111111",2));
        System.out.println(Long.parseLong("11111",2));
        System.out.println(Long.parseLong("111111",2));
        System.out.println(Long.parseLong("1111111",2));
        System.out.println(Long.parseLong("1111",2));
//        System.out.println(Long.toBinaryString(9999));
//        System.out.println(Integer.toBinaryString(9999));
        System.out.println(Integer.toBinaryString(366));
        System.out.println(Integer.toBinaryString(31));
        System.out.println(Integer.toBinaryString(99));
        System.out.println(Long.toBinaryString(9999999999L));
        System.out.println(Long.toBinaryString(9L));
        System.out.println(Long.toBinaryString(366L));
    }
}