(ns tundra.core
  (:require [goog.dom :as gdom]
            [om.dom :as dom]
            [om.next :as om :refer-macros [defui]]
            [tundra.answer :as answer]
            [tundra.parser :as p]
            [tundra.util :as util]))

(enable-console-print!)


(defn answer
  [poll name placeholder]
  (dom/input #js {:name name
                  :ref name
                  :placeholder placeholder
                  :onKeyDown (fn [_]
                               (prn (.-value (dom/node poll name)))
                               (om/transact! poll
                                 `[(answers/create-temp
                                     {:name ~name
                                      :placeholder ~placeholder})]))}))

(defn main
  [poll {:keys [answers/temp] :as props}]
  (prn :node (dom/node poll "app"))
  (apply dom/section nil
    (if (empty? temp)
      (list (answer poll "answer-0" "An answer."))
      (map answer temp))))

(defui Poll
  static om/IQuery
  (query [this]
    [:page/create :poll/answers])

  Object
  (render [this]
    (let [{{:keys [title]} :page/create :as props} (om/props this)]
      (dom/div nil
        (dom/header #js {:id "header"}
          (dom/h1 nil title))
        (dom/input
          #js {:name "new-question"
               :placeholder "What's the question?"
               :onKeyDown #(do %)})
        (main this props)
        (dom/button nil "create poll")))))

(def reconciler
  (om/reconciler
    {:state     (atom {:page/create {:title "new poll"}})
     :normalize true
     :parser    (om/parser {:read p/read-fn :mutate p/mutate-fn})
     :send      (util/transit-post "/api")}))

(om/add-root! reconciler
  Poll (gdom/getElement "app"))
