package com.github.monarchinitiative.hpotextmining.core.miners.scigraph;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * This class represent a "token" from SciGraph server. A token has three properties, id (String), categories (array), and terms (array). Refer the following example:
 * {"token":{"id":"MP:0000751","categories":["Phenotype"],"terms":["myopathy"]},"start":50,"end":58}
 */
public class SciGraphToken {
    private String id; //Term ID
    private List<String> categories; //Only value is "Phenotype" because we only queried in this category (see configuration in AskSciGraphServer).
    private List<String> terms; //Term label, always one string

    @JsonCreator
    public SciGraphToken(@JsonProperty("id") String id,
                         @JsonProperty("categories") List<String> categories,
                         @JsonProperty("terms") List<String> terms) {
        this.id = id;
        this.categories = categories;
        this.terms = terms;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getCategories() {
        return categories;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }

    public List<String> getTerms() {
        return terms;
    }

    public void setTerms(List<String> terms) {
        this.terms = terms;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + id.hashCode();
        hash = 31 * hash + categories.hashCode();
        hash = 31 * hash + terms.hashCode();
        return hash;
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof SciGraphToken)) {
            return false;
        }

        SciGraphToken o = (SciGraphToken) other;
        return this.id.equals(o.id) && this.categories.equals(o.categories) && this.terms.equals(o.terms);
    }
}