import java.math.*;

public abstract class Search {

    protected abstract int k(double a, int l, int r, double[] S);

    protected abstract int step(int n, int r, int l);

    /**
     *  @param S input list
     *  @param a searched value
     */
    public int search(double[] S, double a) {
        return search(0, S.length - 1, S, a);
    }

    private int search(int l, int r, double[] S, double a) {
        final int step = step(S.length, r, l);
        if (l <= r) {
            int k = k(a, l, r, S);
            if (is(S[k], a)) {
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
        public int k(double a, int l, int r, double[] S) {
            return (int) Math.ceil((l+r)/2.0f);
        }

        @Override
        public int step(int n, int r, int l) {
           return 1;
        }
    }

    /**
     *  Aufgabe 2.a
     */
    private static class InterpolationSearch extends BinarySearch {
        
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
        public int step(int n, int r, int l) {
            final int m = r - l + 1;
            return (int) Math.ceil(Math.sqrt(m));
        }
    }

    public static void main(String[] args) {
        final double[] S = {0, .01, .02, .3, .4, .5, .7, .75, .8, .81, .82, .9};
        int pos = new BinarySearch().search(S, .4);
        System.out.println("binary search: " + pos);
        
        pos = new InterpolationSearch().search(S, .4);
        System.out.println("Interpolation search: " + pos);
        
        pos = new QuadraticBinarySearch().search(S, .4);
        System.out.println("Quadratic search: " + pos);
    }

}
