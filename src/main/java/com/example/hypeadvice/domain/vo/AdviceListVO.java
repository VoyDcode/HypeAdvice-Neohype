package com.example.hypeadvice.domain.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class AdviceListVO {

    @JsonProperty("total_results")
    private int totalResults;

    private String query;

    private List<Slip> slips;

    public int getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(int totalResults) {
        this.totalResults = totalResults;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<Slip> getSlips() {
        return slips;
    }

    public void setSlips(List<Slip> slips) {
        this.slips = slips;
    }
}
