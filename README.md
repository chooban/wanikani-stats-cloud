# Wanikani Stats In The Cloud

A companion piece to [the cron job](/chooban/wanikani-stats/) project. It does the same thing,
I just wanted to find out how feasible it is to move simple cron jobs in Google Cloud.

## Setup

Largely inspired by following [this
utorial](https://cloud.google.com/solutions/reliable-task-scheduling-compute-engine). It's a relatively complex setup in
which a cron job is scheduled to invoke a web service which puts a message onto a topic which triggers a cloud function
which queries the stats API and then writes the information to a mongodb instance that's not hosted by Google.

Clearly, this would be much simpler if cron jobs were allowed to put messages onto topics, but that doesn't seem to be a
things. 

## Deployment

### gcf 

This is a Cloud Function which should be deployed under the name `getStats`, triggered b a topic called `getwkstats`. I
have it running with the minimum RAM of 128MB and haven't had any issues with that.

### wk-stats-gatherer

A Java webservice which will puts messages onto the topic to trigger stats requests for each API key saved in the
database.

### gae 

This was a node version of the webservice which I originally wrote before discovering that the "flexible" offering,
required for node apps, doesn't have a free tier.
