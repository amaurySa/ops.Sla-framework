FROM ubuntu:16.04

RUN apt-get update && \
    apt-get install -y netcat bash && \ #netcat est utilisé pour envoyer des données aux workers.
    rm -rf /var/lib/apt/lists/*

WORKDIR /app

COPY run-control.sh . #le script principal qui envoie des nombres aléatoires aux workers.

RUN chmod +x run-control.sh

EXPOSE 8080

CMD ["bash", "run-control.sh"]


