package mate.academy.carsharing.mapper;

import mate.academy.carsharing.config.MapperConfig;
import mate.academy.carsharing.dto.payment.PaymentResponseDto;
import mate.academy.carsharing.model.Payment;
import org.mapstruct.Mapper;

@Mapper(config = MapperConfig.class)
public interface PaymentMapper {
    PaymentResponseDto toResponseDto(Payment payment);
}
