import { createRouter, createWebHistory } from 'vue-router';
import HomeView from '@/views/HomeView.vue';
import AddChocolateView from '@/views/AddChocolateView.vue';
import FactoryChocolatesView from '@/views/FactoryChocolatesView.vue'; 
import EditChocolateView from '@/views/EditChocolateView.vue';
import Login from '@/views/LoginView.vue';
import Registration from '@/views/RegistrationView.vue';
import AddFactoryView from '@/views/AddFactoryView.vue';
import ManagerRegistration from '@/views/ManagerRegistrationView.vue';
import AddWorkerView from '@/views/AddWorkerView.vue';
import EditUserView from '@/views/EditUserView.vue';
import UsersView from '@/views/UsersView.vue';
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
    path: '/factory/:factoryId/chocolates', 
    name: 'factoryChocolates',
    component: FactoryChocolatesView
  },
  {
    path: '/editChocolate',
    name: 'editChocolate',
    component: EditChocolateView
  },
  {
    path: '/users',
    name: 'users',
    component: UsersView
  },
  {
    path: '/addFactory',
    name: 'addFactory',
    component: AddFactoryView
  },
  {
    path: '/registerManager',
    name: 'ManagerRegistration',
    component: ManagerRegistration
  },
  {
    path: '/addWorker', 
    name: 'AddWorker',
    component: AddWorkerView
  },
  {
    path: '/editUser', 
    name: 'EditUser',
    component: EditUserView
  }
]

const router = createRouter({
  history: createWebHistory("/"),
  routes
});

export default router;
