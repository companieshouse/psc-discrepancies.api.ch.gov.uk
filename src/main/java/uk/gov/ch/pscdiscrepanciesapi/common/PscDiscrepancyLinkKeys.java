package uk.gov.ch.pscdiscrepanciesapi.common;

import uk.gov.companieshouse.service.links.LinkKey;

public enum PscDiscrepancyLinkKeys implements LinkKey {
    PSC_DISCREPANCY_REPORT("psc-discrepancy-report");

    private String key;

    private PscDiscrepancyLinkKeys(String key) {
        this.key = key;
    }

    /*
     * (non-Javadoc)
     *
     * @see uk.gov.companieshouse.service.rest.LinkKey#key()
     */
    @Override
    public String key() {
        return this.key;
    }
}
