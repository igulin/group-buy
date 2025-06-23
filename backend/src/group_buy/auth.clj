(ns group-buy.auth
  (:require [buddy.hashers :as hashers]
            [next.jdbc :as jdbc]
            [hugsql.core :as hugsql]))

(hugsql/def-db-fns "sql/auth.sql") ; Загрузка SQL-запросов

(defn register-user!
  [db {:keys [email password]}]
  (if-let [existing (get-user-by-email db {:email email})]
    {:error "User already exists"}
    (let [hashed (hashers/derive password)]
      (create-user! db {:email email :password hashed})
      {:ok "User created"})))

(defn login-user
  [db {:keys [email password]}]
  (if-let [user (get-user-by-email db {:email email})]
    (when (hashers/check password (:password user))
      {:token (generate-jwt user)}) ; Генерация JWT через Buddy
    {:error "Invalid credentials"}))