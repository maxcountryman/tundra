(ns tundra.middleware
  (:require [cognitect.transit :as transit]
            [ring.util.response :refer [content-type]])
  (:import [java.io ByteArrayOutputStream]))


;; Transit utilities.
;;

(defn- transit-request?
  [req]
  (if-let [ct (:content-type req)]
    (let [m (re-find #"^application/transit\+(json|msgpack)" ct)]
      [(not (empty? m)) (keyword (second m))])))

(defn- write-transit
  [inp t opts]
  (let [baos (ByteArrayOutputStream.)
        w    (transit/writer baos t opts)
        _    (transit/write w inp)
        ret (.toString baos)]
    (.reset baos)
    ret))

(defn- read-transit
  [request {:keys [opts]}]
  (let [[res t] (transit-request? request)]
    (if res
      (if-let [body (:body request)]
        (let [rdr (transit/reader body t opts)]
          (try
            [true (transit/read rdr)]
            (catch Exception ex
              [false nil])))))))


;; Middleware.
;;

(def malformed-transit
  {:status  400
   :headers {"Content-Type" "text/plain"}
   :body    "Malformed Transit in request body."})

(defn wrap-transit-body
  [handler & [{:keys [malformed-response]
               :or {malformed-response malformed-transit}
               :as opts}]]
  (fn [req]
    (if-let [[valid? transit] (read-transit req opts)]
      (if valid?
        (handler (assoc req :body transit))
        malformed-response)
      (handler req))))

(defn- assoc-transit-params
  [req transit]
  (let [req (assoc req :transit-params transit)]
    (if (map? transit)
      (update-in req [:params] merge transit)
      req)))

(defn wrap-transit-params
  [handler & [{:keys [malformed-response]
               :or {malformed-response malformed-transit}
               :as opts}]]
  (fn [req]
    (if-let [[valid? transit] (read-transit req opts)]
      (if valid?
        (handler (assoc-transit-params req transit))
        malformed-response))
    (handler req)))

(defn wrap-transit-response
  [handler & [{:as opts}]]
  (let [{:keys [encoding opts] :or {encoding :json}} opts]
    (assert (#{:json :json-verbose :msgpack} encoding)
            "The encoding must be one of #{:json :json-verbose :msgpack}.")
    (fn [req]
      (let [response (handler req)]
        (if (coll? (:body response))
          (let [transit-response
                (update-in response [:body] write-transit encoding opts)]
            (if (contains? (:headers response) "Content-Type")
              transit-response
              (content-type transit-response
                (format "application/transit+%s" (name encoding)))))
          response)))))
