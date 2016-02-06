(ns betbot.util.clj-time-cheshire
  (:require [cheshire.generate :refer [add-encoder]]
            [clj-time.coerce :as coerce])
  (:import (org.joda.time DateTime)))

;; Add encoder for joda DateTime from clj-time
(add-encoder DateTime (fn [c jsonGenerator]
                        (.writeString jsonGenerator (coerce/to-string c))))

