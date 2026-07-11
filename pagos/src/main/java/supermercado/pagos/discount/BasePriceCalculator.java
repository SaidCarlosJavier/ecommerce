package supermercado.pagos.discount;


public class BasePriceCalculator implements PriceCalculator {

    @Override
    public Double calculate(Double basePrice) {
        return basePrice;
    }
}