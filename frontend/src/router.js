
import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router);


import BookManager from "./components/BookManager"

import RentManager from "./components/RentManager"

import DeliveryManager from "./components/DeliveryManager"


import View from "./components/View"
export default new Router({
    // mode: 'history',
    base: process.env.BASE_URL,
    routes: [
            {
                path: '/books',
                name: 'BookManager',
                component: BookManager
            },

            {
                path: '/rents',
                name: 'RentManager',
                component: RentManager
            },

            {
                path: '/deliveries',
                name: 'DeliveryManager',
                component: DeliveryManager
            },


            {
                path: '/views',
                name: 'View',
                component: View
            },


    ]
})
