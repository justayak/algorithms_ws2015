import java.math.*;

public abstract class Search {

    private int compares = 0;
    private int last_n = 0;
    private int last_found = -1;

    protected abstract int k(double a, int l, int r, double[] S);

    protected abstract int step(int n, int r, int l);

    protected abstract String name();

    @Override
    public String toString() {
        return this.name() + 
            " compares: " + this.compares + 
            " n:" + this.last_n + 
            " found:" + this.last_found;
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

    public static void main(String[] args) {
        final BinarySearch bs = new BinarySearch();
        final InterpolationSearch is = new InterpolationSearch();
        final QuadraticBinarySearch qs = new QuadraticBinarySearch();

        final double[] S = {0, .01, .02, .03, .04, .05, .07, .075, .08, .081, .2, .9};
        int pos = bs.search(S, .2);
        System.out.println(bs);
        
        pos = is.search(S, .2);
        System.out.println(is);
        
        pos = qs.search(S, .2);
        System.out.println(is);
    }

}
