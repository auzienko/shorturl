(ns shorturl.env)

(def envars (clojure.edn/read-string (slurp "env.edn")))

(defn env [k]
  (or
   (envars k)
   (System/getenv (name k))))
