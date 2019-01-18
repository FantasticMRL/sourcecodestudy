package com.mrl.primary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @ClassName HashCodeTest
 * @Description TODO
 * @Author lwq
 * @Date 2019/1/15 16:30
 * @Version 1.0
 */
public class HashCodeTest {
    public static void main(String[] args) {
        Set<Computer> set = new HashSet<>();
        set.add(new Computer("联想","黑色"));
        set.add(new Computer("联想","虹色"));
        set.add(new Computer("联想","白色"));
        set.add(new Computer("联想","黑色"));
        set.forEach(System.out::println);
    }
}


@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@ToString
class Computer implements Serializable{

    String brand;
    String color;

    //所有对象hashcode都相同
    @Override
    public int hashCode() {
        return 1;
    }

    //都相等
    @Override
    public boolean equals(Object obj) {
        return true;
    }
}