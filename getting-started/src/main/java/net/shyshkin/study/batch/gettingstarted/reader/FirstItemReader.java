package net.shyshkin.study.batch.gettingstarted.reader;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Component
public class FirstItemReader implements ItemReader<Integer> {

    private final List<Integer> data = IntStream.rangeClosed(1, 10)
            .boxed()
            .collect(Collectors.toList());

    private int currentIndex = -1;

    @Override
    public Integer read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        currentIndex++;
        if (currentIndex >= data.size()) {
            currentIndex = -1;
            return null;
        }
        Integer value = data.get(currentIndex);
        log.debug("Inside item reader: {}", value);
        return value;
    }
}
