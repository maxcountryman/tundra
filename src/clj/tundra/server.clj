(ns tundra.server
  (:require [clout.core :refer [route-matches]]
            [om.next.server :as om]
            [ring.adapter.jetty :refer [run-jetty]]
            [ring.middleware.resource :refer [wrap-resource]]
            [ring.util.response :refer [resource-response]]
            [tundra.middleware :refer [wrap-transit-body
                                       wrap-transit-response
                                       wrap-transit-params]]
            [tundra.datomic :as datomic]
            [tundra.parser :as parser]))


;; Handlers.
;;

(defn index [req]
  (assoc (resource-response (str "html/index.html") {:root "public"})
    :headers {"Content-Type" "text/html"}))

(defn generate-response
  [data & [status]]
  {:status  (or status 200)
   :headers {"Content-Type" "application/transit+json"}
   :body    data})

(defn api
  [req]
  (generate-response
    ((om/parser {:read parser/read-fn :mutate parser/mutate-fn})
      {:conn (:datomic-connection req)} (:transit-params req))))

(defn wrap-connection
  [handler conn]
  (fn [req]
    (handler (assoc req :datomic-connection conn))))

;; Application routes.
;;

(defn handler
  [{:keys [request-method] {:strs [accept]} :headers :as req}]
  (condp route-matches req  ;; TODO: Method matching.
    "/api" (api req)
    "/"    (index req)
    req))


;; Handler.
;;

(defn tundra-handler
  [conn]
  (-> (wrap-connection handler conn)
      wrap-transit-params
      wrap-transit-response
      (wrap-resource "public")))

(defn dev-handler
  [req]
  (let [conn (datomic/new-database "datomic:mem://localhost:4334/tundra")]
    ((tundra-handler conn) req)))


;; HTTP server.
;;

(defn start-server
  [host port conn]
  (prn "Starting API server...")
  (run-jetty (tundra-handler conn)
             {:host host :port port :join? false}))
