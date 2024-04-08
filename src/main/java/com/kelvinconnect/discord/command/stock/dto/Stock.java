
package com.kelvinconnect.discord.command.stock.dto;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "ticker",
    "queryCount",
    "resultsCount",
    "adjusted",
    "results",
    "status",
    "request_id",
    "count"
})
@Generated("jsonschema2pojo")
public class Stock {

    @JsonProperty("ticker")
    private String ticker;
    @JsonProperty("queryCount")
    private Integer queryCount;
    @JsonProperty("resultsCount")
    private Integer resultsCount;
    @JsonProperty("adjusted")
    private Boolean adjusted;
    @JsonProperty("results")
    private List<Result> results;
    @JsonProperty("status")
    private String status;
    @JsonProperty("request_id")
    private String requestId;
    @JsonProperty("count")
    private Integer count;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("ticker")
    public String getTicker() {
        return ticker;
    }

    @JsonProperty("ticker")
    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    @JsonProperty("queryCount")
    public Integer getQueryCount() {
        return queryCount;
    }

    @JsonProperty("queryCount")
    public void setQueryCount(Integer queryCount) {
        this.queryCount = queryCount;
    }

    @JsonProperty("resultsCount")
    public Integer getResultsCount() {
        return resultsCount;
    }

    @JsonProperty("resultsCount")
    public void setResultsCount(Integer resultsCount) {
        this.resultsCount = resultsCount;
    }

    @JsonProperty("adjusted")
    public Boolean getAdjusted() {
        return adjusted;
    }

    @JsonProperty("adjusted")
    public void setAdjusted(Boolean adjusted) {
        this.adjusted = adjusted;
    }

    @JsonProperty("results")
    public List<Result> getResults() {
        return results;
    }

    @JsonProperty("results")
    public void setResults(List<Result> results) {
        this.results = results;
    }

    @JsonProperty("status")
    public String getStatus() {
        return status;
    }

    @JsonProperty("status")
    public void setStatus(String status) {
        this.status = status;
    }

    @JsonProperty("request_id")
    public String getRequestId() {
        return requestId;
    }

    @JsonProperty("request_id")
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    @JsonProperty("count")
    public Integer getCount() {
        return count;
    }

    @JsonProperty("count")
    public void setCount(Integer count) {
        this.count = count;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
