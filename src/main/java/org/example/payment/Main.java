package org.example.payment;

import org.example.payment.mock.PaymentGatewayMock;

public class Main {
    public static void main(String[] args) {
        PaymentGateway mock = new PaymentGatewayMock();
        PaymentProcessor processor = new PaymentProcessor(mock);

        // Example usage
        TransactionResult result = processor.processPayment("user123", 50.0);
        System.out.println("Payment success: " + result.isSuccess());

        TransactionResult refundResult = processor.refundPayment(result.getTransactionId());
        System.out.println("Refund success: " + refundResult.isSuccess());
    }
}
