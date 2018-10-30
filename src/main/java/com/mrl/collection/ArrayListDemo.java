package com.mrl.collection;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class ArrayListDemo
{

    public static void main(String[] args) {
        
//        Double[] arrInt = new Double[10];
//        for(Double x : arrInt){
//            System.err.println(x);
//        }
        
        
//        int[] arrInt2 = new int[2];
//        arrInt[0] = 1;
//        arrInt[1] = 2;
//        arrInt[2] = 3;
//
//        
//        //Arrays
//        int[] copyOf = Arrays.copyOf(arrInt, 3);
//        Arrays.stream(copyOf).forEach(System.out::print);
//        System.out.println("--------------");
////        System.out.println(copyOf.length);
//        Arrays.stream(arrInt).forEach(System.out::print);
//        // 1 2 3 4 5  5-2
//        System.out.println("--------------");
//        System.arraycopy(arrInt, 0, arrInt, 0, 2);
//        
//        System.out.println("--------------");
//        Arrays.stream(arrInt2).forEach(System.out::print);

        
        ArrayList<String> a = new ArrayList<>();
        a.add("1");
        a.add("2");
        a.add("3");
        a.add("4");
        a.add("5");
        
        
        ArrayList<String> b = new ArrayList<>();
        b.add("1");
        b.add("2");
        b.add("7");
        
//        boolean removeAll = a.removeAll(b);
//        a.stream().forEach(System.out::println);
        
//        Iterator<String> iterator = b.iterator();
//        while(iterator.hasNext()){
//            iterator.remove();
////            System.out.println(iterator.next());;
//        }
        
       

        b.forEach(c->{
            boolean removeIf = b.removeIf(R->R.equals("2"));
            System.out.println(c);
        });

        
    }
    
}
