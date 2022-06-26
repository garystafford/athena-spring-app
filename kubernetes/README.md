

```shell
docker push garystafford/athena-spring-app:${TAG}

export ACCOUNT_ID=$(aws sts get-caller-identity --output text --query 'Account')
export AWS_REGION="us-east-1"
export CLUSTER_NAME="<your_cluster>"
export NAMESPACE="athena-spring"

kubectl create namespace ${NAMESPACE}

kubectl apply -f kubernetes/secret.yml -n ${NAMESPACE}

eksctl create iamserviceaccount \
    --name athena-spring-app \
    --namespace ${NAMESPACE} \
    --cluster ${CLUSTER_NAME} \
    --attach-policy-arn "arn:aws:iam::${ACCOUNT_ID}:policy/AthenaSpringApp" \
    --approve \
    --override-existing-serviceaccounts

kubectl apply -f kubernetes/kubernetes.yml -n ${NAMESPACE}

kubectl apply -f kubernetes/athena-spring-srv-hpa.yml -n ${NAMESPACE}

kubectl get pods -n ${NAMESPACE} -w
kubectl get all -n ${NAMESPACE}
kubectl get services -n ${NAMESPACE}
```