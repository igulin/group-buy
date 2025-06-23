(ns group-buy.routes
  (:require [compojure.core :refer [POST]]
            [ring.util.response :as resp]))

(defn auth-routes [db]
  (POST "/register" {params :params}
    (resp/response (auth/register-user! db params)))

  (POST "/login" {params :params}
    (if-let [token (auth/login-user db params)]
      (resp/json {:token token})
      (resp/status 401))))