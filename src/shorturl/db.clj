(ns shorturl.db
  (:require [clojure.java.jdbc :as j]
            [shorturl.env :as env]
            [honey.sql :as sql]
            [honey.sql.helpers :refer :all :as h]))

(def mysql-db {:dbtype (env/env :DBTYPE)
               :host (env/env :HOST)
               :dbname (env/env :DBNAME)
               :user (env/env :USER)
               :password (env/env :PASSWORD)})

(def table-name "redirect")

(defn- resolve-table-name []
  (keyword table-name))

(defn- query! [q]
  (j/query mysql-db q))

(defn- insert-query! [q]
  (j/db-do-prepared mysql-db q))

(defn select-all []
  (query! (-> (h/select :*)
              (h/from (resolve-table-name))
              (sql/format))))

(defn insert-record [slug url]
  (insert-query! (-> (h/insert-into (keyword table-name))
                     (h/columns :slug :url)
                     (h/values [[slug url]])
                     (sql/format))))

(defn find-by-slug [slug]
  (-> (query! (-> (h/select :slug :url)
                  (h/from (keyword table-name))
                  (h/where [:= :slug slug])
                  (sql/format)))
      first
      :url))
