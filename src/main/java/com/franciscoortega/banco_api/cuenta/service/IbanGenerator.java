package com.franciscoortega.banco_api.cuenta.service;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.springframework.stereotype.Component;

@Component
public class IbanGenerator {

    private static final SecureRandom RANDOM = new SecureRandom();

    public String generar() {
        StringBuilder bban = new StringBuilder();
        for (int i = 0; i < 20; i++) {
            bban.append(RANDOM.nextInt(10));
        }
        BigInteger numero = new BigInteger(bban + "142800");
        int control = 98 - numero.mod(BigInteger.valueOf(97)).intValue();
        return String.format("ES%02d%s", control, bban);
    }
}