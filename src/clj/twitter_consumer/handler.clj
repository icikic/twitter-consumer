(ns twitter-consumer.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.util.response :refer [resource-response response content-type]]
            [ring.middleware.json :as middleware]
            [ring.middleware.defaults :refer [wrap-defaults api-defaults]]
            [twitter.api.restful :as twitter]
            [twitter-consumer.user :refer :all]
            [clojure.data.json :as json]
            [clojure.core.async :refer [<!! >!! <! >! go chan buffer]]))


(defn user-tweets
  "Retrieve user tweets. User is identified by its screen name.
  Number of tweets should be limited using count arugment. If not provided, it defaults to 10."
  [screen-name count]
  (twitter/statuses-user-timeline :oauth-creds (make-creds)
                                  :params {:screen-name screen-name
                                           :count count}))

(defn search-tweets
  "Retrieve tweets filtered by hashtag.
  Number of tweets should be limited using count arugment. If not provided, it defaults to 10."
  [hashtag count]
  (twitter/search-tweets :oauth-creds (make-creds)
                         :params {:q (str "#" hashtag)
                                  :count count}))

(defn safe-get
  "Return x if provided, otherwise return default"
  [x, default]
  (if x
    x
    default))

(defn safe-get-10
  "Return x if provided, otherwise 10"
  [x]
  (safe-get x 10))

(defn reduce-user-tweets
  "Extract only text from retrieved user tweets"
  [response]
  (map #(:text %) (:body response)))

(defn reduce-search-tweets
  "Extract only text from retrieved tweets"
  [response]
  (map #(:text %) (get-in response [:body :statuses])))

; core.async channel to decouple heavy processing from
(def heavy-lift-chan (chan))

(defn lift
  "Start a go process to listen on channel"
  [in]
  (go (while true (println (json/pprint (<! in))))))

(defn do-async
  "Put response on a channel for further processin"
  [response]
  (>!! heavy-lift-chan response))


(defroutes app-routes
           (GET "/tweets/:user"
                [user count]
             (let [response (user-tweets user (safe-get-10 count))]
               ; do the heavy lifting async
               (do-async response)
               (identity {:body (reduce-user-tweets response)})))

           (GET "/tweets"
                [q count]
             (let [response (search-tweets q (safe-get-10 count))]
               ; do the heavy lifting async
               (do-async response)
               (identity {:body (reduce-search-tweets response)})))

           (route/not-found "Oops"))

(lift heavy-lift-chan)

(def app
  (-> app-routes
      (middleware/wrap-json-body)
      (middleware/wrap-json-response)
      (wrap-defaults api-defaults)))
