FROM clojure:lein-2.10.0
WORKDIR /app
COPY . /app
RUN lein deps
CMD ["lein", "ring", "server-headless"]