import { createRouter, createWebHistory } from 'vue-router';
import HomeView from '@/views/HomeView.vue';
import AddChocolateView from '@/views/AddChocolateView.vue';
import FactoryChocolatesView from '@/views/FactoryChocolatesView.vue'; 
import EditChocolateView from '@/views/EditChocolateView.vue';
import Login from '@/views/LoginView.vue';
import Registration from '@/views/RegistrationView.vue';
import AddFactoryView from '@/views/AddFactoryView.vue';
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
    path: '/login',
    name: 'Login',
    component: Login
  },
  {
    path: '/register',
    name: 'Registration',
    component: Registration
  },
  {
    path: '/factory/:factoryId/chocolates', // Definišite novu rutu
    name: 'factoryChocolates',
    component: FactoryChocolatesView
  },
  {
    path: '/editChocolate',
    name: 'editChocolate',
    component: EditChocolateView
  },
  {
    path: '/addFactory',
    name: 'addFactory',
    component: AddFactoryView
  }
]

const router = createRouter({
  history: createWebHistory("/"),
  routes
});

export default router;
