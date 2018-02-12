const express = require('express');
const PubSub = require('@google-cloud/pubsub');

const app = express();

const projectId = 'wanikani-stats';
const topic = 'getwkstats';
const port = process.env.PORT || 8080;

app.get('/', (req, res) => {
  res.status(200).end();
});

app.get('/tasks/stats', (req, res) => {
  // As per https://cloud.google.com/appengine/docs/flexible/nodejs/scheduling-jobs-with-cron-yaml#validating_cron_requests
  if (!req.header('X-Appengine-Cron')) return res.status(403).end();

  const pubsubClient = new PubSub({ projectId });
  const dataBuffer = Buffer.from(JSON.stringify({
    apiKey: process.env.API_KEY
  }));

  pubsubClient
    .topic(topic)
    .publisher()
    .publish(dataBuffer)
    .then(result => {
      const messageId = result[0];
      console.log(`Message ${messageId} published`);
      res.status(200).end();
    })
    .catch(err => {
      console.error(`ERROR: ${err}`);
      res.status(500).error(err).end();
    });
});

app.listen(port, () => {
  console.log(`Listening on port ${port}`);
});
