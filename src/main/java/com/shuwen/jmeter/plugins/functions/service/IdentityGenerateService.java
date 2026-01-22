package com.shuwen.jmeter.plugins.functions.service;

import com.shuwen.jmeter.plugins.functions.utils.StringUtils;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

/**
 * 身份证生成服务
 * @author shuwen
 */
public class IdentityGenerateService {

    private static String DEFAULT_INITIAL_RANGE = "QWERTYUIOPASDGFHJKLZXCVBNM";

    private static String DEFAULT_NUMBER_RANGE = "1234567890";

    //324是香港政府定义的身份证校验规则固定值
    private static final int FIXED_NUMBER = 324;

    private static final List<String> VALID_ADDRESS_CODES = new ArrayList<>();
    static {
        // 初始化有效地址码列表
        VALID_ADDRESS_CODES.add("440101"); // 广州荔湾区
        VALID_ADDRESS_CODES.add("440105"); // 广州海珠区
        VALID_ADDRESS_CODES.add("440305"); // 深圳南山区
        VALID_ADDRESS_CODES.add("440307"); // 深圳龙岗区
        VALID_ADDRESS_CODES.add("441900"); // 东莞
        VALID_ADDRESS_CODES.add("450821"); // 平南
    }

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * 随机香港身份证生成
     * @param initialRange 首字母范围，如果为null会使用默认值"QWERTYUIOPASDGFHJKLZXCVBNM"
     * @return
     */
    public static String randomHKIDGen(String initialRange) {
        initialRange = StringUtils.isNonBlank(initialRange) ? initialRange : DEFAULT_INITIAL_RANGE;

        Random random = new Random();
        char initial = initialRange.charAt(random.nextInt(initialRange.length()));

        //8是首位的加权系数；
        int weightedCoefficient = 8;
        AtomicInteger weightedSum = new AtomicInteger(FIXED_NUMBER + charToNumberForHKID(initial) * weightedCoefficient);

        //循环六次获取身份证数字
        List<String> idNumberList = new ArrayList<>();
        IntStream.rangeClosed(1, 6).forEach(i -> {
            char randomNumberChar = DEFAULT_NUMBER_RANGE.charAt(random.nextInt(DEFAULT_NUMBER_RANGE.length()));
            idNumberList.add(String.valueOf(randomNumberChar));
            weightedSum.addAndGet(Character.getNumericValue(randomNumberChar) * (weightedCoefficient - i));
        });

        String checkChar = weightedSum.get() % 11 == 1 ? "A" : String.valueOf(11 - weightedSum.get() % 11);

        return initial + String.join("", idNumberList) + checkChar;
    }

    /**
     * 随机大陆身份证生成
     * @param birthDate, 生日，格式: yyyyMMdd
     * @return
     */
    public static String randomPRCIDGen(String birthDate) {

        Random random = new Random();
        //生成三位随机数
        int randomNumber = random.nextInt(900) + 100;
        String addressCode = VALID_ADDRESS_CODES.get(random.nextInt(VALID_ADDRESS_CODES.size()));

        String id = addressCode + validAndGet(birthDate) + randomNumber;

        //增加检查码
        id += calculatePRCIDCheckChar(id);

        return id;
    }


    /**
     * 计算大陆身份证的检查码
     * @param id, 17位
     * @return
     */
    private static String calculatePRCIDCheckChar(String id) {
        AtomicInteger sum = new AtomicInteger();
        IntStream.rangeClosed(1, 17).forEach(i -> {
            sum.addAndGet(Integer.parseInt(String.valueOf(id.charAt(i - 1))) * getWeightFactor(i));
        });
        int result = (12 - sum.get() % 11) % 11;
        return result == 10 ? "X" : String.valueOf(result);
    }

    /**
     * 计算身份证X位的加权因子
     * @param index
     * @return
     */
    public static int getWeightFactor(int index) {
        return (int) (Math.pow(2.0D, 18 - index) % 11.00);
    }


    private static String validAndGet(String birthDate) {
        try{
            DATE_TIME_FORMATTER.parse(birthDate);
            return birthDate;
        }catch (Exception e){
            return "19921225";
        }
    }


    /**
     * 香港身份证首位字母（A-Z）需要先转换为数字，规则是：
     * A=10，B=11，C=12，…，Z=35（这正是 ASCII码 - 55 的结果：A 的 ASCII=65，65-55=10）。
     * @param initial
     * @return
     */
    private static int charToNumberForHKID(char initial) {
        return Character.getNumericValue(initial) - 55;
    }
}
