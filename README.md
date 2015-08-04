# recalot.com
Generic RESTful Recommender Service

The ideas behind recalot.com are to build a RESTFul recommender system that can be used for all sorts of applications, can easily be extended, can be used for offline as well as online evaluations and can be adjusted for scientific as well as industrial applications. Therefor we decide to build the API with the help of Apache Felix, which is an OSGi Framework. This allows a modular development, modules can easily be replaced in runtime and modules with new functionality can easily be added to the system. Currently the system is in alpha phase and documentation and tutorials are coming soon.

If you have any comments or suggestions for improvement, feel free to contact us <info@recalot.com>

# Architecture Overview
The architecture of recalot.com is following the [MVC pattern](https://en.wikipedia.org/wiki/Model%E2%80%93view%E2%80%93controller) and distinguishes furthermore
between data, recommendations and evaluation affiliation. 

![recalot.com Architecture](https://raw.githubusercontent.com/mys3lf/recalot.com/master/docs/imgs/architecture.png)

The API consists of the following OSGi bundles:

| Bundle  | Description | MVC | Type |
| ------------- | ------------- | ------------- | ------------- |
| **com.recalot.common**  | Contains all interface definitions, helpers and common used classes | Global | Global |  
| **com.recalot.controller.data** | Contains the controller that is responsible for data requests. | Controller | Data | 
| **com.recalot.controller.experiments** | Contains the controller that is responsible for evaluation requests.  | Controller | Evaluation |
| **com.recalot.controller.recommendations** | Contains the controller that is responsible for recommendations requests. | Controller | Recommendations |  
| **com.recalot.model.data.access** | Does the actual "data" work. Listens to new service of data builders. Delegate data source connection processes. Stores data source instances.  | Model | Data |  
| **com.recalot.model.data.connections** | Contains a few data source builders. | Model | Data|  
| **com.recalot.model.experiments.common** | Contains helpers and common used classes for the experiment model classes | Model | Evaluation |  
| **com.recalot.model.experiments.metrics** | Contains experiments metrics. | Model | Evaluation |   
| **com.recalot.model.rec.access** | Does the actual "recommendation" work. Listens to new servicse of recommender builder. Delegate all kind of recommendation computation to the right recommendation instance. Stores recommendation instances.  | Model | Recommendations |  
| **com.recalot.model.rec.recommender** | Contains recommendation algorithms. | Model | Recommendations |  
| **com.recalot.templates** | Contains output templates. | Global | Global |   
| **com.recalot.views.common** | Contains helpers and common used classes for the view classes  | View | Global |  
| **com.recalot.views.data.access** | Allows to access data via REST | View | Data |  
| **com.recalot.views.data.sources** |  Allows to access, connect and delete data sources via REST  | View | Data |  
| **com.recalot.views.data.tracking** | Allows to save tracking information via REST  | View | Data |  
| **com.recalot.views.experiments** |  Allows to perform experiments via REST  | View | Experiments |  
| **com.recalot.views.recommend** | Allows to compute recommendations via REST  | View | Recommendations|  
| **com.recalot.views.recommend.train** | Allows to train recommender algorithms via REST | View | Recommendations |  

Furthermore the following OSGi bundles are also part of the project, but represent unit tests, tools or demos.

| Bundle   | Description |  
|-|-|
| **com.recalot.portal** | A user interface that allows the call a lot of the API functionality. |
| **com.recalot.demos.wallpaper** | A wallpaper recommendations website. |
| **com.recalot.repo** | The Recalot.com OSGi Repository |
| **com.recalot.unittests** | Some unittests |



| First Header  | Second Header | Second Header | Second Header |
| ------------- | ------------- | ------------- | ------------- |
| Content Cell  | Content Cell  | Content Cell  | Content Cell  |
| Content Cell  | Content Cell  | Content Cell  | Content Cell  |


# Getting Started

## Quick Install

The quick install quide will show how to install the "Experiments Portal" bundle, that comes with all available bundles, with an minimal effort. However, we would recommend to install the following bundles as well:
- Apache Felix File Install (org.apache.felix.fileinstall)
- Apache Felix Web Console (org.apache.felix.webconsole)

### Step-by-Step Guide

-   Download [Apache Felix](http://felix.apache.org/downloads.cgi) 
-   Run Felix in your favorite bash
    ```sh
    java -jar bin\felix.jar 
    ```
- Install the following bundles:
    -   Metatype (org.apache.felix.metatype)
        ```sh
        obr:deploy org.apache.felix.metatype
        ```
    -   Jetty (org.apache.felix.http.jetty)
        ```sh
        obr:deploy  org.apache.felix.http.jetty
        ```
- Add the [recalot.com Repository](http://api.recalot.com/repo/repo.xml)
    ```sh
    obr:repos add http://api.recalot.com/repo/repo.xml
    ```
- Install "Recalot.com - Experiments Portal" (com.recalot.portal)
    ```sh
    obr:deploy com.recalot.portal
    ```
- Take a look at the bundles and start everything you need.
    -   List Bundles:
        ```sh
        lb
        ```
    -   Start Bundles:
        ```sh
        start {bundle-id}
        ```

The webservice uses a jetty as a webserver and under the default configuration the jetty listens to port 8080 on your local machine. Try to call http://localhost:8080/sources to list all data sources, that are available, connected or are currently connecting. The default output format is json. 
## Tools

Take a look at ```/portal/```. You will there a solution with an overview of a variety of possible API requests. I did not put much effort in the building of the portal. It was just a testing device for me, but works pretty good so i uploaded it as well. 

## Connect Data Source

Before the training of a recommender it is necessary to connect a data source. You can use one of the predefined data source builders or write your own one. So far the following data source builders exists:
- MySQL Source Builder
- MovieLens File Source Builder

More builders are coming soon. Each builder has this own configurations. Before you connect a data source you can access the configuration of the builder by sending a GET request to ``/sources/{data-source-id}``.  The result contains an configuration object with an array of configuration items. e.g. MySQL Data Builder:
```js
configuration: [
{key: "sql-database", requirement: "Required", type: "String", value: ""},
{key: "sql-password", requirement: "Required", type: "String", value: ""},
{key: "sql-username", requirement: "Required", type: "String", value: ""},
{key: "data-builder-id", requirement: "Hidden", type: "String", value: "mysql"},
{key: "source-id", requirement: "Required", type: "String", value: ""},
{key: "sql-server", requirement: "Required", type: "String", value: ""}
]
```
For a detailed description of the data schema take a look at the swagger documention. Link coming soon. 

This configuration object shows which configuration items the data source needs. The field "requirement" provides information about the requirement of the configuration item. Items with the value "Hidden" and "Required" are required and must be send within the request body. For the connection of a data source, send a PUT request with the necessary configuration items to ```/sources``` e.g. MySQL:

```
Request URL:http://localhost:8080/sources
Request Method:PUT
Form Data
    sql-database:default
    sql-password:password
    sql-username:admin
    data-builder-id:mysql
    source-id:chosenid
    sql-server:mysql://localhost:3306
```

The MySQL Data Source will automaticly create all necessary tables. Take a look at the "Experiments Portal" (```\portal\```), a lot of request can be done there with help of an user interface. 

## Train Recommender
coming soon
## Run Offline Experiment
coming soon
## Start Online Experiment

coming soon

# Advanced Documentation
coming soon

# Todos

 - Write more Tests
 - Write more documentation and tutorials
 - Add Code Comments
 - Add more functionality

# License

Copyright 2015 Matthäus Schmedding

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

## Used Software
-   TROVE
    [Website](http://trove.starlight-systems.com)
    [License](http://trove.starlight-systems.com/license)
-   Uncommons Maths
    [Website](http://maths.uncommons.org/)
    [License](http://www.apache.org/licenses/LICENSE-2.0.html)
-   Apache Mahout
    [Website](http://mahout.apache.org/)
    [License](http://www.apache.org/licenses/LICENSE-2.0)
-   Recommender101
    [Website](http://ls13-www.cs.tu-dortmund.de/homepage/recommender101/index.shtml)
    [License](http://us.codeforge.com/read/239649/license.txt__html)


