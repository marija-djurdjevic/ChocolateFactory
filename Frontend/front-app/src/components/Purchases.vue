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
              <h2>{{ getFactoryName(purchase.factoryId) }}</h2>
              <p><strong>Status:</strong> {{ purchase.status }}</p>
              <p><strong>Price:</strong> {{ purchase.price }}</p>
              <p><strong>Date and Time:</strong> {{ purchase.dateAndTime }}</p>
            </div>
            <button 
              v-if="purchase.status === 'Processing'" 
              @click="cancelPurchase(purchase.id)" 
              class="cancel-button"
            >
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
  
  onMounted(() => {
    loadPurchases();
    loadFactories();
  });
  
  async function loadPurchases() {
    try {
      const response = await axios.get('http://localhost:8080/WebShopAppREST/rest/purchases/userPurchases');
      purchases.value = response.data;
    } catch (error) {
      console.error('Error loading purchases:', error);
    }
  }
  
  async function loadFactories() {
    try {
      const response = await axios.get('http://localhost:8080/WebShopAppREST/rest/factories/all');
      factories.value = response.data;
    } catch (error) {
      console.error('Error loading factories:', error);
    }
  }
  
  function getFactoryName(factoryId) {
    const factory = factories.value.find(factory => factory.id === factoryId);
    return factory ? factory.name : 'Unknown Factory';
  }
  
  function cancelPurchase(purchaseId) {
    axios.post(`http://localhost:8080/WebShopAppREST/rest/purchases/cancel`, { id: purchaseId })
      .then(response => {
        if (response.status === 200) {
          const purchase = purchases.value.find(purchase => purchase.id === purchaseId);
          if (purchase) {
            purchase.status = 'Canceled';
          }
        } else {
          console.error('Error canceling purchase:', response.data);
        }
      })
      .catch(error => {
        console.error('Error canceling purchase:', error);
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
    width: 250px;
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
    height: 300px;
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