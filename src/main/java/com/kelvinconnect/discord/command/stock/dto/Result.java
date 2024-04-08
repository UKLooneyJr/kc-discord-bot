
package com.kelvinconnect.discord.command.stock.dto;

import java.math.BigInteger;
import java.util.LinkedHashMap;
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
    "v",
    "vw",
    "o",
    "c",
    "h",
    "l",
    "t",
    "n"
})
@Generated("jsonschema2pojo")
public class Result {

    @JsonProperty("v")
    private BigInteger v;
    @JsonProperty("vw")
    private Double vw;
    @JsonProperty("o")
    private Double o;
    @JsonProperty("c")
    private Double c;
    @JsonProperty("h")
    private Double h;
    @JsonProperty("l")
    private Double l;
    @JsonProperty("t")
    private Long t;
    @JsonProperty("n")
    private Integer n;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new LinkedHashMap<String, Object>();

    @JsonProperty("v")
    public BigInteger getV() {
        return v;
    }

    @JsonProperty("v")
    public void setV(BigInteger v) {
        this.v = v;
    }

    @JsonProperty("vw")
    public Double getVw() {
        return vw;
    }

    @JsonProperty("vw")
    public void setVw(Double vw) {
        this.vw = vw;
    }

    @JsonProperty("o")
    public Double getO() {
        return o;
    }

    @JsonProperty("o")
    public void setO(Double o) {
        this.o = o;
    }

    @JsonProperty("c")
    public Double getC() {
        return c;
    }

    @JsonProperty("c")
    public void setC(Double c) {
        this.c = c;
    }

    @JsonProperty("h")
    public Double getH() {
        return h;
    }

    @JsonProperty("h")
    public void setH(Double h) {
        this.h = h;
    }

    @JsonProperty("l")
    public Double getL() {
        return l;
    }

    @JsonProperty("l")
    public void setL(Double l) {
        this.l = l;
    }

    @JsonProperty("t")
    public Long getT() {
        return t;
    }

    @JsonProperty("t")
    public void setT(Long t) {
        this.t = t;
    }

    @JsonProperty("n")
    public Integer getN() {
        return n;
    }

    @JsonProperty("n")
    public void setN(Integer n) {
        this.n = n;
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
