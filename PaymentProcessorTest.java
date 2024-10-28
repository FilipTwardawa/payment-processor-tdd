import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class PaymentProcessorTest {
    @Mock
    private PaymentGateway paymentGateway;
    private PaymentProcessor paymentProcessor;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
        paymentProcessor = new PaymentProcessor(paymentGateway);
    }

    @Test
    public void testProcessPayment_Successful() throws Exception {
        TransactionResult result = new TransactionResult(true, "12345", "Payment successful");
        when(paymentGateway.charge("user1", 100.0)).thenReturn(result);

        TransactionResult response = paymentProcessor.processPayment("user1", 100.0);
        assertTrue(response.isSuccess());
        assertEquals("12345", response.getTransactionId());
        verify(paymentGateway).charge("user1", 100.0);
    }

    @Test
    public void testProcessPayment_FailureDueToFunds() throws Exception {
        TransactionResult result = new TransactionResult(false, "12345", "Insufficient funds");
        when(paymentGateway.charge("user1", 100.0)).thenReturn(result);

        TransactionResult response = paymentProcessor.processPayment("user1", 100.0);
        assertFalse(response.isSuccess());
        assertEquals("Insufficient funds", response.getMessage());
    }

    @Test
    public void testProcessPayment_NetworkException() throws Exception {
        when(paymentGateway.charge("user1", 100.0)).thenThrow(new NetworkException("Network error"));

        TransactionResult response = paymentProcessor.processPayment("user1", 100.0);
        assertFalse(response.isSuccess());
        assertEquals("Network error", response.getMessage());
    }

    @Test
    public void testRefundPayment_Successful() throws Exception {
        TransactionResult result = new TransactionResult(true, "54321", "Refund successful");
        when(paymentGateway.refund("54321")).thenReturn(result);

        TransactionResult response = paymentProcessor.refundPayment("54321");
        assertTrue(response.isSuccess());
        assertEquals("54321", response.getTransactionId());
        verify(paymentGateway).refund("54321");
    }

    @Test
    public void testGetPaymentStatus_Success() throws Exception {
        when(paymentGateway.getStatus("54321")).thenReturn(TransactionStatus.COMPLETED);

        TransactionStatus status = paymentProcessor.getPaymentStatus("54321");
        assertEquals(TransactionStatus.COMPLETED, status);
    }

    @Test
    public void testGetPaymentStatus_NetworkException() throws Exception {
        when(paymentGateway.getStatus("54321")).thenThrow(new NetworkException("Network error"));

        TransactionStatus status = paymentProcessor.getPaymentStatus("54321");
        assertEquals(TransactionStatus.FAILED, status);
    }

    @Test
    public void testProcessPayment_InvalidAmount() {
        assertThrows(IllegalArgumentException.class, () -> paymentProcessor.processPayment("user1", -50));
    }

    @Test
    public void testProcessPayment_EmptyUserId() {
        assertThrows(IllegalArgumentException.class, () -> paymentProcessor.processPayment("", 100));
    }

    @Test
    public void testRefundPayment_InvalidTransactionId() {
        assertThrows(IllegalArgumentException.class, () -> paymentProcessor.refundPayment(""));
    }
}
