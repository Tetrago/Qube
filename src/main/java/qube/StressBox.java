package qube;

import qube.algorithm3x3.Algorithm3x3;
import qube.algorithm3x3.ICube;

import java.util.concurrent.*;
import java.util.function.Supplier;

public class StressBox implements Runnable
{
    private static final int MAX_THREADS = 16;

    private final ICube[] cubes_;

    /**
     * Creates a stress tester.
     *
     * @param   count       Number of cubes to attempt to solve.
     * @param   supplier    Supplier that providers new cubes.
     */
    private StressBox(int count, Supplier<ICube> supplier)
    {
        cubes_ = new ICube[count];
        for(int i = 0; i < count; ++i)
        {
            cubes_[i] = supplier.get();
        }
    }

    /**
     * Runs a test on multiple cubes.
     *
     * @param   count       Number of cubes to generate.
     * @param   supplier    Supplier that provides new cubes.
     *
     * @return              Asynchronous future.
     */
    public static Future<Void> test(int count, Supplier<ICube> supplier)
    {
        return CompletableFuture.runAsync(new StressBox(count, supplier));
    }

    @Override
    public void run()
    {
        ExecutorService executor = Executors.newFixedThreadPool(MAX_THREADS);

        CountDownLatch latch = new CountDownLatch(cubes_.length);
        CountDownLatch successes = new CountDownLatch(cubes_.length);

        for(int i = 0; i < cubes_.length; ++i)
        {
            final int index = i;
            final ICube cube = cubes_[index];

            executor.execute(() ->
            {
                try
                {
                    long last  = System.nanoTime();

                    new Algorithm3x3(cube).solve().get();

                    long elapsed = System.nanoTime() - last;
                    double seconds = elapsed * 0.000000001;

                    if(cube.isComplete())
                    {
                        System.out.format("Solved cube (%d/%d) in %.6f seconds.%n", index, cubes_.length, seconds);
                        successes.countDown();
                    }
                    else
                    {
                        System.err.format("Failed to solve cube (%d/%d)%n", index, cubes_.length);
                    }

                    latch.countDown();
                }
                catch(InterruptedException | ExecutionException e)
                {
                    e.printStackTrace();
                }
            });
        }

        try
        {
            latch.await();
            System.out.format("Cubes solved: (%d/%d).%n", cubes_.length - successes.getCount(), cubes_.length);

            if(successes.getCount() == 0)
            {
                System.out.println("All cubes solved!");
            }
            else
            {
                System.err.println("Failed to solve all cubes!");
            }
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }

        executor.shutdown();
    }
}
