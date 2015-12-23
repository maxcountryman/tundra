(ns tundra.datomic
  (:require [clojure.java.io :as io]
            [datomic.api :as d])
  (:import datomic.Util))

(defn new-database
  [db-uri]
  (d/create-database db-uri)
  (let [s (first (Util/readAll (io/reader (io/resource "data/schema.edn"))))
        c   (d/connect db-uri)]
    @(d/transact c s)
    c))
