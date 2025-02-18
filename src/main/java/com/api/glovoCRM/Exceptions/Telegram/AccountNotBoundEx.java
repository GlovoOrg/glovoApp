package com.api.glovoCRM.Exceptions.Telegram;

public class AccountNotBoundEx extends RuntimeException {
    public AccountNotBoundEx(String message) {
        super(message);
    }
}