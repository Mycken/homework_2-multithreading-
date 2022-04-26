package ru.digitalhabbits.homework2.impl;

import lombok.SneakyThrows;
import ru.digitalhabbits.homework2.FileLetterCounter;
import ru.digitalhabbits.homework2.FileReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class MyFileLetterCounter implements FileLetterCounter {
    private static final String FILE_NAME = "D:/Myke/Documents/JavaProjects/DH/HW2-Multithreading/src/test/resources/test.txt";

    private final FileReader reader;
    private CyclicBarrier barrier;
    private List<String> listFileLines;
    private List<Map<Character, Long>> countedLines = new CopyOnWriteArrayList<>();
    private Map<Character, Long> result = new ConcurrentHashMap<>();

    class Worker implements Runnable {

        int line;
        public Worker(int i) {
            this.line = i;
        }

        @SneakyThrows
        @Override
        public void run() {
            countedLines.add(new MyLetterCounter().count(listFileLines.get(line)));
            barrier.await();
            new MyLetterCountMerge().merge(countedLines.get(line), result);
        }

    }
    public Map<Character, Long> getResult() {
        return result;
    }

    public MyFileLetterCounter() {
        reader = new MyFileReader();
    }

    @Override
    public Map<Character, Long> count(File input) throws InterruptedException, FileNotFoundException {
        listFileLines = this
                .reader
                .readLines(input)
                .collect(Collectors.toList());
        int N = listFileLines.size();
        barrier = new CyclicBarrier(N);

        List<Thread> threads = new ArrayList<>(N);
        for (int i = 0; i < N; i++) {
            Thread thread = new Thread(new Worker(i));
            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads)
            thread.join();

        return result;
    }

    public static void main(String[] args) throws FileNotFoundException, InterruptedException {
        System.out.println(new MyFileLetterCounter().count(new File(FILE_NAME)));
    }

}
