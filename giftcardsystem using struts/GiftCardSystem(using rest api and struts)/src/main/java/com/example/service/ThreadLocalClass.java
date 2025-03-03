package com.example.service;

import java.util.HashMap;
import java.util.Map;

public class ThreadLocalClass {

    private static final ThreadLocal<Map<String, Object>> threadLocal = ThreadLocal.withInitial(HashMap::new); //lambda expression

    public static void setUserInfo(long userId, String userType) {
        System.out.println("Thread local setUserInfo called.....");
        Map<String, Object> userInfo = threadLocal.get();
        userInfo.put("userId", userId);
        userInfo.put("userType", userType);
        System.out.println("Stored userId: " + userInfo.get("userId"));
    }

    public static Map<String, Object> getUserInfo() {
        Map<String, Object> userInfo = threadLocal.get();
        if (userInfo.isEmpty()) {
            System.out.println("Warning: ThreadLocal userInfo is empty! Ensure setUserInfo() is called before accessing.");
        }
        return userInfo;
    }

    public static void removeUserInfo() {
        System.out.println("Thread local removeUserInfo called.....");
        threadLocal.remove();
    }
}



//private static final ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal<>() {
//@Override
//protected Map<String, Object> initialValue() {
//    return new HashMap<>();
//}
//};
