package u04;

import java.math.*;
import java.util.Random;

public abstract class Search {

    public int compares = 0;
    private int last_n = 0;
    private int last_found = -1;

    protected abstract int k(double a, int l, int r, double[] S);

    protected abstract int step(int n, int r, int l);

    protected abstract String name();
    protected String extra() {
        return "";
    }

    @Override
    public String toString() {
        return this.name() +
            " compares: " + this.compares +
            " n:" + this.last_n +
            " found:" + this.last_found +
            " extra:" + this.extra();
    }

    /**
     *  @param S input list
     *  @param a searched value
     */
    public int search(double[] S, double a) {
        this.compares = 0;
        this.last_n = S.length;
        final int result = search(0, S.length - 1, S, a);
        this.last_found = result;
        return result;
    }

    private int search(int l, int r, double[] S, double a) {
        final int step = step(S.length, r, l);
        if (l <= r) {
            int k = k(a, l, r, S);
            this.compares += 2;
            if (is(S[k], a)) {
                this.compares -= 1; // as we only need ONE compare..
                return k;
            } else if (S[k] > a) {
                return search(l, k-step, S, a);
            } else {
                return search(k+step, r, S, a);
            }
        } else {
            return -1;
        }
    }

    /**
     *  Floating point "equality"
     */
    public static boolean is(final double a, final double b) {
        final double epsilon = 0.00001;
        return Math.abs(a - b) < epsilon;
    }

    /**
     *  Example Binary Search
     */
    private static class BinarySearch extends Search {

        @Override
        protected String name() { return "Binary Search";}

        @Override
        protected int k(double a, int l, int r, double[] S) {
            return (int) Math.ceil((l+r)/2.0f);
        }

        @Override
        protected int step(int n, int r, int l) {
           return 1;
        }
    }

    /**
     *  Aufgabe 2.a
     */
    private static class InterpolationSearch extends BinarySearch {

        @Override
        protected String name() { return "Interpolation Search";}

        @Override
        public int k(double a, int l, int r, double[] S) {
            final double Sl_1 = l == 0 ? 0 : S[l - 1]; // prevent underflow
            final double Srp1 = (r+1) == S.length ? 1 : S[r + 1]; // prevent overflow
            return l - 1 + (int) Math.ceil(((a-Sl_1)/(Srp1 - Sl_1)) * (r - l + 1));
        }
    }

    /**
     *  Aufgabe 2.b
     */
    private static class QuadraticBinarySearch extends InterpolationSearch {

        @Override
        protected String name() { return "Quadratic Search";}

        @Override
        public int step(int n, int r, int l) {
            final int m = r - l + 1;
            return (int) Math.ceil(Math.sqrt(m));
        }
    }


    private static double[] gen(int n) {
        final double[] S = new double[n];
        final Random r = new Random();
        for (int i = 0; i < n; i++) {
            S[i] = r.nextDouble();
        }
        return S;
    }

    /**
     *
     * @param S the list we want to search in
     * @param a the value we are searching for
     * @param comparesIs the place where we safe the number of cmps for Interpolation
     * @param comparesQs the place where we safe the number of cmps for Quadratic
     * @param i position in the compare lists
     */
    private static void benchmark(double[] S, double a, int[] comparesIs, int[] comparesQs, int i) {
        final BinarySearch bs = new BinarySearch();
        final InterpolationSearch is = new InterpolationSearch();
        final QuadraticBinarySearch qs = new QuadraticBinarySearch();
        int pos = bs.search(S, a);
        System.out.println(bs);

        pos = is.search(S, a);
        System.out.println(is);
        if (comparesIs != null) comparesIs[i] = is.compares;

        pos = qs.search(S, a);
        System.out.println(qs);
        if (comparesQs != null) comparesQs[i] = qs.compares;
    }

    /**
     * Start the program
     * @param args
     */
    public static void main(String[] args) {

        final double[] S = {
            .00000000001,
            .0000000001,
            .000000001,
            .00000001,
            .0000001,
            .000001,
            .00001,
            .0001,
            .001,
            .01,
            .1,
            1
        };

        benchmark(S, .1, null, null, -1);

        final int COUNT = 1000;
        final int n = 1000000;
        final int[] IS = new int[COUNT];
        final int[] QS = new int[COUNT];
        final Random r = new Random();
        for (int i = 0; i < COUNT; i++) {
            benchmark(gen(n), r.nextDouble(), IS, QS, i);
        }

        System.out.println("is avg:" + avg(IS));
    }

    public static double avg(int[] L) {
        double result = 0;
        for (int i = 0; i < L.length; i++) {
            result += L[i];
        }
        return result/L.length;
    }

}
