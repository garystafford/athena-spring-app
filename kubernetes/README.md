# Kubernetes Notes

```shell
# update three base64-encoded values in the Kubernetes Secret file first
# update the NAMED_QUERY_ID value in the Kubernetes deployment file first

export ACCOUNT_ID=$(aws sts get-caller-identity --output text --query 'Account')
export AWS_REGION="us-east-1"
export CLUSTER_NAME="<your_cluster>"
export NAMESPACE="athena-spring"

kubectl create namespace ${NAMESPACE}

kubectl apply -f kubernetes/secret.yml -n ${NAMESPACE}

kubectl apply -f kubernetes/kubernetes.yml -n ${NAMESPACE}

kubectl apply -f kubernetes/athena-spring-srv-hpa.yml -n ${NAMESPACE}

kubectl get pods -n ${NAMESPACE} -w

kubectl get all -n ${NAMESPACE}
kubectl get services -n ${NAMESPACE}
```