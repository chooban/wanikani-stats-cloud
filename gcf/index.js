require('dotenv').config();

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
  MongoClient.connect(mongoDbUrl, (err, db) => {
    if (err) return callback(err);

    const collection = db.collection(process.env.MONGO_COLLECTION);
    collection.insert([stats]);

    db.close();
    callback();
  });
};

module.exports = {
  getStats
};
