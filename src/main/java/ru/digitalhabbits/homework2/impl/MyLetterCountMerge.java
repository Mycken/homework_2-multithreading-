package ru.digitalhabbits.homework2.impl;

import ru.digitalhabbits.homework2.LetterCountMerger;

import java.util.Map;


public class MyLetterCountMerge implements LetterCountMerger {
    @Override
    public Map<Character, Long> merge(Map<Character, Long> first, Map<Character, Long> second) {
        for (Map.Entry<Character, Long> e : first.entrySet()){
            second.merge(e.getKey(), e.getValue(), Long::sum);
        }
        return second;

    }
}
