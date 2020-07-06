(ns mongodb-new.util.readedn
  (:require [clojure.java.io :as cio]
            [clojure.edn :as edn]))

(defn load-edn-config
  [edn-filename]
  (with-open [in-edn (-> edn-filename
                         cio/resource
                         cio/reader
                         (java.io.PushbackReader.))]
    (edn/read in-edn)))
