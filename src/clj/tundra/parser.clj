(ns tundra.parser
  (:require [datomic.api :as d]))


;; Read parser.
;;

(defmulti read-fn (fn [env k params] k))

(defmethod read-fn :default
  [_ k _]
  {:value {:error (str "No handler for " k)}})

(defmethod read-fn :polls/by-id
  [{:keys [conn query]} _ {:keys [id]}]
  (prn "read by-id" id)
  {:value (d/pull @(d/sync conn) (or query '[*]) id)})


;; Mutate parser.
;;

(defmulti mutate-fn (fn [env k params] k))

(defmethod mutate-fn :default
  [_ k _]
  {:value {:error (str "No handler for " k)}})

(defmethod mutate-fn 'polls/create
  [{:keys [conn]} k {:keys [poll/question poll/answers]}]
  (prn "mutate create" question answers)
  (let [tempid (d/tempid conn)]
    {:value tempid
     :action
     (fn []
       @(d/transact conn
          [{:db/id         tempid
            :poll/created  (java.util.Date.)
            :poll/question question
            :poll/answers  (map (fn [text]
                                  {:answer/text text
                                   :answer/votes 0})
                                answers)}]))}))
