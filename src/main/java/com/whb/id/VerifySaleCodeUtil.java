package com.whb.id;


//import com.github.rxyor.common.util.lang2.RadixUtil;
import java.time.LocalDate;
import java.util.Random;

//import org.apache.commons.lang3.RandomUtils;

/**
 *<p>
 * 核销码生成工具类，目前核销码是8位，1位标识年份距2019年的偏移量，2位标识
 * 当天是今天的第几天，5位可以由0~32^5-1的随机INT值转换为指定字符的32进制标识，
 * 年份天数标识放在一起过于明显，分散到下标为1,3,5位置。经过测试，10W个核销码，
 * 冲突率为150/10W, 1W个核销码冲突率几乎为0
 *</p>
 *
 * @author
 * @date 2019/12/10 周二 10:22:00
 * @since 1.0.0
 */
public class VerifySaleCodeUtil {

    /**
     * 核销码表示字符，0~9,A~Z，去除0、O、I、L 4个易混淆的字符还是32位
     */
//    public final static char[] DIGITS = {
//            '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A',
//            'B', 'C', 'D', 'E', 'F', 'G', 'H', 'J', 'K', 'M',
//            'N', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X',
//            'Y', 'Z'};
    public final static char[] DIGITS = {
            '1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};

    /**
     * 开始年份，用户计算年份偏移量
     */
    public final static int BEGIN_YEAR = 2019;

    /**
     * 随机位由5位字符表示
     */
    public final static int RANDOM_LEN = 5;

    /**
     * 5为32进制能表示的最大无符号整数为32^5-1
     */
    private final static int MAX_RANDOM_INT = (int) (Math.pow(DIGITS.length, RANDOM_LEN) - 1);

//    private static final RadixUtil INSTANCE = RadixUtil.builder().digits(DIGITS).build();

    /**
     *<p>
     *生成一个核销码
     *</p>
     *
     * @author
     * @date 2019-12-10 周二 10:35:16
     * @return
     */
    public static String genVerifySaleCode() {
        LocalDate localDate = LocalDate.now();

        //年份偏移量字符表示
        char yearChar = DIGITS[Math.abs(localDate.getYear() - BEGIN_YEAR)];
        //一年的第几天32进制字符表示
//        String dayOfYearString = INSTANCE.convert2String(localDate.getDayOfYear());
        String dayOfYearString = localDate.getDayOfYear()+"";
        //32*32=1024>366, 2位即可表示天的偏移量, 不够2位补32进制第0位字符
        if (dayOfYearString.length() == 1) {
            dayOfYearString = DIGITS[0] + dayOfYearString;
        }

        String randomCode = genRandomCode();

        StringBuilder verifyCode = new StringBuilder(randomCode);

        //sb式混淆
        //下标1位置插入年份偏移标识
        verifyCode.insert(1, yearChar);
        //下标3位置插入天偏移标识第1位
        verifyCode.insert(3, dayOfYearString.charAt(0));
        //下标5位置插入天偏移标识第2位
        verifyCode.insert(5, dayOfYearString.charAt(1));

        return verifyCode.toString();
    }

    /**
     *生成（0~32^5-1）之间的随机整数，并转换为指定表示字符的32进制
     * @return
     */
    private static String genRandomCode() {
        Random random1 = new Random();
        final int random = random1.nextInt(MAX_RANDOM_INT);
        String randomCode = random+"";
//        String randomCode = INSTANCE.convert2String(random);
        StringBuilder sb = new StringBuilder();
        //不足5位，补齐5位
        for (int i = randomCode.length(); i < RANDOM_LEN; i++) {
            sb.append(DIGITS[0]);
        }
        return sb.toString() + randomCode;
    }

}


