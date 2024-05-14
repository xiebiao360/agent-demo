package com.foocloud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyAtm {
    private static Logger LOGGER = LoggerFactory.getLogger(MyAtm.class);

    private static final int account = 10;

    public static void withdrawMoney(int amount) throws InterruptedException {
        Thread.sleep(2000l); // processing going on here
        LOGGER.info("[Application] Successful Withdrawal of [{}] units!", amount);
    }
}
