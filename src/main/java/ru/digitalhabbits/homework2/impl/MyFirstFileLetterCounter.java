package ru.digitalhabbits.homework2.impl;

import ru.digitalhabbits.homework2.FileLetterCounter;
import ru.digitalhabbits.homework2.FileReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class MyFirstFileLetterCounter implements FileLetterCounter {
    private static final String FILE_NAME = "D:/Myke/Documents/JavaProjects/DH/HW2-Multithreading/src/test/resources/test.txt";
    private final FileReader reader;
    private final List<String> listFileLines;

    public MyFirstFileLetterCounter() throws FileNotFoundException {
        reader = new MyFileReader();
        listFileLines = this
                .reader
                .readLines(new File(FILE_NAME))
                .collect(Collectors.toList());
    }

    public static void main(String[] args) throws FileNotFoundException, ExecutionException, InterruptedException {
        System.out.println(new MyFirstFileLetterCounter().count(new File(FILE_NAME)));
    }
    @Override
    public Map<Character, Long> count(File input) throws ExecutionException, InterruptedException {

        ExecutorService executor = Executors.newCachedThreadPool();

        List<Map<Character, Long>> countedLines = new ArrayList<>();

        Map<Character, Long> result = new ConcurrentHashMap<>();

        Callable<List<Map<Character, Long>>> taskCounting
                = new Callable<>() {
            @Override
            public List<Map<Character, Long>> call() throws Exception {
                for (String l : listFileLines)
                    countedLines.add(new MyLetterCounter().count(l));
                return countedLines;
            }
        };

        Callable<Map<Character, Long>> taskMerging =
                new Callable<>() {
                    @Override
                    public Map<Character, Long> call() throws Exception {
                        for (Map m : countedLines) {
                            new MyLetterCountMerge().merge(m, result);
                        }
                        return result;
                    }
                };

        Future<List<Map<Character, Long>>> futureCounting = executor.submit(taskCounting);

        System.out.println(futureCounting.get()); //если этой строки нет, то тогда не считает

        countedLines.stream().collect(Collectors.toList());
        Future<Map<Character,Long>> futureMerging = executor.submit(taskMerging);

        executor.shutdown();

        return futureMerging.get();
    }
}
