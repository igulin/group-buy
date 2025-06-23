(ns group-buy.api-test
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [group-buy.web.routes :as routes]
            [cheshire.core :as json]))

(deftest test-api-endpoints
  (testing "Регистрация пользователя через API"
    (let [response (routes/app-routes 
                   (mock/json-body 
                    (mock/request :post "/register") 
                    {:email "api@test.com" :password "api-pass"}))]
      (is (= 200 (:status response)))
      (is (contains? (json/parse-string (:body response) "ok"))))
  
  (testing "Создание заявки на закупку"
    ;; Сначала логинимся
    (let [login-resp (routes/app-routes 
                     (mock/json-body 
                      (mock/request :post "/login") 
                      {:email "organizer@test.com" :password "pass"}))
          token (-> (json/parse-string (:body login-resp)) (get "token"))
          
          ;; Создаем заявку с токеном
          purchase-req (-> (mock/json-body 
                           (mock/request :post "/purchases")
                           {:title "API Purchase" :description "API test"})
          purchase-req-with-token (assoc-in purchase-req [:headers "Authorization"] (str "Bearer " token))
          response (routes/app-routes purchase-req-with-token)]
      
      (is (= 200 (:status response)))
      (is (contains? (json/parse-string (:body response) "id")))))