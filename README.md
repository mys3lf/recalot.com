# recalot.com
Generic RESTful Recommender Service

The idea behind recalot.com is to build a RESTFul recommender system that can be used for all sorts of applications. Currently the system is in alpha phase and documentation and tutorials are coming soon.

If you have any comments or suggestions for improvement, feel free to contact us <info@recalot.com>

# Architecture Overview

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
## Connect Data Source

Before the training of a recommender it is necessary to connect a data source. You can use one of the predefined data source connectors or write your own one. So far the following data source connectors exists:
- MySQL Connector
- MovieLens File Connector

More connectors are coming soon. 


## Train Recommender
coming soon
## Run Offline Experiment
coming soon
## Start Online Experiment

coming soon

# Advanced Documentation
coming soon

# Todos

 - Write Tests
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