package org.example.payment.mock;

import org.example.payment.*;
import org.example.payment.exceptions.NetworkException;
import org.example.payment.exceptions.PaymentException;
import org.example.payment.exceptions.RefundException;

public class PaymentGatewayMock implements PaymentGateway {

    @Override
    public TransactionResult charge(String userId, double amount) throws NetworkException, PaymentException {
        // Simulating a successful payment
        return new TransactionResult(true, "txn123", "Payment successful");
    }

    @Override
    public TransactionResult refund(String transactionId) throws NetworkException, RefundException {
        // Simulating a successful refund
        return new TransactionResult(true, transactionId, "Refund successful");
    }

    @Override
    public TransactionStatus getStatus(String transactionId) throws NetworkException {
        // Simulating a completed status
        return TransactionStatus.COMPLETED;
    }
}
