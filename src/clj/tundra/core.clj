(ns tundra.core
  (:require [tundra.datomic :as datomic]
            [tundra.server :as server])
  (:gen-class))

(defn -main [& _]
  (let [conn (datomic/new-database "datomic:mem://localhost:4334/tundra")]
    (server/start-server "localhost" 3449 conn)))
