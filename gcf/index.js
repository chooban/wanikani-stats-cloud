require('dotenv').config();

const MongoClient = require('mongodb').MongoClient;

const getWkStats = require('./lib/wanikani').getStats;

const mongoDbUrl = `mongodb://${process.env.MONGO_USER}:${process.env.MONGO_PASS}@${process.env.MONGO_HOST}:${process.env.MONGO_PORT}/${process.env.MONGO_DBNAME}`;

const getStats = (event, callback) => {
  const message = JSON.parse((Buffer.from(event.data.data, 'base64').toString()));

  if (!message.apiKey) {
    callback(new Error("No API key provided"));
    return;
  }

  getWkStats(message.apiKey)
    .then(saveStatsToMongo.bind(this, callback))
    .catch(error => {
      console.error(new Error("Error gettings stats: " + error));
      callback(error);
    });
}

const saveStatsToMongo = (callback, stats) => {
  MongoClient.connect(mongoDbUrl, (err, client) => {
    if (err) return callback(err);
    const db = client.db(process.env.MONGO_DBNAME);
    const collection = db.collection(process.env.MONGO_COLLECTION);
    collection.insert([stats]);

    client.close(callback);
  });
};

module.exports = {
  getStats
};
