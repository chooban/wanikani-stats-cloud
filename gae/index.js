const express = require('express');

const app = express();
const port = process.env.PORT || 8080;

app.get('/tasks/stats', (req, res) => {
  console.log("Should really do stuff");
  res.status(200).end();
});

app.listen(port, () => {
  console.log(`Listening on port ${port}`);
});
