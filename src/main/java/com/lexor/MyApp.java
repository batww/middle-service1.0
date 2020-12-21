package com.lexor;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@ApplicationPath("/")
public class MyApp extends Application {
    @Override
    public Set<Class<?>> getClasses() {
        Set<Class<?>> h = new HashSet<>();
        return h;
    }

//    public static void main(String[] args) {
//        ExecutorService executorService = Executors.newFixedThreadPool(5);
//        for (int i = 0; i < 10; i++) {
//            MyRunnable myRunnable = new MyRunnable(i);
//            executorService.execute(myRunnable);
//        }
//        executorService.shutdown();
//
//    }
}

//class MyRunnable implements Runnable{
//    private final int number;
//
//    public MyRunnable(int number) {
//        this.number = number;
//    }
//
//    @Override
//    public void run() {
//        System.out.println(number + "now started");
//        try {
//            Thread.sleep(2000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        System.out.println(number + "now stop");
//    }
//}
