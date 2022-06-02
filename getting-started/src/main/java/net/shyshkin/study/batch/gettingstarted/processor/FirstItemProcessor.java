package net.shyshkin.study.batch.gettingstarted.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@StepScope
public class FirstItemProcessor implements ItemProcessor<Integer, Long> {

    private final Map<String, String> jobParameters;

    public FirstItemProcessor(@Value("#{jobParameters}") Map<String, String> jobParameters) {
        this.jobParameters = jobParameters;
        log.debug("JobParameters: {}", jobParameters);
    }

    @Override
    public Long process(Integer item) throws Exception {
        log.debug("Inside Item Processor: {}", item);
        String processorPause = jobParameters.get("processorPause");
        if (processorPause != null)
            Thread.sleep(Long.parseLong(processorPause));
        return (long) (item + 20);
    }
}
