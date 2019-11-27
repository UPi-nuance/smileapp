# smileapp

`./gradlew assemble`

`docker build -t smileapp .`

`cd kubernetes`

`docker build -f envoy.Dockerfile -t smile/envoy .`

`kubectl apply -f smileapp.k8s.yaml`

`kubectl get pods`

`kubectl describe pods`

`kubectl logs smileapp-deployment-7554dc8b7b-ft9mw smileapp`

Edit the python script to connect to the correct IP address (e.g. "172.17.0.4") instead of localhost.

`./smileclient.py`

`kubectl delete -f smileapp.k8s.yaml`

