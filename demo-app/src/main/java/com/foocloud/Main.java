package com.foocloud;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {
    private static Logger LOGGER = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        LOGGER.info("[Application] Starting ATM application");
        try {
            MyAtm.withdrawMoney(1000);

            Thread.sleep(1000);

            MyAtm.withdrawMoney(1000);
        } catch (NumberFormatException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}