package comviktorgozhiy.github.ordertohome.Utils;

import java.text.DecimalFormat;

public class StringUtils {

    public static String formatPrice(float price) {
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        return decimalFormat.format(price);
    }
}
