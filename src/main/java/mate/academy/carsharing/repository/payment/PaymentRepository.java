package mate.academy.carsharing.repository.payment;

import java.util.List;
import java.util.Optional;
import mate.academy.carsharing.model.Payment;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    List<Payment> findAllByUserId(Long userId, Pageable pageable);

    Optional<Payment> findByStatusAndUserId(Payment.Status status, Long userId);
}
