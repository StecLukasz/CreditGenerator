package data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class CreditOfferGenerator {

    private static final BigDecimal MAX_CREDIT_AMOUNT = new BigDecimal("150000");
    private static final BigDecimal MAX_COMMITMENT = new BigDecimal("200000");
    private static final BigDecimal DTI_LIMIT_0_12 = new BigDecimal("0.6");
    private static final BigDecimal DTI_LIMIT_13_36 = new BigDecimal("0.6");
    private static final BigDecimal DTI_LIMIT_37_60 = new BigDecimal("0.5");
    private static final BigDecimal DTI_LIMIT_61_100 = new BigDecimal("0.55");
    private static final int MAX_CREDIT_PERIOD = 100;
    private static final int MIN_CREDIT_PERIOD = 6;
    private static final BigDecimal MIN_CREDIT_AMOUNT = new BigDecimal("5000");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int creditPeriod;
        BigDecimal monthlyIncome;
        BigDecimal monthlyCreditCommitments = BigDecimal.ZERO;

        while (true) {
            System.out.println("Enter the credit period (in months):");
            creditPeriod = scanner.nextInt();

            if (creditPeriod < MIN_CREDIT_PERIOD) {
                System.out.println("The credit period is too short. The minimum period is " + MIN_CREDIT_PERIOD + " months.");
                System.out.println("No credit offers available.");
                continue;
            } else if (creditPeriod > MAX_CREDIT_PERIOD) {
                System.out.println("The credit period is too long. The maximum period is " + MAX_CREDIT_PERIOD + " months.");
                System.out.println("No credit offers available.");
                continue;
            }
            break;
        }

        System.out.println("Enter your monthly income (in PLN):");
        monthlyIncome = scanner.nextBigDecimal();

        System.out.println("Enter your monthly living expenses (in PLN):");
        BigDecimal monthlyCosts = scanner.nextBigDecimal();

        if (monthlyCosts.add(monthlyCreditCommitments).compareTo(monthlyIncome) > 0) {
            System.out.println("Your monthly income is not sufficient to cover the living expenses and credit commitments.");
            return;
        }

        while (true) {
            System.out.println("Enter your total monthly credit commitments (in PLN):");
            monthlyCreditCommitments = scanner.nextBigDecimal();

            if (monthlyCreditCommitments.compareTo(MAX_COMMITMENT) > 0) {
                System.out.println("The total monthly credit commitments are too high. The maximum allowed is " + MAX_COMMITMENT + " PLN.");
                continue;
            }
            break;
        }
        scanner.close();

        List<String> offers = generateOffers(creditPeriod, monthlyIncome, monthlyCosts, monthlyCreditCommitments);
        if (offers.isEmpty()) {
            System.out.println("No credit offers available.");
        } else {
            for (String offer : offers) {
                System.out.println(offer);
            }
        }
    }

    private static List<String> generateOffers(int creditPeriod, BigDecimal monthlyIncome, BigDecimal monthlyCosts, BigDecimal monthlyCreditCommitments) {
        List<String> offers = new ArrayList<>();
        int[] intervals = {12, 36, 60, MAX_CREDIT_PERIOD};

        int[] selectedIntervals = selectIntervalsForCreditPeriod(creditPeriod);

        for (int interval : intervals) {
            if (isIntervalSelected(interval, selectedIntervals)) {
                BigDecimal dti = calculateDTI(monthlyIncome, monthlyCosts, monthlyCreditCommitments);
                if (dti.compareTo(getDTILimit(interval)) > 0) {
                    continue;
                }

                BigDecimal maxLoanAmount = calculateMaxLoanAmount(monthlyIncome, monthlyCosts, monthlyCreditCommitments, interval);
                if (maxLoanAmount.compareTo(MIN_CREDIT_AMOUNT) < 0) {
                    continue;
                }

                BigDecimal maxMonthlyPayment = calculateMaxMonthlyPayment(maxLoanAmount, interval);

                String intervalDescription = getIntervalDescription(interval, creditPeriod);
                String offer = String.format("Offer for period %s months with %s interest rate: up to %s PLN at %s PLN/month",
                        intervalDescription,
                        getFormattedInterestRate(interval),
                        maxLoanAmount.toPlainString(),
                        maxMonthlyPayment.toPlainString());
                offers.add(offer);
            }
        }
        return offers;
    }

    private static String getIntervalDescription(int interval, int creditPeriod) {
        if (interval == 12) {
            return "6-12";
        } else if (interval == 36) {
            return creditPeriod > 36 ? "13-36" : "13-" + creditPeriod;
        } else if (interval == 60) {
            return creditPeriod > 60 ? "37-60" : "37-" + creditPeriod;
        } else {
            return "61-" + creditPeriod;
        }
    }

    private static boolean isIntervalSelected(int interval, int[] selectedIntervals) {
        for (int selectedInterval : selectedIntervals) {
            if (interval == selectedInterval) {
                return true;
            }
        }
        return false;
    }

    private static int[] selectIntervalsForCreditPeriod(int creditPeriod) {
        if (creditPeriod <= 12) {
            return new int[]{12};
        } else if (creditPeriod <= 36) {
            return new int[]{12, 36};
        } else if (creditPeriod <= 60) {
            return new int[]{12, 36, 60};
        } else {
            return new int[]{12, 36, 60, MAX_CREDIT_PERIOD};
        }
    }

    private static BigDecimal calculateDTI(BigDecimal monthlyIncome, BigDecimal monthlyCosts, BigDecimal monthlyCreditCommitments) {
        return monthlyCosts.add(monthlyCreditCommitments).divide(monthlyIncome, 2, RoundingMode.HALF_UP);
    }

    private static BigDecimal getDTILimit(int creditPeriod) {
        if (creditPeriod <= 12) {
            return DTI_LIMIT_0_12;
        } else if (creditPeriod <= 36) {
            return DTI_LIMIT_13_36;
        } else if (creditPeriod <= 60) {
            return DTI_LIMIT_37_60;
        } else {
            return DTI_LIMIT_61_100;
        }
    }

    private static String getFormattedInterestRate(int creditPeriod) {
        if (creditPeriod <= 12) {
            return "2%";
        } else {
            return "3%";
        }
    }

    private static BigDecimal getMonthlyInterestRate(int creditPeriod) {
        BigDecimal annualInterestRate;
        if (creditPeriod <= 12) {
            annualInterestRate = new BigDecimal("0.02");
        } else {
            annualInterestRate = new BigDecimal("0.03");
        }
        return annualInterestRate.divide(new BigDecimal("12"), RoundingMode.HALF_UP);
    }

    private static BigDecimal calculateMaxMonthlyPayment(BigDecimal maxLoanAmount, int creditPeriod) {
        BigDecimal monthlyInterestRate = getMonthlyInterestRate(creditPeriod);
        if (monthlyInterestRate.compareTo(BigDecimal.ZERO) == 0) {
            return maxLoanAmount.divide(new BigDecimal(creditPeriod), 2, RoundingMode.HALF_UP);
        } else {
            BigDecimal onePlusMI = monthlyInterestRate.add(BigDecimal.ONE);
            BigDecimal powResult = onePlusMI.pow(creditPeriod);
            BigDecimal denominator = BigDecimal.ONE.divide(powResult, 2, RoundingMode.HALF_UP);
            denominator = BigDecimal.ONE.subtract(denominator);
            return maxLoanAmount.multiply(denominator).divide(monthlyInterestRate, 2, RoundingMode.HALF_UP);
        }
    }

    private static BigDecimal calculateMaxLoanAmount(BigDecimal monthlyIncome, BigDecimal monthlyCosts, BigDecimal monthlyCreditCommitments, int creditPeriod) {
        BigDecimal maxMonthlyPayment = monthlyIncome.multiply(getDTILimit(creditPeriod)).subtract(monthlyCosts).subtract(monthlyCreditCommitments);
        BigDecimal totalPossibleLoan = maxMonthlyPayment.multiply(new BigDecimal(creditPeriod));
        return totalPossibleLoan.min(MAX_CREDIT_AMOUNT);
    }
}
