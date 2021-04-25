# Overview
This is a POC project. The intention for this repository is to simulate integration between AWS Cloud Watch as the event trigger, alert and monitoring. 

Meanwhile the service behind private Network is to do some ETL tasks due to some regulations to put sensitive data inside private data center.

# Process Flow
1. AWS Cloud Watch create event trigger to AWS Lambda in our VPC
2. AWS Lambda invoke HTTP(S) POST to our ETL services inside private network
3. ETL Service do some ETL tasks
4. ETL Service store the custom metrics (latency and etc) back to Cloud Watch
5. Cloud Watch will show all the metrics (lambda and custom metrics) in the Dashboard and trigger Alarm if it reaches a certain threshold.
