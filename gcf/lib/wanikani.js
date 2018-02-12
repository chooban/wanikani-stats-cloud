const superagent = require('superagent-bluebird-promise');
const Promise = require('bluebird');
const rootUrl = 'https://www.wanikani.com/api/v2';

const daysBetween = (from, to) => (
  Math.round(Math.abs(from.getTime() - to.getTime()) / 8.64e7)
);

const userResponse = (apiKey) => superagent
  .get(`${rootUrl}/user`)
  .set('Authorization', `Token token=${apiKey}`)
  .promise();

const assignmentsResponse = (apiKey) => superagent
  .get(`${rootUrl}/assignments`)
  .set('Authorization', `Token token=${apiKey}`)
  .promise();

const groupByStage = (assignments) => {
  const stageNames = [
    'Apprentice',
    'Guru',
    'Master',
    'Enlightened',
    'Burned'
  ];

  return stageNames.reduce((acc, key) => {
    acc[key] = assignments.filter(y => y.srs_stage_name.split(/ /)[0] === key).length;
    return acc;
  }, {});
};

const groupByType = (assignments) => {
  const typeNames = [
    'radical',
    'kanji',
    'vocabulary'
  ];

  return typeNames.reduce((acc, key) => {
    acc[key] = assignments.filter(y => y.subject_type === key).length;
    return acc;
  }, {});
};

const getStats = (apiKey) => {
  return Promise.all([userResponse(apiKey), assignmentsResponse(apiKey)])
    .spread((userResp, assignmentsResp) => {
      const from = new Date(userResp.body.data.started_at);
      const days = daysBetween(from, new Date());
      const assignments = assignmentsResp.body.data.map(x => x.data);

      const levels = groupByStage(assignments);
      const types = groupByType(assignments);

      const stats = {
        ts: new Date(),
        level: userResp.body.data.level,
        day: days,
        byStage: levels,
        byType: types
      };

      return stats;
    })
};

exports.getStats = getStats;
