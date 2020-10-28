package qube;

import qube.algorithm3x3.Algorithm3x3;
import qube.algorithm3x3.ICube;

import java.util.concurrent.*;
import java.util.function.Supplier;

public class StressBox implements Runnable
{
    private static final int MAX_THREADS = 16;

    private final ICube[] cubes_;

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

        for(int i = 0; i < cubes_.length; ++i)
        {
            final int index = i;
            final ICube cube = cubes_[index];

            executor.execute(() ->
            {
                try
                {
                    new Algorithm3x3(cube).solve().get();

                    latch.countDown();
                    System.out.format("Finished solving cube (%d/%d)%n", index, cubes_.length);
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

            System.out.println("All cubes solved!");
        }
        catch(InterruptedException e)
        {
            e.printStackTrace();
        }

        executor.shutdown();
    }
}
