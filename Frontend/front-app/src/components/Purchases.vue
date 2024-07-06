<template>
  <div class="purchases">
    <header class="status-bar">
      <h1>Your Purchases</h1>
    </header>
    <div class="search-bar">
      <input v-model="searchFactory" type="text" placeholder="Factory Name" />
      <input v-model="priceFrom" type="number" placeholder="Price From" />
      <input v-model="priceTo" type="number" placeholder="Price To" />
      <input v-model="dateFrom" type="date" placeholder="Date From" />
      <input v-model="dateTo" type="date" placeholder="Date To" />
      <button @click="applyFilters">Search</button>
    </div>
    <div class="sort-bar">
      <select v-model="sortBy">
        <option value="factory">Factory</option>
        <option value="price">Price</option>
        <option value="date">Date</option>
      </select>
      <select v-model="sortOrder">
        <option value="asc">Ascending</option>
        <option value="desc">Descending</option>
      </select>
      <button @click="applySorting">Sort</button>
    </div>
    <div v-if="filteredPurchases.length === 0" class="empty-purchases">
      <p>You have no purchases.</p>
    </div>
    <div v-else>
      <div class="purchase-items">
        <div v-for="purchase in filteredPurchases" :key="purchase.id" class="purchase-item">
          <div class="purchase-details">
            <p><strong>Factory:</strong> {{ getFactoryName(purchase.factoryId) }}</p>
            <p><strong>Status:</strong> {{ purchase.status }}</p>
            <p><strong>Price:</strong> {{ purchase.price }}</p>
            <p><strong>Order created on:</strong> {{ formatDate(purchase.dateAndTime) }}</p>
          </div>
          <button 
            v-if="purchase.status === 'Processing'" 
            @click="cancelPurchase(purchase.id)" 
            class="cancel-button">
            Cancel
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import axios from 'axios';
import { ref, onMounted } from 'vue';

const purchases = ref([]);
const factories = ref([]);
const filteredPurchases = ref([]);
const username = ref(localStorage.getItem("username") || '');

const user = ref({
  username: '',
  password: '',
  name: '',
  surname: '',
  gender: '',
  birthDate: ''
});

const searchFactory = ref('');
const priceFrom = ref(null);
const priceTo = ref(null);
const dateFrom = ref('');
const dateTo = ref('');
const sortBy = ref('factory');
const sortOrder = ref('asc');

onMounted(() => {
  loadUser();
});

function loadUser() {
  axios.get(`http://localhost:8080/WebShopAppREST/rest/users/authenticateUser?username=${username.value}`)
    .then(response => {
      user.value = response.data;
      console.log(user.value);
      loadFactories();
    })
    .catch(error => {
      console.error('Login failed:', error);
    });
}

function formatDate(dateString) {
  const options = { year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit' };
  return new Date(dateString).toLocaleDateString(undefined, options);
}

async function loadPurchases() {
  console.log(user.value.id);
  try {
    const response = await axios.get(`http://localhost:8080/WebShopAppREST/rest/purchases/${user.value.id}`);
    purchases.value = response.data;
    filteredPurchases.value = purchases.value;
  } catch (error) {
    console.error('Error loading purchases:', error);
  }
}

async function loadFactories() {
  try {
    const response = await axios.get('http://localhost:8080/WebShopAppREST/rest/factories/');
    factories.value = response.data;
    loadPurchases();
  } catch (error) {
    console.error('Error loading factories:', error);
  }
}

function getFactoryName(factoryId) {
  const factory = factories.value.find(factory => factory.id === factoryId);
  return factory ? factory.name : 'Unknown Factory';
}

function cancelPurchase(purchaseId) {
  const purchase = purchases.value.find(p => p.id === purchaseId);
  console.log(purchase);
  axios.post(`http://localhost:8080/WebShopAppREST/rest/purchases/cancel`, purchase)
    .then(response => {
      if (purchase) {
        purchase.status = 'Canceled';
      }
    })
    .catch(error => {
      console.error('Error updating chocolate:', error);
    });
}

function applyFilters() {
  filteredPurchases.value = purchases.value.filter(purchase => {
    const factoryName = getFactoryName(purchase.factoryId).toLowerCase();
    const matchesFactory = !searchFactory.value || factoryName.includes(searchFactory.value.toLowerCase());
    const matchesPrice = (!priceFrom.value || purchase.price >= priceFrom.value) &&
                         (!priceTo.value || purchase.price <= priceTo.value);
    const purchaseDate = new Date(purchase.dateAndTime);
    const matchesDate = (!dateFrom.value || purchaseDate >= new Date(dateFrom.value)) &&
                        (!dateTo.value || purchaseDate <= new Date(dateTo.value));
    return matchesFactory && matchesPrice && matchesDate;
  });
}

function applySorting() {
  filteredPurchases.value = filteredPurchases.value.slice().sort((a, b) => {
    let comparison = 0;
    if (sortBy.value === 'factory') {
      const factoryA = getFactoryName(a.factoryId).toLowerCase();
      const factoryB = getFactoryName(b.factoryId).toLowerCase();
      comparison = factoryA.localeCompare(factoryB);
    } else if (sortBy.value === 'price') {
      comparison = a.price - b.price;
    } else if (sortBy.value === 'date') {
      comparison = new Date(a.dateAndTime) - new Date(b.dateAndTime);
    }
    return sortOrder.value === 'asc' ? comparison : -comparison;
  });
}
</script>

<style scoped>
body {
  background-color: #dd6755;
}

.purchases {
  text-align: center;
  padding: 20px;
  min-height: 100vh;
}

.status-bar {
  background-color: #FFC9AD; 
  color: #333;
  padding: 10px;
  margin-bottom: 20px;
  display: flex;
  justify-content: center;
  align-items: center;
}

.status-bar h1 {
  font-size: 2rem;
}

.search-bar {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 20px;
}

.search-bar input,
.search-bar button {
  margin: 5px;
  padding: 10px;
  font-size: 1rem;
}

.search-bar button {
  background-color: #ff6347; 
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.search-bar button:hover {
  background-color: #ff4500;
}

.sort-bar {
  display: flex;
  justify-content: center;
  align-items: center;
  margin-bottom: 20px;
}

.sort-bar select,
.sort-bar button {
  margin: 5px;
  padding: 10px;
  font-size: 1rem;
  width: 100px;
}

.sort-bar button {
  background-color: #ff6347; 
  color: white;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.sort-bar button:hover {
  background-color: #ff4500;
}

.purchase-items {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
}

.purchase-item {
  background-color: blanchedalmond; 
  width: 330px;
  margin: 20px;
  padding: 15px;
  border-radius: 10px;
  overflow: hidden;
  box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
  cursor: pointer;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: 250px;
}

.purchase-item:hover {
  border: 1px solid blanchedalmond;
}

.purchase-details {
  padding: 20px;
  text-align: left;
  flex-grow: 1;
}

.purchase-details h2 {
  font-size: 1.5rem;
  margin-bottom: 10px;
  text-align: center;
}

.purchase-details p {
  margin-bottom: 5px;
}

.cancel-button {
  background-color: #ff6347; 
  color: white;
  border: none;
  border-radius: 5px;
  padding: 10px;
  cursor: pointer;
  transition: background-color 0.3s;
  width: fit-content;
  margin-left: auto;
}

.cancel-button:hover {
  background-color: #ff4500;
}

.empty-purchases p {
  font-size: 1.5rem;
  color: #333;
}
</style>
