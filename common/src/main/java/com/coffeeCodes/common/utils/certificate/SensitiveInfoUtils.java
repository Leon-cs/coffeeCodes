package com.coffeeCodes.common.utils.certificate;


import org.apache.commons.lang3.StringUtils;

/**
 * 敏感信息屏蔽工具<br/>
 * Created on 2016/7/22 14:47.
 */
public class SensitiveInfoUtils {

    /**
     * [身份证号] 显示最后四位，其他隐藏。共计18位或者15位。<例子：*************5762>
     * @param id
     * @return
     */
    public static String idCardNo(String id){
        if (StringUtils.isEmpty(id)) {
            return "";
        }
        String num = StringUtils.right(id, 4);
        return StringUtils.leftPad(num, StringUtils.length(id), "*");
    }

    /**
     *[手机号码] 前三位，后四位，其他隐藏<例子:138******1234>
     * @param num
     * @return
     */
    public static String mobilePhone(String num){
        if (StringUtils.isEmpty(num)) {
            return "";
        }
        return StringUtils.left(num, 3).concat(StringUtils.removeStart(StringUtils.leftPad(StringUtils.right(num, 4), StringUtils.length(num), "*"), "***"));
    }

    /**
     * [中文姓名] 只显示最后一个汉字，其他隐藏为2个星号<例子：李**>
     * @param name
     * @return
     */
    public static String chineseName(String name){
        if (StringUtils.isEmpty(name)) {
            return "";
        }
        String nameTmp = StringUtils.right(name, 1);
        return StringUtils.leftPad(nameTmp, StringUtils.length(name), "*");
    }

    /**
     * [银行卡号] 前六位，后四位，其他用星号隐藏每位1个星号<例子:6222600**********1234>
     *
     * @param cardNum
     * @return
     */
    public static String bankCard(String cardNum) {
        if (StringUtils.isBlank(cardNum)) {
            return "";
        }
        return StringUtils.left(cardNum, 6).concat(StringUtils.removeStart(StringUtils.leftPad(StringUtils.right(cardNum, 4), StringUtils.length(cardNum), "*"), "******"));
    }

}
