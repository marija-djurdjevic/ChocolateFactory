<template>
    <div class="purchases">
      <header class="status-bar">
        <h1>Your Purchases</h1>
      </header>
      <div v-if="purchases.length === 0" class="empty-purchases">
        <p>You have no purchases.</p>
      </div>
      <div v-else>
        <div class="purchase-items">
          <div v-for="purchase in purchases" :key="purchase.id" class="purchase-item">
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
  const username = ref(localStorage.getItem("username") || '');
  
  const user = ref({
    username: '',
    password: '',
    name: '',
    surname: '',
    gender: '',
    birthDate: ''
  });
  
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
    console.log(user.id);
    try {
      const response = await axios.get(`http://localhost:8080/WebShopAppREST/rest/purchases/${user.value.id}`);
      purchases.value = response.data;
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
    width: 100%;
    margin-top: 10px; 
  }
  
  .cancel-button:hover {
    background-color: #ff4500;
  }
  
  .empty-purchases {
    margin-top: 20px;
    font-size: 1.5em;
  }
  </style>
  