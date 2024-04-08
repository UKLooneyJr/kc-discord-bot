
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
    "ticker",
    "name",
    "market",
    "locale",
    "primary_exchange",
    "type",
    "active",
    "currency_name",
    "cik",
    "composite_figi",
    "share_class_figi",
    "market_cap",
    "phone_number",
    "address",
    "description",
    "sic_code",
    "sic_description",
    "ticker_root",
    "homepage_url",
    "total_employees",
    "list_date",
    "branding",
    "share_class_shares_outstanding",
    "weighted_shares_outstanding",
    "round_lot"
})
@Generated("jsonschema2pojo")
public class Results {

    @JsonProperty("ticker")
    private String ticker;
    @JsonProperty("name")
    private String name;
    @JsonProperty("market")
    private String market;
    @JsonProperty("locale")
    private String locale;
    @JsonProperty("primary_exchange")
    private String primaryExchange;
    @JsonProperty("type")
    private String type;
    @JsonProperty("active")
    private Boolean active;
    @JsonProperty("currency_name")
    private String currencyName;
    @JsonProperty("cik")
    private String cik;
    @JsonProperty("composite_figi")
    private String compositeFigi;
    @JsonProperty("share_class_figi")
    private String shareClassFigi;
    @JsonProperty("market_cap")
    private Double marketCap;
    @JsonProperty("phone_number")
    private String phoneNumber;
    @JsonProperty("address")
    private Address address;
    @JsonProperty("description")
    private String description;
    @JsonProperty("sic_code")
    private String sicCode;
    @JsonProperty("sic_description")
    private String sicDescription;
    @JsonProperty("ticker_root")
    private String tickerRoot;
    @JsonProperty("homepage_url")
    private String homepageUrl;
    @JsonProperty("total_employees")
    private Integer totalEmployees;
    @JsonProperty("list_date")
    private String listDate;
    @JsonProperty("branding")
    private Branding branding;
    @JsonProperty("share_class_shares_outstanding")
    private BigInteger shareClassSharesOutstanding;
    @JsonProperty("weighted_shares_outstanding")
    private BigInteger weightedSharesOutstanding;
    @JsonProperty("round_lot")
    private Integer roundLot;
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

    @JsonProperty("name")
    public String getName() {
        return name;
    }

    @JsonProperty("name")
    public void setName(String name) {
        this.name = name;
    }

    @JsonProperty("market")
    public String getMarket() {
        return market;
    }

    @JsonProperty("market")
    public void setMarket(String market) {
        this.market = market;
    }

    @JsonProperty("locale")
    public String getLocale() {
        return locale;
    }

    @JsonProperty("locale")
    public void setLocale(String locale) {
        this.locale = locale;
    }

    @JsonProperty("primary_exchange")
    public String getPrimaryExchange() {
        return primaryExchange;
    }

    @JsonProperty("primary_exchange")
    public void setPrimaryExchange(String primaryExchange) {
        this.primaryExchange = primaryExchange;
    }

    @JsonProperty("type")
    public String getType() {
        return type;
    }

    @JsonProperty("type")
    public void setType(String type) {
        this.type = type;
    }

    @JsonProperty("active")
    public Boolean getActive() {
        return active;
    }

    @JsonProperty("active")
    public void setActive(Boolean active) {
        this.active = active;
    }

    @JsonProperty("currency_name")
    public String getCurrencyName() {
        return currencyName;
    }

    @JsonProperty("currency_name")
    public void setCurrencyName(String currencyName) {
        this.currencyName = currencyName;
    }

    @JsonProperty("cik")
    public String getCik() {
        return cik;
    }

    @JsonProperty("cik")
    public void setCik(String cik) {
        this.cik = cik;
    }

    @JsonProperty("composite_figi")
    public String getCompositeFigi() {
        return compositeFigi;
    }

    @JsonProperty("composite_figi")
    public void setCompositeFigi(String compositeFigi) {
        this.compositeFigi = compositeFigi;
    }

    @JsonProperty("share_class_figi")
    public String getShareClassFigi() {
        return shareClassFigi;
    }

    @JsonProperty("share_class_figi")
    public void setShareClassFigi(String shareClassFigi) {
        this.shareClassFigi = shareClassFigi;
    }

    @JsonProperty("market_cap")
    public Double getMarketCap() {
        return marketCap;
    }

    @JsonProperty("market_cap")
    public void setMarketCap(Double marketCap) {
        this.marketCap = marketCap;
    }

    @JsonProperty("phone_number")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @JsonProperty("phone_number")
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @JsonProperty("address")
    public Address getAddress() {
        return address;
    }

    @JsonProperty("address")
    public void setAddress(Address address) {
        this.address = address;
    }

    @JsonProperty("description")
    public String getDescription() {
        return description;
    }

    @JsonProperty("description")
    public void setDescription(String description) {
        this.description = description;
    }

    @JsonProperty("sic_code")
    public String getSicCode() {
        return sicCode;
    }

    @JsonProperty("sic_code")
    public void setSicCode(String sicCode) {
        this.sicCode = sicCode;
    }

    @JsonProperty("sic_description")
    public String getSicDescription() {
        return sicDescription;
    }

    @JsonProperty("sic_description")
    public void setSicDescription(String sicDescription) {
        this.sicDescription = sicDescription;
    }

    @JsonProperty("ticker_root")
    public String getTickerRoot() {
        return tickerRoot;
    }

    @JsonProperty("ticker_root")
    public void setTickerRoot(String tickerRoot) {
        this.tickerRoot = tickerRoot;
    }

    @JsonProperty("homepage_url")
    public String getHomepageUrl() {
        return homepageUrl;
    }

    @JsonProperty("homepage_url")
    public void setHomepageUrl(String homepageUrl) {
        this.homepageUrl = homepageUrl;
    }

    @JsonProperty("total_employees")
    public Integer getTotalEmployees() {
        return totalEmployees;
    }

    @JsonProperty("total_employees")
    public void setTotalEmployees(Integer totalEmployees) {
        this.totalEmployees = totalEmployees;
    }

    @JsonProperty("list_date")
    public String getListDate() {
        return listDate;
    }

    @JsonProperty("list_date")
    public void setListDate(String listDate) {
        this.listDate = listDate;
    }

    @JsonProperty("branding")
    public Branding getBranding() {
        return branding;
    }

    @JsonProperty("branding")
    public void setBranding(Branding branding) {
        this.branding = branding;
    }

    @JsonProperty("share_class_shares_outstanding")
    public BigInteger getShareClassSharesOutstanding() {
        return shareClassSharesOutstanding;
    }

    @JsonProperty("share_class_shares_outstanding")
    public void setShareClassSharesOutstanding(BigInteger shareClassSharesOutstanding) {
        this.shareClassSharesOutstanding = shareClassSharesOutstanding;
    }

    @JsonProperty("weighted_shares_outstanding")
    public BigInteger getWeightedSharesOutstanding() {
        return weightedSharesOutstanding;
    }

    @JsonProperty("weighted_shares_outstanding")
    public void setWeightedSharesOutstanding(BigInteger weightedSharesOutstanding) {
        this.weightedSharesOutstanding = weightedSharesOutstanding;
    }

    @JsonProperty("round_lot")
    public Integer getRoundLot() {
        return roundLot;
    }

    @JsonProperty("round_lot")
    public void setRoundLot(Integer roundLot) {
        this.roundLot = roundLot;
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
