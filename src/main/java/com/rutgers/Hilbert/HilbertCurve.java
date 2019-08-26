package com.rutgers.Hilbert;

import java.math.BigInteger;
import java.util.Arrays;
import com.github.davidmoten.guavamini.Preconditions;
import com.github.davidmoten.guavamini.annotations.VisibleForTesting;
import com.rutgers.Core.Message.ARMessage.Header.Profile;
import java.util.List;
import net.tomp2p.peers.Number160;

/**
 * Converts between Hilbert index ({@code BigInteger}) and N-dimensional points.
 *
 * @author  Eduard Giber Renart
 * @version 1.0
*/
public final class HilbertCurve {

    private final int bits;
    private final int dimensions;
    // cached calculations
    private final int length;

    private HilbertCurve(int bits, int dimensions) {
        this.bits = bits;
        this.dimensions = dimensions;
        // cache a calculated values for small perf improvements
        this.length = bits * dimensions;
    }

    /**
     * Returns a builder for and object that performs transformations for a
     * Hilbert curve with the given number of bits.
     * 
     * @param bits
     *            depth of the Hilbert curve. If bits is one, this is the
     *            top-level Hilbert curve
     * @return builder for object to do transformations with the Hilbert Curve
     */
    public static Builder bits(int bits) {
        return new Builder(bits);
    }

    public static SmallHilbertCurve.Builder small() {
        return new SmallHilbertCurve.Builder();
    }

    /**
     * Builds a {@link HilbertCurve} instance.
     */
    public static final class Builder {
        final int bits;

        private Builder(int bits) {
            Preconditions.checkArgument(bits > 0, "bits must be greater than zero");
            Preconditions.checkArgument(bits < 64, "bits must be 63 or less");
            this.bits = bits;
        }

        public HilbertCurve dimensions(int dimensions) {
            Preconditions.checkArgument(dimensions > 1, "dimensions must be at least 2");
            return new HilbertCurve(bits, dimensions);
        }
    }

    /**
     * Converts a point to its Hilbert curve index.
     * 
     * @param point
     *            an array of {@code long}. Each coordinate can be between 0 and
     *            2<sup>bits</sup>-1.
     * @return index (nonnegative {@link BigInteger})
     * @throws IllegalArgumentException
     *             if length of point array is not equal to the number of
     *             dimensions.
     */
    public Number160 index(long... point) {
        Preconditions.checkArgument(point.length == dimensions);
        return toIndex(transposedIndex(bits, point));
    }

    /**
     * Converts a {@link BigInteger} index (distance along the Hilbert Curve
     * from 0) to a point of dimensions defined in the constructor of
     * {@code this}.
     * 
     * @param index
     *            index along the Hilbert Curve from 0. Maximum value 2
     *            <sup>bits * dimensions</sup>-1.
     * @return array of longs being the point
     * @throws NullPointerException
     *             if index is null
     * @throws IllegalArgumentException
     *             if index is negative
     */
    public long[] point(Number160 index) {
        Preconditions.checkNotNull(index);
        //Preconditions.checkArgument(index.signum() != -1, "index cannot be negative");
        return transposedIndexToPoint(bits, transpose(index));
    }

    /**
     * Converts a {@code long} index (distance along the Hilbert Curve from 0)
     * to a point of dimensions defined in the constructor of {@code this}.
     * 
     * @param index
     *            index along the Hilbert Curve from 0. Maximum value 2
     *            <sup>bits+1</sup>-1.
     * @return array of longs being the point
     * @throws IllegalArgumentException
     *             if index is negative
     */
    public long[] point(long index) {
        return point(new Number160(index));
    }

    /**
     * Returns the transposed representation of the Hilbert curve index.
     * 
     * <p>
     * The Hilbert index is expressed internally as an array of transposed bits.
     * 
     * <pre>
      Example: 5 bits for each of n=3 coordinates.
         15-bit Hilbert integer = A B C D E F G H I J K L M N O is stored
         as its Transpose                        ^
         X[0] = A D G J M                    X[2]|  7
         X[1] = B E H K N        &lt;-------&gt;       | /X[1]
         X[2] = C F I L O                   axes |/
                high low                         0------&gt; X[0]
     * </pre>
     * 
     * @param index
     *            index to be tranposed
     * @return transposed index
     */
    @VisibleForTesting
    long[] transpose(Number160 index) {
        byte[] b = index.toByteArray();
        long[] x = new long[dimensions];
        for (int idx = 0; idx < 8 * b.length; idx++) {
            if ((b[b.length - 1 - idx / 8] & (1L << (idx % 8))) != 0) {
                int dim = (length - idx - 1) % dimensions;
                int shift = (idx / dimensions) % bits;
                x[dim] |= 1L << shift;
            }
        }
        return x;
    }

