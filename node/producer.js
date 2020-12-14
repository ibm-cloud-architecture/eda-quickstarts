const kafka_config = require("./kafka_config")
const process = require('process');

const run = async () => {
    const connection = kafka_config.config();
    const producer = connection.producer()
    const TOPIC = process.env.TOPIC
    console.log("Connecting.....")
    await producer.connect()
    console.log("Connected!")
    msg = {
        "Clicked" : "1"
    }
    const result =  await producer.send({
        topic: TOPIC,
        messages: [
            {
                value: JSON.stringify(msg),
                key : "1"
            }
        ]
    })
    console.log(`Send Successfully! ${JSON.stringify(result)}`)
    await producer.disconnect();
}

run().catch(console.error)
