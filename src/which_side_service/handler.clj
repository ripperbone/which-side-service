(ns which-side-service.handler
  (:require [compojure.core :refer :all]
            [compojure.handler :refer [site]]
            [compojure.route :as route]
            [ring.adapter.jetty :as jetty]
            [environ.core :refer [env]]
            ;[ring.middleware.reload :refer [wrap-reload]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [ring.middleware.json :refer [wrap-json-response]]
            [ring.util.response]
            [cheshire.core :as json]
            [clj-time.core]
            [clj-time.local]))

(defn is-even [number]
  (= (mod number 2) 0))

(defn day-of-month-today []
  (clj-time.core/day (clj-time.local/local-now)))

(defn day-of-month-tomorrow []
  (clj-time.core/day
    (clj-time.core/plus (clj-time.local/local-now) (clj-time.core/days 1))))


(defn which-day []
  ; do I care about parking my car on the correct side for today or
  ; should I park it on the opposite side in preparation for tomorrow?
  ; let's say, midnight-noon = today, after noon = tomorrow
  (if
    (> (clj-time.core/hour (clj-time.local/local-now)) 12)
    (day-of-month-tomorrow)
    (day-of-month-today)))

(defn which-side [is-house-even day-of-month]
  (if
    (or
      (and is-house-even (is-even day-of-month))
      (and (not is-house-even) (not (is-even day-of-month))))
    "in front of house."
    "across the street."))

(defroutes app-routes
  (GET "/" []
    (ring.util.response/content-type
      (ring.util.response/response
         "If house number is even, use /even. If house number is odd, use /odd.")
         "text/plain"))

  (GET "/json" []
     (ring.util.response/content-type
        (ring.util.response/response
           (json/generate-string {
                :Date (clj-time.local/local-now)
                :Day (which-day)
                :HouseEven (which-side true (which-day))
                :HouseOdd (which-side false (which-day))
             }))
           "application/json"))


  (GET "/:house-side{(even|odd)}" [house-side]
    (ring.util.response/content-type
      (ring.util.response/response
        (which-side (= house-side "even") (which-day)))
      "text/plain"))

  (route/not-found
    (ring.util.response/content-type
      (ring.util.response/response "Not Found")
      "text/plain")))


(def app
  (wrap-defaults app-routes site-defaults))

; https://stackoverflow.com/questions/46859881/clojure-encode-joda-datetime-with-ring-json
(extend-protocol cheshire.generate/JSONable
   org.joda.time.DateTime
   (to-json [dt gen]
      (cheshire.generate/write-string gen (str dt))))

;(def reloadable-app
;   (wrap-reload #'app))

(defn -main [& [port]]
  (let [port (Integer. (or port (env :port) 3000))]
    (jetty/run-jetty (site #'app) {:port port :join? false})))
