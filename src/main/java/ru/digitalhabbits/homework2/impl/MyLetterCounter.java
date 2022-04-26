package ru.digitalhabbits.homework2.impl;

import ru.digitalhabbits.homework2.LetterCounter;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MyLetterCounter implements LetterCounter {

    @Override
    public Map<Character, Long> count(String input) {
        return input.chars()
                .mapToObj(ch -> new Character((char)ch))
                .collect(Collectors.groupingBy(Function.identity(),Collectors.counting()));
    }


}
