package com.origamimc.strata.utils;

public class SleepUtils {

    // TODO I don't think we need to sleep in most cases
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
