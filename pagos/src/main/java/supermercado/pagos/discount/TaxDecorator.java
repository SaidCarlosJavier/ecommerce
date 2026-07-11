package supermercado.pagos.discount;


public class TaxDecorator implements PriceCalculator {

    private final PriceCalculator calculator;

    public TaxDecorator(PriceCalculator calculator) {
        this.calculator = calculator;
    }

    @Override
    public Double calculate(Double basePrice) {
        return calculator.calculate(basePrice) * 1.18;
    }
}