(ns tundra.util
  (:require [cognitect.transit :as t])
  (:import [goog.net XhrIo]))

(defn hidden
  [is-hidden]
  (if is-hidden
    #js {:display "none"}
    #js {}))

(defn transit-post
  [url]
  (fn [{:keys [remote]} cb]
    (.send XhrIo url
      (fn [e]
        (this-as this
          (cb (t/read (t/reader :json) (.getResponseText this)))))
      "POST" (t/write (t/writer :json) remote)
      #js {"Content-Type" "application/transit+json"})))
