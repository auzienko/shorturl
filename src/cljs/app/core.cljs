(ns app.core
  (:require [helix.core :refer [defnc $]]
            [helix.hooks :as hooks]
            [helix.dom :as d]
            ["react-dom/client" :as rdom]
            [promesa.core :as p]))

            ;; define components using the `defnc` macro

(defnc app []
  (let [[state set-state] (hooks/use-state {:slug nil
                                            :url ""})
        fetch-slug (fn []
                     (p/let [_response (js/fetch "/api/redirect/" (clj->js {:headers {:Content-type "application/json"}
                                                                            :method "POST"
                                                                            :body (js/JSON.stringify #js {:url (:url state)})}))
                             response (.json _response)
                             data (js->clj response :keywordize-keys true)]
                       (set-state assoc :slug  (:slug data))))
        redirect-link (str (.-origin js/location) "/" (:slug state) "/")]
    (d/div {:class-name "bg-pink-100 grid place-items-center h-screen"}
     (if (:slug state)
       (d/div (d/a {:href redirect-link} redirect-link))
       (d/div (d/input {:value (:url state)
                        :on-change #(set-state assoc :url (.. % -target -value))
                        :class-name "form-control border py-2 px-4 border-solid border-gray-600"
                        :placeholder "Enter url"})
              (d/button {:on-click #(fetch-slug)
                         :class-name "border-2 rounded py-2 px-4 uppercase"} "Shorten url"))))))

            ;; start your app with your favorite React renderer
(defonce root (rdom/createRoot (js/document.getElementById "app")))


(defn ^:export init []
  (let []
    (.render root ($ app))))
