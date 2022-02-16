package com.jinpei.id.generator;


import cn.hutool.core.util.RandomUtil;
import com.jinpei.id.common.utils.IdUtils;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.Random;

/**
 * 核销码生成器
 * <p>
 *  要求:
 *     - 不重复
 *     - 无规律
 *     - 纯数字长度 10位,33bit
 * </p>
 * <p>
 *  实现: 35bit
 * +=========================================================================
 * | 6bit 随机数|9bit dayOfMonth | 13bit随机数 | 4bit year | 3bit卡号校验位 |
 * +=========================================================================
 * +=========================================================================
 * | 4bit year|13bit随机数 | 9bit dayOfYear | 6bit 随机数 | 3bit卡号校验位 |
 * +=========================================================================
 * 31-200-16383-2
 *
 * </p>
 * @author labu
 * @date 2022/02/15
 */
@Slf4j
public class VerifyCodeGenerator {
    private static final Random RANDOM = new Random();
    private static final LocalDate START_DATE = LocalDate.of(2021, 1, 1);
    /**
     * 校验bit位数
     */
    private final int validationBits = 3;
    private final int random1Bits = 8;
    private final int dayBits= 9;
    private final int random2Bits= 13;
    private final int yearBits = 4;

    private final int maxCode = ~(-1 << validationBits);
    private final int maxR1 = ~(-1 << random1Bits);
    private final int maxR2 = ~(-1 << random2Bits);
    private final int random1Offset = validationBits;
    private final int dayOffset = random1Offset+random1Bits;
    private final int random2Offset = dayOffset+dayBits;
    private final int yearOffset = random2Offset+random2Bits;

    public Long nextId(){
        LocalDate now = LocalDate.now();
        long dayOfYear = now.getDayOfYear();
        long year = now.getYear();
        long yearBit = (year - START_DATE.getYear())%16;
        long r2 = RandomUtil.randomInt(0, maxR2+1);
        long r1 = RandomUtil.randomInt(0, maxR1+1);
        long originId = yearBit << yearOffset | r2 <<random2Offset|dayOfYear<<dayOffset| r1 <<random1Offset;
        int validationCode = IdUtils.getValidationCode(originId, maxCode);
        return originId+validationCode;
    }

    public Long nextId(LocalDate localDate){
        long dayOfYear = localDate.getDayOfYear();
        long year = localDate.getYear();
        long yearBit = year - START_DATE.getYear();
        long r2 = RandomUtil.randomInt(0, 16384);
        long r1 = RandomUtil.randomInt(0, 128);
        long originId = yearBit << yearOffset | r2 <<random2Offset|dayOfYear<<dayOffset| r1 <<random1Offset;
        int validationCode = IdUtils.getValidationCode(originId, maxCode);
        return originId+validationCode;
    }



    public Long nextRandomNumberId(){
        String number = RandomUtil.randomNumbers(9);
        long originId = Long.parseLong(number)<<validationBits;
        int validationCode = IdUtils.getValidationCode(originId, maxCode);
//        System.out.println("random number:"+number+",originId:"+originId+",validationCode:"+validationCode);
        return originId + validationCode;
    }

    public String nextRandomStringId(){
        return RandomUtil.randomNumbers(10);
    }

    public boolean validateCode(Long id){
            String bitString = Long.toBinaryString(id);
            int bitLength = bitString.length();
            String codeBitString = bitString.substring(bitLength - validationBits);
            int validationCode = Integer.parseInt(codeBitString, 2);
            long originId = id - validationCode;
        return validationCode == IdUtils.getValidationCode(originId, maxCode);
    }
}
