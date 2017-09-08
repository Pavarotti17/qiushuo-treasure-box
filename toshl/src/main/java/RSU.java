
import java.util.ArrayList;
import java.util.List;

public class RSU {

    private static final PriceEst[] priceEst = new PriceEst[] { //.... 
            new PriceEst("2015-07-10", 70.43d), new PriceEst("2016-04-15", 95.88d), new PriceEst("2017-04-15", 95.88),
            new PriceEst("2018-04-15", 95.88) };

    private static final double taxOnDebt = 230000d;

    public static void main(String[] args) throws Throwable {
        new RSU().run();
    }

    private void run() {
        List<Grant> list = new ArrayList<>();

        list.add(new Grant(6.8d, 8900, 5743).addShare(new GrantShare("2017-11-01", 2225)));

        list.add(
                new Grant(6.8d, 10000, 5000).addShare(new GrantShare("2017-04-01", 2500))
                                            .addShare(new GrantShare("2018-04-01", 2500)));
        list.add(
                new Grant(8.25d, 13000, 3250).addShare(new GrantShare("2017-04-01", 3250))
                                             .addShare(new GrantShare("2018-04-01", 3250))
                                             .addShare(new GrantShare("2019-04-01", 3250)));
        list.add(
                new Grant(8.25d, 10000, 2500).addShare(new GrantShare("2017-04-01", 2500))
                                             .addShare(new GrantShare("2018-04-01", 2500))
                                             .addShare(new GrantShare("2019-04-01", 2500)));

        list.add(new Grant(35d, 6300, 0).addShare(new GrantShare("2017-04-01", 1575))
                                        .addShare(new GrantShare("2018-04-01", 1575))
                                        .addShare(new GrantShare("2019-04-01", 1575))
                                        .addShare(new GrantShare("2020-04-01", 1575)));

        list.add(new Grant(48d, 6300, 0).addShare(new GrantShare("2018-04-01", 2750))
                                        .addShare(new GrantShare("2019-04-01", 2750))
                                        .addShare(new GrantShare("2020-04-01", 2750))
                                        .addShare(new GrantShare("2021-04-01", 2750)));

        System.out.println("last price: " + priceEst[priceEst.length - 1].price);
        //        String targetDate="2019-04-16";
        String[] dd = new String[] { "2017-02-16", "2017-04-16", "2018-03-31", "2018-04-02", "2018-04-16", "2019-03-31",
                "2019-04-02", "2019-04-16", "2020-03-16", "2020-04-16" };
        for (String targetDate : dd) {
            double price = list.stream().map((Grant g) -> g.totalPrice(targetDate)).reduce(0d, (a, b) -> a + b);
            double tax = list.stream().map((Grant g) -> g.totalTax(targetDate)).reduce(0d, (a, b) -> a + b);
            tax += taxOnDebt;
            double gain = price - tax;

            System.out.println(
                    targetDate + ": " + Math.round(gain / 10000d) + " = " + Math.round(price / 10000d) + " - "
                            + Math.round(
                                    tax / 10000d));
        }

    }

    private static double getPrice(String date) {
        double last = 0;
        for (PriceEst p : priceEst) {
            if (p.getDate().compareTo(date) <= 0) {
                last = p.price;
            } else {
                break;
            }
        }
        return last;
    }

    private static class Grant {
        private final double basicPrice;
        private final long shares;
        private final long already;
        private final List<GrantShare> left = new ArrayList<>(4);

        public Grant(double basicPrice, long shares, long already) {
            super();
            this.basicPrice = basicPrice;
            this.shares = shares;
            this.already = already;
        }

        public double totalPrice(String date) {
            double price = getPrice(date) - basicPrice;
            if (price <= 0) {
                return 0;
            }
            double sum = price * already;
            for (GrantShare g : left) {
                if (g.getDate().compareTo(date) <= 0) {
                    sum += g.getShare() * price;
                }
            }
            return sum;
        }

        public double totalTax(String date) {
            double sum = 0;
            for (GrantShare g : left) {
                if (g.getDate().compareTo(date) <= 0) {
                    double price = getPrice(g.getDate());
                    price -= basicPrice;
                    sum += price * g.getShare() * 0.4;
                }
            }
            return sum;
        }

        public double getBasicPrice() {
            return basicPrice;
        }

        public long getShares() {
            return shares;
        }

        public long getAlready() {
            return already;
        }

        public List<GrantShare> getLeft() {
            return left;
        }

        public Grant addShare(GrantShare share) {
            this.left.add(share);
            return this;
        }

    }

    private static class GrantShare {

        private final String date;
        private final long share;

        public GrantShare(String date, long share) {
            super();
            this.date = date;
            this.share = share;
        }

        public String getDate() {
            return date;
        }

        public long getShare() {
            return share;
        }

    }

    private static class PriceEst {

        private final String date;
        private final double price;

        public PriceEst(String date, double price) {
            this.date = date;
            this.price = price;
        }

        public String getDate() {
            return date;
        }

        public double getPrice() {
            return price;
        }

    }
}
