package com.mrl.collection.linedlist;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

/**
 * @ClassName LinkedListDemo
 * @Description TODO
 * @Author lwq
 * @Date 2019/1/21 16:17
 * @Version 1.0
 */
public class LinkedListDemo {



    public static void main(String[] args) {
       //测试ArrayList和LinkedList集合增删改速度
        //testCRUD();

        //源码分析
        //1.构造函数
       LinkedList linkedList = new LinkedList();
       //有参构造
        LinkedList linkedList2 = new LinkedList(Arrays.asList("1","2","3","4"));

//        for(int i=0;i<100000;i++){
//            linkedList2.add("hahah"+i);
//        }

//        for (int i = 0; i < linkedList2.size(); i++) {
//            System.out.println(linkedList2.get(i));
//        }
//
//        for (Object o : linkedList2) {
//            System.out.println(o);
//        }

//        long timeStart = System.currentTimeMillis();
//
        for(Iterator iterator = linkedList2.listIterator();iterator.hasNext();){
            Object next = iterator.next();
            if("4".equals(next)){
                iterator.remove();
            }
        }
//        for (int i = 0; i < linkedList2.size(); i++) {
//            System.out.println(linkedList2.get(i));
//        }
//        for (Object o : linkedList2) {
//            System.out.println(o);
//        }
//        long timeEnd= System.currentTimeMillis();
//        System.err.println(timeEnd-timeStart);//681
    }

    private static void testCRUD() {

        //测试ArrayList和LinkedList集合增删改速度

//        ArrayList arrayList = testArrayListCRUD();//6407
//        long timeStart = System.currentTimeMillis();
////        arrayList.add("测试哈哈哈");
//        arrayList.remove(1);
//        long timeEnd= System.currentTimeMillis();
//        System.err.println(timeEnd-timeStart);

//
//        LinkedList linkedList = testLinkedListCRUD();//6407
//        long timeStart = System.currentTimeMillis();
////        linkedList.add("测试30000000");
//        linkedList.remove(1);
//        long timeEnd= System.currentTimeMillis();
//        System.err.println(timeEnd-timeStart);

        //testLinkedListCRUD();//


    }

    private static ArrayList testArrayListCRUD() {
//        long timeStart = System.currentTimeMillis();


        ArrayList<String> list = new ArrayList<>(30000000);

        for(int i=0;i<30000000;i++){
            list.add("测试"+i);
        }

        long timeEnd= System.currentTimeMillis();

//        System.out.println(timeEnd-timeStart);
        return list;
    }
    private static LinkedList testLinkedListCRUD() {
        long timeStart = System.currentTimeMillis();


        LinkedList<String> list = new LinkedList<>();

        for(int i=0;i<30000000;i++){
            list.add("测试"+i);
        }

        long timeEnd= System.currentTimeMillis();

//        System.out.println(timeEnd-timeStart);
        return list;
    }
}
