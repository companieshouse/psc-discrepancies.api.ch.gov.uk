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

#### Configuration

The following are environment variables necessary to run the API:

##### chs-configs/(environment)/psc.discrepancies.api.ch.gov.uk/env:

* _PSC_DISCREPANCIES_API_APPLICATION_NAME_: Used in structured logging to display the application the log line belongs to
* _PSC_DISCREPANCIES_MONGODB_URL_: Points at the MongoDB instance for that environment
* _PSC_DISCREPANCIES_DATABASE_: Refers to the database that holds the collections for the application data
* _PSC_DISCREPANCY_REPORT_SUBMISSION_URI_: The link to the CHIPS REST interfaces submission for a PSC Discrepancy for the corresponding CHIPS environment

##### chs-configs/(environment)/global_env:

* _PSC_DISCREPANCIES_API_PORT_: Port number for where the API is deployed
* _PSC_DISCREPANCIES_API_CH_GOV_UK_URL_: Used to allow ERIC to route requests through the API

#### Vagrant

Installing and running this API in Vagrant requires an update of the `vagrant-development-v2` repository and then running `./clean.sh` and `./setup.sh`.

To start this API in Vagrant, use the following command: `ubic start psc.psc-discrepancy-api`

It is recommended to run this API in Vagrant rather than outside of Vagrant because of the necessary environment variables, which can be found in: `src/main/resources/application.properties`

#### Other Environments

The API is deployed via Concourse or by the release team.

## Usage

The usage of this API is primarily through accessing the web app `psc-discrepancies.web.ch.gov.uk`. At this time (May 12, 2020) there is no requirement to make this a public API.

#### POSTMAN

##### PSC Discrepancy Report

HTTP requests can be sent to the API via a REST client, e.g. Postman, with the following URL as the base URL: 

`http://api.chs-dev.internal:18553/psc-discrepancy-reports`

This URL can be used to create a new PSC Discrepancy Report via a __POST__ request
with a body of a PscDiscrepancyReport with only its `obliged_entity_contact_name`
field filled in, for example:
```json
{
  "obliged_entity_contact_name" : "John Smith"
}
```

A successful 201 Created response will hold the URL of the created report in
a Location header and a copy of the created report in its body. This created report
is important, as it contains the `etag` value necessary for any subsequent PUTs.

The location of the resource will take the form
`http://api.chs-dev.internal:18553/psc-discrepancy-reports/{report-id}` and can then be used to
update the report (via a __PUT__ request) with any combination the following fields:
```json
{
    "etag": "see note below",
    "obliged_entity_email": "jsmith@email.co.uk",
    "company_number": "00006400",
    "obliged_entity_telephone_number": "07788991122",
    "status": "COMPLETE"
}
```

__etag__ note that the etag is initialised in the first POST and changes with each
successful PUT. Thus the __current etag value__ in the system can be found in the
PscDiscrepancyReport or PscDiscrepancy in the body of the response to a POST or PUT.
You can also retrieve it by using a GET for that resource. You must use the current
etag value in any PUT, or you will get an error response. The use of etag in this
way is standard Companies House design and is meant to prevent races between
different clients on the same resource. This is unlikely to happen with just a
single web client for a given resource, but nevertheless this is CH best practice.

__GET__ requests can also be executed to retrieve individual reports

##### PSC Discrepancy

A report for a company may, within the model, have multiple discrepancies. The
current web design only allows for one discrepancy to be raised, but the design
of the API allows multiple discrepancies to exist within a report.

Given a PSC Discrepancy Report existing at URL that looks like:

`http://api.chs-dev.internal:18553/psc-discrepancy-reports/{report-id}`

... to create a PSC Discrepancy for that report, __POST__ the PscDiscrepancy to
the following URL: 

`http://api.chs-dev.internal:18553/psc-discrepancy-reports/{report-id}/discrepancies`

The body of the POST needs to contain the PscDiscrepancy JSON, but you should only
fill out the details field, for example:

```json
{
  "details": "Wrong birthday on John Smith. Should be 01/01/1991 instead of 01/01/1992"
}
```

A __GET__ request can also be done on the discrepancies URL shown above to list
all discrepancies recorded within a report.

