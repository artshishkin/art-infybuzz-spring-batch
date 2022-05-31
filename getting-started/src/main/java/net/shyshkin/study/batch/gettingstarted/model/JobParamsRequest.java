package net.shyshkin.study.batch.gettingstarted.model;

import lombok.Data;

@Data
public class JobParamsRequest {

    private final String paramKey;
    private final String paramValue;

}
