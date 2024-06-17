package mate.academy.carsharing.service.payment;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityNotFoundException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import mate.academy.carsharing.dto.payment.PaymentResponseDto;
import mate.academy.carsharing.mapper.PaymentMapper;
import mate.academy.carsharing.model.Car;
import mate.academy.carsharing.model.Payment;
import mate.academy.carsharing.model.Rental;
import mate.academy.carsharing.model.User;
import mate.academy.carsharing.repository.car.CarRepository;
import mate.academy.carsharing.repository.payment.PaymentRepository;
import mate.academy.carsharing.repository.rental.RentalRepository;
import mate.academy.carsharing.service.payment.strategy.PaymentStrategy;
import mate.academy.carsharing.service.stripe.StripeService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {
    private static final int ONE = 1;
    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final RentalRepository rentalRepository;
    private final CarRepository carRepository;
    private final StripeService stripeService;
    private final PaymentStrategy paymentStrategy;
    @Value("${stripe.api.key}")
    private String apiKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = apiKey;
    }

    @Override
    public List<PaymentResponseDto> getUserPayments(Long userId, Pageable pageable) {
        return paymentRepository.findAllByUserId(userId, pageable)
                .stream()
                .map(paymentMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public PaymentResponseDto create(User user)
            throws StripeException, MalformedURLException {
        if (paymentRepository.findByStatusAndUserId(
                        Payment.Status.PENDING,
                        user.getId()).isPresent()) {
            throw new IllegalArgumentException(""" 
                    You already have a pending payment.
                    + You must pay for it first or cancel your lease""");
        }
        List<Rental> rentals = rentalRepository.findAllByUserId(user.getId());
        if (rentals.isEmpty()) {
            throw new EntityNotFoundException(
                    "No rentals found for user with id - " + user.getId());
        }
        Rental rental = rentals.get(rentals.size() - 1);
        Car car = carRepository.findById(rental.getCarId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find a car with id - " + rental.getCarId()));
        long days = ChronoUnit.DAYS.between(rental.getRentalDate(),
                rental.getRequiredReturnDate());
        BigDecimal price = paymentStrategy.getPaymentService(Payment.Type.PAYMENT)
                .calculateAmount(car.getDailyFee(), days);
        Session session = stripeService.createSession(
                price.longValue(),
                car.getBrand() + " " + car.getModel());
        Payment payment = createPayment(price
                .multiply(BigDecimal.valueOf(0.01))
                .setScale(2,
                         RoundingMode.HALF_UP),
                rental, session, user);
        paymentRepository.save(payment);
        return paymentMapper.toResponseDto(payment);
    }

    @Override
    @Transactional
    public PaymentResponseDto success(User user) {
        Payment payment = updatePaymentStatus(Payment.Status.PAID, user.getId());
        paymentRepository.save(payment);

        return paymentMapper.toResponseDto(payment);
    }

    @Override
    @Transactional
    public void cancel(User user) {
        List<Rental> rentals = rentalRepository.findAllByUserId(user.getId());
        if (rentals.isEmpty()) {
            throw new EntityNotFoundException(
                    "No rentals found for user with id - " + user.getId());
        }
        Rental latestRental = rentals.get(rentals.size() - 1);

        LocalDateTime now = LocalDateTime.now();
        if (now.isBefore(latestRental.getRentalDate().atStartOfDay())) {
            Payment payment = updatePaymentStatus(Payment.Status.CANCELED, user.getId());
            paymentRepository.save(payment);

            Car car = carRepository.findById(latestRental.getCarId())
                    .orElseThrow(() -> new EntityNotFoundException(
                            "Can't find a car with id - " + latestRental.getCarId()));
            car = updateInventory(latestRental);
            carRepository.save(car);
        } else {
            throw new IllegalStateException(
                    "Cannot cancel the payment because the rental period has already started");
        }
    }

    @Override
    public PaymentResponseDto getMyPayment(User user) {
        Payment payment = paymentRepository.findByStatusAndUserId(
                        Payment.Status.PENDING,
                        user.getId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "You don't have an active payment"));
        return paymentMapper.toResponseDto(payment);
    }

    private Payment createPayment(BigDecimal price, Rental rental,
                    Session session, User user) throws MalformedURLException {
        return Payment.builder()
                .type(Payment.Type.PAYMENT)
                .amountToPay(price)
                .rentalId(rental.getId())
                .sessionId(session.getId())
                .sessionUrl(new URL(session.getUrl()))
                .status(Payment.Status.PENDING)
                .userId(user.getId())
                .build();
    }

    private Car updateInventory(Rental rental) {
        Car car = carRepository.findById(rental.getCarId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Can't find a car with id " + rental.getCarId()));
        car.setInventory(car.getInventory() + ONE);
        return car;
    }

    private Payment updatePaymentStatus(Payment.Status status, Long userId) {
        Optional<Payment> optionalPayment = paymentRepository
                .findByStatusAndUserId(Payment.Status.PENDING, userId);
        if (optionalPayment.isPresent()) {
            Payment payment = optionalPayment.get();
            payment.setStatus(status);
            return paymentRepository.save(payment);
        } else {
            // Handle the case where payment is not found
            return null;
        }
    }
}
