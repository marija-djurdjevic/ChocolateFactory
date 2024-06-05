import { createRouter, createWebHistory } from 'vue-router';
import HomeView from '@/views/HomeView.vue';
import AddChocolateView from '@/views/AddChocolateView.vue';
import FactoryChocolatesView from '@/views/FactoryChocolatesView.vue'; 

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView
  },
  {
    path: '/addChocolate',
    name: 'addChocolate',
    component: AddChocolateView
  },
  {
    path: '/factory/:factoryId/chocolates', // Definišite novu rutu
    name: 'factoryChocolates',
    component: FactoryChocolatesView
  }
]

const router = createRouter({
  history: createWebHistory("/"),
  routes
});

export default router;
