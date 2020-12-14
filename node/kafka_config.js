const { Kafka } = require('kafkajs');
const fs = require('fs');
const process = require('process');
process.env['NODE_TLS_REJECT_UNAUTHORIZED'] = 0;

function config() {
  console.log(process.env);
  BROKER = process.env.BROKER
  MECHANISM = process.env.MECHANISM
  USERNAME = process.env.SCRAM_USERNAME
  PASSWORD = process.env.SCRAM_PASSWORD
  CERT = process.env.CERT
  
  return new Kafka({
    clientId: 'connection-id',
    brokers: [BROKER],
    ssl: true,
    sasl: {
      rejectUnauthorized: false,
        mechanism: MECHANISM,
        username: USERNAME,
        password: PASSWORD
    },
    ssl: {
      cert: fs.readFileSync(CERT, 'utf-8'),
    }
  })
}

module.exports = {config}