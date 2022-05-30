package net.shyshkin.study.batch.gettingstarted.writer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
public class FirstItemWriter implements ItemWriter<Long> {

    @Override
    public void write(List<? extends Long> items) throws Exception {
        items.forEach(item -> log.debug("Inside Item Writer: {}", item));
    }
}
