package org.monarchinitiative.hpotextmining.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import org.monarchinitiative.hpotextmining.application.ScreensConfig;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * This class serves as a container for intermediate results of text-mining analysis.
 * Created by Daniel Danis on 6/19/17.
 */
public class DataBucket {

    /**
     * Set of results identified by text-mining analysis which will be presented to curator.
     */
    private Set<BiolarkResult> intermediateResults;

    /**
     * Set of <em>YES</em> terms approved by curator.
     */
    private Set<Term> approvedYesTerms;

    /**
     * Set of <em>NOT</em> terms approved by curator.
     */
    private Set<Term> approvedNotTerms;

    /**
     * Text from publication that is being analyzed.
     */
    private String minedText;

    /**
     * PubMed id of the publication being analyzed.
     */
    private String pmid;

    /**
     * Flag signalizing that the analysis has been aborted.
     */
    private boolean cancelled = false;


    public DataBucket() {
        intermediateResults = new HashSet<>();
        approvedNotTerms = new HashSet<>();
        approvedYesTerms = new HashSet<>();
    }

    /**
     * Submit JSON response from server. JSON will be decoded into a list of {@link BiolarkResult} objects.
     *
     * @param jsonResult JSON response from server.
     */
    public void setJsonResult(String jsonResult) {
        try {
            intermediateResults.addAll(decodePayload(jsonResult));
        } catch (IOException ioe) {
            ScreensConfig.Alerts.showExceptionDialog("Text-mining analysis", "Error",
                    "Error occured during parsing JSON result", ioe);
        }
    }

    /**
     * Get set of intermediate result objects.
     *
     * @return set of {@link BiolarkResult} objects.
     */
    public Set<BiolarkResult> getIntermediateResults() {
        return new HashSet<>(intermediateResults);
    }

    /**
     * Get the text that is being analyzed.
     *
     * @return String of analyzed text.
     */
    public String getMinedText() {
        return minedText;
    }

    /**
     * Set the text that is being analyzed.
     *
     * @param minedText String of analyzed text.
     */
    public void setMinedText(String minedText) {
        this.minedText = minedText;
    }

    /**
     * Get the PMID of the publication from which the mined text is coming from.
     *
     * @return String with PMID.
     */
    public String getPMID() {
        return pmid;
    }

    /**
     * Set the PMID of the publication from which the mined text is coming from.
     *
     * @param pmid String with PMID.
     */
    public void setPMID(String pmid) {
        this.pmid = pmid;
    }

    /**
     * Get set of <em>YES</em> terms approved by curator.
     *
     * @return Set of {@link Term} objects.
     */
    public Set<Term> getApprovedYesTerms() {
        return new HashSet<>(approvedYesTerms);
    }

    /**
     * Add set of <em>YES</em> terms that have been approved by curator.
     *
     * @param approvedYesTerms set of {@link Term}s to be added.
     */
    public void addApprovedYesTerms(Set<Term> approvedYesTerms) {
        this.approvedYesTerms.addAll(approvedYesTerms);
    }

    /**
     * Get set of <em>NOT</em> terms approved by curator.
     *
     * @return Set of {@link Term} objects.
     */
    public Set<Term> getApprovedNotTerms() {
        return new HashSet<>(approvedNotTerms);
    }

    /**
     * Add set of <em>NOT</em> terms that have been approved by curator.
     *
     * @param approvedNotTerms set of {@link Term}s to be added.
     */
    public void addApprovedNotTerms(Set<Term> approvedNotTerms) {
        this.approvedNotTerms.addAll(approvedNotTerms);
    }

    /**
     * Get status of the analysis.
     * @return true if the analysis should be terminated.
     */
    public boolean isCancelled() {
        return cancelled;
    }

    /**
     * Set status of text-mining analysis. The analysis will be cancelled if set to true.
     * @param cancelled set to true if you want to cancel the analysis.
     */
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    /**
     * Clear content of all data structures in this bucket.
     */
    public void clear() {
        this.approvedYesTerms.clear();
        this.approvedNotTerms.clear();
        this.minedText = null;
        this.pmid = null;
    }

    /**
     * Parse JSON string into set of intermediate result objects.
     * @param jsonResponse JSON string to be parsed.
     * @return set of {@link BiolarkResult} objects.
     * @throws IOException
     */
    private static Set<BiolarkResult> decodePayload(String jsonResponse) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        CollectionType javaType = mapper.getTypeFactory().constructCollectionType(Set.class, BiolarkResult.class);
        return mapper.readValue(jsonResponse, javaType);
    }
}
