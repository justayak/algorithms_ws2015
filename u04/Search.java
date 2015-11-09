import java.math.*;

public abstract class Search {

    protected abstract int k(double a, int l, int r, double[] S);

    protected abstract int step(int n);

    /**
     *  @param S input list
     *  @param a searched value
     */
    public int search(double[] S, double a) {
        return search(0, S.length - 1, S, a);
    }

    private int search(int l, int r, double[] S, double a) {
        final int step = step(S.length);
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
     *  Example
     */
    private static class BinarySearch extends Search {
        
        @Override
        public int k(double a, int l, int r, double[] S) {
            return (int) Math.ceil((l+r)/2.0f);
        }

        @Override
        public int step(int n) {
           return 1;
        }

    }

    public static void main(String[] args) {
        final double[] S = {0, .01, .02, .3, .4, .5, .7, .75, .8, .81, .82, .9};

        final int pos = new BinarySearch().search(S, .4);

        System.out.println("qq " + pos);
    }

}
