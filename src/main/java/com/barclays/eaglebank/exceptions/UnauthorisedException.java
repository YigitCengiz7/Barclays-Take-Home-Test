package com.barclays.eaglebank.exceptions;


public class UnauthorisedException extends RuntimeException {
    public UnauthorisedException(String msg) { super(msg); }
}
