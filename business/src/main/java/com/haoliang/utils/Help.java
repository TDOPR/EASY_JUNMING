package com.haoliang.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.text.NumberFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Slf4j
public class Help {
    //private static final LogUtil logger = LogUtil.getLogUtil(Help.class);

    public static String generateTokenUUID() {
        UUID uuId = UUID.randomUUID();
        return uuId.toString();
    }

    public static String getRandNum(int charCount) {
        String charValue = "";
        for (int i = 0; i < charCount; i++) {
            char c = (char) (randomInt(0, 10) + '0');
            charValue += String.valueOf(c);
        }
        return charValue;
    }

    private static int randomInt(int from, int to) {
        Random r = new Random();
        return from + r.nextInt(to - from);
    }

//    public static String[] chars = new String[] { "a", "b", "c", "d", "e", "f",
//            "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
//            "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
//            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
//            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
//            "W", "X", "Y", "Z" };

    public static String[] chars = new String[] { "1", "2", "3", "4", "5", "6",
            "7", "8", "9", "0" };


    public static String generateShortUUID(int size) {
        StringBuffer shortBuffer = new StringBuffer();
        String uuid = generateTokenUUID().replace("-", "");
        for (int i = 0; i < size; i++) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(str, 16);
//            shortBuffer.append(chars[x % 0x3E]);
            shortBuffer.append(chars[x % 0xA]);
        }
        return shortBuffer.toString();
    }

    /**
     * 判断非空
     * @param target
     * @return
     */
    public static boolean isNotNull(Object target) {
        return !isNull(target);
    }

    public static boolean isNull(Object target) {
        boolean isNull = false;
        if (null == target) {
            isNull = true;
        }
        if (!isNull) {
            // String
            if (target instanceof String) {
                String temp = target.toString().trim();
                if ("".equals(temp) || "null".equals(temp)
                        || "undefined".equals(temp)) {
                    isNull = true;
                }
            }
            // StringBuffer
            if (target instanceof StringBuffer) {
                String temp = target.toString().trim();
                if ("".equals(temp) || "null".equals(temp)
                        || "undefined".equals(temp)) {
                    isNull = true;
                }
            }
            // StringBuilder
            if (target instanceof StringBuilder){
                String temp = target.toString().trim();
                if ("".equals(temp) || "null".equals(temp)
                        || "undefined".equals(temp)) {
                    isNull = true;
                }
            }
            // List
            else if (target instanceof List) {
                List<Object> temp = (List<Object>) target;
                if (0 == temp.size()) {
                    isNull = true;
                }
            }
            // Map
            else if (target instanceof Map) {
                Map<Object, Object> temp = (Map<Object, Object>) target;
                if (0 == temp.size()) {
                    isNull = true;
                }
            }
            else if (target instanceof Set) {
                Set<Object> temp = (Set<Object>) target;
                if (0 == temp.size()) {
                    isNull = true;
                }
            }
            // Array
            else if (target.getClass().isArray()) {
                Object[] temp = (Object[]) target;
                if (0 == temp.length) {
                    isNull = true;
                }
            }
        }
        return isNull;
    }

