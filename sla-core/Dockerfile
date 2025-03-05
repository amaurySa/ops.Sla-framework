FROM ubuntu:18.04

# Mettre à jour les paquets de base et installer les dépendances nécessaires
RUN apt-get update && apt-get install -y \
    python2.7 \
    python-pip \
    openjdk-8-jdk \
    mysql-server \
    maven \
    build-essential \
    curl \
    git \
    && rm -rf /var/lib/apt/lists/*

# Définir les variables d'environnement pour Python et Java
ENV PYTHONPATH=/usr/bin/python2.7
ENV JAVA_HOME=/usr/lib/jvm/java-8-openjdk-amd64
ENV PATH=$PATH:$JAVA_HOME/bin

# Vérifier les versions de Python, Java et Maven
RUN python2.7 --version
RUN java -version
RUN mvn -version

# Créer un répertoire de travail
WORKDIR /app

# Copier ton projet dans le conteneur
COPY . /app

# Installer les dépendances Python (si tu as un fichier requirements.txt)
RUN pip2 install --upgrade pip
RUN pip2 install -r requirements.txt

# Construire et installer tous les modules
RUN mvn clean install

# Démarrer MySQL, attendre qu'il soit opérationnel, puis configurer la base de données
RUN service mysql start && \
    sleep 10 && \
    mysql -e "CREATE DATABASE atossla;" && \
    mysql -e "CREATE USER 'atossla'@'localhost' IDENTIFIED BY '_atossla_';" && \
    mysql -e "GRANT ALL PRIVILEGES ON atossla.* TO 'atossla'@'localhost';" && \
    mysql -e "FLUSH PRIVILEGES;"

# Exposer un port (si nécessaire, adapte selon ton projet)
EXPOSE 8080

# Commande pour lancer ton application (ajuste en fonction de ton projet)
CMD service mysql start && bash bin/runserver.sh

