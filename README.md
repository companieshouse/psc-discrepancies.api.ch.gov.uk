# psc-discrepancies.api.ch.gov.uk
This API is for servicing requests from the PSC Discrepancies web service and creating and updating a PSC discrepancy report and discrepancies in a MongoDB.

Upon completing a report, the client sends a status update of `COMPLETED`, which signals the API to then send the discrepancy report to CHIPS.

## Requirements

In order to build this service locally you need:

- [Java](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [Maven](https://maven.apache.org/download.cgi)
- [Git](https://git-scm.com/downloads)
- [MongoDB](https://www.mongodb.com)

## Installation

To download this repository, run the following from the command line and change into the directory:

```
git clone git@github.com:companieshouse/psc-discrepancies.api.ch.gov.uk.git

cd psc-discrepancies.api.ch.gov.uk
```
#### Vagrant

Installing and running this API in vagrant requires an update of the `vagrant-development-v2` repository and then running `./clean.sh` and `./setup.sh`.

To start this API in vagrant, use the following command: `ubic start psc.psc-discrepancy-api`

It is recommended to run this API in vagrant rather than outside of vagrant because of the necessary environment variables, which can be found in: `resources/application.properties`

#### Other Environments

The API is deployed via Concourse or by the release team.

## Usage

The usage of this API is primarily through accessing the web app `psc-discrepancies.web.ch.gov.uk`. At this time there is no requirement to make this a public API.

#### POSTMAN

##### PSC Discrepancy Report

HTTP requests can be sent to the API via the POSTMAN application with the following URL as the base URL: 

`http://api.chs-dev.internal:18553/psc-discrepancy-reports`

This URL can be used to create a new PSC Discrepancy Report via a __POST__ request and the following request body:
```json
{
  "obliged_entity_contact_name" : "John Smith"
}
```

`http://api.chs-dev.internal:18553/psc-discrepancy-reports/{report-id}` can then be used to update the report (via a __PUT__ request) with any combination the following fields:
```json
{
    "etag": "724a80e7235c29a4fa1c849bef36198b3c220561",
    "obliged_entity_email": "jsmith@email.co.uk",
    "company_number": "00006400",
    "obliged_entity_telephone_number": "07788991122",
    "status": "COMPLETE"
}
```

__GET__ requests can also be executed to retrieve individual reports

##### PSC Discrepancy

A record can be created for each discrepancy the obliged entity has found on a company's PSCs.

To create a PSC Discrepancy record, a __POST__ request can be executed on the following URL: 

`http://api.chs-dev.internal:18553/psc-discrepancy-reports/{report-id}/discrepancies`

With the following request body:

```json
{
  "details": "Wrong birthday on John Smith. Should be 01/01/1991 instead of 01/01/1992"
}
```

A __GET__ request can also be done on the above URL to list all discrepancies recorded within a report.

Furthermore, a __GET__ request can be done on the following URL to retrieve an individual report: 

`http://api.chs-dev.internal:18553/psc-discrepancy-reports/{report-id}/discrepancies/{discrepancy-id}`

## Support

The support process for dealing with a bug in this API follows the same process as all other CHS services.

## Design

This API follows Companies House design standards, and the swagger specification can be found in this repository within the `spec` directory.
Built on the REST architecture, there are two controllers. The first has endpoints to allow the creation, update and retrieving of PSC discrepancy report(s). The second controller has endpoints to create and retrieve discrepancies for a report.