//    public static void convertBean2Bean(Object from, Object to) {
//        try {
//            BeanUtils.copyProperties(from, to);
//        }
//        catch (IllegalAccessException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//        catch (InvocationTargetException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }
//    }

    /**
     * 判断target是否存在于boss字符串中
     *
     *
     * @param boss      (.. , .. , ..)
     * @param target    (..)
     * @return
     */
    public static boolean hasContain(String boss, String target) {
        boolean flag = false;
        if (isNull(boss) || isNull(target)) {
            return flag;
        }
        if (("," + boss + ",").indexOf("," + target + ",") >= 0) {
            flag = true;
        }

        return flag;
    }

    /**
     * 判断target是否存在于boss字符串中
     *
     *
     * @param boss      (.. , .. , ..)
     * @param target    (..)
     * @param caseSensitive 是否区分大小写
     * @return
     */
    public static boolean hasContain(String boss, String target,boolean caseSensitive) {
        boolean flag = false;
        if (isNull(boss) || isNull(target)) {
            return flag;
        }
        if(caseSensitive){
            boss = boss.toLowerCase();
            target = target.toLowerCase();
        }
        if (("," + boss + ",").indexOf("," + target + ",") >= 0) {
            flag = true;
        }

        return flag;
    }

    /**
     * 字符串增加内容
     *
     *
     * @param target
     * @param content
     * @param checkExsit   true 排除已经存在的
     *
     * @return
     */
    public static StringBuffer append(StringBuffer target, String content,
                                      boolean checkExsit) {
        if (checkExsit) {
            boolean flag = false;
            if (isNotNull(target)) {
                flag = hasContain(target.toString(), content);
            }
            if (!flag) {
                if (isNotNull(target)) {
                    target.append("," + content);
                }
                else {
                    target.append(content);
                }
            }
        }
        else {
            if (isNotNull(target)) {
                target.append("," + content);
            }
            else {
                target.append(content);
            }
        }

        return target;
    }

    /**
     * 判断对象是否存在， 如果存在返回自己， 如果不存在 返回not
     *
     *
     * @param target
     * @param not
     * @return
     */
    public static Object exsitOrNot(Object target, Object not) {
        if (isNotNull(target)) {
            return target;
        }
        return not;
    }

    /**
     * 解析attr扩展字段信息
     * 有序的LinkedHashMap
     * <一句话功能简述>
     * <功能详细描述>
     * @param attr 值
     * @param splitStr 分隔符
     * @return [参数说明]
     *
     * @return LinkedHashMap<String,String> [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static LinkedHashMap<String, String> parseAttrMap(String attr,
                                                             String splitStr) {
        LinkedHashMap<String, String> attrLinkedHashMap = new LinkedHashMap<String, String>();
        log.info("parseAttrMap attr:{}", attr);
        log.info("parseAttrMap splitStr:{}", splitStr);
        if (Help.isNull(attr)) {
            log.error("parseAttrMap attr为空!");
            return null;
        }
        if (Help.isNull(splitStr)) {
            log.error("parseAttrMap splitStr为空!");
            return null;
        }
        String[] attrStr = attr.split(splitStr);
        for (int i = 0; i < attrStr.length; i++) {
            String attrStrValue = attrStr[i];
            if(isNotNull(attrStrValue)){
                String[] valueStr = attrStrValue.split(":");
                if (valueStr.length == 2) {
                    attrLinkedHashMap.put(valueStr[0], valueStr[1]);
                }else{
                    int strLength = attrStrValue.indexOf(":");
                    if(strLength!=-1){
                        String mapKey = attrStrValue.substring(0, strLength);
                        String mapValue = attrStrValue.substring(strLength + 1,
                                attrStrValue.length());
                        attrLinkedHashMap.put(mapKey, mapValue);
                    }
                }
            }
        }
        return attrLinkedHashMap;
    }

    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]+");
        Matcher isNum = pattern.matcher(str);
        if(!isNum.matches() ){
            return false;
        }
        return true;
    }

    /**
     * <将字符串转换成int类型的list>
     * <功能详细描述>
     * @param in_str
     * @return [参数说明]
     *
     * @return List<Integer> [返回类型说明]
     * @exception throws [违例类型] [违例说明]
     * @see [类、类#方法、类#成员]
     */
    public static List<Integer> splitStringToList(String in_str){
        List<Integer> result =  new ArrayList<Integer>();
        if(isNotNull(in_str)){
            String[] in_arr =  in_str.split(",");
            for(String in:in_arr){
                try{
                    if(null !=in &&in.length()>0){
                        result.add(Integer.parseInt(in));
                    }
                }catch(Exception e){
                    log.info("message{}",e.getMessage());
                }
            }
        }
        return result;
    }

    /**
     *
     * @param in_str
     * @param split	分隔符
     * @return
     */
    public static List<Integer> splitStringToList(String in_str, String split){
        List<Integer> result =  new ArrayList<Integer>();
        if(isNotNull(in_str)){
            String[] in_arr =  in_str.split(split);
            for(String in:in_arr){
                try{
                    if(null !=in &&in.length()>0){
                        result.add(Integer.parseInt(in));
                    }
                }catch(Exception e){
                    log.info("message{}",e.getMessage());
                }
            }
        }
        return result;
    }
    /**
     *
     * @param split	分隔符
     * @param num	个数
     * @return
     */
    public static List<Integer> splitStringToList(String in_str,String split, int num) {
        List<Integer> result =  new ArrayList<Integer>();
        if(isNotNull(in_str)){
            String[] in_arr =  in_str.split(split);
            if(in_arr.length<num){
                num = in_arr.length;
            }
            for(int i = 0 ; i<num ; i++){
                String in = in_arr[i];
                try{
                    if(null !=in &&in.length()>0){
                        result.add(Integer.parseInt(in));
                    }
                }catch(Exception e){
                    log.info("message{}",e.getMessage());
                }
            }
        }
        return result;
    }

    public static String splitMoney(String s) {
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();

        String format =currencyFormat.format(Double.parseDouble(s));
        if(format.contains("￥")) {
            format = format.replace("￥", "");
        }
        if(format.contains("$")) {
            format = format.replace("$", "");
        }
        return format;
    }

    public static Double enhanceDigit(Double number){
        return number==null?0d:number;
    }
    public static Float enhanceDigit(Float number){
        return number==null?0f:number;
    }
    public static Integer enhanceDigit(Integer number){
        return number==null?0:number;
    }

    public static String getEnASCII(String str){
        StringBuffer bf = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            char tmp = str.charAt(i);
            bf.append(Integer.valueOf(tmp));
            /*if ((tmp >= 'A' && tmp <= 'Z') || (tmp >= 'a' && tmp <= 'z')){
            }*/
        }
        return bf.toString();
    }

    public static String getUID(String userId, String id){
        if(Help.isNull(userId)){
            return null;
        }
        String enASCII = id+getEnASCII(userId);
        int length = enASCII.length();
        if(length >= 8){
            enASCII = enASCII.substring(0,8);
        }else{
            String s = "";
            for (int i = 0; i < 8-length; i++) {
                s += i;
            }
            enASCII += s;
        }
        return enASCII;
    }

    public static void main(String[] args) {
        Double d = enhanceDigit(12.56d);
        Float f = enhanceDigit(12.5f);
        Integer i = enhanceDigit(12);
        System.out.println("d:"+d+"----f:"+f+"----i:"+i);
        Double d0 = null;
        d0 = enhanceDigit(d0);

        Float f0 = null;
        f0 = enhanceDigit(f0);


        Integer i0 = null;
        i0 = enhanceDigit(i0);
        System.out.println("d0:"+d0+"----f0:"+f0+"----i0:"+i0);
    }
}
