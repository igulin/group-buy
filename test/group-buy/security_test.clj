(ns group-buy.security-test
  (:require [clojure.test :refer :all]
            [group-buy.db.core :as db]
            [next.jdbc :as jdbc]))

(def test-db {:dbtype "h2:mem" :dbname "test" :user "sa" :password ""})

(deftest test-password-security
  (testing "Пароли хранятся в хешированном виде"
    (db/register-user! test-db "security@test.com" "plain-password")
    (let [db-password (-> (jdbc/execute! test-db 
                          ["SELECT password FROM users WHERE email = 'security@test.com'"])
                          first :users/password)]
      (is (not= "plain-password" db-password))
      (is (db/verify-password "plain-password" db-password))))