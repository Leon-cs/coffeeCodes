package com.coffeeCodes.common.exceptions;

/**
 * Created by ChangSheng on 2017/1/16 19:50.
 */
public class CoffeeCodesException extends RuntimeException{

    public CoffeeCodesException() {
    }
    public CoffeeCodesException(Throwable e) {
        super(e);
    }

    public CoffeeCodesException(String message) {
        super(message);
    }

    public CoffeeCodesException(String message, Throwable e) {
        super(message, e);
    }
}
