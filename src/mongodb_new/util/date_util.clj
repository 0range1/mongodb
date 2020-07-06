(ns mongodb-new.util.date-util
  (:import (java.time LocalDateTime)
           (java.time.format DateTimeFormatter)))

(defn now []
  (.format (DateTimeFormatter/ofPattern "yyyy-MM-dd HH:mm:ss") (LocalDateTime/now)))

(defn createdAt [data]
  (assoc data :created_at (now)))

(defn updatedAt [data]
  (assoc data :updated_at (now)))


