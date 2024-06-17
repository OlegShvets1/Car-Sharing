package mate.academy.carsharing.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Builder
@Entity
@Data
@Table(name = "rentals")
@SQLDelete(sql = "UPDATE rentals SET is_deleted = TRUE WHERE id = ?")
@Where(clause = "is_deleted = FALSE")
@NoArgsConstructor
@AllArgsConstructor
public class Rental {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "rental_date")
    private LocalDate rentalDate;

    @NotNull
    @Column(name = "required_return_date")
    private LocalDate requiredReturnDate;

    @Column(name = "actual_return_date")
    private LocalDate actualReturnDate;

    @NotNull
    @Column(name = "car_id")
    private Long carId;

    @NotNull
    @Column(name = "user_id")
    private Long userId;

    @NotNull
    @Column(name = "is_active")
    private boolean isActive = true;

    @Column(name = "is_deleted")
    private boolean isDeleted = false;
}
