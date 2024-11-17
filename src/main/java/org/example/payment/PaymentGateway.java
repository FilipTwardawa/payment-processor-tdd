package org.example.payment;

import org.example.payment.exceptions.*;

public interface PaymentGateway {
    TransactionResult charge(String userId, double amount) throws NetworkException, PaymentException;

    TransactionResult refund(String transactionId) throws NetworkException, RefundException;

    TransactionStatus getStatus(String transactionId) throws NetworkException;
}
