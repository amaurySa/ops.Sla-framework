# SLA Framework

[![License badge](https://img.shields.io/badge/license-Apache_2.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

The SLA Framework is an implementation of an SLA lifecycle manager system, compliant with WS-Agreement. This project is part of [FIWARE](http://www.fiware.org/).

The SLA Framework is a web, multi-platform application that allows to manage the whole lifecycle of service level agreements (from template creation to violation detection). It is a plugin-based decoupled component that can be adapted and exended to work on different platforms. It is an open source project, released under the Apache 2 license.

It is used in FIWARE, in conjunction with the [SLA Dashboard project](https://github.com/Atos-FiwareOps/sla-dashboard) to allow to offer certain quality of service over the provided services, establishing service level agreements for them and registering the violations of the terms thanks to the use of the FIWARE Monitoring Component. It can operate at host level, virtual machine level and service level.

For any feedbacks or bug reports, please use the the github issues tool.

## Table of contents

- [Overall description](#overall-description)
- [Features implemented](#features-implemented)
- [Installation](#installation)
    - [Requirements](#requirements)
    - [Installation](#installation)
        - [Download the project](#download-the-project)
        - [Creating the mysql database](#creating-the-mysql-database)
        - [Importing the code into eclipse](#importing-the-code-into-eclipse)
    - [Configuration](#configuration)
    - [Compiling](#compiling)
    - [Running](#running)
    - [Installation verification](#installation-verification)
    - [Testing](#testing)
- [API specification](#api-specification)
    - [REST API conventions](#rest-api-conventions)
        - [Providers](#group-providers)
            - [List all providers] (#list-all-providers-get)
            - [Create a new provider] (#create-a-new-provider-post)
            - [Get provider info] (#get-provider-info-get)
            - [Delete provider] (#delete-provider-delete)
        - [Templates](#group-templates)
            - [List all templates] (#list-all-templates-get)
            - [Create a new template] (#create-a-new-template-post)
            - [Get template info] (#get-template-info-get)
            - [Modify an existing template] (#modify-an-existing-template-put)
            - [Delete template] (#delete-template-delete)
        - [Agreements](#group-agreements)
            - [List all agreements] (#list-all-agreements-get)
            - [Create a new agreement] (#create-a-new-agreement-post)
            - [List all active agreements] (#list-all-active-agreements-get)
            - [Get agreements per template and consumer] (#get-agreements-per-template-and-consumer-get)
            - [Get agreement info] (#get-agreement-info-get)
            - [Delete agreement] (#delete-agreement-delete)
            - [Get agreement context] (#get-agreement-context-get)
            - [Get agreement guarantee terms status] (#get-agreement-guarantee-terms-status-get)
        - [Enforcement Jobs](#group-enforcement-jobs)
            - [Get enforcement jobs] (#get-enforcement-jobs-get)
            - [Get enforcement job by agreement] (#get-enforcement-job-by-agreement-get)
            - [Start enforcement job for agreement] (#start-enforcement-job-for-agreement-put)
            - [Stop enforcement job for agreement] (#stop-enforcement-job-for-agreement-put)
        - [Violations](#group-violations)
            - [Get violations] (#get-violations-get)
            - [Get violation info] (#get-violation-info-get)
        - [Penalties](#group-penalties)
            - [Get penalties] (#get-penalties-get)
            - [Get penalty info] (#get-penalty-info-get)
- [Advanced topics](#advanced-topics)
- [License](#license)


## Overall description

The SLA Manager core provides mechanisms to support service level agreements
management in individual and federated environments, based on
[WS-Agreement specification](http://ogf.org/documents/GFD.192.pdf). 

The platform only pupports one-shot negotiation at the moment. The capabilities 
provided currently by the Atos SLA Manager core module are:

+ A language and a protocol to define and advertise the capabilities of service providers in SLA Template forms.
+ Creation agreements based on the templates.
+ Monitoring agreement compliance at runtime. An agreement between a service consumer and a service provider specifies one or more Service Level Objectives (SLOs). These are the expressions of the requirements of the service consumer and of the assurances by the service provider about the quality of services. An agreement lifecycle includes the creation, monitoring and termination of the agreement.

The WS-Agreement specification describes an XML schema for specifying service level 
agreements (both applicable to SLA Templates, Agreement Offers and Agreements). SLA 
Templates, Agreement Offers and Agreements are defined and described using the 
[WS-Agreement schema](http://schemas.ggf.org/graap/2007/03/ws-agreement).

##Features implemented

In this version (v1.1) the support for a language to express temporal
monitoring restrictions. Thus, a service provider is able to define
conditions like, for example, an availability of 99% calculated over 24 hours.

That means that, although the monitoring data informs about breaches of some
KPI conditions, violations only will be raised when the mean of these measurements
over the specified interval exceeds the defined threshold.

This is possible thanks to the implementation of this feature in the Fiware
monitoring component. The last version allows to query the monitoring data
providing the interval we are interested in.

The SLA Manager supports this fature by adding a new data structure in the XML
representation of the SLA Template/Agreement. For example, if a service provider
wants to express that an availability of 99% of his service, calculated over 6
hours, the resulting template will include this in the ServiceLevelObjective
section in the form of a CustomServiceLevel:

    <wsag:Template xmlns:wsag="http://www.ggf.org/namespaces/ws-agreement" xmlns:sla="http://sla.atos.eu" wsag:TemplateId="11c3e6d7-550c-4882-a419-09ece0c71d15">
        <wsag:Name>Template2</wsag:Name>
        <wsag:Context>
            <wsag:AgreementInitiator>Provider2</wsag:AgreementInitiator>
            <wsag:ServiceProvider>AgreementInitiator</wsag:ServiceProvider>
            <wsag:ExpirationTime>0037-01-04T15:08:15CET</wsag:ExpirationTime>
            <sla:Service>Service2</sla:Service>
        </wsag:Context>
        <wsag:Terms>
            <wsag:All>
                <wsag:ServiceDescriptionTerm />
                <wsag:ServiceProperties wsag:ServiceName="Service2">
                    <wsag:VariableSet>
                        <wsag:Variable wsag:Name="sysUptime" />
                    </wsag:VariableSet>
                </wsag:ServiceProperties>
                <wsag:GuaranteeTerm wsag:Name="sysUptime">
                    <wsag:ServiceScope wsag:ServiceName="Service2" />
                    <wsag:ServiceLevelObjective>
                        <wsag:KPITarget>
                            <wsag:KPIName>sysUptime</wsag:KPIName>
                            <wsag:CustomServiceLevel>
                                {"policy": "(1 breach, 6 hours)", "constraint": "sysUptime LT 99"}
                            </wsag:CustomServiceLevel>
                        </wsag:KPITarget>
                    </wsag:ServiceLevelObjective>
                </wsag:GuaranteeTerm>
            </wsag:All>
        </wsag:Terms>
    </wsag:Template>
    
## Installation

### Requirements

The requirements to install a working copy of the sla core are:

* Oracle JDK >=1.6
* Database to install the database schema for the service: Mysql>=5.0
* Maven >= 3.0

### Installation

All commands shown here are ready to be executed from the 
root directory of the project (i.e., the one with the 
_configuration.properties_ file) 

#### Download the project

Clone the project using git from the
[sla core repository](https://github.com/Atos-FiwareOps/sla-framework.git)

	$ git clone https://github.com/Atos-FiwareOps/sla-framework.git

It is recommended to checkout the latest released version 
if developing for a specific project. So, if 0.1.0 version wants to be checked out:

	$ git checkout tags/0.1.0

#### Creating the mysql database

From mysql command tool, create a database (with a user with sufficient 
privileges, as root):

	$ mysql -p -u root 
	
	mysql> CREATE DATABASE atossla;

Create a user:

	mysql> CREATE USER atossla@localhost IDENTIFIED BY '_atossla_';
	mysql> GRANT ALL PRIVILEGES ON atossla.* TO atossla@localhost; -- * optional WITH GRANT OPTION;

The SLA Core webapp will create all the needed tables when loaded by first time.

The names used here are the default values of the sla core. See 
[configuration](#configuration) to know how to change the values.

#### Importing the code into eclipse

The core of the Atos SLS Manager has been developed using the Eclipse Java IDE, 
although others Java editors could be used, here we only provide the 
instructions about how to import the code into Eclipse.

The first step is to tell Maven to create the necessary Eclipse project 
files executing this:

	$ mvn eclipse:eclipse

The previous command is going to generate the eclipse project files: 
.settings, .classpath, and .project. Again, please never upload those 
files to the svn, it is going to deconfigure the eclipse of other 
developers (it is easy to fix, just an annoying waste of time).

After it, from your eclipse you can import the project. Go to 
"import project from file", go to the trunk folder, and you should 
see the "ATOSSLA" project ready to be imported in your Eclipse. 

### Configuration

The project is made up of five main modules:

- SLA Repository
- SLA Enforcement
- SLA Service
- SLA Tools
- SLA Personalization

A _configuration.properties.sample_ that is placed in the parent directory 
has to be copied to *configuration.properties*.

Several parameters can be configured through this file.

1. tomcat.directory when building, war will be automatically copied to this directory,
1. db.\* allows to configure the database username, password and name in case it has been changed from the proposed 
   one in the section [Creating the mysql database](#database). It can be selected if queries from hibernate must be 
   shown or not. These parameters can be overriden at deployment time through the use of environment variables 
   (see section [Running](#running)),
1. log.\* allows to configure the log files to be generated 
   and the level of information,
1. enforcement.\* several parameters from the enforcement can be customized,
1. service.basicsecurity.\* basic security is enabled
   These parameters can be used to set the user name and password to access to the rest services.
1.   ''parser.*'' different parsers can be implemented for the agreement and template. By default, wsag standard parsers are have been implemented and configured in the file. Also dateformat can be configured.

Another way for creating in an automated manner the configurations properties 
file is to set some global variables and run the bin/autoconfigure.sh script.
It is a simple script that takes the values from the exported OS variables, 
substitutes the proper values in the configuration.properties.sample file and
creates the configuration.properties file with the declared values. This allows, 
for example, to automatically configure and deploy the SLA Manager Core in
continuous integration systems like Jenkins.

More concretely, the variables to be set are:

    sla_tomcat_directory
    sla_db_password
    sla_db_host
    sla_log_slaatos_fullpathFilename
    sla_log_thirdpartysw_fullpathFilename
    sla_service_basicsecurity_password
    sla_enforcement_constraintEvaluator_class
    sla_enforcement_metricsRetriever_class

As an example, some of the values we set to get the preview version of
the SLA Core in Fiware to be configured automatcally by Jenkins are:

    sla_tomcat_directory="target\/"
    sla_db_host=192.168.205.36
    sla_log_slaatos_fullpathFilename="\/var\/log\/tomcat7\/atosslafile.log"
    sla_log_thirdpartysw_fullpathFilename="\/var\/log\/tomcat7\/atosslafullfile.log"
    sla_enforcement_constraintEvaluator_class=eu.atos.xifi.sla.monitoring.OrionConstraintEvaluator
    sla_enforcement_metricsRetriever_class=eu.atos.xifi.sla.monitoring.OrionMetricsRetriever

If you're creating the database using the command _mvn test exec:java -f sla-repository/pom.xml_ please make sure that you configure properly sla-repository\src\main\resources\META-INF\persistence.xml. Make sure you're setting the username, password and connection url with the proper parameters.

	<property name="hibernate.connection.username" value="atossla" />
	<property name="hibernate.connection.password" value="_atossla_" />
	<property name="hibernate.connection.url" value="jdbc:mysql://localhost:3306/atossla" />
	

### Compiling
	
	$ mvn install
	
If you want to skip tests:
	
	$ mvn install -Dmaven.test.skip=true
	
The result of the command is a war in _sla-service/target_. The war is also copied to
the directory pointed by _tomcat.directory_ in the _configuration.properties_ file.

### Running

If the war was successfully copied to tomcat.directory, 
then start your tomcat to run the server.

Alternatively, you can run an embedded tomcat:

	$ bin/runserver.sh

that is just a shortcut for:

	$ mvn tomcat:run -f sla-service/pom.xml
	
	
Some configuration parameters can be overriden using environment variables or jdk variables. The list of
parameters overridable is:

* `DB_DRIVER`; default value is `com.mysql.jdbc.Driver`
* `DB_URL`; default value is `jdbc:mysql://${db.host}:${db.port}/${db.name}`
* `DB_USERNAME`; default value is `${db.username}`
* `DB_PASSWORD`; default value is `${db.password}`
* `DB_SHOWSQL`; default value is `${db.showSQL}`

F.e., to use a different database configuration:

	$ export DB_URL=jdbc:mysql://localhost:3306/sla
	$ export DB_USERNAME=sla
	$ export DB_PASSWORD=<secret>
	$ bin/runserver.sh 

### Installation verification

Check that everything is working performing the following HTTP call:

	$ curl http://localhost:8080/sla-service/providers

The actual address depends on the tomcat configuration. 
The embedded tomcat uses _http://localhost:8080/sla-service/_ as service root url. 

### Testing

All the unit tests are run automatically when the project compiles, unless you
skip them, as stated in the [Compiling](#compiling) section.

You can find the tests for every single project in the _src/java/test_ folders.

## API specification

Note: there is an [Apiary version](http://docs.slamanagercore.apiary.io)
of this page with a more readable and structured format, as well as a
mock server to perform some tests at.

### REST API conventions

The REST interface to the sla-core system has the following conventions:

* Every entity is created with a POST to the collection url. The body request contains the serialized entity in the 
  format specified in the content-type header. The location header of the response refers to the url of the new 
  allocated resource. The return code is a 201 on success. Templates and agreements have special considerations 
  (see the corresponding section). 
* A query for an individual item is a GET to the url of the resource (collection url + external id). The format of the 
  response is specified in the http header with the accept parameter. The return code is 200. As expected, a not found
  resource returns a 404.
* Any other query is usually a GET to the collection's url, using the GET parameters as the query parameters. The 
  result is a list of entities that match the parameters, despite the actual number of entities. The return code is 
  200, even if the list is empty.
* Any unexpected error processing the request returns a 5xx.
* An entity (or list) is serialized in the response body by default with the format specified in the Content-type 
  header (if specified). The request may have an Accept header, that will be used if the resource allows more than one 
  Content-type.
* Updating an entity involves a PUT request, with the corresponding resource serialized in the body in the format 
  specified in the content-type header. The return code is 200.
* If a query has begin and/or end parameters, the following search is done: `begin <= entity date < end`

### Group Providers

#### Povider Collection [/providers]

##### List all providers [GET]

This operations allows to get all the registered providers in the SLA Manager Core.

+ Request

    + Headers
    
            user: basic authentication user
            password: basic authentication password

+ Response 200 (application/xml)

    + Body

            <?xml version="1.0" encoding="UTF-8"?>
            <providers>
                <provider>
                    <uuid>9a9b83ff-cce2-43d6-9a2e-dea7c7b4e48b</uuid>
                    <name>Provider 1</name>
                </provider>
                <provider>
                    <uuid>f9d5b739-85ee-4ca1-939d-8c9d882f5da8</uuid>
                    <name>Provider 2</name>
                </provider>
            </providers>

+ Response 200 (application/json)

    + Body

            [
                {
                    "id":null,
                    "uuid":"9a9b83ff-cce2-43d6-9a2e-dea7c7b4e48b",
                    "name":"Provider 1"
                },
                {
                    "id":null,
                    "uuid":"f9d5b739-85ee-4ca1-939d-8c9d882f5da8",
                    "name":"Provider 2"
                }
            ]

##### Create a new provider [POST]

Allows to create a new provider. It takes a XML body containing
the name of the provider.

+ name (string) - The name of the provider

+ Request (application/xml)

    + headers
    
            user: basic authentication user
            password: basic authentication password

    + body
            
            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <provider>
                <name>Provider 3</name>
            </provider>

+ Response 201 (application/xml)

    + Body

            <?xml version="1.0" encoding="UTF-8" standalone="yes"?>
            <provider>
                <uuid>8145c0ba-f719-45ae-b293-baa942a4a156</uuid>
                <name>Provider 3</name>
            </provider>

+ Request (application/json)

    + headers
    
            user: basic authentication user
            password: basic authentication password

    + body
            
            {
                "name":"Provider 3"
            }

+ Response 201 (application/json)

    + Body

            {
                "uuid":"8145c0ba-f719-45ae-b293-baa942a4a156",
                "name":"Provider 3"
            }

+ Response 409

        Returned when the uuid or name already exists in the database.

#### Provider [/providers/{uuid}]

##### Get provider info [GET]

+ Parameters

    + uuid (string) - Id of the provider

+ Request

    + Headers
    
            user: basic authentication user
            password: basic authentication password

+ Response 200 (application/xml)

    + Body

            <?xml version="1.0" encoding="UTF-8"?>
            <provider>
                <uuid>9a9b83ff-cce2-43d6-9a2e-dea7c7b4e48b</uuid>
                <name>Provider 1</name>
            </provider>

+ Response 200 (application/json)

    + Body

            {
                "id":null,
                "uuid":"9a9b83ff-cce2-43d6-9a2e-dea7c7b4e48b",
                "name":"Provider 1"
            }

+ Response 404

        Returned when the object is not in the database.

##### Delete provider [DELETE]

+ Parameters

    + uuid (string) - Id of the provider

+ Request

    + Headers
    
            user: basic authentication user
            password: basic authentication password

+ Response 204
+ Response 404

        Returned when the given uuid doesn't exist in the database.

+ Response 409

        Returned when the provider code is being used.

### Group Templates

The TemplateId matches the TemplateId attribute of wsag:Template element when the template is created. A template is serialized in XML as defined by [WS-Agreement schema](http://schemas.ggf.org/graap/2007/03/ws-agreement).

#### Template Collection [/templates{?serviceIds}{?providerId}]

##### List all templates [GET]

This operations allows to get all the registered providers in the SLA Manager Core.


+ Parameters

    + serviceIds (optional, array[string]) - String with coma separated values with the id's of service that is associated to the template.
    + providerId (optional, string) - Id of the provider that is offering the template.

+ Request

    + Headers
    
            user: basic authentication user
            password: basic authentication password

+ Response 200 (application/xml)

    + Body

            <?xml version="1.0" encoding="UTF-8"?>
            <templates>
                <wsag:Template xmlns:wsag="http://www.ggf.org/namespaces/ws-agreement" xmlns:sla="http://sla.atos.eu" wsag:TemplateId="c3bcd8fb-e632-4aa6-a4d6-b0979f9da116">
                    <wsag:Name>Template1</wsag:Name>
                    <wsag:Context>
                        <wsag:AgreementInitiator>Provider1</wsag:AgreementInitiator>
                        <wsag:ServiceProvider>AgreementInitiator</wsag:ServiceProvider>
                        <wsag:ExpirationTime>0037-01-04T15:08:15CET</wsag:ExpirationTime>
                        <sla:Service>Service1</sla:Service>
                    </wsag:Context>
                    <wsag:Terms>
                        <wsag:All>
                            <wsag:ServiceDescriptionTerm />
                            <wsag:ServiceProperties wsag:ServiceName="Service1">
                                <wsag:VariableSet>
                                    <wsag:Variable wsag:Name="percDiskUsed" />
                                </wsag:VariableSet>
                            </wsag:ServiceProperties>
                            <wsag:GuaranteeTerm wsag:Name="percDiskUsed">
                                <wsag:ServiceScope wsag:ServiceName="Service1" />
                                <wsag:ServiceLevelObjective>
                                    <wsag:KPITarget>
                                        <wsag:KPIName>percDiskUsed</wsag:KPIName>
                                        <wsag:CustomServiceLevel>{"constraint": "percDiskUsed GT 50"}</wsag:CustomServiceLevel>
                                    </wsag:KPITarget>
                                </wsag:ServiceLevelObjective>
                            </wsag:GuaranteeTerm>
                        </wsag:All>
                    </wsag:Terms>
                </wsag:Template>
                <wsag:Template xmlns:wsag="http://www.ggf.org/namespaces/ws-agreement" xmlns:sla="http://sla.atos.eu" wsag:TemplateId="11c3e6d7-550c-4882-a419-09ece0c71d15">
                    <wsag:Name>Template2</wsag:Name>
                    <wsag:Context>
                        <wsag:AgreementInitiator>Provider2</wsag:AgreementInitiator>
                        <wsag:ServiceProvider>AgreementInitiator</wsag:ServiceProvider>
                        <wsag:ExpirationTime>0037-01-04T15:08:15CET</wsag:ExpirationTime>
                        <sla:Service>Service2</sla:Service>
                    </wsag:Context>
                    <wsag:Terms>
                        <wsag:All>
                            <wsag:ServiceDescriptionTerm />
                            <wsag:ServiceProperties wsag:ServiceName="Service2">
                                <wsag:VariableSet>
                                    <wsag:Variable wsag:Name="sysUptime" />
                                </wsag:VariableSet>
                            </wsag:ServiceProperties>
                            <wsag:GuaranteeTerm wsag:Name="sysUptime">
                                <wsag:ServiceScope wsag:ServiceName="Service2" />
                                <wsag:ServiceLevelObjective>
                                    <wsag:KPITarget>
                                        <wsag:KPIName>sysUptime</wsag:KPIName>
                                        <wsag:CustomServiceLevel>{"policy": "(1 breach, 12 hours)", "constraint": "sysUptime LT 90"}</wsag:CustomServiceLevel>
                                    </wsag:KPITarget>
                                </wsag:ServiceLevelObjective>
                            </wsag:GuaranteeTerm>
                        </wsag:All>
                    </wsag:Terms>
                </wsag:Template>
            </templates>
            
+ Response 200 (application/json)

    + Body

            [
                {
                    "templateId":"c3bcd8fb-e632-4aa6-a4d6-b0979f9da116",
                    "context":{
                        "agreementInitiator":"Provider1",
                        "agreementResponder":null,
                        "serviceProvider":"AgreementInitiator",
                        "expirationTime":"0037-01-04T15:08:15CET",
                        "templateId":null,
                        "service":"Service1"
                    },
                    "terms":{
                        "allTerms":{
                            "serviceDescriptionTerm":{
                                "name":null,
                                "serviceName":null
                            },
                            "serviceProperties":[
                                {
                                    "name":null,
                                    "serviceName":"Service1",
                                    "variableSet":{
                                        "variables":[
                                            {
                                                "name":"percDiskUsed",
                                                "metric":null,
                                                "location":null
                                            }
                                        ]
                                    }
                                }
                            ],
                            "guaranteeTerms":[
                                {
                                    "name":"percDiskUsed",
                                    "serviceScope":{
                                        "serviceName":"Service1",
                                        "value":""
                                    },
                                    "qualifyingCondition":null,
                                    "businessValueList":null,
                                    "serviceLevelObjetive":{
                                        "kpitarget":{
                                            "kpiName":"percDiskUsed",
                                            "customServiceLevel":"{\"constraint\": \"percDiskUsed GT 50\"}"
                                        }
                                    }
                                }
                            ]
                        }
                    },
                    "name":"Template1"
                },
                {
                    "templateId":"11c3e6d7-550c-4882-a419-09ece0c71d15",
                    "context":{
                        "agreementInitiator":"Provider2",
                        "agreementResponder":null,
                        "serviceProvider":"AgreementInitiator",
                        "expirationTime":"0037-01-04T15:08:15CET",
                        "templateId":null,
                        "service":"Service2"
                    },
                    "terms":{
                        "allTerms":{
                            "serviceDescriptionTerm":{
                                "name":null,
                                "serviceName":null
                            },
                            "serviceProperties":[
                                {
                                    "name":null,
                                    "serviceName":"Service2",
                                    "variableSet":{
                                        "variables":[
                                            {
                                                "name":"sysUptime",
                                                "metric":null,
                                                "location":null
                                            }
                                        ]
                                    }
                                }
                            ],
                            "guaranteeTerms":[
                                {
                                    "name":"sysUptime",
                                    "serviceScope":{
                                        "serviceName":"Service2",
                                        "value":""
                                    },
                                    "qualifyingCondition":null,
                                    "businessValueList":null,
                                    "serviceLevelObjetive":{
                                        "kpitarget":{
                                            "kpiName":"sysUptime",
                                            "customServiceLevel":"{\"policy\": \"(1 breach, 12 hours)\", \"constraint\": \"sysUptime LT 90\"}"
                                        }
                                    }
                                }
                            ]
                        }
                    },
                    "name":"Template2"
                }
            ]

##### Create a new template [POST]

Allows to create a new template. It takes a XML body the description of the
template, following the [WS-Agreement schema](http://schemas.ggf.org/graap/2007/03/ws-agreement).
It might include a TemplateId or not. In case of not beeing included, a uuid will be assigned.

+ TemplateId (optional, string) - The id of the template.
+ Name (string) - A human readable name for the agreement.
+ Context (object)
    + AgreementInitiator (string) - The name of the agreement initiator (in our case, an existing provider)
    + AgreementResponder (optional, string) - The name of the responder.
    + ServiceProvider (string) - The name of the service provider (an existing provider)
    + Service (string) - The service ID the agreement will monitor.
    + ExpirationTime (string) - The expiration date of the template (YYYY-MM-DDThh:mm:ss+hhmm format)
+ Terms (object) - A set of terms.
    + ServiceDescriptionTerm (object)
        + Name (string) - The name of the service description term
        + ServiceName (string) - The service ID the services are based on.
        + Value (string) - Human readable description of the service description term.
    + ServiceProperties (array[VariableSet]) - A list of variable sets.
        + Name (string) - Name of the properties.
        + ServiceName (string) - Name of the service where the properties apply.
        + VariableSet (array[Variable]) - A list of variables.
            + Variable (object)
                + Name (string) - The name of the variable.
    + GuaranteeTerm (array[ServiceLevelObjetive]) - A list of ServiceScope and ServiceLevelObjective.
        + Name (string) - Descriptive name of the Guarantee Term.
        + ServiceScope (object)
            + ServiceName (string) - name of the service.
            + Value (string) - Id of the scope of the service.
        + ServiceLevelObjetive (object)
            + KPITarget (object)
                + KPIName (string) - Name of the KPI to be monitored.
                + CustomServiceLevel (string) - Custom values to be fulfilled by the monitored KPI.

+ Request (application/xml)

    + headers
    
            user: basic authentication user
            password: basic authentication password

    + body
            
            <wsag:Template xmlns:wsag="http://www.ggf.org/namespaces/ws-agreement" xmlns:sla="http://sla.atos.eu">
                <wsag:Name>Template1</wsag:Name>
                <wsag:Context>
                    <wsag:AgreementInitiator>Provider1</wsag:AgreementInitiator>
                    <wsag:ServiceProvider>AgreementInitiator</wsag:ServiceProvider>
                    <wsag:ExpirationTime>0037-01-04T15:08:15CET</wsag:ExpirationTime>
                    <sla:Service>Service1</sla:Service>
                </wsag:Context>
                <wsag:Terms>
                    <wsag:All>
                        <wsag:ServiceDescriptionTerm />
                        <wsag:ServiceProperties wsag:ServiceName="Service1">
                            <wsag:VariableSet>
                                <wsag:Variable wsag:Name="percDiskUsed" />
                            </wsag:VariableSet>
                        </wsag:ServiceProperties>
                        <wsag:GuaranteeTerm wsag:Name="percDiskUsed">
                            <wsag:ServiceScope wsag:ServiceName="Service1" />
                            <wsag:ServiceLevelObjective>
                                <wsag:KPITarget>
                                    <wsag:KPIName>percDiskUsed</wsag:KPIName>
                                    <wsag:CustomServiceLevel>{"constraint": "percDiskUsed GT 50"}</wsag:CustomServiceLevel>
                                </wsag:KPITarget>
                            </wsag:ServiceLevelObjective>
                        </wsag:GuaranteeTerm>
                    </wsag:All>
                </wsag:Terms>
            </wsag:Template>

+ Response 201 (application/xml)

    + Body

            <wsag:Template xmlns:wsag="http://www.ggf.org/namespaces/ws-agreement" xmlns:sla="http://sla.atos.eu" wsag:TemplateId="c3bcd8fb-e632-4aa6-a4d6-b0979f9da116">
                <wsag:Name>Template1</wsag:Name>
                <wsag:Context>
                    <wsag:AgreementInitiator>Provider1</wsag:AgreementInitiator>
                    <wsag:ServiceProvider>AgreementInitiator</wsag:ServiceProvider>
                    <wsag:ExpirationTime>0037-01-04T15:08:15CET</wsag:ExpirationTime>
                    <sla:Service>Service1</sla:Service>
                </wsag:Context>
                <wsag:Terms>
                    <wsag:All>
                        <wsag:ServiceDescriptionTerm />
                        <wsag:ServiceProperties wsag:ServiceName="Service1">
                            <wsag:VariableSet>
                                <wsag:Variable wsag:Name="percDiskUsed" />
                            </wsag:VariableSet>
                        </wsag:ServiceProperties>
                        <wsag:GuaranteeTerm wsag:Name="percDiskUsed">
                            <wsag:ServiceScope wsag:ServiceName="Service1" />
                            <wsag:ServiceLevelObjective>
                                <wsag:KPITarget>
                                    <wsag:KPIName>percDiskUsed</wsag:KPIName>
                                    <wsag:CustomServiceLevel>{"constraint": "percDiskUsed GT 50"}</wsag:CustomServiceLevel>
                                </wsag:KPITarget>
                            </wsag:ServiceLevelObjective>
                        </wsag:GuaranteeTerm>
                    </wsag:All>
                </wsag:Terms>
            </wsag:Template>

+ Request (application/json)

    + headers
    
            user: basic authentication user
            password: basic authentication password

    + body
            
            {
                "context":{
                    "agreementInitiator":"Provider1",
                    "agreementResponder":null,
                    "serviceProvider":"AgreementInitiator",
                    "expirationTime":"0037-01-04T15:08:15CET",
                    "templateId":null,
                    "service":"Service1"
                },
                "terms":{
                    "allTerms":{
                        "serviceDescriptionTerm":{
                            "name":null,
                            "serviceName":null
                        },
                        "serviceProperties":[
                            {
                                "name":null,
                                "serviceName":"Service1",
                                "variableSet":{
                                    "variables":[
                                        {
                                            "name":"percDiskUsed",
                                            "metric":null,
                                            "location":null
                                        }
                                    ]
                                }
                            }
                        ],
                        "guaranteeTerms":[
                            {
                                "name":"percDiskUsed",
                                "serviceScope":{
                                    "serviceName":"Service1",
                                    "value":""
                                },
                                "qualifyingCondition":null,
                                "businessValueList":null,
                                "serviceLevelObjetive":{
                                    "kpitarget":{
                                        "kpiName":"percDiskUsed",
                                        "customServiceLevel":"{\"constraint\": \"percDiskUsed GT 50\"}"
                                    }
                                }
                            }
                        ]
                    }
                },
                "name":"Template1"
            }

+ Response 201 (application/json)

    + Body

            {
                "templateId":"c3bcd8fb-e632-4aa6-a4d6-b0979f9da116",
                "context":{
                    "agreementInitiator":"Provider1",
                    "agreementResponder":null,
                    "serviceProvider":"AgreementInitiator",
                    "expirationTime":"0037-01-04T15:08:15CET",
                    "templateId":null,
                    "service":"Service1"
                },
                "terms":{
                    "allTerms":{
                        "serviceDescriptionTerm":{
                            "name":null,
                            "serviceName":null
                        },
                        "serviceProperties":[
                            {
                                "name":null,
                                "serviceName":"Service1",
                                "variableSet":{
                                    "variables":[
                                        {
                                            "name":"percDiskUsed",
                                            "metric":null,
                                            "location":null
                                        }
                                    ]
                                }
                            }
                        ],
                        "guaranteeTerms":[
                            {
                                "name":"percDiskUsed",
                                "serviceScope":{
                                    "serviceName":"Service1",
                                    "value":""
                                },
                                "qualifyingCondition":null,
                                "businessValueList":null,
                                "serviceLevelObjetive":{
                                    "kpitarget":{
                                        "kpiName":"percDiskUsed",
                                        "customServiceLevel":"{\"constraint\": \"percDiskUsed GT 50\"}"
                                    }
                                }
                            }
                        ]
                    }
                },
                "name":"Template1"
            }


+ Response 409

        Returned when the uuid or name already exists in the database.
        Returned when the provider uuid specified in the template doesn't exist in the database.
        
+ Response 500

        Returned when incorrect data has been suplied.
        
#### Template [/templates/{TemplateId}]

##### Get template info [GET]

+ Parameters

    + TemplateId (string) - Id of the template

+ Request

    + Headers
    
            user: basic authentication user
            password: basic authentication password

+ Response 200 (application/xml)

    + Body

            <wsag:Template xmlns:wsag="http://www.ggf.org/namespaces/ws-agreement" xmlns:sla="http://sla.atos.eu" wsag:TemplateId="c3bcd8fb-e632-4aa6-a4d6-b0979f9da116">
                <wsag:Name>Template1</wsag:Name>
                <wsag:Context>
                    <wsag:AgreementInitiator>Provider1</wsag:AgreementInitiator>
                    <wsag:ServiceProvider>AgreementInitiator</wsag:ServiceProvider>
                    <wsag:ExpirationTime>0037-01-04T15:08:15CET</wsag:ExpirationTime>
                    <sla:Service>Service1</sla:Service>
                </wsag:Context>
                <wsag:Terms>
                    <wsag:All>
                        <wsag:ServiceDescriptionTerm />
                        <wsag:ServiceProperties wsag:ServiceName="Service1">
                            <wsag:VariableSet>
                                <wsag:Variable wsag:Name="percDiskUsed" />
                            </wsag:VariableSet>
                        </wsag:ServiceProperties>
                        <wsag:GuaranteeTerm wsag:Name="percDiskUsed">
                            <wsag:ServiceScope wsag:ServiceName="Service1" />
                            <wsag:ServiceLevelObjective>
                                <wsag:KPITarget>
                                    <wsag:KPIName>percDiskUsed</wsag:KPIName>
                                    <wsag:CustomServiceLevel>{"constraint": "percDiskUsed GT 50"}</wsag:CustomServiceLevel>
                                </wsag:KPITarget>
                            </wsag:ServiceLevelObjective>
                        </wsag:GuaranteeTerm>
                    </wsag:All>
                </wsag:Terms>
            </wsag:Template>

+ Response 200 (application/json)

    + Body

            {
                "templateId":"c3bcd8fb-e632-4aa6-a4d6-b0979f9da116",
                "context":{
                    "agreementInitiator":"Provider1",
                    "agreementResponder":null,
                    "serviceProvider":"AgreementInitiator",
                    "expirationTime":"0037-01-04T15:08:15CET",
                    "templateId":null,
                    "service":"Service1"
                },
                "terms":{
                    "allTerms":{
                        "serviceDescriptionTerm":{
                            "name":null,
                            "serviceName":null
                        },
                        "serviceProperties":[
                            {
                                "name":null,
                                "serviceName":"Service1",
                                "variableSet":{
                                    "variables":[
                                        {
                                            "name":"percDiskUsed",
                                            "metric":null,
                                            "location":null
                                        }
                                    ]
                                }
                            }
                        ],
                        "guaranteeTerms":[
                            {
                                "name":"percDiskUsed",
                                "serviceScope":{
                                    "serviceName":"Service1",
                                    "value":""
                                },
                                "qualifyingCondition":null,
                                "businessValueList":null,
                                "serviceLevelObjetive":{
                                    "kpitarget":{
                                        "kpiName":"percDiskUsed",
                                        "customServiceLevel":"{\"constraint\": \"percDiskUsed GT 50\"}"
                                    }
                                }
                            }
                        ]
                    }
                },
                "name":"Template1"
            }

+ Response 404

        Returned when the object is not in the database.

##### Modify an existing template [PUT]

Updates the template identified by TemplateId. The body might include a TemplateId or not. In case of including a TemplateId in the file, it must match with the one from the url.

+ Parameters

    + TemplateId (optional, string) - Id of the template.

+ Request (application/xml)

    + headers
    
            user: basic authentication user
            password: basic authentication password

    + body
            
            <wsag:Template xmlns:wsag="http://www.ggf.org/namespaces/ws-agreement" xmlns:sla="http://sla.atos.eu">
                <wsag:Name>Template13</wsag:Name>
                <wsag:Context>
                    <wsag:AgreementInitiator>Provider1</wsag:AgreementInitiator>
                    <wsag:ServiceProvider>AgreementInitiator</wsag:ServiceProvider>
                    <wsag:ExpirationTime>0037-01-04T15:08:15CET</wsag:ExpirationTime>
                    <sla:Service>Service1</sla:Service>
                </wsag:Context>
                <wsag:Terms>
                    <wsag:All>
                        <wsag:ServiceDescriptionTerm />
                        <wsag:ServiceProperties wsag:ServiceName="Service1">
                            <wsag:VariableSet>
                                <wsag:Variable wsag:Name="percDiskUsed" />
                            </wsag:VariableSet>
                        </wsag:ServiceProperties>
                        <wsag:GuaranteeTerm wsag:Name="percDiskUsed">
                            <wsag:ServiceScope wsag:ServiceName="Service1" />
                            <wsag:ServiceLevelObjective>
                                <wsag:KPITarget>
                                    <wsag:KPIName>percDiskUsed</wsag:KPIName>
                                    <wsag:CustomServiceLevel>{"constraint": "percDiskUsed GT 50"}</wsag:CustomServiceLevel>
                                </wsag:KPITarget>
                            </wsag:ServiceLevelObjective>
                        </wsag:GuaranteeTerm>
                    </wsag:All>
                </wsag:Terms>
            </wsag:Template>

+ Response 200 (application/xml)

    + Body

            <wsag:Template xmlns:wsag="http://www.ggf.org/namespaces/ws-agreement" xmlns:sla="http://sla.atos.eu" wsag:TemplateId="c3bcd8fb-e632-4aa6-a4d6-b0979f9da116">
                <wsag:Name>Template13</wsag:Name>
                <wsag:Context>
                    <wsag:AgreementInitiator>Provider1</wsag:AgreementInitiator>
                    <wsag:ServiceProvider>AgreementInitiator</wsag:ServiceProvider>
                    <wsag:ExpirationTime>0037-01-04T15:08:15CET</wsag:ExpirationTime>
                    <sla:Service>Service1</sla:Service>
                </wsag:Context>
                <wsag:Terms>
                    <wsag:All>
                        <wsag:ServiceDescriptionTerm />
                        <wsag:ServiceProperties wsag:ServiceName="Service1">
                            <wsag:VariableSet>
                                <wsag:Variable wsag:Name="percDiskUsed" />
                            </wsag:VariableSet>
                        </wsag:ServiceProperties>
                        <wsag:GuaranteeTerm wsag:Name="percDiskUsed">
                            <wsag:ServiceScope wsag:ServiceName="Service1" />
                            <wsag:ServiceLevelObjective>
                                <wsag:KPITarget>
                                    <wsag:KPIName>percDiskUsed</wsag:KPIName>
                                    <wsag:CustomServiceLevel>{"constraint": "percDiskUsed GT 50"}</wsag:CustomServiceLevel>
                                </wsag:KPITarget>
                            </wsag:ServiceLevelObjective>
                        </wsag:GuaranteeTerm>
                    </wsag:All>
                </wsag:Terms>
            </wsag:Template>

+ Request (application/json)

    + headers
    
            user: basic authentication user
            password: basic authentication password

    + body
            
            {
                "context":{
                    "agreementInitiator":"Provider1",
                    "agreementResponder":null,
                    "serviceProvider":"AgreementInitiator",
                    "expirationTime":"0037-01-04T15:08:15CET",
                    "templateId":null,
                    "service":"Service1"
                },
                "terms":{
                    "allTerms":{
                        "serviceDescriptionTerm":{
                            "name":null,
                            "serviceName":null
                        },
                        "serviceProperties":[
                            {
                                "name":null,
                                "serviceName":"Service1",
                                "variableSet":{
                                    "variables":[
                                        {
                                            "name":"percDiskUsed",
                                            "metric":null,
                                            "location":null
                                        }
                                    ]
                                }
                            }
                        ],
                        "guaranteeTerms":[
                            {
                                "name":"percDiskUsed",
                                "serviceScope":{
                                    "serviceName":"Service1",
                                    "value":""
                                },
                                "qualifyingCondition":null,
                                "businessValueList":null,
                                "serviceLevelObjetive":{
                                    "kpitarget":{
                                        "kpiName":"percDiskUsed",
                                        "customServiceLevel":"{\"constraint\": \"percDiskUsed GT 50\"}"
                                    }
                                }
                            }
                        ]
                    }
                },
                "name":"Template13"
            }

+ Response 200 (application/json)

    + Body

            {
                "templateId":"c3bcd8fb-e632-4aa6-a4d6-b0979f9da116",
                "context":{
                    "agreementInitiator":"Provider1",
                    "agreementResponder":null,
                    "serviceProvider":"AgreementInitiator",
                    "expirationTime":"0037-01-04T15:08:15CET",
                    "templateId":null,
                    "service":"Service1"
                },
                "terms":{
                    "allTerms":{
                        "serviceDescriptionTerm":{
                            "name":null,
                            "serviceName":null
                        },
                        "serviceProperties":[
                            {
                                "name":null,
                                "serviceName":"Service1",
                                "variableSet":{
                                    "variables":[
                                        {
                                            "name":"percDiskUsed",
                                            "metric":null,
                                            "location":null
                                        }
                                    ]
                                }
                            }
                        ],
                        "guaranteeTerms":[
                            {
                                "name":"percDiskUsed",
                                "serviceScope":{
                                    "serviceName":"Service1",
                                    "value":""
                                },
                                "qualifyingCondition":null,
                                "businessValueList":null,
                                "serviceLevelObjetive":{
                                    "kpitarget":{
                                        "kpiName":"percDiskUsed",
                                        "customServiceLevel":"{\"constraint\": \"percDiskUsed GT 50\"}"
                                    }
                                }
                            }
                        ]
                    }
                },
                "name":"Template13"
            }


+ Response 409

        Returned when the uuid from the url doesn't match with the one from the file or when the system has already an agreement associated .
        Returned when the template has agreements associated.
        Returned when the provider doesn't exist.
        
+ Response 500

        Returned when incorrect data has been suplied.

##### Delete template [DELETE]

+ Parameters

    + TemplateId (optional, string) - Id of the template.

+ Response 204
+ Response 404

        Returned when the given uuid doesn't exist in the database.

+ Response 409

        Returned when agreements are still associated to the template.
        
### Group Agreements

The AgreementId matches the AgreementId attribute of wsag:Agreement element when the agreement is created. An agreement is serialized in XML as defined by [WS-Agreement schema](http://schemas.ggf.org/graap/2007/03/ws-agreement).

#### Agreement Collection [/agreements{?consumerId}{?providerId}{?templateId}{?active}]

##### List all agreements [GET]

This operation allows to get all the registered agreements in the SLA Manager Core.

+ Parameters

    + consumerId (optional, string) - uuid of the consumer
    + providerId (optional, string) - uuid of the provider
    + templateId (optional, string) - uuid of the template the agreement is based on
    + active (optional, boolean) - If true, agreements currently enforced are returned

+ Request

    + Headers
    
            user: basic authentication user
            password: basic authentication password

+ Response 200 (application/xml)

    + Body

            <?xml version="1.0" encoding="UTF-8"?>
            <agreements>
                <wsag:Agreement xmlns:wsag="http://www.ggf.org/namespaces/ws-agreement" xmlns:sla="http://sla.atos.eu" wsag:AgreementId="87726a36-e570-4482-b3cc-1cb3d8998bb8">
                    <wsag:Name>Agreement1</wsag:Name>
                    <wsag:Context>
                        <wsag:AgreementInitiator>user1@serviceuser.com</wsag:AgreementInitiator>
                        <wsag:AgreementResponder>Provider1</wsag:AgreementResponder>
                        <wsag:ServiceProvider>AgreementResponder</wsag:ServiceProvider>
                        <wsag:ExpirationTime>0024-03-07T11:08:24CET</wsag:ExpirationTime>
                        <wsag:TemplateId>e7efbfc1-3039-44c1-82d5-3d0e04cf2368</wsag:TemplateId>
                        <sla:Service>Service1</sla:Service>
                    </wsag:Context>
                    <wsag:Terms>
                        <wsag:All>
                            <wsag:ServiceDescriptionTerm />
                            <wsag:ServiceProperties wsag:Name="ServiceProperties" wsag:ServiceName="Service1">
                                <wsag:VariableSet>
                                    <wsag:Variable wsag:Name="percCPULoad" />
                                    <wsag:Variable wsag:Name="percDiskUsed" />
                                    <wsag:Variable wsag:Name="percRAMUsed" />
                                </wsag:VariableSet>
                            </wsag:ServiceProperties>
                            <wsag:GuaranteeTerm wsag:Name="percCPULoad">
                                <wsag:ServiceScope wsag:ServiceName="Service1">Service1</wsag:ServiceScope>
                                <wsag:ServiceLevelObjective>
                                    <wsag:KPITarget>
                                        <wsag:KPIName>percCPULoad</wsag:KPIName>
                                        <wsag:CustomServiceLevel>{"constraint": "percCPULoad LT 90"}</wsag:CustomServiceLevel>
                                    </wsag:KPITarget>
                                </wsag:ServiceLevelObjective>
                            </wsag:GuaranteeTerm>
                            <wsag:GuaranteeTerm wsag:Name="percDiskUsed">
                                <wsag:ServiceScope wsag:ServiceName="Service1">Service1</wsag:ServiceScope>
                                <wsag:ServiceLevelObjective>
                                    <wsag:KPITarget>
                                        <wsag:KPIName>percDiskUsed</wsag:KPIName>
                                        <wsag:CustomServiceLevel>{"constraint": "percDiskUsed LT 80"}</wsag:CustomServiceLevel>
                                    </wsag:KPITarget>
                                </wsag:ServiceLevelObjective>
                            </wsag:GuaranteeTerm>
                            <wsag:GuaranteeTerm wsag:Name="percRAMUsed">
                                <wsag:ServiceScope wsag:ServiceName="Service1">Service1</wsag:ServiceScope>
                                <wsag:ServiceLevelObjective>
                                    <wsag:KPITarget>
                                        <wsag:KPIName>percRAMUsed</wsag:KPIName>
                                        <wsag:CustomServiceLevel>{"constraint": "percRAMUsed LT 80"}</wsag:CustomServiceLevel>
                                    </wsag:KPITarget>
                                </wsag:ServiceLevelObjective>
                            </wsag:GuaranteeTerm>
                        </wsag:All>
                    </wsag:Terms>
                </wsag:Agreement>
                <wsag:Agreement xmlns:wsag="http://www.ggf.org/namespaces/ws-agreement" xmlns:sla="http://sla.atos.eu" wsag:AgreementId="51a3ec44-f755-4ee8-824b-0ce1001e990a">
                    <wsag:Name>WirecloudAgreement70_80_60</wsag:Name>
                    <wsag:Context>
                        <wsag:AgreementInitiator>user2@serviceuser.com</wsag:AgreementInitiator>
                        <wsag:AgreementResponder>provider2</wsag:AgreementResponder>
                        <wsag:ServiceProvider>AgreementResponder</wsag:ServiceProvider>
                        <wsag:ExpirationTime>0024-03-07T11:08:24CET</wsag:ExpirationTime>
                        <wsag:TemplateId>e7efbfc1-3039-44c1-82d5-3d0e04cf2368</wsag:TemplateId>
                        <sla:Service>Service2</sla:Service>
                    </wsag:Context>
                    <wsag:Terms>
                        <wsag:All>
                            <wsag:ServiceDescriptionTerm />
                            <wsag:ServiceProperties wsag:Name="ServiceProperties" wsag:ServiceName="Service2">
                                <wsag:VariableSet>
                                    <wsag:Variable wsag:Name="percCPULoad" />
                                    <wsag:Variable wsag:Name="percDiskUsed" />
                                    <wsag:Variable wsag:Name="percRAMUsed" />
                                </wsag:VariableSet>
                            </wsag:ServiceProperties>
                            <wsag:GuaranteeTerm wsag:Name="percCPULoad">
                                <wsag:ServiceScope wsag:ServiceName="Service2">Service2</wsag:ServiceScope>
                                <wsag:ServiceLevelObjective>
                                    <wsag:KPITarget>
                                        <wsag:KPIName>percCPULoad</wsag:KPIName>
                                        <wsag:CustomServiceLevel>{"constraint": "percCPULoad LT 70"}</wsag:CustomServiceLevel>
                                    </wsag:KPITarget>
                                </wsag:ServiceLevelObjective>
                            </wsag:GuaranteeTerm>
                            <wsag:GuaranteeTerm wsag:Name="percDiskUsed">
                                <wsag:ServiceScope wsag:ServiceName="Service2">Service2</wsag:ServiceScope>
                                <wsag:ServiceLevelObjective>
                                    <wsag:KPITarget>
                                        <wsag:KPIName>percDiskUsed</wsag:KPIName>
                                        <wsag:CustomServiceLevel>{"constraint": "percDiskUsed LT 80"}</wsag:CustomServiceLevel>
                                    </wsag:KPITarget>
                                </wsag:ServiceLevelObjective>
                            </wsag:GuaranteeTerm>
                            <wsag:GuaranteeTerm wsag:Name="percRAMUsed">
                                <wsag:ServiceScope wsag:ServiceName="Service2">Service2</wsag:ServiceScope>
                                <wsag:ServiceLevelObjective>
                                    <wsag:KPITarget>
                                        <wsag:KPIName>percRAMUsed</wsag:KPIName>
                                        <wsag:CustomServiceLevel>{"constraint": "percRAMUsed LT 60"}</wsag:CustomServiceLevel>
                                    </wsag:KPITarget>
                                </wsag:ServiceLevelObjective>
                            </wsag:GuaranteeTerm>
                        </wsag:All>
                    </wsag:Terms>
                </wsag:Agreement>
            </agreements>
            
+ Response 200 (application/json)

    + Body

            [
                {
                    "agreementId":"87726a36-e570-4482-b3cc-1cb3d8998bb8",
                    "name":"Agreement1",
                    "context":{
                        "agreementInitiator":"user1@serviceuser.com",
                        "agreementResponder":"Provider1",
                        "serviceProvider":"AgreementResponder",
                        "expirationTime":"0024-03-07T11:08:24CET",
                        "templateId":"e7efbfc1-3039-44c1-82d5-3d0e04cf2368",
                        "service":"Service1"
                    },
                    "terms":{
                        "allTerms":{
                            "serviceDescriptionTerm":{
                                "name":null,
                                "serviceName":null
                            },
                            "serviceProperties":[
                                {
                                    "name":"ServiceProperties",
                                    "serviceName":"Service1",
                                    "variableSet":{
                                        "variables":[
                                            {
                                                "name":"percCPULoad",
                                                "metric":null,
                                                "location":null
                                            },
                                            {
                                                "name":"percDiskUsed",
                                                "metric":null,
                                                "location":null
                                            },
                                            {
                                                "name":"percRAMUsed",
                                                "metric":null,
                                                "location":null
                                            }
                                        ]
                                    }
                                }
                            ],
                            "guaranteeTerms":[
                                {
                                    "name":"percCPULoad",
                                    "serviceScope":{
                                        "serviceName":"Service1",
                                        "value":"Service1"
                                    },
                                    "qualifyingCondition":null,
                                    "businessValueList":null,
                                    "serviceLevelObjetive":{
                                        "kpitarget":{
                                            "kpiName":"percCPULoad",
                                            "customServiceLevel":"{\"constraint\": \"percCPULoad LT 90\"}"
                                        }
                                    }
                                },
                                {
                                    "name":"percDiskUsed",
                                    "serviceScope":{
                                        "serviceName":"Service1",
                                        "value":"Service1"
                                    },
                                    "qualifyingCondition":null,
                                    "businessValueList":null,
                                    "serviceLevelObjetive":{
                                        "kpitarget":{
                                            "kpiName":"percDiskUsed",
                                            "customServiceLevel":"{\"constraint\": \"percDiskUsed LT 80\"}"
                                        }
                                    }
                                },
                                {
                                    "name":"percRAMUsed",
                                    "serviceScope":{
                                        "serviceName":"Service1",
                                        "value":"Service1"
                                    },
                                    "qualifyingCondition":null,
                                    "businessValueList":null,
                                    "serviceLevelObjetive":{
                                        "kpitarget":{
                                            "kpiName":"percRAMUsed",
                                            "customServiceLevel":"{\"constraint\": \"percRAMUsed LT 80\"}"
                                        }
                                    }
                                }
                            ]
                        }
                    }
                },
                {
                    "agreementId":"51a3ec44-f755-4ee8-824b-0ce1001e990a",
                    "name":"WirecloudAgreement70_80_60",
                    "context":{
                        "agreementInitiator":"user2@serviceuser.com",
                        "agreementResponder":"Provider2",
                        "serviceProvider":"AgreementResponder",
                        "expirationTime":"0026-05-08T11:09:58CET",
                        "templateId":"99bea98a-6df4-4ed7-94ef-1067962f130c",
                        "service":"Service2"
                    },
                    "terms":{
                        "allTerms":{
                            "serviceDescriptionTerm":{
                                "name":null,
                                "serviceName":null
                            },
                            "serviceProperties":[
                                {
                                    "name":"ServiceProperties",
                                    "serviceName":"Service2",
                                    "variableSet":{
                                        "variables":[
                                            {
                                                "name":"percCPULoad",
                                                "metric":null,
                                                "location":null
                                            },
                                            {
                                                "name":"percDiskUsed",
                                                "metric":null,
                                                "location":null
                                            },
                                            {
                                                "name":"percRAMUsed",
                                                "metric":null,
                                                "location":null
                                            }
                                        ]
                                    }
                                }
                            ],
                            "guaranteeTerms":[
                                {
                                    "name":"percCPULoad",
                                    "serviceScope":{
                                        "serviceName":"Service2",
                                        "value":"Service2"
                                    },
                                    "qualifyingCondition":null,
                                    "businessValueList":null,
                                    "serviceLevelObjetive":{
                                        "kpitarget":{
                                            "kpiName":"percCPULoad",
                                            "customServiceLevel":"{\"constraint\": \"percCPULoad LT 70\"}"
                                        }
                                    }
                                },
                                {
                                    "name":"percDiskUsed",
                                    "serviceScope":{
                                        "serviceName":"Service2",
                                        "value":"Service2"
                                    },
                                    "qualifyingCondition":null,
                                    "businessValueList":null,
                                    "serviceLevelObjetive":{
                                        "kpitarget":{
                                            "kpiName":"percDiskUsed",
                                            "customServiceLevel":"{\"constraint\": \"percDiskUsed LT 80\"}"
                                        }
                                    }
                                },
                                {
                                    "name":"percRAMUsed",
                                    "serviceScope":{
                                        "serviceName":"Service2",
                                        "value":"Service2"
                                    },
                                    "qualifyingCondition":null,
                                    "businessValueList":null,
                                    "serviceLevelObjetive":{
                                        "kpitarget":{
                                            "kpiName":"percRAMUsed",
                                            "customServiceLevel":"{\"constraint\": \"percRAMUsed LT 60\"}"
                                        }
                                    }
                                }
                            ]
                        }
                    }
                }
            ]

##### Create a new agreement [POST]

Allows to create a new agreement. It takes a XML body the description of the
agreement, following the [WS-Agreement schema](http://schemas.ggf.org/graap/2007/03/ws-agreement).
It might include a AgreementId or not. In case of not beeing included, a uuid will be assigned.

+ AgreementId (optional, string) - The id of the agreement.
+ Name (string) - A human readable name for the agreement.
+ Context (object)
    + AgreementInitiator (string) - The name of the agreement initiator (in our case, an existing provider)
    + AgreementResponder (optional, string) - The name of the responder.
    + ServiceProvider (string) - The name of the service provider (an existing provider)
    + Service (string) - The service ID the agreement will monitor.
    + TemplateId (string) - The template id the new agreement is based on.
    + ExpirationTime (string) - The expiration date of the template (YYYY-MM-DDThh:mm:ss+hhmm format)
+ Terms (object) - A set of terms.
    + ServiceDescriptionTerm (object)
        + Name (string) - The name of the service description term
        + ServiceName (string) - The service ID the services are based on.
        + Value (string) - Human readable description of the service description term.
    + ServiceProperties (array[VariableSet]) - A list of variable sets.
        + Name (string) - Name of the properties.
        + ServiceName (string) - Name of the service where the properties apply.
        + VariableSet (array[Variable]) - A list of variables.
            + Variable (object)
                + Name (string) - The name of the variable.
    + GuaranteeTerm (array[ServiceLevelObjetive]) - A list of ServiceScope and ServiceLevelObjective.
        + Name (string) - Descriptive name of the Guarantee Term.
        + ServiceScope (object)
            + ServiceName (string) - name of the service.
            + Value (string) - Id of the scope of the service.
        + ServiceLevelObjetive (object)
            + KPITarget (object)
                + KPIName (string) - Name of the KPI to be monitored.
                + CustomServiceLevel (string) - Custom values to be fulfilled by the monitored KPI.

+ Request (application/xml)

    + headers
    
            user: basic authentication user
            password: basic authentication password

    + body
            
            <wsag:Agreement xmlns:wsag="http://www.ggf.org/namespaces/ws-agreement" xmlns:sla="http://sla.atos.eu">
                <wsag:Name>Agreement3</wsag:Name>
                <wsag:Context>
                    <wsag:AgreementInitiator>user3@serviceuser.com</wsag:AgreementInitiator>
                    <wsag:AgreementResponder>provider3</wsag:AgreementResponder>
                    <wsag:ServiceProvider>AgreementResponder</wsag:ServiceProvider>
                    <wsag:ExpirationTime>0024-03-07T11:08:24CET</wsag:ExpirationTime>
                    <wsag:TemplateId>e7efbfc1-3039-44c1-82d5-3d0e04cf2368</wsag:TemplateId>
                    <sla:Service>Service3</sla:Service>
                </wsag:Context>
                <wsag:Terms>
                    <wsag:All>
                        <wsag:ServiceDescriptionTerm />
                        <wsag:ServiceProperties wsag:Name="ServiceProperties" wsag:ServiceName="Service3">
                            <wsag:VariableSet>
                                <wsag:Variable wsag:Name="percCPULoad" />
                                <wsag:Variable wsag:Name="percDiskUsed" />
                                <wsag:Variable wsag:Name="percRAMUsed" />
                            </wsag:VariableSet>
                        </wsag:ServiceProperties>
                        <wsag:GuaranteeTerm wsag:Name="percCPULoad">
                            <wsag:ServiceScope wsag:ServiceName="Service3">Service3</wsag:ServiceScope>
                            <wsag:ServiceLevelObjective>
                                <wsag:KPITarget>
                                    <wsag:KPIName>percCPULoad</wsag:KPIName>
                                    <wsag:CustomServiceLevel>{"constraint": "percCPULoad LT 90"}</wsag:CustomServiceLevel>
                                </wsag:KPITarget>
                            </wsag:ServiceLevelObjective>
                        </wsag:GuaranteeTerm>
                        <wsag:GuaranteeTerm wsag:Name="percDiskUsed">
                            <wsag:ServiceScope wsag:ServiceName="Service3">Service3</wsag:ServiceScope>
                            <wsag:ServiceLevelObjective>
                                <wsag:KPITarget>
                                    <wsag:KPIName>percDiskUsed</wsag:KPIName>
                                    <wsag:CustomServiceLevel>{"constraint": "percDiskUsed LT 80"}</wsag:CustomServiceLevel>
                                </wsag:KPITarget>
                            </wsag:ServiceLevelObjective>
                        </wsag:GuaranteeTerm>
                        <wsag:GuaranteeTerm wsag:Name="percRAMUsed">
                            <wsag:ServiceScope wsag:ServiceName="Service3">Service3</wsag:ServiceScope>
                            <wsag:ServiceLevelObjective>
                                <wsag:KPITarget>
                                    <wsag:KPIName>percRAMUsed</wsag:KPIName>
                                    <wsag:CustomServiceLevel>{"constraint": "percRAMUsed LT 80"}</wsag:CustomServiceLevel>
                                </wsag:KPITarget>
                            </wsag:ServiceLevelObjective>
                        </wsag:GuaranteeTerm>
                    </wsag:All>
                </wsag:Terms>
            </wsag:Agreement>

+ Response 201 (application/xml)

    + Body

            <wsag:Agreement xmlns:wsag="http://www.ggf.org/namespaces/ws-agreement" xmlns:sla="http://sla.atos.eu" wsag:AgreementId="87726a36-e570-4482-b3cc-1cb3d8998bb8">
                <wsag:Name>Agreement3</wsag:Name>
                <wsag:Context>
                    <wsag:AgreementInitiator>user3@serviceuser.com</wsag:AgreementInitiator>
                    <wsag:AgreementResponder>provider3</wsag:AgreementResponder>
                    <wsag:ServiceProvider>AgreementResponder</wsag:ServiceProvider>
                    <wsag:ExpirationTime>0024-03-07T11:08:24CET</wsag:ExpirationTime>
                    <wsag:TemplateId>e7efbfc1-3039-44c1-82d5-3d0e04cf2368</wsag:TemplateId>
                    <sla:Service>Service3</sla:Service>
                </wsag:Context>
                <wsag:Terms>
                    <wsag:All>
                        <wsag:ServiceDescriptionTerm />
                        <wsag:ServiceProperties wsag:Name="ServiceProperties" wsag:ServiceName="Service3">
                            <wsag:VariableSet>
                                <wsag:Variable wsag:Name="percCPULoad" />
                                <wsag:Variable wsag:Name="percDiskUsed" />
                                <wsag:Variable wsag:Name="percRAMUsed" />
                            </wsag:VariableSet>
                        </wsag:ServiceProperties>
                        <wsag:GuaranteeTerm wsag:Name="percCPULoad">
                            <wsag:ServiceScope wsag:ServiceName="Service3">Service3</wsag:ServiceScope>
                            <wsag:ServiceLevelObjective>
                                <wsag:KPITarget>
                                    <wsag:KPIName>percCPULoad</wsag:KPIName>
                                    <wsag:CustomServiceLevel>{"constraint": "percCPULoad LT 90"}</wsag:CustomServiceLevel>
                                </wsag:KPITarget>
                            </wsag:ServiceLevelObjective>
                        </wsag:GuaranteeTerm>
                        <wsag:GuaranteeTerm wsag:Name="percDiskUsed">
                            <wsag:ServiceScope wsag:ServiceName="Service3">Service3</wsag:ServiceScope>
                            <wsag:ServiceLevelObjective>
                                <wsag:KPITarget>
                                    <wsag:KPIName>percDiskUsed</wsag:KPIName>
                                    <wsag:CustomServiceLevel>{"constraint": "percDiskUsed LT 80"}</wsag:CustomServiceLevel>
                                </wsag:KPITarget>
                            </wsag:ServiceLevelObjective>
                        </wsag:GuaranteeTerm>
                        <wsag:GuaranteeTerm wsag:Name="percRAMUsed">
                            <wsag:ServiceScope wsag:ServiceName="Service3">Service3</wsag:ServiceScope>
                            <wsag:ServiceLevelObjective>
                                <wsag:KPITarget>
                                    <wsag:KPIName>percRAMUsed</wsag:KPIName>
                                    <wsag:CustomServiceLevel>{"constraint": "percRAMUsed LT 80"}</wsag:CustomServiceLevel>
                                </wsag:KPITarget>
                            </wsag:ServiceLevelObjective>
                        </wsag:GuaranteeTerm>
                    </wsag:All>
                </wsag:Terms>
            </wsag:Agreement>

+ Request (application/json)

    + headers
    
            user: basic authentication user
            password: basic authentication password

    + body
            
            {
                "name":"Agreement3",
                "context":{
                    "agreementInitiator":"user3@serviceuser.com",
                    "agreementResponder":"Provider3",
                    "serviceProvider":"AgreementResponder",
                    "expirationTime":"0024-03-07T11:08:24CET",
                    "templateId":"e7efbfc1-3039-44c1-82d5-3d0e04cf2368",
                    "service":"Service3"
                },
                "terms":{
                    "allTerms":{
                        "serviceDescriptionTerm":{
                            "name":null,
                            "serviceName":null
                        },
                        "serviceProperties":[
                            {
                                "name":"ServiceProperties",
                                "serviceName":"Service3",
                                "variableSet":{
                                    "variables":[
                                        {
                                            "name":"percCPULoad",
                                            "metric":null,
                                            "location":null
                                        },
                                        {
                                            "name":"percDiskUsed",
                                            "metric":null,
                                            "location":null
                                        },
                                        {
                                            "name":"percRAMUsed",
                                            "metric":null,
                                            "location":null
                                        }
                                    ]
                                }
                            }
                        ],
                        "guaranteeTerms":[
                            {
                                "name":"percCPULoad",
                                "serviceScope":{
                                    "serviceName":"Service3",
                                    "value":"Service3"
                                },
                                "qualifyingCondition":null,
                                "businessValueList":null,
                                "serviceLevelObjetive":{
                                    "kpitarget":{
                                        "kpiName":"percCPULoad",
                                        "customServiceLevel":"{\"constraint\": \"percCPULoad LT 90\"}"
                                    }
                                }
                            },
                            {
                                "name":"percDiskUsed",
                                "serviceScope":{
                                    "serviceName":"Service3",
                                    "value":"Service3"
                                },
                                "qualifyingCondition":null,
                                "businessValueList":null,
                                "serviceLevelObjetive":{
                                    "kpitarget":{
                                        "kpiName":"percDiskUsed",
                                        "customServiceLevel":"{\"constraint\": \"percDiskUsed LT 80\"}"
                                    }
                                }
                            },
                            {
                                "name":"percRAMUsed",
                                "serviceScope":{
                                    "serviceName":"Service3",
                                    "value":"Service3"
                                },
                                "qualifyingCondition":null,
                                "businessValueList":null,
                                "serviceLevelObjetive":{
                                    "kpitarget":{
                                        "kpiName":"percRAMUsed",
                                        "customServiceLevel":"{\"constraint\": \"percRAMUsed LT 80\"}"
                                    }
                                }
                            }
                        ]
                    }
                }
            }

+ Response 201 (application/json)

    + Body

            {
                "agreementId":"87726a36-e570-4482-b3cc-1cb3d8998bb8",
                "name":"Agreement3",
                "context":{
                    "agreementInitiator":"user3@serviceuser.com",
                    "agreementResponder":"Provider3",
                    "serviceProvider":"AgreementResponder",
                    "expirationTime":"0024-03-07T11:08:24CET",
                    "templateId":"e7efbfc1-3039-44c1-82d5-3d0e04cf2368",
                    "service":"Service3"
                },
                "terms":{
                    "allTerms":{
                        "serviceDescriptionTerm":{
                            "name":null,
                            "serviceName":null
                        },
                        "serviceProperties":[
                            {
                                "name":"ServiceProperties",
                                "serviceName":"Service3",
                                "variableSet":{
                                    "variables":[
                                        {
                                            "name":"percCPULoad",
                                            "metric":null,
                                            "location":null
                                        },
                                        {
                                            "name":"percDiskUsed",
                                            "metric":null,
                                            "location":null
                                        },
                                        {
                                            "name":"percRAMUsed",
                                            "metric":null,
                                            "location":null
                                        }
                                    ]
                                }
                            }
                        ],
                        "guaranteeTerms":[
                            {
                                "name":"percCPULoad",
                                "serviceScope":{
                                    "serviceName":"Service3",
                                    "value":"Service3"
                                },
                                "qualifyingCondition":null,
                                "businessValueList":null,
                                "serviceLevelObjetive":{
                                    "kpitarget":{
                                        "kpiName":"percCPULoad",
                                        "customServiceLevel":"{\"constraint\": \"percCPULoad LT 90\"}"
                                    }
                                }
                            },
                            {
                                "name":"percDiskUsed",
                                "serviceScope":{
                                    "serviceName":"Service3",
                                    "value":"Service3"
                                },
                                "qualifyingCondition":null,
                                "businessValueList":null,
                                "serviceLevelObjetive":{
                                    "kpitarget":{
                                        "kpiName":"percDiskUsed",
                                        "customServiceLevel":"{\"constraint\": \"percDiskUsed LT 80\"}"
                                    }
                                }
                            },
                            {
                                "name":"percRAMUsed",
                                "serviceScope":{
                                    "serviceName":"Service3",
                                    "value":"Service3"
                                },
                                "qualifyingCondition":null,
                                "businessValueList":null,
                                "serviceLevelObjetive":{
                                    "kpitarget":{
                                        "kpiName":"percRAMUsed",
                                        "customServiceLevel":"{\"constraint\": \"percRAMUsed LT 80\"}"
                                    }
                                }
                            }
                        ]
                    }
                }
            }

+ Response 409

        Returned when the uuid or name already exists in the database.
        Returned when the provider uuid specified in the agreement doesn't exist in the database.
        Returned when the template uuid specified in the agreement doesn't exist in the database.
        
+ Response 500

        Returned when incorrect data has been suplied.
        
#### Active agreement collection [/agreements/active]

##### List all active agreements [GET]

This operation allows to get all the active agreements in the SLA Manager Core.

+ Request

    + Headers
    
            user: basic authentication user
            password: basic authentication password

+ Response 200 (application/xml)

    + Body

            <?xml version="1.0" encoding="UTF-8"?>
            <agreements>
                <wsag:Agreement xmlns:wsag="http://www.ggf.org/namespaces/ws-agreement" xmlns:sla="http://sla.atos.eu" wsag:AgreementId="87726a36-e570-4482-b3cc-1cb3d8998bb8">
                    <wsag:Name>Agreement1</wsag:Name>
                    <wsag:Context>
                        <wsag:AgreementInitiator>user1@serviceuser.com</wsag:AgreementInitiator>
                        <wsag:AgreementResponder>Provider1</wsag:AgreementResponder>
                        <wsag:ServiceProvider>AgreementResponder</wsag:ServiceProvider>
                        <wsag:ExpirationTime>0024-03-07T11:08:24CET</wsag:ExpirationTime>
                        <wsag:TemplateId>e7efbfc1-3039-44c1-82d5-3d0e04cf2368</wsag:TemplateId>
                        <sla:Service>Service1</sla:Service>
                    </wsag:Context>
                    <wsag:Terms>
                        <wsag:All>
                            <wsag:ServiceDescriptionTerm />
                            <wsag:ServiceProperties wsag:Name="ServiceProperties" wsag:ServiceName="Service1">
                                <wsag:VariableSet>
                                    <wsag:Variable wsag:Name="percCPULoad" />
                                    <wsag:Variable wsag:Name="percDiskUsed" />
                                    <wsag:Variable wsag:Name="percRAMUsed" />
                                </wsag:VariableSet>
                            </wsag:ServiceProperties>
                            <wsag:GuaranteeTerm wsag:Name="percCPULoad">
                                <wsag:ServiceScope wsag:ServiceName="Service1">Service1</wsag:ServiceScope>
                                <wsag:ServiceLevelObjective>
                                    <wsag:KPITarget>
                                        <wsag:KPIName>percCPULoad</wsag:KPIName>
                                        <wsag:CustomServiceLevel>{"constraint": "percCPULoad LT 90"}</wsag:CustomServiceLevel>
                                    </wsag:KPITarget>
                                </wsag:ServiceLevelObjective>
                            </wsag:GuaranteeTerm>
                            <wsag:GuaranteeTerm wsag:Name="percDiskUsed">
                                <wsag:ServiceScope wsag:ServiceName="Service1">Service1</wsag:ServiceScope>
                                <wsag:ServiceLevelObjective>
                                    <wsag:KPITarget>
                                        <wsag:KPIName>percDiskUsed</wsag:KPIName>
                                        <wsag:CustomServiceLevel>{"constraint": "percDiskUsed LT 80"}</wsag:CustomServiceLevel>
                                    </wsag:KPITarget>
                                </wsag:ServiceLevelObjective>
                            </wsag:GuaranteeTerm>
                            <wsag:GuaranteeTerm wsag:Name="percRAMUsed">
                                <wsag:ServiceScope wsag:ServiceName="Service1">Service1</wsag:ServiceScope>
                                <wsag:ServiceLevelObjective>
                                    <wsag:KPITarget>
                                        <wsag:KPIName>percRAMUsed</wsag:KPIName>
                                        <wsag:CustomServiceLevel>{"constraint": "percRAMUsed LT 80"}</wsag:CustomServiceLevel>
                                    </wsag:KPITarget>
                                </wsag:ServiceLevelObjective>
                            </wsag:GuaranteeTerm>
                        </wsag:All>
                    </wsag:Terms>
                </wsag:Agreement>
            </agreements>

+ Response 200 (application/json)

    + Body

            {
                "agreementId":"87726a36-e570-4482-b3cc-1cb3d8998bb8",
                "name":"Agreement3",
                "context":{
                    "agreementInitiator":"user3@serviceuser.com",
                    "agreementResponder":"Provider3",
                    "serviceProvider":"AgreementResponder",
                    "expirationTime":"0024-03-07T11:08:24CET",
                    "templateId":"e7efbfc1-3039-44c1-82d5-3d0e04cf2368",
                    "service":"Service3"
                },
                "terms":{
                    "allTerms":{
                        "serviceDescriptionTerm":{
                            "name":null,
                            "serviceName":null
                        },
                        "serviceProperties":[
                            {
                                "name":"ServiceProperties",
                                "serviceName":"Service3",
                                "variableSet":{
                                    "variables":[
                                        {
                                            "name":"percCPULoad",
                                            "metric":null,
                                            "location":null
                                        },
                                        {
                                            "name":"percDiskUsed",
                                            "metric":null,
                                            "location":null
                                        },
                                        {
                                            "name":"percRAMUsed",
                                            "metric":null,
                                            "location":null
                                        }
                                    ]
                                }
                            }
                        ],
                        "guaranteeTerms":[
                            {
                                "name":"percCPULoad",
                                "serviceScope":{
                                    "serviceName":"Service3",
                                    "value":"Service3"
                                },
                                "qualifyingCondition":null,
                                "businessValueList":null,
                                "serviceLevelObjetive":{
                                    "kpitarget":{
                                        "kpiName":"percCPULoad",
                                        "customServiceLevel":"{\"constraint\": \"percCPULoad LT 90\"}"
                                    }
                                }
                            },
                            {
                                "name":"percDiskUsed",
                                "serviceScope":{
                                    "serviceName":"Service3",
                                    "value":"Service3"
                                },
                                "qualifyingCondition":null,
                                "businessValueList":null,
                                "serviceLevelObjetive":{
                                    "kpitarget":{
                                        "kpiName":"percDiskUsed",
                                        "customServiceLevel":"{\"constraint\": \"percDiskUsed LT 80\"}"
                                    }
                                }
                            },
                            {
                                "name":"percRAMUsed",
                                "serviceScope":{
                                    "serviceName":"Service3",
                                    "value":"Service3"
                                },
                                "qualifyingCondition":null,
                                "businessValueList":null,
                                "serviceLevelObjetive":{
                                    "kpitarget":{
                                        "kpiName":"percRAMUsed",
                                        "customServiceLevel":"{\"constraint\": \"percRAMUsed LT 80\"}"
                                    }
                                }
                            }
                        ]
                    }
                }
            }

#### Agreements per template and consumer [/agreementsPerTemplateAndConsumer{?consumerId}{?templateUUID}]

##### Get agreements per template and consumer [GET]

+ Parameters

    + consumerId (optional, string) - uuid of the consumer
    + templateUUID (optional, string) - uuid of the template the agreement is based on

+ Request

    + Headers
    
            user: basic authentication user
            password: basic authentication password

+ Response 200 (application/xml)

    + Body

            <?xml version="1.0" encoding="UTF-8"?>
            <agreements>
                <wsag:Agreement xmlns:wsag="http://www.ggf.org/namespaces/ws-agreement" xmlns:sla="http://sla.atos.eu" wsag:AgreementId="87726a36-e570-4482-b3cc-1cb3d8998bb8">
                    <wsag:Name>Agreement1</wsag:Name>
                    <wsag:Context>
                        <wsag:AgreementInitiator>user1@serviceuser.com</wsag:AgreementInitiator>
                        <wsag:AgreementResponder>Provider1</wsag:AgreementResponder>
                        <wsag:ServiceProvider>AgreementResponder</wsag:ServiceProvider>
                        <wsag:ExpirationTime>0024-03-07T11:08:24CET</wsag:ExpirationTime>
                        <wsag:TemplateId>e7efbfc1-3039-44c1-82d5-3d0e04cf2368</wsag:TemplateId>
                        <sla:Service>Service1</sla:Service>
                    </wsag:Context>
                    <wsag:Terms>
                        <wsag:All>
                            <wsag:ServiceDescriptionTerm />
                            <wsag:ServiceProperties wsag:Name="ServiceProperties" wsag:ServiceName="Service1">
                                <wsag:VariableSet>
                                    <wsag:Variable wsag:Name="percCPULoad" />
                                    <wsag:Variable wsag:Name="percDiskUsed" />
                                    <wsag:Variable wsag:Name="percRAMUsed" />
                                </wsag:VariableSet>
                            </wsag:ServiceProperties>
                            <wsag:GuaranteeTerm wsag:Name="percCPULoad">
                                <wsag:ServiceScope wsag:ServiceName="Service1">Service1</wsag:ServiceScope>
                                <wsag:ServiceLevelObjective>
                                    <wsag:KPITarget>
                                        <wsag:KPIName>percCPULoad</wsag:KPIName>
                                        <wsag:CustomServiceLevel>{"constraint": "percCPULoad LT 90"}</wsag:CustomServiceLevel>
                                    </wsag:KPITarget>
                                </wsag:ServiceLevelObjective>
                            </wsag:GuaranteeTerm>
                            <wsag:GuaranteeTerm wsag:Name="percDiskUsed">
                                <wsag:ServiceScope wsag:ServiceName="Service1">Service1</wsag:ServiceScope>
                                <wsag:ServiceLevelObjective>
                                    <wsag:KPITarget>
                                        <wsag:KPIName>percDiskUsed</wsag:KPIName>
                                        <wsag:CustomServiceLevel>{"constraint": "percDiskUsed LT 80"}</wsag:CustomServiceLevel>
                                    </wsag:KPITarget>
                                </wsag:ServiceLevelObjective>
                            </wsag:GuaranteeTerm>
                            <wsag:GuaranteeTerm wsag:Name="percRAMUsed">
                                <wsag:ServiceScope wsag:ServiceName="Service1">Service1</wsag:ServiceScope>
                                <wsag:ServiceLevelObjective>
                                    <wsag:KPITarget>
                                        <wsag:KPIName>percRAMUsed</wsag:KPIName>
                                        <wsag:CustomServiceLevel>{"constraint": "percRAMUsed LT 80"}</wsag:CustomServiceLevel>
                                    </wsag:KPITarget>
                                </wsag:ServiceLevelObjective>
                            </wsag:GuaranteeTerm>
                        </wsag:All>
                    </wsag:Terms>
                </wsag:Agreement>
                <wsag:Agreement xmlns:wsag="http://www.ggf.org/namespaces/ws-agreement" xmlns:sla="http://sla.atos.eu" wsag:AgreementId="51a3ec44-f755-4ee8-824b-0ce1001e990a">
                    <wsag:Name>WirecloudAgreement70_80_60</wsag:Name>
                    <wsag:Context>
                        <wsag:AgreementInitiator>user2@serviceuser.com</wsag:AgreementInitiator>
                        <wsag:AgreementResponder>provider2</wsag:AgreementResponder>
                        <wsag:ServiceProvider>AgreementResponder</wsag:ServiceProvider>
                        <wsag:ExpirationTime>0024-03-07T11:08:24CET</wsag:ExpirationTime>
                        <wsag:TemplateId>e7efbfc1-3039-44c1-82d5-3d0e04cf2368</wsag:TemplateId>
                        <sla:Service>Service2</sla:Service>
                    </wsag:Context>
                    <wsag:Terms>
                        <wsag:All>
                            <wsag:ServiceDescriptionTerm />
                            <wsag:ServiceProperties wsag:Name="ServiceProperties" wsag:ServiceName="Service2">
                                <wsag:VariableSet>
                                    <wsag:Variable wsag:Name="percCPULoad" />
                                    <wsag:Variable wsag:Name="percDiskUsed" />
                                    <wsag:Variable wsag:Name="percRAMUsed" />
                                </wsag:VariableSet>
                            </wsag:ServiceProperties>
                            <wsag:GuaranteeTerm wsag:Name="percCPULoad">
                                <wsag:ServiceScope wsag:ServiceName="Service2">Service2</wsag:ServiceScope>
                                <wsag:ServiceLevelObjective>
                                    <wsag:KPITarget>
                                        <wsag:KPIName>percCPULoad</wsag:KPIName>
                                        <wsag:CustomServiceLevel>{"constraint": "percCPULoad LT 70"}</wsag:CustomServiceLevel>
                                    </wsag:KPITarget>
                                </wsag:ServiceLevelObjective>
                            </wsag:GuaranteeTerm>
                            <wsag:GuaranteeTerm wsag:Name="percDiskUsed">
                                <wsag:ServiceScope wsag:ServiceName="Service2">Service2</wsag:ServiceScope>
                                <wsag:ServiceLevelObjective>
                                    <wsag:KPITarget>
                                        <wsag:KPIName>percDiskUsed</wsag:KPIName>
                                        <wsag:CustomServiceLevel>{"constraint": "percDiskUsed LT 80"}</wsag:CustomServiceLevel>
                                    </wsag:KPITarget>
                                </wsag:ServiceLevelObjective>
                            </wsag:GuaranteeTerm>
                            <wsag:GuaranteeTerm wsag:Name="percRAMUsed">
                                <wsag:ServiceScope wsag:ServiceName="Service2">Service2</wsag:ServiceScope>
                                <wsag:ServiceLevelObjective>
                                    <wsag:KPITarget>
                                        <wsag:KPIName>percRAMUsed</wsag:KPIName>
                                        <wsag:CustomServiceLevel>{"constraint": "percRAMUsed LT 60"}</wsag:CustomServiceLevel>
                                    </wsag:KPITarget>
                                </wsag:ServiceLevelObjective>
                            </wsag:GuaranteeTerm>
                        </wsag:All>
                    </wsag:Terms>
                </wsag:Agreement>
            </agreements>
            
+ Response 200 (application/json)

    + Body

            [
                {
                    "agreementId":"87726a36-e570-4482-b3cc-1cb3d8998bb8",
                    "name":"Agreement1",
                    "context":{
                        "agreementInitiator":"user1@serviceuser.com",
                        "agreementResponder":"Provider1",
                        "serviceProvider":"AgreementResponder",
                        "expirationTime":"0024-03-07T11:08:24CET",
                        "templateId":"e7efbfc1-3039-44c1-82d5-3d0e04cf2368",
                        "service":"Service1"
                    },
                    "terms":{
                        "allTerms":{
                            "serviceDescriptionTerm":{
                                "name":null,
                                "serviceName":null
                            },
                            "serviceProperties":[
                                {
                                    "name":"ServiceProperties",
                                    "serviceName":"Service1",
                                    "variableSet":{
                                        "variables":[
                                            {
                                                "name":"percCPULoad",
                                                "metric":null,
                                                "location":null
                                            },
                                            {
                                                "name":"percDiskUsed",
                                                "metric":null,
                                                "location":null
                                            },
                                            {
                                                "name":"percRAMUsed",
                                                "metric":null,
                                                "location":null
                                            }
                                        ]
                                    }
                                }
                            ],
                            "guaranteeTerms":[
                                {
                                    "name":"percCPULoad",
                                    "serviceScope":{
                                        "serviceName":"Service1",
                                        "value":"Service1"
                                    },
                                    "qualifyingCondition":null,
                                    "businessValueList":null,
                                    "serviceLevelObjetive":{
                                        "kpitarget":{
                                            "kpiName":"percCPULoad",
                                            "customServiceLevel":"{\"constraint\": \"percCPULoad LT 90\"}"
                                        }
                                    }
                                },
                                {
                                    "name":"percDiskUsed",
                                    "serviceScope":{
                                        "serviceName":"Service1",
                                        "value":"Service1"
                                    },
                                    "qualifyingCondition":null,
                                    "businessValueList":null,
                                    "serviceLevelObjetive":{
                                        "kpitarget":{
                                            "kpiName":"percDiskUsed",
                                            "customServiceLevel":"{\"constraint\": \"percDiskUsed LT 80\"}"
                                        }
                                    }
                                },
                                {
                                    "name":"percRAMUsed",
                                    "serviceScope":{
                                        "serviceName":"Service1",
                                        "value":"Service1"
                                    },
                                    "qualifyingCondition":null,
                                    "businessValueList":null,
                                    "serviceLevelObjetive":{
                                        "kpitarget":{
                                            "kpiName":"percRAMUsed",
                                            "customServiceLevel":"{\"constraint\": \"percRAMUsed LT 80\"}"
                                        }
                                    }
                                }
                            ]
                        }
                    }
                },
                {
                    "agreementId":"51a3ec44-f755-4ee8-824b-0ce1001e990a",
                    "name":"WirecloudAgreement70_80_60",
                    "context":{
                        "agreementInitiator":"user2@serviceuser.com",
                        "agreementResponder":"Provider2",
                        "serviceProvider":"AgreementResponder",
                        "expirationTime":"0026-05-08T11:09:58CET",
                        "templateId":"99bea98a-6df4-4ed7-94ef-1067962f130c",
                        "service":"Service2"
                    },
                    "terms":{
                        "allTerms":{
                            "serviceDescriptionTerm":{
                                "name":null,
                                "serviceName":null
                            },
                            "serviceProperties":[
                                {
                                    "name":"ServiceProperties",
                                    "serviceName":"Service2",
                                    "variableSet":{
                                        "variables":[
                                            {
                                                "name":"percCPULoad",
                                                "metric":null,
                                                "location":null
                                            },
                                            {
                                                "name":"percDiskUsed",
                                                "metric":null,
                                                "location":null
                                            },
                                            {
                                                "name":"percRAMUsed",
                                                "metric":null,
                                                "location":null
                                            }
                                        ]
                                    }
                                }
                            ],
                            "guaranteeTerms":[
                                {
                                    "name":"percCPULoad",
                                    "serviceScope":{
                                        "serviceName":"Service2",
                                        "value":"Service2"
                                    },
                                    "qualifyingCondition":null,
                                    "businessValueList":null,
                                    "serviceLevelObjetive":{
                                        "kpitarget":{
                                            "kpiName":"percCPULoad",
                                            "customServiceLevel":"{\"constraint\": \"percCPULoad LT 70\"}"
                                        }
                                    }
                                },
                                {
                                    "name":"percDiskUsed",
                                    "serviceScope":{
                                        "serviceName":"Service2",
                                        "value":"Service2"
                                    },
                                    "qualifyingCondition":null,
                                    "businessValueList":null,
                                    "serviceLevelObjetive":{
                                        "kpitarget":{
                                            "kpiName":"percDiskUsed",
                                            "customServiceLevel":"{\"constraint\": \"percDiskUsed LT 80\"}"
                                        }
                                    }
                                },
                                {
                                    "name":"percRAMUsed",
                                    "serviceScope":{
                                        "serviceName":"Service2",
                                        "value":"Service2"
                                    },
                                    "qualifyingCondition":null,
                                    "businessValueList":null,
                                    "serviceLevelObjetive":{
                                        "kpitarget":{
                                            "kpiName":"percRAMUsed",
                                            "customServiceLevel":"{\"constraint\": \"percRAMUsed LT 60\"}"
                                        }
                                    }
                                }
                            ]
                        }
                    }
                }
            ]

#### Agreement [/agreements/{AgreementId}]

##### Get agreement info [GET]

+ Parameters

    + AgreementId (string) - Id of the agreement

+ Request

    + Headers
    
            user: basic authentication user
            password: basic authentication password

+ Response 200 (application/xml)

    + Body

            <wsag:Agreement xmlns:wsag="http://www.ggf.org/namespaces/ws-agreement" xmlns:sla="http://sla.atos.eu" wsag:AgreementId="87726a36-e570-4482-b3cc-1cb3d8998bb8">
                <wsag:Name>Agreement3</wsag:Name>
                <wsag:Context>
                    <wsag:AgreementInitiator>user3@serviceuser.com</wsag:AgreementInitiator>
                    <wsag:AgreementResponder>provider3</wsag:AgreementResponder>
                    <wsag:ServiceProvider>AgreementResponder</wsag:ServiceProvider>
                    <wsag:ExpirationTime>0024-03-07T11:08:24CET</wsag:ExpirationTime>
                    <wsag:TemplateId>e7efbfc1-3039-44c1-82d5-3d0e04cf2368</wsag:TemplateId>
                    <sla:Service>Service3</sla:Service>
                </wsag:Context>
                <wsag:Terms>
                    <wsag:All>
                        <wsag:ServiceDescriptionTerm />
                        <wsag:ServiceProperties wsag:Name="ServiceProperties" wsag:ServiceName="Service3">
                            <wsag:VariableSet>
                                <wsag:Variable wsag:Name="percCPULoad" />
                                <wsag:Variable wsag:Name="percDiskUsed" />
                                <wsag:Variable wsag:Name="percRAMUsed" />
                            </wsag:VariableSet>
                        </wsag:ServiceProperties>
                        <wsag:GuaranteeTerm wsag:Name="percCPULoad">
                            <wsag:ServiceScope wsag:ServiceName="Service3">Service3</wsag:ServiceScope>
                            <wsag:ServiceLevelObjective>
                                <wsag:KPITarget>
                                    <wsag:KPIName>percCPULoad</wsag:KPIName>
                                    <wsag:CustomServiceLevel>{"constraint": "percCPULoad LT 90"}</wsag:CustomServiceLevel>
                                </wsag:KPITarget>
                            </wsag:ServiceLevelObjective>
                        </wsag:GuaranteeTerm>
                        <wsag:GuaranteeTerm wsag:Name="percDiskUsed">
                            <wsag:ServiceScope wsag:ServiceName="Service3">Service3</wsag:ServiceScope>
                            <wsag:ServiceLevelObjective>
                                <wsag:KPITarget>
                                    <wsag:KPIName>percDiskUsed</wsag:KPIName>
                                    <wsag:CustomServiceLevel>{"constraint": "percDiskUsed LT 80"}</wsag:CustomServiceLevel>
                                </wsag:KPITarget>
                            </wsag:ServiceLevelObjective>
                        </wsag:GuaranteeTerm>
                        <wsag:GuaranteeTerm wsag:Name="percRAMUsed">
                            <wsag:ServiceScope wsag:ServiceName="Service3">Service3</wsag:ServiceScope>
                            <wsag:ServiceLevelObjective>
                                <wsag:KPITarget>
                                    <wsag:KPIName>percRAMUsed</wsag:KPIName>
                                    <wsag:CustomServiceLevel>{"constraint": "percRAMUsed LT 80"}</wsag:CustomServiceLevel>
                                </wsag:KPITarget>
                            </wsag:ServiceLevelObjective>
                        </wsag:GuaranteeTerm>
                    </wsag:All>
                </wsag:Terms>
            </wsag:Agreement>

+ Response 200 (application/json)

    + Body

            {
                "agreementId":"87726a36-e570-4482-b3cc-1cb3d8998bb8",
                "name":"Agreement3",
                "context":{
                    "agreementInitiator":"user3@serviceuser.com",
                    "agreementResponder":"Provider3",
                    "serviceProvider":"AgreementResponder",
                    "expirationTime":"0024-03-07T11:08:24CET",
                    "templateId":"e7efbfc1-3039-44c1-82d5-3d0e04cf2368",
                    "service":"Service3"
                },
                "terms":{
                    "allTerms":{
                        "serviceDescriptionTerm":{
                            "name":null,
                            "serviceName":null
                        },
                        "serviceProperties":[
                            {
                                "name":"ServiceProperties",
                                "serviceName":"Service3",
                                "variableSet":{
                                    "variables":[
                                        {
                                            "name":"percCPULoad",
                                            "metric":null,
                                            "location":null
                                        },
                                        {
                                            "name":"percDiskUsed",
                                            "metric":null,
                                            "location":null
                                        },
                                        {
                                            "name":"percRAMUsed",
                                            "metric":null,
                                            "location":null
                                        }
                                    ]
                                }
                            }
                        ],
                        "guaranteeTerms":[
                            {
                                "name":"percCPULoad",
                                "serviceScope":{
                                    "serviceName":"Service3",
                                    "value":"Service3"
                                },
                                "qualifyingCondition":null,
                                "businessValueList":null,
                                "serviceLevelObjetive":{
                                    "kpitarget":{
                                        "kpiName":"percCPULoad",
                                        "customServiceLevel":"{\"constraint\": \"percCPULoad LT 90\"}"
                                    }
                                }
                            },
                            {
                                "name":"percDiskUsed",
                                "serviceScope":{
                                    "serviceName":"Service3",
                                    "value":"Service3"
                                },
                                "qualifyingCondition":null,
                                "businessValueList":null,
                                "serviceLevelObjetive":{
                                    "kpitarget":{
                                        "kpiName":"percDiskUsed",
                                        "customServiceLevel":"{\"constraint\": \"percDiskUsed LT 80\"}"
                                    }
                                }
                            },
                            {
                                "name":"percRAMUsed",
                                "serviceScope":{
                                    "serviceName":"Service3",
                                    "value":"Service3"
                                },
                                "qualifyingCondition":null,
                                "businessValueList":null,
                                "serviceLevelObjetive":{
                                    "kpitarget":{
                                        "kpiName":"percRAMUsed",
                                        "customServiceLevel":"{\"constraint\": \"percRAMUsed LT 80\"}"
                                    }
                                }
                            }
                        ]
                    }
                }
            }

+ Response 404

        Returned when the object is not in the database.

##### Delete agreement [DELETE]

+ Parameters

    + AgreementId (string) - Id of the agreement

+ Response 204
+ Response 404

        Returned when the given uuid doesn't exist in the database.

+ Response 409

        Returned when agreements are still associated to the template.
        
#### Agreement context [/agreements/{AgreementId}/context]

##### Get agreement context [GET]

Only the context from the agreement identified by AgreementId is returned.

+ Parameters

    + AgreementId (string) - Id of the agreement

+ Request

    + Headers
    
            user: basic authentication user
            password: basic authentication password

+ Response 200 (application/xml)

    + Body
    
            <?xml version="1.0" encoding="UTF-8"?>
            <wsag:Context xmlns:wsag="http://www.ggf.org/namespaces/ws-agreement" xmlns:sla="http://sla.atos.eu">
                <wsag:AgreementInitiator>user1@serviceuser.com</wsag:AgreementInitiator>
                <wsag:AgreementResponder>Provider1</wsag:AgreementResponder>
                <wsag:ServiceProvider>AgreementResponder</wsag:ServiceProvider>
                <wsag:ExpirationTime>0024-03-07T11:08:24CET</wsag:ExpirationTime>
                <wsag:TemplateId>e7efbfc1-3039-44c1-82d5-3d0e04cf2368</wsag:TemplateId>
                <sla:Service>Service1</sla:Service>
            </wsag:Context>

+ Response 200 (application/json)

    + Body
    
            {
                "AgreementInitiator":"user1@serviceuser.com",
                "AgreementResponder":"Provider1",
                "ServiceProvider":"AgreementResponder",
                "ExpirationTime":"0024-03-07T11:08:24CET",
                "TemplateId":"e7efbfc1-3039-44c1-82d5-3d0e04cf2368",
                "Service":"Service1"
            }
    
+ Response 404

        Returned when the uuid doesn't exist in the database.

+ Response 500

        Returned when the data agreement was recorded incorrectly and the data cannot be supplied.

#### Agreement guarantee terms status [/agreements/{AgreementId}/guaranteestatus]

##### Get agreement guarantee terms status [GET]

Gets the information of the status of the different Guarantee Terms of an agreement.

There are three available states: NON_DETERMINED, FULFILLED, VIOLATED.

+ Parameters

    + AgreementId (string) - Id of the agreement

+ Request

    + Headers
    
            user: basic authentication user
            password: basic authentication password

+ Response 200 (application/xml)

    + Body
    
            <?xml version="1.0" encoding="UTF-8"?>
            <guaranteestatus AgreementId="87726a36-e570-4482-b3cc-1cb3d8998bb8" value="VIOLATED">
                <guaranteetermstatus name="percCPULoad" value="VIOLATED" />
                <guaranteetermstatus name="percDiskUsed" value="FULFILLED" />
                <guaranteetermstatus name="percRAMUsed" value="FULFILLED" />
            </guaranteestatus>

+ Response 200 (application/json)

    + Body
    
            {
                "AgreementId":"87726a36-e570-4482-b3cc-1cb3d8998bb8",
                "guaranteestatus":"VIOLATED",
                "guaranteeterms":[
                    {
                        "name":"percCPULoad",
                        "status":"VIOLATED"
                    },
                    {
                        "name":"percDiskUsed",
                        "status":"FULFILLED"
                    },
                    {
                        "name":"percRAMUsed",
                        "status":"FULFILLED"
                    }
                ]
            }

+ Response 404

        Returned when the uuid doesn't exist in the database.

### Group Enforcement jobs

An enforcement job is the entity which starts the enforcement of the agreement guarantee terms. An agreement can be enforced only if an enforcement job, linked with it, has been previously created and started. An enforcement job is automatically created when an agreement is created, so there is no need to create one to start an enforcement.

#### Enforcement jobs collection [/enforcements]

##### Get enforcement jobs [GET]

+ Request

    + Headers
    
            user: basic authentication user
            password: basic authentication password

+ Response 200 (application/xml)

    + Body

            <?xml version="1.0" encoding="UTF-8"?>
            <enforcementJobs>
                <enforcement_job>
                    <agreement_id>22a548ec-122a-4aa1-b4e1-eee7b79c12b5</agreement_id>
                    <enabled>true</enabled>
                    <last_executed>2015-08-06T13:42:30CEST</last_executed>
                </enforcement_job>
                <enforcement_job>
                    <agreement_id>166e6666-eb00-4e97-bbbb-53e2f4c78b54</agreement_id>
                    <enabled>true</enabled>
                    <last_executed>2015-08-06T13:42:30CEST</last_executed>
                </enforcement_job>
                <enforcement_job>
                    <agreement_id>a5124ad1-d506-48d5-9666-78da4c432dc6</agreement_id>
                    <enabled>true</enabled>
                    <last_executed>2015-08-06T13:42:30CEST</last_executed>
                </enforcement_job>
                <enforcement_job>
                    <agreement_id>e133b07e-3b6a-40d3-902f-22be13266198</agreement_id>
                    <enabled>true</enabled>
                    <last_executed>2015-08-06T13:42:30CEST</last_executed>
                </enforcement_job>
                <enforcement_job>
                    <agreement_id>87726a36-e570-4482-b3cc-1cb3d8998bb8</agreement_id>
                    <enabled>true</enabled>
                    <last_executed>2015-08-06T13:42:30CEST</last_executed>
                </enforcement_job>
                <enforcement_job>
                    <agreement_id>51a3ec44-f755-4ee8-824b-0ce1001e990a</agreement_id>
                    <enabled>true</enabled>
                    <last_executed>2015-08-06T13:42:30CEST</last_executed>
                </enforcement_job>
            </enforcementJobs>

+ Response 200 (application/json)

            [
                {
                    "enabled":true,
                    "agreement_id":"22a548ec-122a-4aa1-b4e1-eee7b79c12b5",
                    "last_executed":"2015-08-06T13:42:30CEST"
                },
                {
                    "enabled":true,
                    "agreement_id":"166e6666-eb00-4e97-bbbb-53e2f4c78b54",
                    "last_executed":"2015-08-06T13:42:30CEST"
                },
                {
                    "enabled":true,
                    "agreement_id":"a5124ad1-d506-48d5-9666-78da4c432dc6",
                    "last_executed":"2015-08-06T13:42:30CEST"
                },
                {
                    "enabled":true,
                    "agreement_id":"e133b07e-3b6a-40d3-902f-22be13266198",
                    "last_executed":"2015-08-06T13:42:30CEST"
                },
                {
                    "enabled":true,
                    "agreement_id":"87726a36-e570-4482-b3cc-1cb3d8998bb8",
                    "last_executed":"2015-08-06T13:42:30CEST"
                },
                {
                    "enabled":true,
                    "agreement_id":"51a3ec44-f755-4ee8-824b-0ce1001e990a",
                    "last_executed":"2015-08-06T13:42:30CEST"
                }
            ]

#### Enforcement job by agreement [/enforcements/{AgreementId}]

##### Get enforcement job by agreement [GET]

+ Parameters

    + AgreementId (string) - Id of the agreement

+ Request

    + Headers
    
            user: basic authentication user
            password: basic authentication password

+ Response 200 (application/xml)

    + Body

            <?xml version="1.0" encoding="UTF-8"?>
            <enforcement_job>
                <agreement_id>a5124ad1-d506-48d5-9666-78da4c432dc6</agreement_id>
                <enabled>true</enabled>
                <last_executed>2015-08-06T14:02:30CEST</last_executed>
            </enforcement_job>
            
+ Response 200 (application/json)

    + Body

            {
                "enabled":true,
                "agreement_id":"a5124ad1-d506-48d5-9666-78da4c432dc6",
                "last_executed":"2015-08-06T14:02:30CEST"
            }

+ Response 404

        Returned when the given uuid doesn't exist in the database.

#### Start enforcement job for agreement [/enforcements/{AgreementId}/start]

##### Start enforcement job for agreement [PUT]

+ Parameters

    + AgreementId (string) - Id of the agreement

+ Request

    + Headers
    
            user: basic authentication user
            password: basic authentication password

+ Response 200 (application/xml)

    + Body

            The enforcement job with agreement-uuid {agreement-id} has started

+ Response 200 (application/json)

    + Body

            The enforcement job with agreement-uuid {agreement-id} has started

+ Response 403

            Returned when it was not possible to start the job.

#### Stop enforcement job for agreement [/enforcements/{AgreementId}/stop]

##### Stop enforcement job for agreement [PUT]

+ Parameters

    + AgreementId (string) - Id of the agreement

+ Request

    + Headers
    
            user: basic authentication user
            password: basic authentication password

+ Response 200 (application/xml)

    + Body

            The enforcement job with agreement-uuid {agreement-id} has stoppped

+ Response 200 (application/json)

    + Body

            The enforcement job with agreement-uuid {agreement-id} has stoppped

+ Response 403

            Returned when it was not possible to stop the job.

### Group Violations

#### Violation collection [/violations{?agreementId}{?guaranteeTerm}{?providerId}{?begin}{?end}]

##### Get violations [GET]

+ Parameters

    + agreementId (optional, string) - If specified, search the violations of the agreement with this agreementId.
    + guaranteeTerm (optional, string) - If specified, search the violations of the guarantee term with this name.
    + providerId (optional, string) - If specified, search the violations raised by this provider.
    + begin (optional, string) - If specified, set a lower limit of date of violations to search. Date format: yyyy-MM-dd'T'HH:mm:ss
    + end (optional, string) - If specified, set an upper limit of date of violations to search. Date format: yyyy-MM-dd'T'HH:mm:ss

+ Request

    + Headers
    
            user: basic authentication user
            password: basic authentication password

+ Response 200 (application/xml)

    + Body
    
            <?xml version="1.0" encoding="UTF-8"?>
            <violations>
                <violation>
                    <uuid>83e6e958-a34e-4089-9e48-7ebf6fda366a</uuid>
                    <contract_uuid>22a548ec-122a-4aa1-b4e1-eee7b79c12b5</contract_uuid>
                    <service_name>Service1</service_name>
                    <service_scope>Service1</service_scope>
                    <metric_name>percDiskUsed</metric_name>
                    <datetime>2015-07-28T15:09:29CEST</datetime>
                    <actual_value>0.34827652639805917</actual_value>
                </violation>
                <violation>
                    <uuid>720415c7-fa01-4d48-9235-547820c05b00</uuid>
                    <contract_uuid>a5124ad1-d506-48d5-9666-78da4c432dc6</contract_uuid>
                    <service_name>Service1</service_name>
                    <service_scope>Service1</service_scope>
                    <metric_name>sysUptime</metric_name>
                    <datetime>2015-08-06T12:21:01CEST</datetime>
                    <actual_value>0</actual_value>
                </violation>
                <violation>
                    <uuid>875f2e40-d549-41b5-9955-df048a8d35c9</uuid>
                    <contract_uuid>a5124ad1-d506-48d5-9666-78da4c432dc6</contract_uuid>
                    <service_name>Service1</service_name>
                    <service_scope>Service1</service_scope>
                    <metric_name>sysUptime</metric_name>
                    <datetime>2015-08-06T12:22:41CEST</datetime>
                    <actual_value>0</actual_value>
                </violation>
            </violations>
            
+ Response 200 (application/json)

    + Body
    
            [
                {
                    "uuid":"83e6e958-a34e-4089-9e48-7ebf6fda366a",
                    "datetime":"2015-07-28T15:09:29CEST",
                    "contract_uuid":"22a548ec-122a-4aa1-b4e1-eee7b79c12b5",
                    "service_name":"Service1",
                    "service_scope":"Service1",
                    "metric_name":"percDiskUsed",
                    "actual_value":"0.34827652639805917"
                },
                {
                    "uuid":"720415c7-fa01-4d48-9235-547820c05b00",
                    "datetime":"2014-08-13T10:01:01CEST",
                    "contract_uuid":"a5124ad1-d506-48d5-9666-78da4c432dc6",
                    "service_name":"Service1",
                    "service_scope":"Service1",
                    "metric_name":"sysUptime",
                    "actual_value":"0"
                },
                {
                    "uuid":"875f2e40-d549-41b5-9955-df048a8d35c9",
                    "datetime":"2015-08-06T12:22:41CEST",
                    "contract_uuid":"a5124ad1-d506-48d5-9666-78da4c432dc6",
                    "service_name":"Service1",
                    "service_scope":"Service1",
                    "metric_name":"sysUptime",
                    "actual_value":"0"
                }
            ]
            
+ Response 404

        Returned when erroneous data is provided in the call.
        
#### Violation [/violations/{uuid}]

##### Get violation info [GET]

        Retrieves information from a violation identified by the uuid.

+ Parameters

    + uuid (string) - The uuid of the violation.

+ Request

    + Headers
    
            user: basic authentication user
            password: basic authentication password

+ Response 200 (application/xml)

    + Body
    
            <?xml version="1.0" encoding="UTF-8"?>
            <violation>
                <uuid>83e6e958-a34e-4089-9e48-7ebf6fda366a</uuid>
                <contract_uuid>22a548ec-122a-4aa1-b4e1-eee7b79c12b5</contract_uuid>
                <service_name>Service1</service_name>
                <service_scope>Service1</service_scope>
                <metric_name>percDiskUsed</metric_name>
                <datetime>2015-07-28T15:09:29CEST</datetime>
                <actual_value>0.34827652639805917</actual_value>
            </violation>

+ Response 200 (application/json)

    + Body
    
            {
                "uuid":"83e6e958-a34e-4089-9e48-7ebf6fda366a",
                "datetime":"2015-07-28T15:09:29CEST",
                "contract_uuid":"22a548ec-122a-4aa1-b4e1-eee7b79c12b5",
                "service_name":"Service1",
                "service_scope":"Service1",
                "metric_name":"percDiskUsed",
                "actual_value":"0.34827652639805917"
            }
            
+ Response 404

        Returned when the uuid doesn't exist in the database.


### Group Penalties

#### Penalty collection [/penalties{?agreementId}{?guaranteeTerm}{?begin}{?end}]

##### Get penalties [GET]

+ Parameters

    + agreementId (optional, string) - If specified, search the penalties of the agreement with this agreementId.
    + guaranteeTerm (optional, string) - If specified, search the penalties of the guarantee term with this name.
    + begin (optional, string) - If specified, set a lower limit of date of penalties to search. Date format: yyyy-MM-dd'T'HH:mm:ss
    + end (optional, string) - If specified, set an upper limit of date of penalties to search. Date format: yyyy-MM-dd'T'HH:mm:ss

+ Request

    + Headers
    
            user: basic authentication user
            password: basic authentication password

+ Response 200 (application/xml)

    + Body
    
            <?xml version="1.0" encoding="UTF-8"?>
            <penalties>
                <penalty xmlns:sla="http://sla.atos.eu" xmlns:wsag="http://www.ggf.org/namespaces/ws-agreement">
                    <uuid>ec7fd8ec-d917-49a2-ad80-80ff9aa8269c</uuid>
                    <agreement>22a548ec-122a-4aa1-b4e1-eee7b79c12b5</agreement>
                    <datetime>2015-01-21T18:42:00CET</datetime>
                    <definition type="discount" expression="35" unit="%" validity="P1D"/>
                </penalty>
                <penalty xmlns:sla="http://sla.atos.eu" xmlns:wsag="http://www.ggf.org/namespaces/ws-agreement">
                    <uuid>36f45694-c58a-443f-abad-6145206b9faa</uuid>
                    <agreement>a5124ad1-d506-48d5-9666-78da4c432dc6</agreement>
                    <datetime>2015-01-21T18:42:00CET</datetime>
                    <definition type="discount" expression="40" unit="%" validity="P1D"/>
                </penalty>
            </penalties>

+ Response 200 (application/json)

    + Body
    
            {
                [
                    {
                        "uuid":"bfc4bc66-d647-453a-b813-d130f6116daf",
                        "datetime":"2015-01-21T18:49:00CET",
                        "definition":{
                            "type":"discount",
                            "expression":"35",
                            "unit":"%",
                            "validity":"P1D"
                        },
                        "agreement":"22a548ec-122a-4aa1-b4e1-eee7b79c12b5"
                    },
                    {
                        "uuid":"36f45694-c58a-443f-abad-6145206b9faa",
                        "datetime":"2015-01-21T18:49:00CET",
                        "definition":{
                            "type":"discount",
                            "expression":"40",
                            "unit":"%",
                            "validity":"P1D"
                        },
                        "agreement":"a5124ad1-d506-48d5-9666-78da4c432dc6"
                    }
                ]
            }

+ Response 404

        Returned when erroneous data is provided in the call.

#### Penalty [/penalties/{uuid}]

##### Get penalty info [GET]

        Retrieves information from a penalty identified by the uuid.

+ Parameters

    + uuid (string) - The uuid of the penalty.

+ Request

    + Headers
    
            user: basic authentication user
            password: basic authentication password

+ Response 200 (application/xml)

    + Body
    
            <?xml version="1.0" encoding="UTF-8"?>
            <penalty xmlns:sla="http://sla.atos.eu" xmlns:wsag="http://www.ggf.org/namespaces/ws-agreement">
                <uuid>ec7fd8ec-d917-49a2-ad80-80ff9aa8269c</uuid>
                <agreement>agreement-a</agreement>
                <datetime>2015-01-21T18:42:00CET</datetime>
                <definition type="discount" expression="35" unit="%" validity="P1D"/>
            </penalty>
            
+ Response 404

        Returned when the uuid doesn't exist in the database.

## Advanced topics

The operations performed by this software, follow the guidelines defined in the
WS-Agreement standard. It describes a recommendation in both the data
representation and SLA management workflow. To know more about it, please refer
to the [WS-Agreement](sla-core/docs/ws-agreement.md) section.

If you want to further develop this component, please read about its
architecture and how it works internally in the
[SLA Core Developer Manual](sla-core/docs/developer-guide.md) section.

##License##

Licensed under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0)
"# ops.Sla-framework" 
