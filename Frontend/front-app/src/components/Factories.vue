<template>
  <div class="factory-display">
    <header class="status-bar">
      <div v-if="isLoggedIn" class="user-container">
        <img class="user-button" src="/assets/profile.png" style="width: 40px; height: 40px;" @click="showUserDetails" />
        <span class="username">{{ username }}</span>
        <button v-if="isAdmin" class="show-users-button" @click="goToShowUsers">Show Users</button>
      </div>
      <h1>Factory Display</h1>
      <img v-if="isLoggedIn && isCustomer" class="customer-button" src="/assets/cart.png" style="width: 40px; height: 40px;" @click="goToShoppingCart" />
      <img v-if="isLoggedIn && isCustomer" class="customer-button" src="/assets/box.png" style="width: 50px; height: 50px;" @click="goToPurchases" />
      <button v-if="isLoggedIn && isAdmin" class="admin-button" @click="goToRegisterManager">New Manager</button>
      <button v-if="!isLoggedIn" class="login-button" @click="goToLogin">Login</button>
      <button v-if="isLoggedIn && isAdmin" class="admin-button" @click="goToAddFactory">Add Factory</button>
      <button v-if="isLoggedIn" class="login-button" @click="logout">Logout</button>
    </header>
    <div class="search-bar">
      <input v-model="searchFactoryName" placeholder="Factory Name">
      <input v-model="searchChocolateName" placeholder="Chocolate Name">
      <input v-model="searchLocation" placeholder="Location">
      <input v-model="searchAverageGrade" placeholder="Average Grade" type="number" min="1" max="5">
      <button @click="searchFactories">Search</button>
    </div>
    <div class="filter-bar">
      <input v-model="filterChocolateType" placeholder="Chocolate Type">
      <input v-model="filterChocolateCategory" placeholder="Chocolate Category">
      <select v-model="showOpenFactories" id="showOpenFactories">
      <option value="">All Factories</option>
      <option value="true">Open Factories</option>
      <option value="false">Closed Factories</option>
      </select>
      <button @click="applyFilters">Filter</button>
      <div class="sort-controls">
        <label for="sortCriteria">Sort by:</label>
        <select v-model="sortCriteria" id="sortCriteria">
          <option value="name">Factory Name</option>
          <option value="location">Location</option>
          <option value="grade">Average Grade</option>
        </select>
        <select v-model="sortOrder" id="sortOrder">
          <option value="asc">Ascending</option>
          <option value="desc">Descending</option>
        </select>
        <button @click="applySort">Sort</button>
      </div>
    </div>
    <div class="factory-cards">
      <div v-for="(factory, index) in filteredFactories" :key="factory.id" class="factory-card" :style="{ backgroundColor: index % 2 === 0 ? '#FFE4B5' : '#FFC9AD' }">
        <div class="factory-info">
          <div class="factory-logo">
              <img :src="'data:image/jpeg;base64,' + factory.imageString" alt="Factory Image" />
            </div>
          <div class="factory-details">
            <h2>{{ factory.name }}</h2>
            <div class="location-info" v-if="factory.locationInfo">
              <p>{{ factory.locationInfo.address }}</p>
              <p>{{ factory.locationInfo.longitude }}, {{ factory.locationInfo.latitude }}</p>
            </div>
            <p><strong>Status:</strong> {{ factory.status ? 'Open' : 'Closed' }}</p>
            <p><strong>Average Grade:</strong> {{ factory.grade }}</p>
            <div class="button-group">
              <button v-if="isManager" @click="addWorker(factory.id)">Add Worker</button>
              <button v-if="isManager" @click="addChocolate(factory.id)">Add Chocolate</button>
              <button @click="showChocolates(factory.id)">Show Chocolates</button>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>


<script setup>
import axios from 'axios';
import { onMounted, ref, computed } from 'vue';
import { useRouter } from 'vue-router';

