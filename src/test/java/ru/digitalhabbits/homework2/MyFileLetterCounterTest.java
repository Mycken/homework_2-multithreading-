package ru.digitalhabbits.homework2;

import org.junit.jupiter.api.Test;
import ru.digitalhabbits.homework2.impl.MyFileLetterCounter;
import ru.digitalhabbits.homework2.impl.MyFileReader;
import ru.digitalhabbits.homework2.impl.MyLetterCountMerge;
import ru.digitalhabbits.homework2.impl.MyLetterCounter;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import static com.google.common.io.Resources.getResource;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
class MyFileLetterCounterTest {
    @Test
    void file_reader_should_return_expected_lines_number() throws FileNotFoundException {
        var file = getFile("test.txt");
        MyFileReader reader = new MyFileReader();
        assertThat(reader.readLines(file).count()).isEqualTo(1000L);
    }

    @Test
    void letter_counter_return_expected_chars_numbers_of_first_line() throws FileNotFoundException {
        var file = getFile("test.txt");
        var reader = new MyFileReader();

        assertThat(new MyLetterCounter().count(reader.readLines(file).findFirst().get())).containsOnly(
                entry('a', 2L)
                , entry('b', 2L)
                , entry('c', 3L)
                , entry('d', 3L)
                , entry('e', 4L)
                , entry('f', 2L)
        );
    }

    @Test
    void letter_count_merge_should_return_expected_merge_result() {
        Map<Character, Long> first = new ConcurrentHashMap<>();
        first.put('a',1L);
        first.put('b',2L);
        first.put('c',3L);
        first.put('d',4L);

        Map<Character, Long> second = new ConcurrentHashMap<>();
        second.put('a',5L);
        second.put('b',6L);
        second.put('c',7L);
        second.put('d',8L);

        MyLetterCountMerge countMerge = new MyLetterCountMerge();

        assertThat(countMerge.merge(first,second)).containsOnly(
                entry('a', 6L)
                , entry('b', 8L)
                , entry('c', 10L)
                , entry('d', 12L)
        );
    }

    @Test
    void file_letter_counting_should_return_predicted_count() throws FileNotFoundException, InterruptedException, BrokenBarrierException, ExecutionException {
        var file = getFile("test.txt");
        var counter = new MyFileLetterCounter();

        Map<Character, Long> count = new MyFileLetterCounter().count(file);

        assertThat(count).containsOnly(
                entry('a', 2697L),
                entry('b', 2683L),
                entry('c', 2647L),
                entry('d', 2613L),
                entry('e', 2731L),
                entry('f', 2629L)
        );
    }

    private File getFile(String name) {
        return new File(getResource(name).getPath());
    }

}