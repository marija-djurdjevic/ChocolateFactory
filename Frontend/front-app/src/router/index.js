import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '@/views/HomeView.vue'
import AddChocolateView from '@/views/AddChocolateView.vue'


const router = createRouter({
  history: createWebHistory("/"),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView
    },
    {
      path: '/addChocolate',
      name: 'addChocolate',
      component: AddChocolateView
    }

  ]
})

export default router