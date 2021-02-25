/*
 * Copyright (c) 2018. Phasmid Software
 */

package edu.neu.coe.info6205.util;

import java.util.Arrays;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.Collections;

import edu.neu.coe.info6205.sort.Helper;
import edu.neu.coe.info6205.sort.Sort;
import edu.neu.coe.info6205.sort.simple.InsertionSort;

import static edu.neu.coe.info6205.util.Utilities.formatWhole;

/**
 * This class implements a simple Benchmark utility for measuring the running time of algorithms.
 * It is part of the repository for the INFO6205 class, taught by Prof. Robin Hillyard
 * <p>
 * It requires Java 8 as it uses function types, in particular, UnaryOperator&lt;T&gt; (a function of T => T),
 * Consumer&lt;T&gt; (essentially a function of T => Void) and Supplier&lt;T&gt; (essentially a function of Void => T).
 * <p>
 * In general, the benchmark class handles three phases of a "run:"
 * <ol>
 *     <li>The pre-function which prepares the input to the study function (field fPre) (may be null);</li>
 *     <li>The study function itself (field fRun) -- assumed to be a mutating function since it does not return a result;</li>
 *     <li>The post-function which cleans up and/or checks the results of the study function (field fPost) (may be null).</li>
 * </ol>
 * <p>
 * Note that the clock does not run during invocations of the pre-function and the post-function (if any).
 *
 * @param <T> The generic type T is that of the input to the function f which you will pass in to the constructor.
 */
public class Benchmark_Timer<T> implements Benchmark<T> {

    /**
     * Calculate the appropriate number of warmup runs.
     *
     * @param m the number of runs.
     * @return at least 2 and at most m/10.
     */
    static int getWarmupRuns(int m) {
        return Integer.max(2, Integer.min(10, m / 10));
    }

    /**
     * Run function f m times and return the average time in milliseconds.
     *
     * @param supplier a Supplier of a T
     * @param m        the number of times the function f will be called.
     * @return the average number of milliseconds taken for each run of function f.
     */
    @Override
    public double runFromSupplier(Supplier<T> supplier, int m) {
        logger.info("Begin run: " + description + " with " + formatWhole(m) + " runs");
        // Warmup phase
        final Function<T, T> function = t -> {
            fRun.accept(t);
            return t;
        };
//        System.out.println("\nWarm up");
        new Timer().repeat(getWarmupRuns(m), supplier, function, fPre, null);
//        System.out.println("\nActual One");
        // Timed phase
        return new Timer().repeat(m, supplier, function, fPre, fPost);
    }

    /**
     * Constructor for a Benchmark_Timer with option of specifying all three functions.
     *
     * @param description the description of the benchmark.
     * @param fPre        a function of T => T.
     *                    Function fPre is run before each invocation of fRun (but with the clock stopped).
     *                    The result of fPre (if any) is passed to fRun.
     * @param fRun        a Consumer function (i.e. a function of T => Void).
     *                    Function fRun is the function whose timing you want to measure. For example, you might create a function which sorts an array.
     *                    When you create a lambda defining fRun, you must return "null."
     * @param fPost       a Consumer function (i.e. a function of T => Void).
     */
    public Benchmark_Timer(String description, UnaryOperator<T> fPre, Consumer<T> fRun, Consumer<T> fPost) {
        this.description = description;
        this.fPre = fPre;
        this.fRun = fRun;
        this.fPost = fPost;
    }

    /**
     * Constructor for a Benchmark_Timer with option of specifying all three functions.
     *
     * @param description the description of the benchmark.
     * @param fPre        a function of T => T.
     *                    Function fPre is run before each invocation of fRun (but with the clock stopped).
     *                    The result of fPre (if any) is passed to fRun.
     * @param fRun        a Consumer function (i.e. a function of T => Void).
     *                    Function fRun is the function whose timing you want to measure. For example, you might create a function which sorts an array.
     */
    public Benchmark_Timer(String description, UnaryOperator<T> fPre, Consumer<T> fRun) {
        this(description, fPre, fRun, null);
    }

    /**
     * Constructor for a Benchmark_Timer with only fRun and fPost Consumer parameters.
     *
     * @param description the description of the benchmark.
     * @param fRun        a Consumer function (i.e. a function of T => Void).
     *                    Function fRun is the function whose timing you want to measure. For example, you might create a function which sorts an array.
     *                    When you create a lambda defining fRun, you must return "null."
     * @param fPost       a Consumer function (i.e. a function of T => Void).
     */
    public Benchmark_Timer(String description, Consumer<T> fRun, Consumer<T> fPost) {
        this(description, null, fRun, fPost);
    }

    /**
     * Constructor for a Benchmark_Timer where only the (timed) run function is specified.
     *
     * @param description the description of the benchmark.
     * @param f           a Consumer function (i.e. a function of T => Void).
     *                    Function f is the function whose timing you want to measure. For example, you might create a function which sorts an array.
     */
    public Benchmark_Timer(String description, Consumer<T> f) {
        this(description, null, f, null);
        
    }
        
