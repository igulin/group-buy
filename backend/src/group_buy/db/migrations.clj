(ns group-buy.db.migrations
  (:require [next.jdbc :as jdbc]
            [hugsql.core :as hugsql]))

(defn create-tables! [db]
  (jdbc/execute! db ["
    CREATE TABLE IF NOT EXISTS users (
        id SERIAL PRIMARY KEY,
        email VARCHAR(255) UNIQUE NOT NULL,
        password VARCHAR(255) NOT NULL,
        created_at TIMESTAMP DEFAULT NOW()
    )"])
  
  (jdbc/execute! db ["
    CREATE TABLE IF NOT EXISTS purchase_requests (
        id SERIAL PRIMARY KEY,
        title VARCHAR(255) NOT NULL,
        description TEXT,
        organizer_id INTEGER REFERENCES users(id),
        status VARCHAR(50) DEFAULT 'open',
        created_at TIMESTAMP DEFAULT NOW()
    )"])
  
  (jdbc/execute! db ["
    CREATE TABLE IF NOT EXISTS supplier_prices (
        id SERIAL PRIMARY KEY,
        supplier_id INTEGER REFERENCES users(id),
        request_id INTEGER REFERENCES purchase_requests(id),
        price NUMERIC(10,2) NOT NULL,
        created_at TIMESTAMP DEFAULT NOW()
    )"])
  
  (println "Database tables created"))