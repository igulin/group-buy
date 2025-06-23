(ns group-buy.core
  (:require [rum.core :as rum]
            [cljs-ajax.core :as ajax]))


(rum/defcs registration-form <
  (rum/local "" :email)
  (rum/local "" :password)
  [state]
  (let [email (:email state)
        password (:password state)]
    [:div.form
      [:input {:type "email"
               :value @email
               :on-change #(reset! email (-> % .-target .-value))}]
      [:input {:type "password"
               :value @password
               :on-change #(reset! password (-> % .-target .-value))}]
      [:button {:on-click #(ajax/POST "/register"
                         {:params {:email @email :password @password}
                          :handler (fn [resp] (js/console.log resp))})}
        "Sign Up"]]))