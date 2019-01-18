package com.mrl.primary;

import java.lang.reflect.Field;

/**
 * @ClassName ModifyString
 * @Description TODO
 * @Author lwq
 * @Date 2019/1/15 17:10
 * @Version 1.0
 */
public class ModifyString {

    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        String str = "Hello World";
        System.out.println("str="+str);

        Field value = String.class.getDeclaredField("value");

        value.setAccessible(true);

        //获取str对象上的value属性值
        char[] v = (char[]) value.get(str);

        v[5] = '_';
        System.out.println("str="+str);

        Class c = int.class;

    }
}
