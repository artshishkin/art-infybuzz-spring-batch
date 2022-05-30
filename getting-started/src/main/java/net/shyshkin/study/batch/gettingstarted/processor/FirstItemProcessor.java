package net.shyshkin.study.batch.gettingstarted.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FirstItemProcessor implements ItemProcessor<Integer, Long> {
    @Override
    public Long process(Integer item) throws Exception {
        log.debug("Inside Item Processor: {}", item);
        return (long) (item + 20);
    }
}
