# psc-discrepancies.api.ch.gov.uk
This API is for servicing requests from the PSC Discrepancies web service and creating and updating a PSC discrepancy report and discrepancies in a MongoDB.

Upon completing a report, the client sends a status update of `COMPLETED`, which signals the API to then send the discrepancy report to CHIPS.

## Requirements

In order to build this service locally you need:

- [Java](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
- [Maven](https://maven.apache.org/download.cgi)
- [Git](https://git-scm.com/downloads)
- [MongoDB](https://www.mongodb.com)

### Getting Started

Run the following from the command line to download the repository and change into the directory:

```
git clone git@github.com:companieshouse/psc-discrepancies.api.ch.gov.uk.git

cd psc-discrepancies.api.ch.gov.uk
```

## Installation

Installing and running this API requires an update of the `vagrant-development-v2` repository and then running `./clean.sh` and `./setup.sh`. 

## Usage

The usage of this API is primarily done via accessing the web app. This API is currently not fit for purpose for internal staff, and requires a
someone who is obliged to report discrepancies (e.g. bank staff members) to fill and complete the form in the `psc-discrepancies.web.ch.gov.uk` 
service.

## Support

The support process for dealing with a bug in this API follows the same process as all other CHS services.

## Design

This API follows Companies House design standards, and the swagger specification can be found in this repository within the `spec` directory.
