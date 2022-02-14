package com.jinpei.id.generator;

import cn.hutool.core.lang.Validator;
import com.jinpei.id.common.utils.IdUtils;
import com.jinpei.id.generator.base.CardIdGeneratorable;


/**
 * 10位数字核销码生产,33 bit
 *
 * @author labu
 * @date 2022/02/14
 */
public class IDGenerator implements CardIdGeneratorable {
    /**
     * 时间bit数，时间的单位为秒，
     * 29 bit位时间可以表示17年,每年31536000秒,29位bit最大表示536870911秒
     * 22 bit位时间可以表示0.133年,22位bit最大表示4194303秒
     */
    private final int timeBits = 22;

    /**
     * 机器编码bit数
     */
    private final int machineBits = 3;

    /**
     * 每秒序列bit数
     * 单台每秒31个
     */
    private final int sequenceBits = 5;

    /**
     * 校验bit位数
     */
    private final int validationBits = 3;
    /**
     * 上一次时间戳
     */
    private long lastStamp = -1L;

    /**
     * 序列
     */
    private long sequence = randomSequence();

    /**
     * 机器编号
     */
    @SuppressWarnings("UnusedAssignment")
    private long machineId = 1L;

    /**
     * 时间左移bit数
     */
    private int timeOffset = 0;

    /**
     * 机器编码左移bit数
     */
    private int machineOffset = 0;

    /**
     * 序列左移bit数
     */
    private int sequenceOffset = 0;

    /**
     * 最大序列号
     */
    private long maxSequence = 0L;

    /**
     * 最大校验码
     */
    private int maxCode = 0;

    /**
     * 开始时间，默认为2019-01-01
     */
    private final String startTimeString = "2022-01-01 00:00:00";
    /**
     * 起始时间戳
     */
    private long startTimeStamp = 0L;

    private static final long MAX_ID = 9999999999L;

    private static final long MIN_ID = 1000000000L;

    private void init() {
        sequenceOffset = validationBits;
        timeOffset = sequenceOffset + sequenceBits;
        machineOffset = timeOffset + timeBits;
        maxSequence = ~(-1L << sequenceBits);
        startTimeStamp = IdUtils.getTimeStampSecond(startTimeString);
        maxCode = ~(-1 << validationBits);
    }

    public IDGenerator(int machineId) {
        int maxMachineId = ~(-1 << machineBits);
        if (machineId > maxMachineId || machineId < 1) {
            throw new IllegalArgumentException("Machine id should be between 1 and " + maxMachineId);
        }

        this.machineId = machineId;
        init();
    }

    public synchronized long generate() {
        long curStamp = getCurrentSecond();
//        System.out.println("originStamp:"+curStamp);
        if (curStamp < lastStamp) {
            throw new IllegalArgumentException("Clock moved backwards. Refusing to generate id");
        }

        if (curStamp == lastStamp) {
            sequence = (sequence + 1) & maxSequence;
            if (sequence == 0L) {
                curStamp = getNextSecond();
            }
        } else {
            sequence = randomSequence();
        }
        lastStamp = curStamp;
//        System.out.println("machineId bit:"+Long.toBinaryString(machineId << machineOffset));
//        System.out.println("timeStamp bit:"+Long.toBinaryString((curStamp - startTimeStamp) << timeOffset)+" long:"+((curStamp - startTimeStamp) << timeOffset));
//        System.out.println("sequence bit:"+Long.toBinaryString(sequence << sequenceOffset));
        long originId = machineId << machineOffset
                | (curStamp - startTimeStamp) << timeOffset
                | sequence << sequenceOffset;
//        System.out.println("originId bit:"+(Long.toBinaryString(originId)));
//        System.out.println("originId:"+originId);

        int validationCode = IdUtils.getValidationCode(originId, maxCode);
//        System.out.println("validationCode bit:"+Long.toBinaryString(validationCode));
//        System.out.println("validationCode:"+validationCode);
//        System.out.println("shotId:"+(originId + validationCode));
        return originId + validationCode;
    }

    private long getCurrentSecond() {
        return System.currentTimeMillis() / 1000;
    }

    /**
     * 获取下一秒钟
     *
     * @return 时间戳（秒）
     */
    private long getNextSecond() {
        long second = getCurrentSecond();
        while (second <= lastStamp) {
            IdUtils.sleep(20);
            second = getCurrentSecond();
        }
        return second;
    }

    /**
     * 校验卡号是否合法
     *
     * @param id 卡号
     * @return boolean 合法返回true，反之false
     */
    public boolean validate(long id) {
        if (id > MAX_ID || id < MIN_ID) {
            return false;
        }

        return validateCode(id, startTimeStamp, timeBits, timeOffset, validationBits, maxCode);
    }

    /**
     * 解析卡号
     *
     * @param id 卡号
     * @return 解析结果依次是时间戳、机器编码、序列号
     */
    public Long[] parse(long id) {
        if (!validate(id)) {
            return null;
        }

        String bitString = Long.toBinaryString(id);
        int bitLength = bitString.length();
        long timestamp = Long.parseLong(bitString.substring(bitLength - timeOffset - timeBits, bitLength - timeOffset),
                2);
        long machineId = Long.parseLong(bitString.substring(0, bitLength - machineOffset), 2);
        long sequence = Long.parseLong(bitString.substring(bitLength - sequenceOffset - sequenceBits,
                bitLength - sequenceOffset), 2);
        return new Long[]{(timestamp + startTimeStamp) * 1000, machineId, sequence};
    }

    public static long ipv4ToLong(String strIP) {
        if (Validator.isIpv4(strIP)) {
            long[] ip = new long[4];
            // 先找到IP地址字符串中.的位置
            int position1 = strIP.indexOf(".");
            int position2 = strIP.indexOf(".", position1 + 1);
            int position3 = strIP.indexOf(".", position2 + 1);
            // 将每个.之间的字符串转换成整型
            ip[0] = Long.parseLong(strIP.substring(0, position1));
            ip[1] = Long.parseLong(strIP.substring(position1 + 1, position2));
            ip[2] = Long.parseLong(strIP.substring(position2 + 1, position3));
            ip[3] = Long.parseLong(strIP.substring(position3 + 1));
            return (ip[2] << 8) + ip[3];
//            return (ip[0] << 24) + (ip[1] << 16) + (ip[2] << 8) + ip[3];
        }
        return 0;
    }
}
