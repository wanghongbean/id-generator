package com.whb.id;

import org.junit.Test;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.Assert.*;

public class VerifySaleCodeUtilTest {
    @Test
    public void test_base(){

    }

    @Test
    public void test(){

        final int batch = 100;
        List<BigDecimal> statics = new ArrayList<>(batch);
        int conflictSum = 0;
        final int size = 100000;

        for (int j = 0; j < batch; j++) {
            Set<String> hashSet = new HashSet<>(size);
            for (int i = 0; i < size; i++) {
                String code = genVerifyCode();
//                String code = VerifySaleCodeUtil.genVerifySaleCode();
                hashSet.add(code);
            }
            int conflict = size - hashSet.size();
            conflictSum += conflict;
            statics.add(new BigDecimal(conflict).divide(new BigDecimal(size)));
        }
        System.out.println(batch + "轮测试，每轮随机生成" + size + "个核销码的平均冲突率为:" + new BigDecimal(conflictSum)
                .divide(new BigDecimal(batch * size)));
        statics.forEach(System.out::println);
    }

    @Test
    public void test_gen(){
        for (int i = 0; i < 100; i++) {
            System.out.println(genVerifyCode());
        }
    }


    /**
     * 生成核销码的方法，生成规格为：两位数年 + 当前是今年第几天 + 7位随机数
     * 如果生成的核销码较多，可以通过缓存或者redis进行当天的核销码是否重复判断
     *
     */
    public static String genVerifyCode() {
        String year = new SimpleDateFormat("yy").format(new Date());
        LocalDateTime now = LocalDateTime.now();
        int year1 = now.getYear();
        int dayOfYear = now.getDayOfYear();
        String day = String.format("%tj", new Date());
        double random = Math.random() * 10000000;
        while (random < 1000000) {
            random = Math.random() * 10000000;
        }
        int intRandom = Double.valueOf(random).intValue();
        String verifyCode = year + day + intRandom;
        return verifyCode;
    }

}