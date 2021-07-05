package uk.gov.ch.pscdiscrepanciesapi.models.email;

import uk.gov.companieshouse.email_producer.model.EmailData;

public class ReportConfirmationEmailData extends EmailData {

    String referenceNumber;
    String companyNumber;
    String companyName;
    String pscName;
    String[] pscTypes;
    String pscMoreInformation;

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }

    public String getCompanyNumber() {
        return companyNumber;
    }

    public void setCompanyNumber(String companyNumber) {
        this.companyNumber = companyNumber;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getPscName() {
        return pscName;
    }

    public void setPscName(String pscName) {
        this.pscName = pscName;
    }

    public String[] getPscTypes() {
        return pscTypes;
    }

    public void setPscTypes(String[] pscTypes) {
        this.pscTypes = pscTypes;
    }

    public String getPscMoreInformation() {
        return pscMoreInformation;
    }

    public void setPscMoreInformation(String pscMoreInformation) {
        this.pscMoreInformation = pscMoreInformation;
    }
}
