package ru.digitalhabbits.homework2;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ExecutionException;

/**
 * Counter characters in given file
 */
public interface FileLetterCounter {

    Map<Character, Long> count(File input) throws ExecutionException, InterruptedException, BrokenBarrierException, FileNotFoundException;

}
