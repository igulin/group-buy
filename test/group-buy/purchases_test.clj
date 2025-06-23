(ns group-buy.purchases-test
  (:require [clojure.test :refer :all]
            [group-buy.db.core :as db]
            [next.jdbc :as jdbc]))

(def test-db {:dbtype "h2:mem" :dbname "test" :user "sa" :password ""})

(defn db-fixture [f]
  ;; Создаем таблицы
  (jdbc/execute! test-db ["
    CREATE TABLE users (
        id SERIAL PRIMARY KEY,
        email VARCHAR(255) UNIQUE NOT NULL,
        password VARCHAR(255) NOT NULL,
        created_at TIMESTAMP DEFAULT NOW()
    )"])
  
  (jdbc/execute! test-db ["
    CREATE TABLE purchase_requests (
        id SERIAL PRIMARY KEY,
        title VARCHAR(255) NOT NULL,
        description TEXT,
        organizer_id INTEGER REFERENCES users(id),
        status VARCHAR(50) DEFAULT 'open',
        created_at TIMESTAMP DEFAULT NOW()
    )"])
  
  (jdbc/execute! test-db ["
    CREATE TABLE supplier_prices (
        id SERIAL PRIMARY KEY,
        supplier_id INTEGER REFERENCES users(id),
        request_id INTEGER REFERENCES purchase_requests(id),
        price NUMERIC(10,2) NOT NULL,
        created_at TIMESTAMP DEFAULT NOW()
    )"])
  
  ;; Добавляем тестовых пользователей
  (jdbc/execute! test-db ["
    INSERT INTO users (email, password) 
    VALUES ('organizer@test.com', 'pass'), 
           ('supplier@test.com', 'pass')"])
  
  (f)
  
  ;; Очистка
  (jdbc/execute! test-db ["DROP TABLE supplier_prices"])
  (jdbc/execute! test-db ["DROP TABLE purchase_requests"])
  (jdbc/execute! test-db ["DROP TABLE users"]))

(use-fixtures :once db-fixture)

(deftest test-purchase-request
  (testing "Создание заявки на закупку"
    (let [organizer-id (-> (jdbc/execute! test-db 
                          ["SELECT id FROM users WHERE email = 'organizer@test.com'"])
                          first :users/id)
          request {:title "Test Purchase" :description "Test description"}
          result (db/create-purchase-request! test-db organizer-id request)]
      (is (contains? result :id))))
  
(deftest test-add-supplier-price
  (testing "Добавление цены поставщиком"
    (let [supplier-id (-> (jdbc/execute! test-db 
                         ["SELECT id FROM users WHERE email = 'supplier@test.com'"])
                         first :users/id)
          request-id (-> (jdbc/execute! test-db 
                         ["SELECT id FROM purchase_requests"])
                         first :purchase_requests/id)
          result (db/add-supplier-price! test-db supplier-id request-id 99.99)]
      (is (contains? result :id))))

(deftest test-view-purchase-request
  (testing "Просмотр заявки с ценами"
    (let [request-id (-> (jdbc/execute! test-db 
                         ["SELECT id FROM purchase_requests"])
                         first :purchase_requests/id)
          result (db/get-purchase-request test-db request-id)]
      (is (contains? result :purchase_requests/id))
      (is (seq (:supplier_prices/prices result)))))