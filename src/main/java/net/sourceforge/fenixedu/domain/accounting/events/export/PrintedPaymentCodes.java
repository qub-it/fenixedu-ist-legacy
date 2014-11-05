/**
 * Copyright © 2002 Instituto Superior Técnico
 *
 * This file is part of FenixEdu Core.
 *
 * FenixEdu Core is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * FenixEdu Core is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with FenixEdu Core.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.fenixedu.academic.domain.accounting.events.export;

import java.util.HashSet;
import java.util.Set;

import org.fenixedu.academic.domain.accounting.PaymentCode;

public class PrintedPaymentCodes {

    private Set<String> paymentCodes;

    public PrintedPaymentCodes() {
        this.paymentCodes = new HashSet<String>();
    }

    public String exportAsString() {
        StringBuilder result = new StringBuilder();

        for (String code : this.paymentCodes) {
            result.append(code).append(",");
        }

        result.delete(result.length() - 1, result.length());

        return result.toString();
    }

    public Set<String> getPaymentCodes() {
        return paymentCodes;
    }

    public void addPaymentCode(final PaymentCode paymentCode) {
        this.paymentCodes.add(paymentCode.getCode());
    }

    public static PrintedPaymentCodes importFromString(final String value) {
        String[] codes = value.split(",");

        PrintedPaymentCodes printPaymentCodes = new PrintedPaymentCodes();

        for (String c : codes) {
            printPaymentCodes.paymentCodes.add(c);
        }

        return printPaymentCodes;
    }
}