const factories = ref([]);
const router = useRouter();
const searchFactoryName = ref('');
const searchLocation = ref('');
const searchAverageGrade = ref(null);
const searchChocolateName = ref('');
const filterChocolateType = ref('');
const filterChocolateCategory = ref('');
const showOnlyOption = ref('all'); 
const sortCriteria = ref('name'); 
const sortOrder = ref('asc'); 
const showOpenFactories = ref('');

const isLoggedIn = ref(false);
const isAdmin = ref(false);
const isManager = ref(false);
const isCustomer = ref(false);
const username = ref(localStorage.getItem("username") || '');
const role = localStorage.getItem("role");

onMounted(() => {
  console.log(localStorage.getItem("token"));
  console.log(localStorage.getItem("role"));
  isLoggedIn.value = checkLoggedIn();
  isAdmin.value = checkAdmin();
  isManager.value = checkManager();
  isCustomer.value = role === 'Customer';
  console.log(isAdmin.value);
  loadFactories();
});

function goToShowUsers() {
  router.push('/users');
}

function addWorker(factoryId) {
  router.push({ path: '/addWorker', query: { factoryId } });
} 

function checkLoggedIn() {
  const token = localStorage.getItem('token');
  return !!token;
}

function checkAdmin() {
  console.log(role);
  return role === 'Administrator';
}

function checkManager() { 
  return role === 'Manager';
}

function goToRegisterManager() {
  router.push('/register');
}

function showUserDetails() {
  router.push('/editUser');
}

function goToShoppingCart() {
  router.push('shoppingCart');
}

function goToPurchases() {
  router.push('purchases');
}

async function loadChocolatesForFactory(factory) {
  try {
    const response = await axios.get(`http://localhost:8080/WebShopAppREST/rest/chocolates/${factory.id}`);
    factory.chocolates = response.data;
  } catch (error) {
    console.error('Error loading chocolates for factory:', error);
  }
}

function loadFactories() {
  axios.get('http://localhost:8080/WebShopAppREST/rest/factories/')
    .then(async response => {
      factories.value = response.data;
      for (const factory of factories.value) {
        try {
          const locationResponse = await axios.get(`http://localhost:8080/WebShopAppREST/rest/locations/findLocation?id=${factory.locationId}`);          
          const location = locationResponse.data;
          factory.locationInfo = {
            longitude: location.longitude,
            latitude: location.latitude,
            address: location.address
          };
          console.log(location.address);
          await loadChocolatesForFactory(factory);
        } catch (error) {
          console.error('Error loading location:', error);
        }
      }
    })
    .catch(error => {
      console.error('Error loading factories:', error);
    });
}

function searchFactories() {
  axios.get('http://localhost:8080/WebShopAppREST/rest/factories/')
    .then(async response => {
      factories.value = response.data;
      for (const factory of factories.value) {
        try {
          const locationResponse = await axios.get(`http://localhost:8080/WebShopAppREST/rest/locations/findLocation?id=${factory.locationId}`);          const location = locationResponse.data;
          factory.locationInfo = {
            longitude: location.longitude, 
            latitude: location.latitude,
            address: location.address
          };
          await loadChocolatesForFactory(factory);
        } catch (error) {
          console.error('Error loading location:', error);
        }
      }
      applyFilters();
    })
    .catch(error => {
      console.error('Error loading factories:', error);
    });
}

