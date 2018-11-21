
package com.util;

import java.math.BigDecimal;
import org.apache.commons.validator.routines.IBANValidator;

import com.validation.BicValidator;

/**
 *
 */
public class SepaUtil {

    public static void validateIban(String iban) throws SepaValidationException {
        if (!IBANValidator.getInstance().isValid(iban)) {
            throw new SepaValidationException("Invalid IBAN " + iban);
        }
    }

    public static void validateBic(String bic) throws SepaValidationException {
        if (bic == null || bic.isEmpty() || !(new BicValidator().isValid(bic))) {
            throw new SepaValidationException("Invalid BIC " + bic);
        }
    }

    public static BigDecimal floatToBigInt2Digit(float f) {
        return new BigDecimal(f).setScale(2, BigDecimal.ROUND_HALF_UP);
    }
}
