package ru.digitalhabbits.homework2.impl;

import ru.digitalhabbits.homework2.FileLetterCounter;
import ru.digitalhabbits.homework2.FileReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class AsyncFileLetterCounter implements FileLetterCounter {
    private static final String FILE_NAME = "D:/Myke/Documents/JavaProjects/DH/HW2-Multithreading/src/test/resources/test.txt";
    private final FileReader reader;
    private List<String> listFileLines;

    Map<Character, Long> result = new ConcurrentHashMap<>();

    public AsyncFileLetterCounter() throws FileNotFoundException {
        reader = new MyFileReader();
    }

    public static void main(String[] args) throws FileNotFoundException, ExecutionException, InterruptedException {
        System.out.println(new AsyncFileLetterCounter().count(new File(FILE_NAME)));
    }
    @Override
    public Map<Character, Long> count(File input) throws ExecutionException, InterruptedException, FileNotFoundException {

        ExecutorService executor = Executors.newCachedThreadPool();

        listFileLines = this
                .reader
                .readLines(input)
                .collect(Collectors.toList());

        Callable<Map<Character, Long>> taskCounting
                = new Callable<Map<Character, Long>>() {
            @Override
            public Map<Character, Long> call() throws Exception {
                for (String l : listFileLines) {
                    result = new MyLetterCountMerge().merge(result, new MyLetterCounter().count(l));
                }
                return result;
            }
        };

        Future<Map<Character, Long>> futureCounting = executor.submit(taskCounting);

        executor.shutdown();

        return futureCounting.get();
    }
}
