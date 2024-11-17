package org.example.payment;

import org.example.payment.exceptions.*;

public class PaymentProcessor {
    private final PaymentGateway paymentGateway;

    public PaymentProcessor(PaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    public TransactionResult processPayment(String userId, double amount) {
        if (userId == null || userId.isEmpty()) throw new IllegalArgumentException("UserId cannot be null or empty");
        if (amount <= 0) throw new IllegalArgumentException("Amount must be positive");

        try {
            return paymentGateway.charge(userId, amount);
        } catch (NetworkException | PaymentException e) {
            System.err.println("Error processing payment: " + e.getMessage());
            return new TransactionResult(false, null, e.getMessage());
        }
    }

    public TransactionResult refundPayment(String transactionId) {
        if (transactionId == null || transactionId.isEmpty())
            throw new IllegalArgumentException("TransactionId cannot be null or empty");

        try {
            return paymentGateway.refund(transactionId);
        } catch (NetworkException | RefundException e) {
            System.err.println("Error processing refund: " + e.getMessage());
            return new TransactionResult(false, null, e.getMessage());
        }
    }

    public TransactionStatus getPaymentStatus(String transactionId) {
        if (transactionId == null || transactionId.isEmpty())
            throw new IllegalArgumentException("TransactionId cannot be null or empty");

        try {
            return paymentGateway.getStatus(transactionId);
        } catch (NetworkException e) {
            System.err.println("Error retrieving status: " + e.getMessage());
            return TransactionStatus.FAILED;
        }
    }
}
