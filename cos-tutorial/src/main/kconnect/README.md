# Update the custom image

## Build 

```
docker build -t quay.io/ibmcase/eda-kconnect-cluster-image .
```


## Verify

```
docker run -ti quay.io/ibmcase/eda-kconnect-cluster-image   bash -c "ls /opt/kafka/plugins"
```