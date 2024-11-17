package org.example.payment.spy;

import org.example.payment.*;

public class PaymentGatewaySpy implements PaymentGateway {
    private int chargeCallCount = 0;
    private int refundCallCount = 0;
    private int statusCallCount = 0;

    @Override
    public TransactionResult charge(String userId, double amount) {
        chargeCallCount++;
        return new TransactionResult(true, "txn123", "Payment successful");
    }

    @Override
    public TransactionResult refund(String transactionId) {
        refundCallCount++;
        return new TransactionResult(true, transactionId, "Refund successful");
    }

    @Override
    public TransactionStatus getStatus(String transactionId) {
        statusCallCount++;
        return TransactionStatus.COMPLETED;
    }

    public int getChargeCallCount() {
        return chargeCallCount;
    }

    public int getRefundCallCount() {
        return refundCallCount;
    }

    public int getStatusCallCount() {
        return statusCallCount;
    }
}
