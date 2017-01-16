package com.coffeeCodes.common.utils.certificate;

import org.apache.commons.lang3.time.DateUtils;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by shenbobo on 16/11/28.
 */
public class CertificateUtil {

    /**
     * 根据身份编号获取性别
     *
     * @param idCard 身份编号
     * @return 性别(M-男，F-女，N-未知)
     */
    public static String getGenderByIdCard(String idCard) {
        String sGender = "FEMALE";

        String sCardNum = idCard.substring(16, 17);
        if (Integer.parseInt(sCardNum) % 2 != 0) {
            sGender = "MALE";
        }
        return sGender;
    }

    /**
     * 根据身份编号获取生日
     *
     * @param idCard 身份编号
     * @return 生日(yyyyMMdd)
     */
    public static String getBirthByIdCard(String idCard) {
        return idCard.substring(6, 14);
    }

    /**
     * 根据身份编号获取生日
     *
     * @param idCard 身份编号
     * @return 生日(yyyyMMdd)
     */
    public static Date getBirthDate(String idCard) {
        Date ageDate = null;
        try {
            ageDate = DateUtils.parseDate(idCard.substring(6, 14), Locale.CHINA,"yyyMMdd");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return ageDate;
    }

    /**
     * 验证18位身份编码是否合法
     *
     * @param idCard 身份编码
     * @return 是否合法
     */
    public static boolean validateIdCard(String idCard) {
        boolean bTrue = false;
        if (idCard.length() == 18) {
            Pattern pattern = Pattern.compile("\\d{17}[0-9a-zA-Z]");
            Matcher matcher = pattern.matcher(idCard);
            if (matcher.matches()) {
                bTrue = true;
            }
        }
        return bTrue;
    }

    /**
     * 根据日期获取年龄
     *
     * @param birthday
     * @return
     */
    public static int getAgeByBirthday(Date birthday) {
        Calendar cal = Calendar.getInstance();
        int yearNow = cal.get(Calendar.YEAR);
        int monthNow = cal.get(Calendar.MONTH) + 1;
        int dayOfMonthNow = cal.get(Calendar.DAY_OF_MONTH);

        cal.setTime(birthday);
        int yearBirth = cal.get(Calendar.YEAR);
        int monthBirth = cal.get(Calendar.MONTH) + 1;
        int dayOfMonthBirth = cal.get(Calendar.DAY_OF_MONTH);
        int age = yearNow - yearBirth;
        if (monthNow <= monthBirth) {
            if (monthNow == monthBirth) {
                if (dayOfMonthNow < dayOfMonthBirth) {
                    age--;
                }
            } else {
                age--;
            }
        }
        return age;
    }


}
