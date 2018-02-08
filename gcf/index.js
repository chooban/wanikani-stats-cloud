const getStats = require('./lib/getstats').getStats;

exports.getStatsPs = (event, callback) => {
  const message = JSON.parse((Buffer.from(event.data.data, 'base64').toString()));

  if (!message.apiKey) {
    callback(new Error("No API key provided"));
    return;
  }
  getStats(message.apiKey)
    .then(stats => {
      console.log("Got stats: " + JSON.stringify(stats));
      callback();
    })
    .catch(error => {
      console.error(new Error("Error gettings stats: " + error));
      callback(error);
    });
}

exports.getStatsWs = (req, res) => {
  if (req.body.apiKey === undefined) {
    res.status(400).send('No key defined!');
    return;
  }

  getStats(req.body.apiKey)
    .then(stats => {
      res.status(200).send(stats);
    })
    .catch(error => {
      console.error(error);
      res.status(500).send('Error retrieving stats');
    });
};
