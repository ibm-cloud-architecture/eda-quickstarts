const kafka_config = require("./kafka_config")
const process = require('process');

async function run(){
    try
    {
        const config = kafka_config.config()
        const consumer = config.consumer({groupId: "consumer-id"})
        
        console.log("Connecting...")
        await consumer.connect()
        console.log("Connected!!!")
        TOPIC = process.env.KAFKA_TOPIC
        console.log(`Subscribed to topic ${KAFKA_TOPIC}`)
        await consumer.subscribe({
            topic: TOPIC,
            // fromBeginning: true
        })
        
        await consumer.run({
            "eachMessage": async result => {
                console.log(`Recived message ${result.message.value} with key: ${result.message.key}`)
            }
        })
    }
    catch(exception)
    {
        console.error(`Some problem occured: ${exception}`)
    }
}

run();