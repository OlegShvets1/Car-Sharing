package mate.academy.carsharing.service.payment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.stripe.Stripe;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import mate.academy.carsharing.dto.payment.PaymentResponseDto;
import mate.academy.carsharing.exception.PaymentNotFoundException;
import mate.academy.carsharing.mapper.PaymentMapper;
import mate.academy.carsharing.model.Payment;
import mate.academy.carsharing.model.Rental;
import mate.academy.carsharing.model.User;
import mate.academy.carsharing.repository.car.CarRepository;
import mate.academy.carsharing.repository.payment.PaymentRepository;
import mate.academy.carsharing.repository.rental.RentalRepository;
import mate.academy.carsharing.service.stripe.StripeService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class PaymentServiceImplTest {
    private static final String SESSION_URL = """
            https://checkout.stripe.com/c/pay/cs_test_a1ndISstjYYKtBLevlLr5hwVYP
            + fj3UTnSyOjMsXbUTAf1rMKdKTMx7AnXt#fidkdWxOYHwnPyd1blpxYHZxWlFcampIV
            + GRwc2FAQXQwMUtsUXVtTDJvfScpJ2N3amhWYHdzYHcnP3F3cGApJ2lkfGpwcVF8dWA
            + nPyd2bGtiaWBabHFgaCcpJ2BrZGdpYFVpZGZgbWppYWB3dic%2FcXdwYHgl""";
    private static final String SESSION_ID =
            "cs_test_a1ndISstjYYKtBLevlLr5hwVYPfj3UTnSyOjMsXbUTAf1rMKdKTMx7AnXt";

    @InjectMocks
    private PaymentServiceImpl paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private RentalRepository rentalRepository;

    @Mock
    private CarRepository carRepository;

    @Mock
    private PaymentMapper paymentMapper;

    @Mock
    private StripeService stripeService;

    @BeforeAll
    static void beforeAll() {
        Stripe.apiKey = "sk_test_4eC39HqLyjWDarjtT1zdp7dc";
    }

    @Test
    void getUserPayments_ValidParams_Ok()
            throws MalformedURLException {
        Pageable pageable = PageRequest.of(0, 5);

        Payment firstPayment = createPayment(Payment.Status.PAID, 1L, BigDecimal.valueOf(10 + 1));
        Payment secondPayment = createPayment(Payment.Status.PAID, 2L, BigDecimal.valueOf(10 + 2));

        List<Payment> expectedPayments = List.of(firstPayment, secondPayment);

        PaymentResponseDto firstDto = createResponseDto(firstPayment);
        PaymentResponseDto secondDto = createResponseDto(secondPayment);

        when(paymentRepository.findAllByUserId(1L, pageable)).thenReturn(expectedPayments);
        when(paymentMapper.toResponseDto(firstPayment)).thenReturn(firstDto);
        when(paymentMapper.toResponseDto(secondPayment)).thenReturn(secondDto);

        List<PaymentResponseDto> expected = List.of(firstDto, secondDto);
        List<PaymentResponseDto> actual = paymentService.getUserPayments(1L, pageable);

        assertEquals(expected, actual);

        verify(paymentRepository, times(1)).findAllByUserId(any(), any());
        verify(paymentMapper, times(2)).toResponseDto(any());
        verifyNoMoreInteractions(paymentRepository);
        verifyNoMoreInteractions(paymentMapper);
    }

    @Test
    public void testCancel_NoRentalsFound() {
        User user = new User();
        user.setId(1L);
        when(rentalRepository.findAllByUserId(user.getId())).thenReturn(new ArrayList<>());

        assertThrows(EntityNotFoundException.class, () -> paymentService.cancel(user));
    }

    @Test
    public void testCancel_RentalPeriodNotStarted() {
        User user = new User();
        user.setId(1L);
        Rental rental = new Rental();
        rental.setId(1L);
        rental.setCarId(1L);
        rental.setRentalDate(LocalDate.now().plusDays(1));

        List<Rental> rentals = new ArrayList<>();
        rentals.add(rental);

        when(rentalRepository.findAllByUserId(user.getId())).thenReturn(rentals);

        assertThrows(PaymentNotFoundException.class, () -> paymentService.cancel(user));
    }

    @Test
    void getMyPayment_ValidParams_Ok()
            throws MalformedURLException {
        Payment payment = createPayment(Payment.Status.PENDING, 1L, BigDecimal.TEN);

        PaymentResponseDto expected = createResponseDto(payment);

        User user = new User();
        user.setId(1L);

        when(paymentRepository.findByStatusAndUserId(Payment.Status.PENDING, user.getId()))
                .thenReturn(Optional.of(payment));
        when(paymentMapper.toResponseDto(any())).thenReturn(expected);

        PaymentResponseDto actual = paymentService.getMyPayment(user);

        assertEquals(expected, actual);
    }

    @Test
    void getMyPayment_NonValidParams_ThrowsException() {
        User user = new User();
        user.setId(1L);

        when(paymentRepository.findByStatusAndUserId(any(), any())).thenReturn(Optional.empty());

        EntityNotFoundException exception =
                assertThrows(EntityNotFoundException.class,
                        () -> paymentService.getMyPayment(user));

        String expected = "You don't have an active payment";
        String actual = exception.getMessage();

        assertEquals(expected, actual);
    }

    private PaymentResponseDto createResponseDto(Payment payment) {
        return new PaymentResponseDto(
                payment.getId(),
                payment.getRentalId(),
                payment.getStatus(),
                payment.getType(),
                payment.getSessionUrl(),
                payment.getSessionId(),
                payment.getAmountToPay());
    }

    private Payment createPayment(Payment.Status status, long rentalId, BigDecimal price)
            throws MalformedURLException {
        return Payment.builder()
                .sessionUrl(new URL(SESSION_URL))
                .sessionId(SESSION_ID)
                .status(status)
                .type(Payment.Type.PAYMENT)
                .userId(1L)
                .rentalId(rentalId)
                .build();
    }
}
