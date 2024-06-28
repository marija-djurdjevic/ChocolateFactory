<template>
  <div class="factory-display">
    <header class="status-bar">
      <h1>Factory Display</h1>
      <button v-if="!isLoggedIn" class="login-button" @click="goToLogin">Login</button>
      <button v-if="isLoggedIn && isAdmin" class="admin-button" @click="goToAddFactory">Add Factory</button>
      <button v-if="isLoggedIn && isAdmin" class="login-button" @click="logout">Logout</button>
    </header>
    <div class="search-bar">
      <input v-model="searchFactoryName" placeholder="Factory Name">
      <input v-model="searchChocolateName" placeholder="Chocolate Name">
      <input v-model="searchLocation" placeholder="Location">
      <input v-model="searchAverageGrade" placeholder="Average Grade" type="number" min="1" max="5">
      <button @click="searchFactories">Search</button>
    </div>
    <div class="factory-cards">
      <div v-for="(factory, index) in filteredFactories" :key="factory.id" class="factory-card" :style="{ backgroundColor: index % 2 === 0 ? '#ffe4b5' : '#FFC9AD' }">
        <div class="factory-info">
          <div class="factory-logo">
            <img :src="factory.image" alt="Factory Logo" />
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
              <button @click="addChocolate(factory.id)">Add Chocolate</button>
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

const isLoggedIn = ref(false);
const isAdmin = ref(false);

onMounted(() => {
  isLoggedIn.value = checkLoggedIn();
  isAdmin.value = checkAdmin();
  loadFactories();
});

function checkLoggedIn() {
  const token = localStorage.getItem('token');
  return !!token;
}

async function checkAdmin() {
  try {
    const response = await axios.get('http://localhost:8080/WebShopAppREST/rest/users/admin');
    return response.data === true;
  } catch (error) {
    console.error('Error checking admin status:', error);
    return false;
  }
}

function loadFactories() {
  axios.get('http://localhost:8080/WebShopAppREST/rest/factories/')
    .then(response => {
      factories.value = response.data;
      factories.value.forEach(async factory => {
        try {
          const locationResponse = await axios.get(`http://localhost:8080/WebShopAppREST/rest/locations/findLocation?id=${factory.locationId}`);
          const location = locationResponse.data;
          let factoryLocation = {
            longitude: location.longitude,
            latitude: location.latitude,
            address: location.address
          };
          factory.locationInfo = factoryLocation;
        } catch (error) {
          console.error('Error loading location:', error);
        }
      });
    })
    .catch(error => {
      console.error('Error loading factories:', error);
    });
}

function goToLogin() {
  router.push('/login');
}

function logout() {
  localStorage.removeItem('token');
  isLoggedIn.value = false;
  isAdmin.value = false;
  router.push('/login');
}

function goToAddFactory() {
  router.push('/addFactory');
}

const filteredFactories = computed(() => {
  const openFactories = factories.value.filter(factory => factory.status);
  const closedFactories = factories.value.filter(factory => !factory.status);
  return [...openFactories, ...closedFactories];
});
</script>

<style>
body {
  background-color: #dd6755;
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
  font-size: 1rem; /* Adjust font size to match other buttons */
  width: 80px; /* Match width of other buttons */
  height: 40px; /* Match height of other buttons */
  margin-left: auto; /* Align to the right */
}

.login-button:hover {
  background-color: #ff4500; /* OrangeRed */
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
  background-color: #ff6347; /* Tomato */
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.button-group input:hover {
  background-color: #ff4500; /* OrangeRed */
}

</style>
