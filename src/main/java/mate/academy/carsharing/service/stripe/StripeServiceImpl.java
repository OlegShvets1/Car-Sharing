package mate.academy.carsharing.service.stripe;

import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import org.springframework.stereotype.Component;

@Component
public class StripeServiceImpl implements StripeService {
    private static final String USD = "usd";
    private static final long DEFAULT_QUANTITY = 1L;
    private static final String SUCCESS_URL = "http://localhost:8088/api/payments/success";
    private static final String CANCEL_URL = "http://localhost:8088/api/payments/cancel";

    public Session createSession(Long price, String unitName) throws StripeException {
        SessionCreateParams params = SessionCreateParams.builder()
                .setSuccessUrl(SUCCESS_URL)
                .setCancelUrl(CANCEL_URL)
                .addLineItem(SessionCreateParams.LineItem.builder()
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency(USD)
                                .setUnitAmount(price)
                                .setProductData(SessionCreateParams.LineItem
                                        .PriceData.ProductData.builder()
                                        .setName(unitName)
                                        .build())
                                .build())
                        .setQuantity(DEFAULT_QUANTITY)
                        .build()
                )
                .setMode(SessionCreateParams.Mode.PAYMENT)
                .build();
        return Session.create(params);
    }
}