    /**
     * <p>
     * Given the axes (coordinates) of a point in N-Dimensional space, find the
     * distance to that point along the Hilbert curve. That distance will be
     * transposed; broken into pieces and distributed into an array.
     *
     * <p>
     * The number of dimensions is the length of the hilbertAxes array.
     *
     * <p>
     * Note: In Skilling's paper, this function is called AxestoTranspose.
     * 
     * @param mutate
     * 
     * @param point
     *            Point in N-space
     * @return The Hilbert distance (or index) as a transposed Hilbert index
     */
    @VisibleForTesting
    static long[] transposedIndex(int bits, long... point) {
        final long M = 1L << (bits - 1);
        final int n = point.length; // n: Number of dimensions
        final long[] x = Arrays.copyOf(point, n);
        long p, q, t;
        int i;
        // Inverse undo
        for (q = M; q > 1; q >>= 1) {
            p = q - 1;
            for (i = 0; i < n; i++)
                if ((x[i] & q) != 0)
                    x[0] ^= p; // invert
                else {
                    t = (x[0] ^ x[i]) & p;
                    x[0] ^= t;
                    x[i] ^= t;
                }
        } // exchange
          // Gray encode
        for (i = 1; i < n; i++)
            x[i] ^= x[i - 1];
        t = 0;
        for (q = M; q > 1; q >>= 1)
            if ((x[n - 1] & q) != 0)
                t ^= q - 1;
        for (i = 0; i < n; i++)
            x[i] ^= t;

        return x;
    }

    /**
     * Converts the Hilbert transposed index into an N-dimensional point
     * expressed as a vector of {@code long}.
     * 
     * In Skilling's paper this function is named {@code TransposeToAxes}
     * 
     * @param transposedIndex
     *            distance along the Hilbert curve in transposed form
     * @return the coordinates of the point represented by the transposed index
     *         on the Hilbert curve
     */
    static long[] transposedIndexToPoint(int bits, long... x) {
        final long N = 2L << (bits - 1);
        // Note that x is mutated by this method (as a performance improvement
        // to avoid allocation)
        int n = x.length; // number of dimensions
        long p, q, t;
        int i;
        // Gray decode by H ^ (H/2)
        t = x[n - 1] >> 1;
        // Corrected error in Skilling's paper on the following line. The
        // appendix had i >= 0 leading to negative array index.
        for (i = n - 1; i > 0; i--)
            x[i] ^= x[i - 1];
        x[0] ^= t;
        // Undo excess work
        for (q = 2; q != N; q <<= 1) {
            p = q - 1;
            for (i = n - 1; i >= 0; i--)
                if ((x[i] & q) != 0L)
                    x[0] ^= p; // invert
                else {
                    t = (x[0] ^ x[i]) & p;
                    x[0] ^= t;
                    x[i] ^= t;
                }
        } // exchange
        return x;
    }

    // Quote from Paul Chernoch
    // Interleaving means take one bit from the first matrix element, one bit
    // from the next, etc, then take the second bit from the first matrix
    // element, second bit from the second, all the way to the last bit of the
    // last element. Combine those bits in that order into a single BigInteger,
    // which can have as many bits as necessary. This converts the array into a
    // single number.
    @VisibleForTesting
    Number160 toIndex(long... transposedIndex) {
        byte[] b = new byte[length];
        int bIndex = length - 1;
        long mask = 1L << (bits - 1);
        for (int i = 0; i < bits; i++) {
            for (int j = 0; j < transposedIndex.length; j++) {
                if ((transposedIndex[j] & mask) != 0) {
                    b[length - 1 - bIndex / 8] |= 1 << (bIndex % 8);
                }
                bIndex--;
            }
            mask >>= 1;
        }
        // b is expected to be BigEndian
        return new Number160(1, b);
    }
    
    public Number160 index(Profile profile) {
        long [] points;
        int index = 0; 
        
        List<String> singleList = profile.getSingleList();
        
        if(singleList.size() <= 1) {
            System.out.println("Profile needs to be >= 2");
            return null;
        }
        
        int size = singleList.size();
        points = new long[size];
        
        for(String single : singleList) {
            points[index] = Base39.decodeBase39(single);
            index++;
        }

        return index(points);
    }
    
    public Number160 index(List<String> profile, String... payload) {
        long [] points;
        int index = 0; 
        //List<String> singleList = profile.getSingleList();
        
        int size = profile.size(); //+ payload.length;
        points = new long[size];
        
        
        for(String single : profile) {
            points[index] = Base39.decodeBase39(single);
            index++;
        }
        
//        for(String pload : payload) {
//            points[index] = Base39.decodeBase39(pload);
//            index++;
//        }

        return index(points);
    }
}