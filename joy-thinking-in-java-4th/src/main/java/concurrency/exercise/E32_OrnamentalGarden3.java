package concurrency.exercise;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static net.mindview.util.Print.print;

class Count {
    private int count = 0;
    private Random rand = new Random(47);

    // Remove the synchronized keyword to see counting fail:
    public synchronized int increment() {
        int temp = count;
        if (rand.nextBoolean()) // Yield half the time
            Thread.yield();
        return (count = ++temp);
    }

    public synchronized int value() {
        return count;
    }
}

class Entrance3 implements Runnable {

    private final CountDownLatch latch;
    private static Count count = new Count();
    private static List<Entrance3> entrances = new ArrayList<Entrance3>();
    private int number;
    private final int id;
    private static volatile boolean canceled;

    public static void cancel() {
        canceled = true;
    }

    public Entrance3(CountDownLatch ltc, int id) {
        latch = ltc;
        this.id = id;
        entrances.add(this);
    }

    @Override
    public void run() {
        while (!canceled) {
            synchronized (this) {
                ++number;
            }
            print(this + " Total: " + count.increment());
            try {
                TimeUnit.MILLISECONDS.sleep(100);
            } catch (InterruptedException e) {
                print("sleep interrupted");
            }
        }
        latch.countDown();
        print("Stopping " + this);
    }

    public synchronized int getValue() {
        return number;
    }

    public String toString() {
        return "Entrance " + id + ": " + getValue();
    }

    public static int getTotalCount() {
        return count.value();
    }

    public static int sumEntrances() {
        int sum = 0;
        for (Entrance3 entrance : entrances)
            sum += entrance.getValue();
        return sum;
    }
}

public class E32_OrnamentalGarden3 {
    public static void main(String[] args) throws Exception {
        CountDownLatch latch = new CountDownLatch(5);
        ExecutorService exec = Executors.newCachedThreadPool();
        for (int i = 0; i < 5; i++)
            exec.execute(new Entrance3(latch, i));
        TimeUnit.SECONDS.sleep(3);
        Entrance3.cancel();
        exec.shutdown();
        latch.await(); // Wait for results
        print("Total: " + Entrance3.getTotalCount());
        print("Sum of Entrances: " + Entrance3.sumEntrances());
    }
}
