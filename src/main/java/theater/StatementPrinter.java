package theater;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

/**
 * This class generates a statement for a given invoice of performances.
 * (Original comment retained)
 */
public class StatementPrinter {
    // invoice and plays should not change once initialized → use private final
    private final Invoice invoice;
    private final Map<String, Play> plays;

    public StatementPrinter(Invoice invoice, Map<String, Play> plays) {
        this.invoice = invoice;
        this.plays = plays;
    }

    /**
     * Returns a formatted statement of the invoice associated with this printer.
     * (Original comment retained)
     *
     * @return the formatted statement
     * @throws RuntimeException if one of the play types is not known
     */
    public String statement() {
        int totalAmount = 0;
        int volumeCredits = 0;

        // Use StringBuilder for building the result string efficiently
        final StringBuilder result =
                new StringBuilder("Statement for " + invoice.getCustomer() + System.lineSeparator());

        final NumberFormat frmt = NumberFormat.getCurrencyInstance(Locale.US);

        // Loop through each performance and calculate values
        for (Performance performance : invoice.getPerformances()) {
            final Play play = getPlay(performance);

            // Extracted methods improve readability and reduce duplication
            final int thisAmount = calculateAmount(performance, play);
            final int thisPerformanceCredits = calculateVolumeCredits(performance, play);

            totalAmount += thisAmount;
            volumeCredits += thisPerformanceCredits;

            // print line for this order (original formatting retained)
            result.append(String.format(
                    "  %s: %s (%s seats)%n",
                    play.getName(),
                    frmt.format(thisAmount / Constants.PERCENT_FACTOR),
                    performance.getAudience()
            ));
        }

        // Add summary lines
        result.append(String.format(
                "Amount owed is %s%n",
                frmt.format(totalAmount / Constants.PERCENT_FACTOR)
        ));
        result.append(String.format(
                "You earned %s credits%n",
                volumeCredits
        ));

        return result.toString();
    }

    /**
     * Calculates the charge amount for a performance.
     * Extracted to remove duplication and improve clarity.
     *
     * @param performance the performance
     * @param play        the play information
     * @return the amount in cents for this performance
     * @throws RuntimeException if the play type is not supported
     */
    private int calculateAmount(Performance performance, Play play) {
        final int audience = performance.getAudience();
        final String type = play.getType();

        int result;
        switch (type) {
            case "tragedy":
                result = Constants.TRAGEDY_BASE_AMOUNT;
                if (audience > Constants.TRAGEDY_AUDIENCE_THRESHOLD) {
                    result += Constants.TRAGEDY_OVER_BASE_CAPACITY_PER_PERSON
                            * (audience - Constants.TRAGEDY_AUDIENCE_THRESHOLD);
                }
                break;

            case "comedy":
                result = Constants.COMEDY_BASE_AMOUNT;
                if (audience > Constants.COMEDY_AUDIENCE_THRESHOLD) {
                    result += Constants.COMEDY_OVER_BASE_CAPACITY_AMOUNT
                            + (Constants.COMEDY_OVER_BASE_CAPACITY_PER_PERSON
                            * (audience - Constants.COMEDY_AUDIENCE_THRESHOLD));
                }
                result += Constants.COMEDY_AMOUNT_PER_AUDIENCE * audience;
                break;

            case "history":
                // New play type: history
                result = Constants.HISTORY_BASE_AMOUNT;
                if (audience > Constants.HISTORY_AUDIENCE_THRESHOLD) {
                    result += Constants.HISTORY_OVER_BASE_CAPACITY_PER_PERSON
                            * (audience - Constants.HISTORY_AUDIENCE_THRESHOLD);
                }
                break;

            case "pastoral":
                // New play type: pastoral
                result = Constants.PASTORAL_BASE_AMOUNT;
                if (audience > Constants.PASTORAL_AUDIENCE_THRESHOLD) {
                    result += Constants.PASTORAL_OVER_BASE_CAPACITY_PER_PERSON
                            * (audience - Constants.PASTORAL_AUDIENCE_THRESHOLD);
                }
                break;

            default:
                // Keep original behavior for truly unknown types
                throw new RuntimeException(String.format("unknown type: %s", type));
        }
        return result;
    }

    /**
     * Calculates volume credits earned for a performance.
     * Extracted method to isolate business rules.
     *
     * @param performance the performance
     * @param play        the play information
     * @return the volume credits for this performance
     */
    private int calculateVolumeCredits(Performance performance, Play play) {
        final int audience = performance.getAudience();
        final String type = play.getType();

        int result;

        // Different play types may have different base thresholds for credits
        switch (type) {
            case "history":
                result = Math.max(
                        audience - Constants.HISTORY_VOLUME_CREDIT_THRESHOLD, 0);
                break;

            case "pastoral":
                // Pastoral performances earn more credits: base plus an extra bonus
                result = Math.max(
                        audience - Constants.PASTORAL_VOLUME_CREDIT_THRESHOLD, 0);
                // extra bonus: one additional credit for every two attendees
                result += audience / 2;
                break;

            default:
                // tragedy, comedy, or any other types that use the base threshold
                result = Math.max(
                        audience - Constants.BASE_VOLUME_CREDIT_THRESHOLD, 0);
                break;
        }

        // add extra credit for every five comedy attendees (original rule)
        if ("comedy".equals(type)) {
            result += audience / Constants.COMEDY_EXTRA_VOLUME_FACTOR;
        }

        return result;
    }

    // ----------------------------------------------------------------------
    // Public helper methods required by the tests
    // ----------------------------------------------------------------------

    /**
     * A helper method to calculate amount for a given performance and play.
     *
     * @param performance the performance
     * @param play        the play information
     * @return amount in cents
     */
    public int getAmount(Performance performance, Play play) {
        return calculateAmount(performance, play);
    }

    /**
     * Get the Play object for a given performance.
     *
     * @param performance the performance
     * @return the Play associated with the performance
     */
    public Play getPlay(Performance performance) {
        return plays.get(performance.getPlayID());
    }

    /**
     * Calculate volume credits earned for a performance.
     *
     * @param performance the performance
     * @param play        the play information
     * @return credits value
     */
    public int getVolumeCredits(Performance performance, Play play) {
        return calculateVolumeCredits(performance, play);
    }

    /**
     * Convert cents to a USD formatted string.
     *
     * @param amount amount in cents
     * @return formatted USD string
     */
    public String usd(int amount) {
        final NumberFormat frmt = NumberFormat.getCurrencyInstance(Locale.US);
        return frmt.format(amount / Constants.PERCENT_FACTOR);
    }

    /**
     * Calculate total amount from the invoice data.
     *
     * @return total amount in cents
     */
    public int getTotalAmount() {
        int total = 0;
        for (Performance performance : invoice.getPerformances()) {
            final Play play = getPlay(performance);
            total += calculateAmount(performance, play);
        }
        return total;
    }

    /**
     * Calculate total volume credits from the invoice data.
     *
     * @return credits total
     */
    public int getTotalVolumeCredits() {
        int total = 0;
        for (Performance performance : invoice.getPerformances()) {
            final Play play = getPlay(performance);
            total += calculateVolumeCredits(performance, play);
        }
        return total;
    }
}
