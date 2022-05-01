package ru.digitalhabbits.homework2.impl;

import lombok.SneakyThrows;
import ru.digitalhabbits.homework2.FileLetterCounter;
import ru.digitalhabbits.homework2.FileReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

//todo Make your impl
public class AsyncFileLetterCounterBQRunnable implements FileLetterCounter {

    private final FileReader reader;
//    private List<Map<Character, Long>> countedLines = new CopyOnWriteArrayList<>();
    private Map<Character, Long> result = new ConcurrentHashMap<>();
    private static final int BUFFER_SIZE = 100;

    public Map<Character, Long> getResult() {
        return result;
    }

    public AsyncFileLetterCounterBQRunnable() {
        reader = new MyFileReader();
    }

    @SneakyThrows
    @Override
    public Map<Character, Long> count(File input) throws InterruptedException, FileNotFoundException {

        BlockingQueue<String> queue = new LinkedBlockingQueue<>();
        ExecutorService executorReading = Executors.newFixedThreadPool(10);
//        ExecutorService executorCounting = Executors.newCachedThreadPool();

        Runnable taskCounting = new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                while (queue.size() == 0){
                    wait ();
                }
                notify();
                result = new MyLetterCountMerge().merge(result,new MyLetterCounter().count(queue.remove()));
            }
        };

        Runnable taskReading = new Runnable() {
            @SneakyThrows
            @Override
            public void run() {
                System.out.println(new MyFileReader().readLines(input)
                        .peek(line -> queue.add(line))
                        .count());
            }
        };

        executorReading.execute(taskReading);

        executorReading.execute(taskCounting);
        executorReading.execute(taskCounting);
        executorReading.execute(taskCounting);
        executorReading.execute(taskCounting);
        executorReading.execute(taskCounting);
        executorReading.execute(taskCounting);
        executorReading.execute(taskCounting);

//        executorReading.shutdown();
//        executorCounting.shutdown();

        return result;
    }

    public static void main(String[] args) throws BrokenBarrierException, FileNotFoundException, ExecutionException, InterruptedException {
        FileLetterCounter counter = new AsyncFileLetterCounterBQRunnable();
        System.out.println(counter.count(new File("D:/Myke/Documents/JavaProjects/DH/HW2-Multithreading/src/test/resources/test.txt")));
    }


}
