# flix-freaks

This repo contains the backend services for a simple movie recommender system. The system makes recommendations based on the popularity of content of
other users as well as tracking user behavior to compute implicit ratings for users. The services are split into seperate packages:
1) analytics: allows admin users to query the popularity of content as well as analyze the behavior of individual users
2) builder: handles requests to build build implicit recommendations
3) collector: logs user behaviors in order to make recommendations
4) flix_freaks: main API to view available movies and recommendations
5) recommender: services to provide personalized recommendations

## Prerequisites
Current development uses the following:
* openJDK 11.0.8 Coretto
* Scala 2.13
* postgresql

## Getting Started
First, modify `resources/application.conf` to include your updated database configuration properties. Once your database connection is setup you
need to populate the tables with data to bootstrap the algorithms. Run all modules in `scripts` to do so. Once the database is setup and the tables populated, each package can be run seperately by running the file in the modules `app` package.

Alternatively, since each module is designed independently, you may choose to configure a seperate `application.conf` file for each module.