function applyFilters() {
  let filtered = factories.value;

  if (searchFactoryName.value) {
    filtered = filtered.filter(factory => factory.name && factory.name.toLowerCase().includes(searchFactoryName.value.toLowerCase()));
  }

  if (searchLocation.value) {
    filtered = filtered.filter(factory => factory.locationInfo && factory.locationInfo.address && factory.locationInfo.address.toLowerCase().includes(searchLocation.value.toLowerCase()));
  }

  if (searchAverageGrade.value !== null) {
    filtered = filtered.filter(factory => factory.grade == searchAverageGrade.value);
  }

  if (searchChocolateName.value) {
    filtered = filtered.filter(factory => {
      if (factory.chocolates && factory.chocolates.length > 0) {
        return factory.chocolates.some(chocolate => chocolate.name && chocolate.name.toLowerCase().includes(searchChocolateName.value.toLowerCase()));
      }
      return false;
    });
  }

  if (filterChocolateType.value) {
    filtered = filtered.filter(factory => {
      if (factory.chocolates && factory.chocolates.length > 0) {
        return factory.chocolates.some(chocolate => chocolate.chocolateType && chocolate.chocolateType.toLowerCase().includes(filterChocolateType.value.toLowerCase()));
      }
      return false;
    });
  }

  if (filterChocolateCategory.value) {
    filtered = filtered.filter(factory => {
      if (factory.chocolates && factory.chocolates.length > 0) {
        return factory.chocolates.some(chocolate => chocolate.chocolateSort && chocolate.chocolateSort.toLowerCase().includes(filterChocolateCategory.value.toLowerCase()));
      }
      return false;
    });
  }

  if (showOpenFactories.value === 'true') {
    filtered = filtered.filter(factory => factory.status);
  } else if (showOpenFactories.value === 'false') {
    filtered = filtered.filter(factory => !factory.status);
  }

  factories.value = filtered;

  
}


function applySort() {
  let sorted = factories.value;

  switch (sortCriteria.value) {
    case 'name':
      sorted.sort((a, b) => {
        const nameA = a.name.toUpperCase();
        const nameB = b.name.toUpperCase();
        if (sortOrder.value === 'asc') {
          return nameA < nameB ? -1 : nameA > nameB ? 1 : 0;
        } else {
          return nameA > nameB ? -1 : nameA < nameB ? 1 : 0;
        }
      });
      break;
    case 'location':
      sorted.sort((a, b) => {
        const locationA = a.locationInfo ? a.locationInfo.address.toUpperCase() : '';
        const locationB = b.locationInfo ? b.locationInfo.address.toUpperCase() : '';
        if (sortOrder.value === 'asc') {
          return locationA < locationB ? -1 : locationA > locationB ? 1 : 0;
        } else {
          return locationA > locationB ? -1 : locationA < locationB ? 1 : 0;
        }
      });
      break;
    case 'grade':
      sorted.sort((a, b) => {
        if (sortOrder.value === 'asc') {
          return a.grade - b.grade;
        } else {
          return b.grade - a.grade;
        }
      });
      break;
    default:
      break;
  }

  factories.value = sorted;
}

function addChocolate(factoryId) {
  router.push({ path: '/addChocolate', query: { factoryId } });
}

function showChocolates(factoryId) {
  router.push({ path: `/factory/${factoryId}/chocolates` });
}

function goToLogin() {
  router.push('/login');
}

function logout() {
  localStorage.removeItem('token'); 
  localStorage.removeItem('username'); 
  localStorage.removeItem('role'); 
  isLoggedIn.value = false; 
  isAdmin.value = false; 
  isManager.value = false;
  router.push('/'); 
}

function goToAddFactory() {
  router.push('/addFactory');
}

const filteredFactories = computed(() => {
  return factories.value;
});
</script>
<style>
body {
  background-color: #dd6755;
}

.show-users-button {
  background-color: #ff6347;
  color: white;
  border: none;
  border-radius: 5px;
  padding: 10px;
  cursor: pointer;
  transition: background-color 0.3s;
  font-size: 1rem;
  margin-left: 10px;
  height: 40px;
}

.factory-display {
  text-align: center;
  padding: 20px;
  min-height: 100vh;
}

