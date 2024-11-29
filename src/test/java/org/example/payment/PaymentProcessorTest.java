package org.example.payment;

import org.example.payment.exceptions.*;
import org.example.payment.mock.PaymentGatewayMock;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentProcessorTest {

    // Test for processPayment
    @Test
    void testProcessPaymentSuccess() {
        PaymentGateway mock = new PaymentGatewayMock();
        PaymentProcessor processor = new PaymentProcessor(mock);

        TransactionResult result = processor.processPayment("user123", 50.0);
        assertTrue(result.isSuccess());
        assertEquals("txn123", result.getTransactionId());
        assertEquals("Payment successful", result.getMessage());
    }

    @Test
    void testProcessPaymentInsufficientFunds() throws NetworkException, PaymentException {
        PaymentGateway mock = new PaymentGateway() {
            @Override
            public TransactionResult charge(String userId, double amount) throws PaymentException {
                throw new PaymentException("Insufficient funds");
            }

            @Override
            public TransactionResult refund(String transactionId) { return null; }

            @Override
            public TransactionStatus getStatus(String transactionId) { return null; }
        };

        PaymentProcessor processor = new PaymentProcessor(mock);
        TransactionResult result = processor.processPayment("user123", 50.0);

        assertFalse(result.isSuccess());
        assertNull(result.getTransactionId());
        assertEquals("Insufficient funds", result.getMessage());
    }

    @Test
    void testProcessPaymentHandlesNetworkException() throws NetworkException, PaymentException {
        PaymentGateway mock = new PaymentGateway() {
            @Override
            public TransactionResult charge(String userId, double amount) throws NetworkException {
                throw new NetworkException("Network error");
            }

            @Override
            public TransactionResult refund(String transactionId) { return null; }

            @Override
            public TransactionStatus getStatus(String transactionId) { return null; }
        };

        PaymentProcessor processor = new PaymentProcessor(mock);
        TransactionResult result = processor.processPayment("user123", 50.0);

        assertFalse(result.isSuccess());
        assertNull(result.getTransactionId());
        assertEquals("Network error", result.getMessage());
    }

    @Test
    void testProcessPaymentWithInvalidAmount() {
        PaymentGateway mock = new PaymentGatewayMock();
        PaymentProcessor processor = new PaymentProcessor(mock);

        assertThrows(IllegalArgumentException.class, () -> processor.processPayment("user123", -50.0));
    }

    // Test for refundPayment
    @Test
    void testRefundPaymentSuccess() {
        PaymentGateway mock = new PaymentGatewayMock();
        PaymentProcessor processor = new PaymentProcessor(mock);

        TransactionResult refundResult = processor.refundPayment("txn123");
        assertTrue(refundResult.isSuccess());
        assertEquals("Refund successful", refundResult.getMessage());
    }

    @Test
    void testRefundPaymentWithNonexistentTransaction() throws RefundException, NetworkException {
        PaymentGateway mock = new PaymentGateway() {
            @Override
            public TransactionResult refund(String transactionId) throws RefundException {
                throw new RefundException("Transaction does not exist");
            }

            @Override
            public TransactionResult charge(String userId, double amount) { return null; }

            @Override
            public TransactionStatus getStatus(String transactionId) { return null; }
        };

        PaymentProcessor processor = new PaymentProcessor(mock);
        TransactionResult result = processor.refundPayment("nonexistent_txn");

        assertFalse(result.isSuccess());
        assertEquals("Transaction does not exist", result.getMessage());
    }

    @Test
    void testRefundPaymentHandlesNetworkException() throws RefundException, NetworkException {
        PaymentGateway mock = new PaymentGateway() {
            @Override
            public TransactionResult refund(String transactionId) throws NetworkException {
                throw new NetworkException("Network error during refund");
            }

            @Override
            public TransactionResult charge(String userId, double amount) { return null; }

            @Override
            public TransactionStatus getStatus(String transactionId) { return null; }
        };

        PaymentProcessor processor = new PaymentProcessor(mock);
        TransactionResult result = processor.refundPayment("txn123");

        assertFalse(result.isSuccess());
        assertEquals("Network error during refund", result.getMessage());
    }

    // Test for getPaymentStatus
    @Test
    void testGetPaymentStatusSuccess() {
        PaymentGateway mock = new PaymentGatewayMock();
        PaymentProcessor processor = new PaymentProcessor(mock);

        TransactionStatus status = processor.getPaymentStatus("txn123");
        assertEquals(TransactionStatus.COMPLETED, status);
    }

    @Test
    void testGetPaymentStatusWithNonexistentTransaction() throws NetworkException {
        PaymentGateway mock = new PaymentGateway() {
            @Override
            public TransactionStatus getStatus(String transactionId) throws NetworkException {
                throw new NetworkException("Transaction not found");
            }

            @Override
            public TransactionResult charge(String userId, double amount) { return null; }

            @Override
            public TransactionResult refund(String transactionId) { return null; }
        };

        PaymentProcessor processor = new PaymentProcessor(mock);
        TransactionStatus status = processor.getPaymentStatus("nonexistent_txn");

        assertEquals(TransactionStatus.FAILED, status);
    }

    @Test
    void testGetPaymentStatusHandlesNetworkException() throws NetworkException {
        PaymentGateway mock = new PaymentGateway() {
            @Override
            public TransactionStatus getStatus(String transactionId) throws NetworkException {
                throw new NetworkException("Network issue while fetching status");
            }

            @Override
            public TransactionResult charge(String userId, double amount) { return null; }

            @Override
            public TransactionResult refund(String transactionId) { return null; }
        };

        PaymentProcessor processor = new PaymentProcessor(mock);
        TransactionStatus status = processor.getPaymentStatus("txn123");

        assertEquals(TransactionStatus.FAILED, status);
    }
}
