public class PaymentProcessor {
    private final PaymentGateway paymentGateway;

    public PaymentProcessor(PaymentGateway paymentGateway) {
        this.paymentGateway = paymentGateway;
    }

    public TransactionResult processPayment(String userId, double amount) {
        if (userId == null || userId.isEmpty()) {
            throw new IllegalArgumentException("User ID cannot be null or empty.");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive.");
        }

        try {
            TransactionResult result = paymentGateway.charge(userId, amount);
            logTransaction(result);
            return result;
        } catch (NetworkException | PaymentException e) {
            logError("Payment processing failed for userId: " + userId, e);
            return new TransactionResult(false, null, e.getMessage());
        }
    }

    public TransactionResult refundPayment(String transactionId) {
        if (transactionId == null || transactionId.isEmpty()) {
            throw new IllegalArgumentException("Transaction ID cannot be null or empty.");
        }

        try {
            TransactionResult result = paymentGateway.refund(transactionId);
            logTransaction(result);
            return result;
        } catch (NetworkException | RefundException e) {
            logError("Refund failed for transactionId: " + transactionId, e);
            return new TransactionResult(false, null, e.getMessage());
        }
    }

    public TransactionStatus getPaymentStatus(String transactionId) {
        if (transactionId == null || transactionId.isEmpty()) {
            throw new IllegalArgumentException("Transaction ID cannot be null or empty.");
        }

        try {
            return paymentGateway.getStatus(transactionId);
        } catch (NetworkException e) {
            logError("Failed to retrieve status for transactionId: " + transactionId, e);
            return TransactionStatus.FAILED;
        }
    }

    private void logTransaction(TransactionResult result) {
        if (result.isSuccess()) {
            System.out.println("Transaction successful: " + result.getTransactionId());
        } else {
            System.out.println("Transaction failed: " + result.getMessage());
        }
    }

    private void logError(String message, Exception e) {
        System.err.println(message);
        e.printStackTrace();
    }
}
