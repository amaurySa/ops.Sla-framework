#!/bin/bash

# Lancer le serveur TCP en C en arrière-plan
./tcp_server &

# Lancer Flask
python3 app.py
