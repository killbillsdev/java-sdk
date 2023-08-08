package com.example;

import java.util.List;
import java.util.Map;

/**
 * Hello world!
 *
 */
public class App {
    public static void main(String[] args) {
        System.out.println("Hello World!");
        int sum = Sdk.add(8, 2); // Use the add method from the imported package
        System.out.println("Sum: " + sum);
        List<Map<String, Object>> stores = Sdk.getStores("prod", "e82376a1-2869-461b-9a6b-1f10bc87bedc");
        System.out.println("Sum: " + stores);
    }
}
