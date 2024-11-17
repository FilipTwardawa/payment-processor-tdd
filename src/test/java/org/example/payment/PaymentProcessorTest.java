package org.example.payment;

import org.example.payment.mock.PaymentGatewayMock;
import org.example.payment.spy.PaymentGatewaySpy;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PaymentProcessorTest {

    @Test
    void testProcessPaymentSuccess() {
        PaymentGateway mock = new PaymentGatewayMock();
        PaymentProcessor processor = new PaymentProcessor(mock);

        TransactionResult result = processor.processPayment("user123", 50.0);
        assertTrue(result.isSuccess());
        assertEquals("txn123", result.getTransactionId());
    }

    @Test
    void testRefundPaymentSuccess() {
        PaymentGateway mock = new PaymentGatewayMock();
        PaymentProcessor processor = new PaymentProcessor(mock);

        TransactionResult result = processor.processPayment("user123", 50.0);
        TransactionResult refundResult = processor.refundPayment(result.getTransactionId());

        assertTrue(refundResult.isSuccess());
    }

    @Test
    void testGetPaymentStatus() {
        PaymentGateway mock = new PaymentGatewayMock();
        PaymentProcessor processor = new PaymentProcessor(mock);

        TransactionResult result = processor.processPayment("user123", 50.0);
        TransactionStatus status = processor.getPaymentStatus(result.getTransactionId());

        assertEquals(TransactionStatus.COMPLETED, status);
    }

    @Test
    void testProcessPaymentWithInvalidAmount() {
        PaymentGateway mock = new PaymentGatewayMock();
        PaymentProcessor processor = new PaymentProcessor(mock);

        assertThrows(IllegalArgumentException.class, () -> processor.processPayment("user123", -50.0));
    }

    @Test
    void testSpyForChargeCallCount() {
        PaymentGatewaySpy spy = new PaymentGatewaySpy();
        PaymentProcessor processor = new PaymentProcessor(spy);

        processor.processPayment("user123", 50.0);
        assertEquals(1, spy.getChargeCallCount());
    }
}