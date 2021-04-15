package uk.gov.ch.pscdiscrepanciesapi.models.rest;

public enum ObligedEntityTypes {
    CREDIT_INSTITUTION(1),
    FINANCIAL_INSTITUTION(2),
    AUDITOR_EXTERNAL_ACCOUNTANT_TAX_ADVISOR(3),
    NOTARY_INDEPENDENT_LEGAL_PROFESSIONAL(4),
    TRUST_COMPANY_SERVICE_PROVIDER(5),
    ESTATE_AGENT_INTERMEDIARY(6),
    ENTITY_TRADING_HIGH_VALUE_CASH_GOODS(7),
    GAMBLING_SERVICE_PROVIDER(8),
    CURRENCY_EXCHANGE_SERVICE_PROVIDER(9),
    CUSTODIAN_WALLET_PROVIDER(10),
    ART_DEALER_GALLERY_AUCTION(11),
    ART_DEALER_FREE_PORTS(12),
    INSOLVENCY_PRACTITONER(13);

    private final int id;

    ObligedEntityTypes(int id) {
        this.id = id;
    }

    /**
     * Returns Obliged Entity Type with requested id if it exists.
     * @param id integer to match to the valid obliged entity types.
     * @return entity type with matching id, or null.
     */
    public static ObligedEntityTypes getEntityTypeFromId(Integer id){
        for (ObligedEntityTypes entity : ObligedEntityTypes.values()){
            if( id == entity.id){
                return entity;
            }
        }
        return null;
    }
}
