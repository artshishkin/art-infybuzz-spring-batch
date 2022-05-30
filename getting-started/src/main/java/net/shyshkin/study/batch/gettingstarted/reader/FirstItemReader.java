package net.shyshkin.study.batch.gettingstarted.reader;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.stereotype.Component;

import java.util.Deque;
import java.util.LinkedList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Component
public class FirstItemReader implements ItemReader<Integer> {

    private final Deque<Integer> fifo = IntStream.rangeClosed(1, 10)
            .boxed()
            .collect(Collectors.toCollection(LinkedList::new));

    @Override
    public Integer read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        Integer value = fifo.poll();
        log.debug("Inside item reader: {}", value);
        return value;
    }
}
