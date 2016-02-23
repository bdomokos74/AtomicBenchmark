package org.bds;

import java.io.BufferedWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.LongAdder;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by bdomokos on 19/10/15.
 */
public class AtomicBenchmark implements Runnable{
    interface Counter {
        void inc();
        int get();
    }
    static class SimpleCounter implements Counter {
        private int cnt;
        @Override
        public void inc() {
            cnt++;
        }
        @Override
        public int get() {
            return cnt;
        }
    }
    static class AtomicCounter implements Counter {
        private AtomicInteger cnt = new AtomicInteger();

        @Override
        public void inc() {
            cnt.incrementAndGet();
        }
        @Override
        public int get() {
            return cnt.get();
        }
    }

    static class SyncCounter implements Counter {
        private int cnt;

        @Override
        public synchronized void inc() {
            cnt++;
        }
        @Override
        public synchronized int get() {
            return cnt;
        }
    }

    static class LockCounter implements Counter {
        private int cnt;
        private ReentrantLock lock = new ReentrantLock();
        @Override
        public void inc() {
            lock.lock();
            cnt++;
            lock.unlock();
        }
        @Override
        public int get() {
            lock.lock();
            int tmp = cnt;
            lock.unlock();
            return tmp;
        }
    }
    static class LongAdderCounter implements Counter {
        private LongAdder adder = new LongAdder();
        @Override
        public void inc() {
            adder.increment();
        }
        @Override
        public int get() {
            return adder.intValue();
        }
    }

    private Counter counter;
    public AtomicBenchmark(Counter counter) {
        this.counter = counter;
    }

    @Override
    public void run() {
        int maxCount = 10000;
        for (int i = 0; i < maxCount; i++) {
            counter.inc();
        }
    }

    public static void main(String[] args) throws Exception {
        Path output = Paths.get("timing_data.txt");
        try(BufferedWriter writer = Files.newBufferedWriter(output)) {
            int n = 10000;
            for(String name: Arrays.asList("SimpleCounter", "AtomicCounter", "LockCounter",
                    "LongAdderCounter", "SyncCounter")) {
                Class testing = Class.forName("org.bds.AtomicBenchmark$"+name);
                for (int i = 0; i < n; i++) {
                    Counter aCounter = (Counter)testing.newInstance();
                    Thread t1 = new Thread(new AtomicBenchmark(aCounter));
                    Thread t2 = new Thread(new AtomicBenchmark(aCounter));
                    long startTime = System.nanoTime();
                    t1.start();
                    t2.start();
                    t1.join();
                    t2.join();
                    long endTime = System.nanoTime();
                    long diff = endTime - startTime;
                    if(diff>=0) {
                        writer.write(String.valueOf((double)diff/1000.0));
                        writer.write(",");
                        writer.write(name);
                        writer.newLine();
                    }
                }
            }
        }
    }
}
