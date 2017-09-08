
public class Loan {
    static final int PENAL_MONTH = 6;
    static final double INTEREST = 0.056;
    static final double LOAN = 1510000;
    static final int PAY_MONTH = 9 * 12;

    public static void main(String[] args) throws Throwable {
        Calculator even30 = new Calculator(true, 8469.69d);
        Calculator odd30 = new Calculator(false, LOAN / 30d / 12);
        Calculator even20 = new Calculator(true, 10293.51d);
        Calculator odd20 = new Calculator(false, LOAN / 20d / 12);
        Calculator even15 = new Calculator(true, 12250.51d);
        Calculator odd15 = new Calculator(false, LOAN / 15d / 12);
        for (int i = 0; i < PAY_MONTH; ++i) {
            even30.nextMonth();
            odd30.nextMonth();
            even20.nextMonth();
            odd20.nextMonth();
            even15.nextMonth();
            odd15.nextMonth();
        }

        System.out.println(even30);
        System.out.println(odd30);
        System.out.println(even20);
        System.out.println(odd20);
        System.out.println(even15);
        System.out.println(odd15);

    }

    private static class Calculator {
        /** 等额本息 */
        private final boolean even;
        private double fistMonth = -1;
        private final double paybackPerMonth;
        private double belongs = 0;
        private double interestSum = 0;
        private double loanLeft = LOAN;

        public Calculator(boolean even, double paybackPerMonth) {
            super();
            this.even = even;
            this.paybackPerMonth = paybackPerMonth;
            if (even) {
                fistMonth = this.paybackPerMonth;
            }
        }

        public void nextMonth() {
            double interest = loanLeft * INTEREST / 12;
            this.interestSum += interest;
            double belong = 0;
            if (even) {// 本息
                belong = paybackPerMonth - interest;
            } else { //本金
                belong = paybackPerMonth;
                if (this.fistMonth < 0) {
                    fistMonth = this.paybackPerMonth + interest;
                }
            }
            this.belongs += belong;
            this.loanLeft -= belong;
        }

        private double penal() {
            return PENAL_MONTH * loanLeft * INTEREST / 12d;
        }

        @Override
        public String toString() {
            String s = "";
            s += "firstMonth=" + this.fistMonth;
            s += ", left=" + loanLeft / 10000d;
            s += ", belongs=" + this.belongs / 10000d;
            s += ", interestSum=" + this.interestSum / 10000d;
            s += ", interestSumPenal=" + (penal() + this.interestSum) / 10000d;
            return s;
        }

    }

}
