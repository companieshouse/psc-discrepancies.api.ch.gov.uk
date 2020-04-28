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
### Vagrant

Installing and running this API in vagrant requires an update of the `vagrant-development-v2` repository and then running `./clean.sh` and `./setup.sh`.
### Other Environments

The API is deployed via Concourse or by the release team.

## Usage

The usage of this API is primarily through accessing the web app `psc-discrepancies.web.ch.gov.uk`. At this time there is no requirement to make this a public API.

## Support

The support process for dealing with a bug in this API follows the same process as all other CHS services.

## Design

This API follows Companies House design standards, and the swagger specification can be found in this repository within the `spec` directory.
Built on the REST architecture, there are two controllers. The first has endpoints to allow the creation, update and retrieving of PSC discrepancy report(s). The second controller has endpoints to create and retrieve discrepancies for a report.
