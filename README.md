# flix-freaks

This repo contains the backend services for a simple movie recommender system. The system makes recommendations based on the popularity of content of
other users as well as tracking user behavior to compute implicit ratings for users. The services are split into seperate packages:
1) analytics: allows admin users to query the popularity of content as well as analyze the behavior of individual users
2) builder: handles requests to build build implicit recommendations
3) collector: logs user behaviors in order to make recommendations
4) flix_freaks: main API to view available movies and recommendations
5) recommender: services to provide personalized recommendations
