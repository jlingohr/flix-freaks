# flix-freaks

This repo contains the backend services for a simple movie recommender system. The system makes recommendations based on the popularity of content of
other users as well as tracking user behavior to compute implicit ratings for users. The services are split into seperate packages and are designed to run
as microservices in order to prevent long-running queries from bottlenecking the system:
1) analytics: allows admin users to query the popularity of content as well as analyze the behavior of individual users
2) builder: services to build recommendations in an off-line setting
3) collector: logs user behaviors as events from which the builder can build new recommendations
4) flix_freaks: main API to view available movies and movie details
5) recommender: services to provide personalized and non-personalized recommendations. Non-personalized recommendations are based off of popularity and
movie similarity. Personalized recommendations are generated from a user's explicit movie ratings or implicit behavior.

### Note Design Decisions

Resource limitations have led to certain design decisions which I will note here:
1) Rather than use PostgreSQL for all data, it would be more appropriate to store user events in a seperate DB such as Kafka since these events
would come it at a much higher rate and require writes, which could block other services from reading


## Prerequisites
Current development uses the following:
* openJDK 11.0.8 Coretto
* Scala 2.13
* postgresql

## Getting Started
First, modify `resources/application.conf` to include your updated database configuration properties. Once your database connection is setup you
need to populate the tables with data to bootstrap the algorithms. Run all modules in `scripts` to do so. Once the database is setup and the tables populated, each package can be run seperately by running the file in the modules `app` package.

Alternatively, since each module is designed independently, you may choose to configure a seperate `application.conf` file for each module.
