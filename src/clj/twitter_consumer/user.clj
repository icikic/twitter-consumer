(ns twitter-consumer.user
  (:require
    [slingshot.slingshot :refer :all]
    [twitter.oauth :as oauth])
  (:import
    (java.util Properties)))

(defn load-config-file
  "Load properties file from classpath"
  [file-name]
  (let [file-reader (.. (Thread/currentThread)
                        (getContextClassLoader)
                        (getResourceAsStream file-name))
        props (Properties.)]
    (.load props file-reader)
    (into {} props)))

;; load app consumer key and secret from config file ;;
(def ^:dynamic *config* (load-config-file "oauth.properties"))

(defn assert-get
  "Get property value or throw exception"
  [key-name]
  (or (get *config* key-name)
      (throw+ (Exception. (format "please define %s in the resources/oauth.properties file" key-name)))))

(def app-consumer-key (assert-get "app.consumer.key"))
(def app-consumer-secret (assert-get "app.consumer.secret"))
(def user-access-token (assert-get "user.access.token"))
(def user-access-token-secret (assert-get "user.access.token.secret"))

;; make oauth credits with app consumer data and user access tokens
(defn make-creds []
  (oauth/make-oauth-creds app-consumer-key
                          app-consumer-secret
                          user-access-token
                          user-access-token-secret))
