<template>
  <div class="factory-chocolates">
    <header class="status-bar">
      <h1>{{ factory.name }} - Chocolates</h1>
    </header>
    <div class="factory-info">
      <div class="factory-logo">
              <img :src="'data:image/jpeg;base64,' + factory.imageString" alt="Factory Image" />
      </div>
      <div class="factory-details">
        <div class="location-info" v-if="factory.locationInfo">
          <p>{{ factory.locationInfo.address }}</p>
          <p>{{ factory.locationInfo.longitude }}, {{ factory.locationInfo.latitude }}</p>
          <LocationMap :longitude="factory.locationInfo.longitude" :latitude="factory.locationInfo.latitude" />
        </div>
        <p><strong>Status:</strong> {{ factory.status ? 'Open' : 'Closed' }}</p>
        <p><strong>Working hours:</strong> {{ factory.worktime }}</p>
        <p><strong>Average Grade:</strong> {{ factory.grade }}</p>
      </div>
    </div>
    <div class="chocolates-list">
      <h2>Chocolates</h2>
      <div class="chocolate-items">
        <div v-for="(chocolate, index) in chocolates" :key="chocolate.id" class="chocolate-item" :style="{ backgroundColor: index % 2 === 0 ? '#ffe4b5' : '#FFC9AD' }">
          <div class="chocolate-details">
            <h3>{{ chocolate.name }}</h3>
            <p><strong>Sort:</strong> {{ chocolate.chocolateSort }}</p>
            <p><strong>Type:</strong> {{ chocolate.chocolateType }}</p>
            <p><strong>Weight:</strong> {{ chocolate.gramsOfChocolate }} grams</p>
            <p><strong>Description:</strong> {{ chocolate.chocolateDescription }}</p>
            <p><strong>Status:</strong> {{ chocolate.available ? 'Available' : 'Not Available' }}</p>
            <p><strong>Amount:</strong> {{ chocolate.amountOfChocolate }}</p>
          </div>
          <div class="chocolate-image">
            <img :src="'data:image/jpeg;base64,' + chocolate.imageString" alt="Chocolate Image" />
          </div>
          <div class="button-group">
            <input v-if="isManager" v-on:click="editChocolate(chocolate)" type="submit" value="Edit">
            <input v-if="isManager" v-on:click="deleteChocolate(chocolate.id)" type="submit" value="Delete">
            <input v-if="isWorker" v-on:click="openEditAmountDialog(chocolate)" type="submit" value="Edit Amount"> 
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import axios from 'axios';
import { onMounted, ref } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import LocationMap from './LocationMap.vue';

const route = useRoute();
const router = useRouter();
const factoryId = route.params.factoryId;
const factory = ref({});
const chocolates = ref([]);
const isManager = ref(false); 
const isWorker = ref(false);
const role = localStorage.getItem("role");

onMounted(() => {
  loadFactory();
  loadChocolates();
  isManager.value = role === 'Manager';
  isWorker.value = role === 'Worker';
});

async function loadFactory() {
  try {
    const response = await axios.get(`http://localhost:8080/WebShopAppREST/rest/factories/${factoryId}`);
    factory.value = response.data;
    console.log(factory.value);
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

function editChocolate(chocolate) {
  console.log(chocolate);
  router.push({ path: '/editChocolate', query: { chocolate: JSON.stringify(chocolate), factoryId: factoryId } });
}

async function deleteChocolate(chocolateId) {
  try {
    await axios.delete(`http://localhost:8080/WebShopAppREST/rest/chocolates/${chocolateId}`);
    chocolates.value = chocolates.value.filter(chocolate => chocolate.id !== chocolateId);
  } catch (error) {
    console.error('Error deleting chocolate:', error);
  }
}
function openEditAmountDialog(chocolate) {
  const newAmount = prompt(`Enter new amount for ${chocolate.name}:`, chocolate.amountOfChocolate);
  console.log(newAmount);
  if (newAmount !== null && !isNaN(newAmount)) {
    updateChocolateAmount(chocolate.id, parseInt(newAmount));
  }
}

async function updateChocolateAmount(chocolateId, newAmount) {
  try {
    console.log(newAmount);
    const token = localStorage.getItem('token'); 
    const response = await axios.put(
      `http://localhost:8080/WebShopAppREST/rest/factories/${factoryId}/updateChocolateAmount/${chocolateId}?newAmount=${newAmount}`,
      {}, 
      {
        headers: {
          Authorization: `Bearer ${token}`, 
        },
      }
    );
    const updatedChocolate = response.data;
    const index = chocolates.value.findIndex((choc) => choc.id === chocolateId);
    if (index !== -1) {
      chocolates.value[index] = updatedChocolate;
    }
  } catch (error) {
    console.error('Error updating chocolate amount:', error);
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
  width: 150px;
  height: 150px;
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