    public static void main(String[] args) {
        Random random = new Random();
        int m = 1000; 
        int n = 500; 
        for (int k = 0; k < 5; k++) {
            Integer[] random_a = new Integer[n];
            Integer[] ascending = new Integer[n];
            Integer[] partial_a = new Integer[n];
            Integer[] descending = new Integer[n];
            for (int i = 0; i < n; i++) {
            	random_a[i] = random.nextInt();	            
	            if(i >= n/2) {
	            	partial_a[i]=i;
	            }
	            else {
	            	partial_a[i]=random.nextInt();
	            }
            }
            
            ascending = Arrays.copyOf(random_a, n);
            descending = Arrays.copyOf(random_a, n);
            Arrays.sort(ascending);
            
            Arrays.sort(descending, Collections.reverseOrder());
//            System.out.println("\n");
//            System.out.println("unsorted: ");
//            for(int i = 0; i < arrayvo.length; i++) {
//            	System.out.print(" "+ arrayvo[i] +" ");
//            }

            
            
            
            // Random
            InsertionSort<Integer> random_s = new InsertionSort<>();
            
            UnaryOperator<Integer[]> pre_r = (x) -> Arrays.copyOf(random_a, random_a.length);
            Consumer<Integer[]> Run_r = (x) -> {
            	random_s.sort(x, random_a[0], random_a[random_a.length-1]);
            };
            final Helper<Integer> helper_r = random_s.getHelper();
            Consumer<Integer[]> post_r = (x) -> {
                if (!helper_r.sorted(x)) throw new RuntimeException("not sorted");
            };
            
            Benchmark<Integer[]> bm_r = new Benchmark_Timer<>("random",pre_r, Run_r, post_r);
            double x_r = bm_r.run(random_a, m);
            System.out.println("InsertionSort random: length: " + n + " Running time: " + x_r + " millisecs");
            
            // Partial
            InsertionSort<Integer> partial = new InsertionSort<>();
            
            UnaryOperator<Integer[]> pre_p = (y) -> Arrays.copyOf(partial_a, partial_a.length);
            Consumer<Integer[]> Run_p = (y) -> {
            	partial.sort(y, partial_a[0], partial_a[partial_a.length-1]);
            };
            final Helper<Integer> helper_p = partial.getHelper();
            Consumer<Integer[]> post_p = (y) -> {
                if (!helper_p.sorted(y)) throw new RuntimeException("not sorted");
            };
            
            Benchmark<Integer[]> bm_p = new Benchmark_Timer<>("partially-order",pre_p, Run_p, post_p);
            double x_p = bm_p.run(partial_a, m);
            System.out.println("InsertionSort partially-order: length: " + n + " Running time: " + x_p + " millisecs");
            
            
            // Reverse
            InsertionSort<Integer> reverse = new InsertionSort<>();
            
            Integer[] d = descending;
            UnaryOperator<Integer[]> pre_v = (a) -> Arrays.copyOf(d, d.length);
            Consumer<Integer[]> Run_v = (a) -> {
            	reverse.sort(a, d[0], d[d.length-1]);
            };
            final Helper<Integer> helper_v = reverse.getHelper();
            Consumer<Integer[]> post_v = (a) -> {
                if (!helper_v.sorted(a)) throw new RuntimeException("not sorted");
            };
            
            Benchmark<Integer[]> bm_v = new Benchmark_Timer<>("reverse-order",pre_v, Run_v, post_v);
            double x_v = bm_v.run(descending, m);
            System.out.println("InsertionSort reverse-order: length: " + n + " Running time: " + x_v + " millisecs");
            
            // Normal
            InsertionSort<Integer> normal = new InsertionSort<>();
            
            Integer[] a = ascending;
            UnaryOperator<Integer[]> pre = (x) -> Arrays.copyOf(a, a.length);
            Consumer<Integer[]> Run = (x) -> {
            	normal.sort(x, a[0], a[a.length-1]);
            };
            final Helper<Integer> helper = normal.getHelper();
            Consumer<Integer[]> post = (x) -> {
                if (!helper.sorted(x)) throw new RuntimeException("not sorted");
            };
            
            Benchmark<Integer[]> bm = new Benchmark_Timer<>("normal order",pre, Run, post);
            double x = bm.run(ascending, m);
            System.out.println("InsertionSort normal order: length: " + n + " Running time: " + x + " millisecs");
            
//            System.out.println("sorted: ");
//            for(int i = 0; i < arrayvo.length; i++) {
//            	System.out.print(" "+ arrayvo[i] +" ");
//            }
//            System.out.println("\n");
//            System.out.println("---------\n" + (k+1));
//            array = new Integer[0];
//            arraypo = new Integer[0];
//            arrayvo = new Integer[0];
//            arrayo = new Integer[0];
//            System.out.println("cleared: ");
//            for(int i = 0; i < arrayo.length; i++) {
//            	
//            	System.out.print(" "+ arrayo[i] +" ");
//            }
            n = n * 2;
            
            
        }
    }
    

        
    

    private final String description;
    private final UnaryOperator<T> fPre;
    private final Consumer<T> fRun;
    private final Consumer<T> fPost;

    final static LazyLogger logger = new LazyLogger(Benchmark_Timer.class);
}
