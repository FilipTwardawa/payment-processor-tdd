public class Main {
    public static void main(String[] args) {
        PaymentGateway mockGateway = new MockPaymentGateway();
        PaymentProcessor processor = new PaymentProcessor(mockGateway);

        TransactionResult paymentResult = processor.processPayment("user123", 100.0);
        System.out.println("Payment Result: " + paymentResult.getMessage());

        TransactionStatus status = processor.getPaymentStatus(paymentResult.getTransactionId());
        System.out.println("Payment Status: " + status);

        TransactionResult refundResult = processor.refundPayment(paymentResult.getTransactionId());
        System.out.println("Refund Result: " + refundResult.getMessage());
    }
}

class MockPaymentGateway implements PaymentGateway {
    @Override
    public TransactionResult charge(String userId, double amount) throws NetworkException, PaymentException {
        return new TransactionResult(true, "trans123", "Payment processed successfully.");
    }

    @Override
    public TransactionResult refund(String transactionId) throws NetworkException, RefundException {
        return new TransactionResult(true, transactionId, "Refund processed successfully.");
    }

    @Override
    public TransactionStatus getStatus(String transactionId) throws NetworkException {
        return TransactionStatus.COMPLETED;
    }
}
