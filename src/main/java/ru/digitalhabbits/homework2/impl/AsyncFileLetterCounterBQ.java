package ru.digitalhabbits.homework2.impl;

import lombok.SneakyThrows;
import ru.digitalhabbits.homework2.FileLetterCounter;
import ru.digitalhabbits.homework2.FileReader;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.stream.Collectors;

//todo Make your impl
public class AsyncFileLetterCounterBQ implements FileLetterCounter {

    private final FileReader reader;
    private List<Map<Character, Long>> countedLines = new CopyOnWriteArrayList<>();
    private Map<Character, Long> result = new ConcurrentHashMap<>();
    private static final int BUFFER_SIZE = 100;

    //    class Worker implements Runnable {
//        int line;
//        public Worker(int i) {
//            this.line = i;
//        }
//
//        @SneakyThrows
//
//        @Override
//        public void run() {
//            countedLines.add(new MyLetterCounter().count(listFileLines.get(line)));
//            barrier.await();
//            new MyLetterCountMerge().merge(countedLines.get(line), result);
//        }
//    }
    public Map<Character, Long> getResult() {
        return result;
    }

    public AsyncFileLetterCounterBQ() {
        reader = new MyFileReader();
    }

    @SneakyThrows
    @Override
    public Map<Character, Long> count(File input) throws InterruptedException, FileNotFoundException {

        BlockingQueue<String> queue = new LinkedBlockingQueue<>();

        ExecutorService executor = Executors.newCachedThreadPool();


        Callable taskReading = new Callable() {
            @SneakyThrows
            @Override
            public List <String> call() {
            return new MyFileReader().readLines(input)
                        .peek(line -> {
                            while (queue.size() == BUFFER_SIZE) {
                                try {
                                    wait();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            queue.add(line);
                        })
                        .collect(Collectors.toList());
            }
        };

        Callable taskCounting = new Callable() {
            @Override
            public Map<Character, Long> call() throws Exception {
                while (queue.size() == 0){
                    wait ();
                }
                notifyAll();
                return result = new MyLetterCountMerge().merge(result,new MyLetterCounter().count(queue.remove()));
            }
        };

//        Callable taskMerging = new Callable() {
//            @Override
//            public Map<Character, Long> call() throws Exception {
//                return null;
//            }
//        };


        List<Callable<Map<Character,Long>>> taskList = Arrays.asList(
                taskReading,
                taskCounting
//                ,taskMerging
        );

//        executor.invokeAll(taskList);
        executor.submit(taskReading);
        executor.submit(taskCounting);

        return result;
    }

    public static void main(String[] args) throws BrokenBarrierException, FileNotFoundException, ExecutionException, InterruptedException {
        FileLetterCounter counter = new AsyncFileLetterCounterBQ();
        counter.count(new File("D:/Myke/Documents/JavaProjects/DH/HW2-Multithreading/src/test/resources/test.txt"));
    }


}
