package u04;

import java.util.Arrays;
import java.util.Collections;
import java.util.Random;
import java.util.jar.Pack200;

/**
 * Homework U04
 */
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
     * @param S input list
     * @param a searched value
     */
    public int search(double[] S, double a) {
        this.compares = 0;
        this.last_n = S.length;
        final int result = search(0, S.length - 1, S, a);
        this.last_found = result;
        return result;
    }

    protected int search(int l, int r, double[] S, double a) {
        final int step = step(S.length, r, l);
        if (l <= r) {
            final int k = k(a, l, r, S);
            if (is(S[k], a)) {
                return k;
            } else if (gt(S[k], a)) {
                return this.searchLeft(k, step, l, r, S, a);
            } else {
                return this.searchRight(k, step, l, r, S, a);
            }
        } else {
            return -1;
        }
    }

    protected int searchLeft(final int k, final int step, final int l, final int r, double[] S, double a) {
        return search(l, k - step, S, a);
    }

    protected int searchRight(final int k, final int step, final int l, final int r, double[] S, double a) {
        return search(k + step, r, S, a);
    }

    /**
     * Floating point "equality"
     */
    public boolean is(final double a, final double b) {
        this.compares += 1;
        final double epsilon = 0.0000000000000000000000000000000001;
        return Math.abs(a - b) < epsilon;
    }

    public boolean gt(final double a, final double b) {
        this.compares += 1;
        return a > b;
    }

    /**
     * Example Binary Search
     */
    private static class BinarySearch extends Search {

        @Override
        protected String name() {
            return "Binary Search";
        }

        @Override
        protected int k(double a, int l, int r, double[] S) {
            return (int) Math.ceil((l + r) / 2.0f);
        }

        @Override
        protected int step(int n, int r, int l) {
            return 1;
        }
    }

    /**
     * Aufgabe 2.a
     */
    private static class InterpolationSearch extends BinarySearch {

        @Override
        protected String name() {
            return "Interpolation Search";
        }

        @Override
        public int k(double a, int l, int r, double[] S) {
            final double Sl_1 = l == 0 ? 0 : S[l - 1]; // prevent underflow
            final double Srp1 = (r + 1) == S.length ? 1 : S[r + 1]; // prevent overflow
            int res = l - 1 + (int) Math.ceil(((a - Sl_1) / (Srp1 - Sl_1)) * (r - l + 1));
            return res;
        }
    }

    /**
     * Aufgabe 2.b
     */
    private static class QuadraticBinarySearch extends InterpolationSearch {

        @Override
        protected String name() {
            return "Quadratic Search";
        }

        @Override
        public int step(int n, int r, int l) {
            final int m = r - l + 1;
            return (int) Math.ceil(Math.sqrt(m));
        }

        protected int searchLeft(int k, final int step, final int l, final int r, double[] S, double a) {
            int kplus = Math.max(k - step, l);
            boolean stopMe = false;
            while(true) {
                if (is(a, S[kplus])) {
                    return kplus;
                }
                if (gt(a, S[kplus])) {
                    return this.search(kplus+1, k, S, a);
                }
                if (stopMe) return -1;
                kplus = Math.max(kplus - step, l);
                k = Math.max(k - step, l);
                if (kplus == l) {
                    stopMe = true;
                }
            }
        }

        protected int searchRight(int k, final int step, final int l, final int r, double[] S, double a) {
            int kplus = Math.min(k + step, r);
            boolean stopMe = false;
            while(true) {
                if (is(a, S[kplus])) {
                    return kplus;
                }
                this.compares -= 1; // make this "one" compare..
                if (gt(S[kplus], a)) {
                    return this.search(k, kplus-1, S, a);
                }
                if (stopMe) return -1;
                kplus = Math.min(kplus + step, r);
                k = Math.min(k + step, r);
                if (kplus == r) {
                    stopMe = true;
                }
            }
        }
    }

    private static double[] gen(int n) {
        final double[] S = new double[n];
        final Random r = new Random();
        for (int i = 0; i < n; i++) {
            S[i] = r.nextDouble();
        }
        Arrays.sort(S);
        System.out.println(Arrays.toString(S));
        return S;
    }

    // =============================================================================

    /**
     * @param S          the list we want to search in
     * @param a          the value we are searching for
     * @param comparesIs the place where we safe the number of cmps for Interpolation
     * @param comparesQs the place where we safe the number of cmps for Quadratic
     * @param i          position in the compare lists
     */
    private static void benchmark(double[] S, double a, int[] comparesIs, int[] comparesQs, int i) {
        final BinarySearch bs = new BinarySearch();
        final InterpolationSearch is = new InterpolationSearch();
        final QuadraticBinarySearch qs = new QuadraticBinarySearch();
        int posA = bs.search(S, a);
        System.out.println(bs);

        int posB = is.search(S, a);
        System.out.println(is);
        if (comparesIs != null) comparesIs[i] = is.compares;

        int posC = qs.search(S, a);
        System.out.println(qs);
        if (comparesQs != null) comparesQs[i] = qs.compares;

        if (posA != posB || posB != posC) {
            System.out.println(Arrays.toString(S));
            System.out.println(a);
            throw new RuntimeException("Not the same results: " + posA + "," + posB + "," + posC);
        }
    }

    /**
     * Start the program
     *
     * @param args
     */
    public static void main(String[] args) {

        double[] S = {
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

        final int COUNT = 10000;
        final int n = 1000;
        final int[] IS = new int[COUNT];
        final int[] QS = new int[COUNT];
        final Random r = new Random();
        for (int i = 0; i < COUNT; i++) {
            final double[] list = gen(n);
            double a = r.nextDouble() >= 0.5 ? r.nextDouble() : list[r.nextInt(list.length)];
            benchmark(list, a, IS, QS, i);
        }

        System.out.println("is avg:" + avg(IS));
    }

    public static double avg(int[] L) {
        double result = 0;
        for (int i = 0; i < L.length; i++) {
            result += L[i];
        }
        return result / L.length;
    }

}
