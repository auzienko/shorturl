(ns shorturl.core (:require [ring.adapter.jetty :as ring-jetty]
                            [reitit.ring :as ring]
                            [ring.util.response :as r]
                            [muuntaja.core :as m]
                            [shorturl.db :as db]
                            [shorturl.slug :as s]
                            [reitit.ring.middleware.muuntaja :as muuntaja]))

;; https://github.com/alndvz/vid4/blob/master/src/vid4/core.clj

(defn- redirect-hadler [req]
  (let [slug (get-in req [:path-params :slug])
        url (db/find-by-slug slug)]
    (if url
      (r/redirect url 307)
      (r/not-found "Not found"))))

(defn- create-redirect-hadler [req]
  (let [url (get-in req [:body-params :url])
        slug (s/create-slug)]
    (db/insert-record slug url)
    (r/response (str "Create slug " slug))))

(def app
  (ring/ring-handler
   (ring/router
    ["/"
     [":slug/" redirect-hadler]
     ["api/"
      ["redirect/" {:post create-redirect-hadler}]]
     ["" {:handler (fn [req] {:body "hello" :status 200})}]]
    {:data {:muuntaja m/instance
            :middleware [muuntaja/format-middleware]}})))

(defn start []
  (ring-jetty/run-jetty #'app {:port  3000
                               :join? false}))

(def server (start))

(.stop server)