Furthermore, a __GET__ request can be done on the following URL to retrieve an individual discrepancy: 

`http://api.chs-dev.internal:18553/psc-discrepancy-reports/{report-id}/discrepancies/{discrepancy-id}`

## Support

The support process for dealing with a bug in this API follows the same process as all other CHS services.
TODO: more here

## Design
The sequence diagram below shows a high-level overview of the interactions between the user, web service, API service and CHIPS.
![alt text](design/OverviewSequence.svg)

Note that some elements are missing from the diagram, to keep the diagram simple.

### An overview of how the different systems interact.
In order to keep the web service as stateless as possible, the REST service
 is used to store the growing model. Each webscreen contains one or two
 pieces of information that are added to the growing report; when that page's data
 is submitted to the web service, the web service in turn stores that pages information
 in the API.

In a different design, the growing report and
 its discrepancy data would be stored in a web-service specific session store. We
 rejected this as the session store is not really fit for purpose at the moment
 (see TODO).
 
The design of the system is simplified by storing the growing report in the API and
 having the web service add to it incrementally. When the web journey
 is finished and the report is complete, the web service updates the status of the
 report, using a PUT on the API to change the status to COMPLETE. This signals the
 API to validate the report as a whole and send it on to CHIPS.

### Model
TODO: one discrepancy at the moment.

### Data storage
The API Service, like most other Companies House services, stores its back-end
 data in MongoDB, and that is not shown here. Each successful POST or PUT causes
 data to be stored in MongoDB. The model used by the DB can be found in the Java
 package `uk.gov.ch.psciscrepanciesapi.models.entity`.
 
### Validation
1. JSON data submitted to the API is validated: invalid data is rejected, leaving
the stored model unchanged.
1. In the incremental design of growing the report with POSTs and PUTs, each POST
 and PUT validates the individual fields that are currently set there, leaving unset
 fields unvalidated.
1. Only when the status is changed to COMPLETE are the whole report and its child
 discrepancies validated. At this point, all mandatory fields in the report and any
 child discrepancies must be set and there must be at least at least one report.

### Whitelist copying of data into API
Data submitted to the API by POST and PUT is not blindly saved into the API.

Specifically: each API object consists of a mixture of:
* 'client-settable' fields that may be set by the API client (such as the
PscDiscrepancyReport's companyNumber)
* 'non-client-settable' fields that are only meant to be set by the API, such as
`kind`, `etag`, or `links`.
By copying the client-settable fields in and skipping the non-client-settable
fields, we prevent malicious or inadvertent alteration of those non-client-settable
fields.

TODOs:
etag notes
support notes

Links to other projects
https://companieshouse.atlassian.net/wiki/spaces/TC/pages/1625456764/5MLD+CHIPS+Design+Documentation+-+PscDiscrepancyService
https://github.com/companieshouse/psc-discrepancies.web.ch.gov.uk/
Tickets to check:
Better merging
Interceptors
Exception handler
Structured logging
POST with any fields
Spike: how do we stop an UPDATE from changing stuff that has already been sent? Should we?
Idempotency in CHIPS service
Design docs for pipeline
CHIPS REST Interfaces: Change back to plain text? Ask Les, Bruce.
Multiple submission issue on web


There is no notion of the flow of POST a report… iterative PUT… PUT PUT, POST a discrepancy, PUT with status=COMPLETE… no overview of the lifecycle of a report, in other words.

The support section could refer to this lifecycle with regard to what could go wrong in normal operation.

We probably need an overview of how all our services fit together somewhere. This project is as good a place as any.

Config - what about the config in chs-config? On a side-note, are there any comments that we could add to the existing config files in chs-config and the application.properties?

The path to application.properties is wrong.

The Usage/Postman section: good, but needs to talk about what happens with etag. Possibly refer out to a doc on etag. We need to be clear that etag must match what is in system.


![alt text](design/StatusCompleteAction.svg)

This API follows Companies House design standards, and the swagger specification can be found in this repository within the `spec` directory.
Built on the REST architecture, there are two controllers. The first has endpoints to allow the creation, update and retrieving of PSC discrepancy report(s). The second controller has endpoints to create and retrieve discrepancies for a report.

TODO More detail here.