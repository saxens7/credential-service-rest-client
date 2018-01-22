[![License](https://img.shields.io/badge/License-EPL%201.0-red.svg)](https://opensource.org/licenses/EPL-1.0)
[![Build Status](https://travis-ci.org/dellemc-symphony/credential-service-rest-client.svg?branch=master)](https://travis-ci.org/dellemc-symphony/credential-service-rest-client)
[![Codecov](https://img.shields.io/codecov/c/github/dellemc-symphony/credential-service-rest-client.svg)](https://codecov.io/gh/dellemc-symphony/credential-service-rest-client)
[![Slack](http://img.shields.io/badge/slack-join%20the%20chat-00B9FF.svg?style=flat-square)](https://codecommunity.slack.com/messages/symphony)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.dell.cpsd/credential-service-rest-client/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.dell.cpsd/credential-service-rest-client)
[![Semver](http://img.shields.io/SemVer/2.0.0.png)](http://semver.org/spec/v2.0.0.html)

# credential-service-rest-client

WORK IN PROGRESS

## Description
This repository contains a client that communicates with Credential Service REST API tp perform CRUD operation for secrets.

## Documentation
You can find additional documentation for Project Symphony at [dellemc-symphony.readthedocs.io](http://dellemcsymphony.readthedocs.io/en/latest/).

## Before you begin
Verify that the following tools are installed:
 
* Apache Maven 3.0.5+
* Java Development Kit (version 8)

## Building
Run the following command to build this project:
```bash
mvn clean install
```
## Consume Client
Add credential-service-rest-client-starter dependency in you project POM
Import com.dell.cpsd.credential.config.CredntialServiceClientConfig.class in your project configuration
Autowire CredentialServiceClient in your class to start consuming Credential Service REST API.


## Contributing 
Project Symphony is a collection of services and libraries housed at [GitHub][github].
 
Contribute code and make submissions at the relevant GitHub repository level. See [our documentation][contributing] for details on how to contribute.

## Community 
Reach out to us on the Slack [#symphony][slack] channel by requesting an invite at [{code}Community][codecommunity].
 
You can also join [Google Groups][googlegroups] and start a discussion.
 
[slack]: https://codecommunity.slack.com/messages/symphony
[googlegroups]: https://groups.google.com/forum/#!forum/dellemc-symphony
[codecommunity]: http://community.codedellemc.com/
[contributing]: http://dellemc-symphony.readthedocs.io/en/latest/contributingtosymphony.html
[github]: https://github.com/dellemc-symphony
[documentation]: https://dellemc-symphony.readthedocs.io/en/latest/