.status-bar {
  background-color: #FFC9AD; /* Bisque */
  color: #333;
  padding: 10px;
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.status-bar h1 {
  font-size: 2rem;
  flex-grow: 1;
  text-align: center
}

.search-bar {
  display: flex;
  justify-content: center; /* Center the search bar */
  gap: 10px; /* Add some space between elements */
  margin-bottom: 20px;
}

.user-container {
  display: flex;
  align-items: center;
}

.user-container img {
  cursor: pointer;
  border-radius: 50%;
}

.user-container .username {
  margin-left: 10px; /* Adjust the spacing as needed */
}

.search-bar input {
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  width: 150px; /* Reduce width */
}

.search-bar button {
  padding: 10px;
  background-color: #ff6347; /* Tomato */
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s;
  width: 80px; /* Match width of login button */
  height: 40px; /* Match height of login button */
}

.search-bar button:hover {
  background-color: #ff4500; /* OrangeRed */
}

.login-button {
  background-color: #ff6347; /* Tomato */
  color: white;
  border: none;
  border-radius: 5px;
  padding: 10px;
  cursor: pointer;
  transition: background-color 0.3s;
  font-size: 1rem; 
  width: 80px; 
  height: 40px;
  margin-left: auto; 
}

.login-button:hover {
  background-color: #ff4500; 
}

.admin-button {
  background-color: #ff6347; 
  color: white;
  border: none;
  border-radius: 5px;
  padding: 10px;
  cursor: pointer;
  transition: background-color 0.3s;
  font-size: 1rem;
  width: 120px; 
  height: 40px; 
  margin-right: 10px; 
}

.admin-button:hover {
  background-color: #ff4500; 
}

.user-button {
  color: white;
  border: none;
  border-radius: 5px;
  padding: 10px;
  cursor: pointer;
  transition: background-color 0.3s;
  font-size: 1rem;
  width: 120px; 
  height: 40px;  
}

.user-button:hover {
  background-color: #ff4500; 
}

.customer-button {
  color: white;
  border: none;
  border-radius: 5px;
  padding: 10px;
  cursor: pointer;
  transition: background-color 0.3s;
  font-size: 1rem;
  width: 120px; 
  height: 40px; 
  margin-right: 10px; 
}

.filter-bar {
  display: flex;
  justify-content: center; 
  gap: 10px; 
  margin-bottom: 20px;
}

.filter-bar input {
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  width: 150px; 
  height:20px;
}

.filter-bar label {
  margin: 0 10px;
}

.filter-bar button {
  padding: 10px;
  background-color: #ff6347;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  height:40px;
  width:80px;
  transition: background-color 0.3s;
}

.filter-bar button:hover {
  background-color: #ff4500; 
}

.sort-controls {
  display: flex;
  align-items: center;
  gap: 10px;
}

.sort-controls label {
  margin-right: 10px;
}

.sort-controls select {
  padding: 8px;
  border-radius: 5px;
}

.sort-controls button {
  padding: 10px;
  background-color: #ff6347;
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.sort-controls button:hover {
  background-color: #ff4500; 
}

.factory-cards {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-around;
}

.factory-card {
  width: 300px;
  margin: 20px;
  border: 1px solid #ccc;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  transition: box-shadow 0.3s;
}

.factory-card:hover {
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
}

.factory-info {
  padding: 20px;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.factory-logo img {
  width: 150px;
  height: 150px;
  object-fit: cover;
  border-radius: 50%;
  margin-bottom: 20px;
}

.factory-details {
  text-align: left;
  width: 100%;
}

.factory-details h2 {
  font-size: 1.5rem;
  margin-bottom: 10px;
  text-align: center; 
}

.location-info p {
  margin: 5px 0;
}

.factory-details p {
  margin: 5px 0;
  text-align: center; 
}

.button-group {
  display: flex;
  flex-direction: column;
  width: 100%;
  align-items: center;
}

.button-group input {
  width: 80%;
  margin: 10px 0;
  padding: 10px;
  background-color: #ff6347; 
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.button-group input:hover {
  background-color: #ff4500; 
}

.user img {
  width: 30px;
  height: 30px;
  cursor: pointer;
  border-radius: 50%;
}

</style>