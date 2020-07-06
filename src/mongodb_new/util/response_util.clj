(ns mongodb-new.util.response-util)

(defn failResponse
  ([errcode errmsg]
   {:isSuccess false
    :errcode errcode
    :errmsg errmsg
    :result {}})
  ([response]
   (failResponse (:errcode response) (:errmsg response)))
  )

(defn succResponse
  [result]
  {:isSuccess true
   :errcode 0
   :errmsg ""
   :result result})