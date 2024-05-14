package mate.academy.carsharing.controller;

import com.stripe.exception.StripeException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.MalformedURLException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import mate.academy.carsharing.dto.rental.RentalRequestDto;
import mate.academy.carsharing.dto.rental.RentalResponseDto;
import mate.academy.carsharing.model.User;
import mate.academy.carsharing.service.rental.RentalService;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Rentals controller", description = "Endpoints for managing rentals")
@RestController
@RequestMapping("/rentals")
@RequiredArgsConstructor
public class RentalsController {
    private final RentalService rentalService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Create a new rental",
            description = "Endpoint for creating and saving a new rental")
    public RentalResponseDto addRental(
            Authentication authentication,
            @RequestBody @Valid RentalRequestDto requestDto) {
        return rentalService.addRental(
                (User) authentication.getPrincipal(), requestDto);
    }

    @GetMapping("/mine")
    @Operation(summary = "View all user`s rentals",
            description = "The endpoint views all user`s rentals sorted by page")
    public List<RentalResponseDto> getRentals(
            Authentication authentication,
            Pageable pageable) {
        return rentalService.getAllRentals(
                (User) authentication.getPrincipal(),
                pageable);
    }

    @PostMapping("/return")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Set an actual return date",
            description = "End point for setting the car return date "
                    + "If car is returned earlier or later than expected,"
                    + " then user should pay fine")
    public RentalResponseDto setReturnDate(
            Authentication authentication)
            throws StripeException,
            MalformedURLException {
        return rentalService.setReturnDate((User) authentication.getPrincipal());
    }

    @GetMapping
    @PreAuthorize("hasAuthority('MANAGER')")
    @Operation(summary = "Get all user's rentals",
            description = "Endpoint for getting all available user's rentals"
            + " is still active or not")
    public List<RentalResponseDto> getUserRentals(
            @RequestParam(name = "user_id") Long userId,
            @RequestParam(name = "is_active") boolean isActive,
            Pageable pageable) {
        return rentalService.getUserRentals(userId, isActive, pageable);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('MANAGER')")
    @Operation(summary = "Get a specific rental",
            description = "Endpoint for getting a specific rental."
                    + " Allowed for Managers only")
    public RentalResponseDto getSpecificRental(
            @PathVariable Long id) {
        return rentalService.getSpecificRental(id);
    }

    @GetMapping("/active")
    @PreAuthorize("hasAuthority('MANAGER')")
    @Operation(summary = "Get all active rentals",
            description = "Endpoint for getting all active rentals with pageable sorting."
                    + " Allowed for Managers only")
    public List<RentalResponseDto> getAllActiveRentals(Pageable pageable) {
        return rentalService.getAllActiveRentals(pageable);
    }
}
