FROM debian:bullseye-slim

RUN apt-get update && apt-get install -y --no-install-recommends ca-certificates && rm -rf /var/lib/apt/lists/*

COPY cpu_stress /usr/local/bin/cpu_stress

CMD ["/usr/local/bin/cpu_stress", "2"]  # Consomme 2 CPU par défaut, modifiable
