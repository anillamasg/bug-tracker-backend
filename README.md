# Bug Tracker Application

> BugTracker is a Spring boot multi-module maven project designed and developed in Microservice Architecture.
> BugTracker supports both Docker and Kubernetes (Minikube).
> BugTracker is a test project for self-development.

## Steps to run the application in Docker.

> 1. Make sure Docker is running in local.
> 2. Open terminal and go to project root folder "bugTracker".
> 3. Run "docker-compose up -d".
> 4. All the microservices should run at this point.
> 5. Either use Postman or Frontend to trigger the endpoints.
> 6. To stop all the containers, run "docker-compose down".

## Steps to run the application in Kubernetes (Minikube). 

> 1. Add the following to "/etc/hosts".
   127.0.0.1	authentication.bugtracker.com
   127.0.0.1	profile.bugtracker.com
   127.0.0.1	project.bugtracker.com
   127.0.0.1	ticket.bugtracker.com
   127.0.0.1	userproject.bugtracker.com
> 2. Make sure Docker is running in local.
> 3. Make sure Minikube and kubectl is installed in local.
> 4. Open terminal and run "minikube start --memory=4g".
> 5. Once minikube is started, run "sudo minikube tunnel".
> 6. Create a new terminal tab and go to "bugTracker/k8s/minikube".
> 7. Run all the pods and its components.
> 8. To do so, run "kubectl apply -f bootstrap/postgres".
> 9. Run the above command for all the bootstrap and services components.
> 10. Once all the pods are running, deploy bugTracker frontend application to browse. 

## Postman 

Import Bug Tracker.postman_collection.json from the project root directory to Postman.