(ns shorturl.slug)

(def charset "QWERTYUIOPASDFGHJKLZXCVBNM1234567890")

(defn create-slug []
  (apply str (take 4 (repeatedly (fn [] (rand-nth charset))))))

