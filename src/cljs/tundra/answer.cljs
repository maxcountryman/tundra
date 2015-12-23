(ns tundra.answer
  (:require [clojure.string :as string]
            [om.dom :as dom]
            [om.next :as om :refer-macros [defui]]))

(def ESCAPE_KEY 27)
(def ENTER_KEY 13)

(defn submit
  [c {:keys [db/id poll/question] :as props} e]
  (let [edit-text (string/trim (or (om/get-state c :edit-text) ""))]
    (when-not (= edit-text question)
      (om/transact! c
        (cond-> '[(poll/cancel-edit)]
          (= :temp id)
          (conj '(polls/delete-temp))

          (and (not (string/blank? edit-text))
               (not= edit-text question))
          (into
            `[(poll/update {:db/id ~id :poll/title ~edit-text})
              '[:polls/by-id ~id]]))))
    (doto e (.preventDefault) (.stopPropagation))))

(defn edit
  [c {:keys [db/id poll/question] :as props}]
  (om/transact! c `[(todo/edit {:db/id ~id})])
  (om/update-state! c merge {:needs-focus true :edit-text question}))

(defn key-down [c {:keys [todo/title] :as props} e]
  (condp == (.-keyCode e)
    ESCAPE_KEY
      (do
        (om/transact! c '[(todo/cancel-edit)])
        (om/update-state! c assoc :edit-text title)
        (doto e (.preventDefault) (.stopPropagation)))
    ENTER_KEY
      (submit c props e)
    nil))

(defn change [c e]
  (om/update-state! c assoc
    :edit-text (.. e -target -value)))


;; Poll Answer.

(defui PollAnswer
  static om/Ident
  (ident [this {:keys [db/id]}]
    [:answer/by-id id])

  static om/Query
  (query [this]
    [:db/id :poll/editing :answer/text])

  Object
  (render [this]
    (let [props (om/props this)
          {:keys [poll/editing]} props])
    (dom/div nil "answer")))

(def answer (om/factory PollAnswer {:keyfn :db/id}))
