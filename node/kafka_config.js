const { Kafka } = require('kafkajs');
const fs = require('fs');
const process = require('process');
process.env['NODE_TLS_REJECT_UNAUTHORIZED'] = 0;

function config() {
  BROKER = process.env.KAFKA_BOOTSTRAP_SERVERS
  MECHANISM = process.env.SASL_MECHANISM
  USERNAME = process.env.KAFKA_SCRAM_USERNAME
  PASSWORD = process.env.KAFKA_SCRAM_PASSWORD
  CERT = process.env.KAFKA_SASL_TRUSTSTORE_LOCATION

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