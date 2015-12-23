(ns tundra.parser
  (:require [om.next :as om]))

(enable-console-print!)


;; Read parser.
;;

(defmulti read-fn om/dispatch)

(defmethod read-fn :default
  [{:keys [state]} k _]
  (let [st @state]
    (if (contains? st k)
      {:value (get st k)}
      {:remote true})))


;; Mutate parser.
;;

(defmulti mutate-fn om/dispatch)

(defmethod mutate-fn :default
  [_ _ _]
  {:remote true})

(defmethod mutate-fn 'answers/create-temp
  [{:keys [state]} _ new-answer]
  (prn :answers/create-temp state new-answer)
  {:action
   (fn []
    (swap! state assoc :answers/temp new-answer))})
