package com.mrl.collection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * @ClassName ArrayListDemo
 * @Description TODO
 * @Author lwq
 * @Date 10/28/2018 11:30 PM
 * @Version 1.0
 */
public class ArrayListDemo {

    private static int size;

    public static void main(String[] args) {

        //ArrayList源码分析
        //1.构造方法
        //无参构造
        List<String> list1 = new ArrayList<>();
//        System.out.println(list1.size());
//        //有参构造
//        ArrayList<String> list2 = new ArrayList<>(0);
//        System.out.println(list2);
//        //
//        ArrayList<String> list3 = new ArrayList<>(Arrays.asList("2","1"));
//        list3.forEach(System.out::println);

//        if(( size = list3.size()) >0){
//            System.out.println("Hahha");
//        }
//
//        final Object[] a1 = {};
//        final Object[] a2 = {};
//
//        Object[] a = a1;
//
//        System.out.println(a==a1);
//        System.out.println(a==a2);
//
//
//        //新增
//        list2.add("1");
//        list2.add("2");
//
//        System.out.println(5>>1);

        //报错Exception in thread "main" java.lang.OutOfMemoryError: Requested array size exceeds VM limit
//        ArrayList list4 = new ArrayList<>(Integer.MAX_VALUE);

        list1.add("11");
        list1.add("22");
        list1.add("33");
        list1.add("44");
        list1.add("55");
        //list1.forEach(System.out::println);

        for (int i = 0; i < list1.size(); i++) {
            //System.out.println(list1.get(i));
        }

//         for(Iterator<String> iterator = list1.iterator();iterator.hasNext();){
//           // iterator.next();
//        }

        for (String s : list1) {

        }

        list1.forEach(c->{

        });

        list1.subList(1,3).forEach(System.out::println);
        System.out.println("--------");
        list1.forEach(System.out::println);





    }



}
