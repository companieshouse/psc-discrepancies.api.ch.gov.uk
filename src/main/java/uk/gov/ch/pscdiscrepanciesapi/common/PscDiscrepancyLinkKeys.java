package uk.gov.ch.pscdiscrepanciesapi.common;

import uk.gov.companieshouse.service.links.LinkKey;

public enum PscDiscrepancyLinkKeys implements LinkKey {
    // Code analysis wants to use the constant value here
    //however that would be an illegal forward reference, and I cannot use the enum
    //as annotations need constants. Therefore both are needed and neither can be removed.

    PSC_DISCREPANCY_REPORT("psc_discrepancy_report");//NOSONAR
    public static final String REPORT_LINK = "psc_discrepancy_report";

    private String key;

    PscDiscrepancyLinkKeys(String key) {
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
