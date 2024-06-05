<template>
    <div class="factory-chocolates">
      <header class="status-bar">
        <h1>{{ factory.name }} - Chocolates</h1>
      </header>
      <div class="factory-info">
        <div class="factory-logo">
          <img :src="factory.image" alt="Factory Logo" />
        </div>
        <div class="factory-details">
          <div class="location-info" v-if="factory.locationInfo">
            <p>{{ factory.locationInfo.address }}</p>
            <p>{{ factory.locationInfo.longitude }}, {{ factory.locationInfo.latitude }}</p>
          </div>
          <p><strong>Status:</strong> {{ factory.status ? 'Open' : 'Closed' }}</p>
          <p><strong>Working hours:</strong> {{ factory.worktime }}</p>
          <p><strong>Average Grade:</strong> {{ factory.grade }}</p>
        </div>
      </div>
      <div class="chocolates-list">
        <h2>Chocolates</h2>
        <div class="chocolate-items">
          <div v-for="chocolate in chocolates" :key="chocolate.id" class="chocolate-item" :style="{ backgroundColor: index % 2 === 0 ? '#ffe4b5' : '#FFC9AD' }">
            <div class="chocolate-details">
              <h3>{{ chocolate.name }}</h3>
              <p><strong>Sort:</strong> {{ chocolate.chocolateSort }}</p>
              <p><strong>Type:</strong> {{ chocolate.chocolateType }}</p>
              <p><strong>Weight:</strong> {{ chocolate.gramsOfChocolate }} grams</p>
              <p><strong>Description:</strong> {{ chocolate.chocolateDescription }}</p>
              <p><strong>Status:</strong> {{ chocolate.isAvailable ? 'Available' : 'Not Available' }}</p>
              <p><strong>Amount:</strong> {{ chocolate.amountOfChocolate }}</p>
            </div>
            <div class="chocolate-image">
              <img :src="chocolate.imagePath" alt="Chocolate Image" />
            </div>
          </div>
        </div>
      </div>
    </div>
  </template>
  
  <script setup>
  import axios from 'axios';
  import { onMounted, ref } from 'vue';
  import { useRoute } from 'vue-router';
  
  const route = useRoute();
  const factoryId = route.params.factoryId;
  const factory = ref({});
  const chocolates = ref([]);
  
  onMounted(() => {
    loadFactory();
    loadChocolates();
  });
  
  async function loadFactory() {
    try {
      const response = await axios.get(`http://localhost:8080/WebShopAppREST/rest/factories/${factoryId}`);
      factory.value = response.data;
      const locationResponse = await axios.get(`http://localhost:8080/WebShopAppREST/rest/locations/findLocation?id=${factory.value.locationId}`);
      factory.value.locationInfo = locationResponse.data;
    } catch (error) {
      console.error('Error loading factory:', error);
    }
  }
  
  async function loadChocolates() {
    try {
      const response = await axios.get(`http://localhost:8080/WebShopAppREST/rest/chocolates/${factoryId}`);
      chocolates.value = response.data;
    } catch (error) {
      console.error('Error loading chocolates:', error);
    }
  }
  </script>
  
  <style>
  .factory-chocolates {
    text-align: center;
    padding: 20px;
    min-height: 100vh;
  }
  
  .status-bar {
    background-color: #FFC9AD; /* Bisque */
    color: #333;
    padding: 10px;
    margin-bottom: 20px;
  }
  
  .status-bar h1 {
    font-size: 2rem;
  }
  
  .factory-info {
    display: flex;
    justify-content: center;
    align-items: center;
    padding: 20px;
  }
  
  .factory-logo img {
    width: 100px;
    height: 100px;
    object-fit: cover;
    border-radius: 50%;
    margin-bottom: 20px;
  }
  
  .factory-details {
    margin-left: 20px;
    text-align: center; /* Promijenjeno na centrirano */
  }
  
  .factory-details .factory-name {
    font-size: 1.5rem;
    margin-bottom: 10px;
  }
  
  .location-info p {
    margin: 5px 0;
  }
  
  .chocolates-list {
    margin-top: 20px;
  }
  
  .chocolate-items {
    display: flex;
    flex-wrap: wrap;
    justify-content: center;
  }
  
  .chocolate-item {
    width: 250px; 
    margin: 20px;
    padding: 15px;
    border: 1px solid #ccc;
    border-radius: 10px;
    overflow: hidden;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
    transition: transform 0.3s ease;
    cursor: pointer;
  }
  
  .chocolate-item:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
  }
  
  .chocolate-details {
    text-align: left;
  }
  
  .chocolate-image img {
    max-width: 100%;
    height: auto;
    border-radius: 10px;
    box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  }
  </style>
  