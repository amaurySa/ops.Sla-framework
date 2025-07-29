#!/bin/bash
#Lance le serveur TCP en arrière-plan, puis Flask.
export PYTHONIOENCODING=utf-8

# Lancer le serveur TCP en arrière-plan
/app/tcp_server &

# Lancer Flask
python3 /app/app.py


