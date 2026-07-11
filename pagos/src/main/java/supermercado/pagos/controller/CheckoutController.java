package supermercado.pagos.controller;

import org.springframework.web.bind.annotation.*;
import supermercado.pagos.api.CheckoutFacade;
import supermercado.pagos.model.Transaction;

@RestController
@RequestMapping("/api/checkout")
public class CheckoutController {

    private final CheckoutFacade checkoutFacade;

    public CheckoutController(CheckoutFacade checkoutFacade) {
        this.checkoutFacade = checkoutFacade;
    }

    @PostMapping
    public Transaction checkout(@RequestParam Long userId,
                                @RequestParam String paymentMethod) {
        return checkoutFacade.processCheckout(userId, paymentMethod);
    }
}