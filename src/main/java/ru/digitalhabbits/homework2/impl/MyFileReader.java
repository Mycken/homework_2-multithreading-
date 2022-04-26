package ru.digitalhabbits.homework2.impl;

import ru.digitalhabbits.homework2.FileReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.stream.Stream;

public class MyFileReader implements FileReader {


    @Override
    public Stream<String> readLines(File file) throws FileNotFoundException {
        return new BufferedReader(new java.io.FileReader(file)).lines();
    }
}
