<template>
  <div class="factory-display">
    <header class="status-bar">
      <h1>Factory Display</h1>
    </header>
    <div class="factory-cards">
      <div v-for="(factory, index) in filteredFactories" :key="factory.id" class="factory-card" :style="{ backgroundColor: index % 2 === 0 ? '#ffe4b5' : '#FFC9AD' }">
        <div class="factory-info">
          <div class="factory-logo">
            <img :src="factory.image" alt="Factory Logo" />
          </div>
          <div class="factory-details">
            <h2>{{ factory.name }}</h2>
            <div class="location-info" v-if="factory.locationInfo">
              <p><strong>Address:</strong> {{ factory.locationInfo.address }}</p>
              <p><strong>Longitude:</strong> {{ factory.locationInfo.longitude }}</p>
              <p><strong>Latitude:</strong> {{ factory.locationInfo.latitude }}</p>
            </div>
            <p><strong>Status:</strong> {{ factory.status ? 'Open' : 'Closed' }}</p>
            <p><strong>Average Grade:</strong> {{ factory.grade }}</p>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import axios from 'axios';
import { onMounted, ref, computed } from 'vue';

const factories = ref([]);

onMounted(() => {
  loadFactories();
})

function loadFactories() {
  axios.get('http://localhost:8080/WebShopAppREST/rest/factories/')
    .then(response => {
      factories.value = response.data;
      // Pronalazimo informacije o lokaciji za svaku fabriku
      factories.value.forEach(async factory => {
        try {
          const locationResponse = await axios.get(`http://localhost:8080/WebShopAppREST/rest/locations/findLocation?id=${factory.locationId}`);
          const location = locationResponse.data;
          // Kreiramo objekat sa informacijama o lokaciji
          let factoryLocation = {
            longitude: location.longitude,
            latitude: location.latitude,
            address: location.address
          };
          // Dodajemo informacije o lokaciji u objekat fabrike
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

const filteredFactories = computed(() => {
  // Sortiramo fabrike tako da su one sa statusom Open prve
  const openFactories = factories.value.filter(factory => factory.status);
  const closedFactories = factories.value.filter(factory => !factory.status);
  return [...openFactories, ...closedFactories];
});

</script>

<style>
body{
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
}

.status-bar h1 {
  font-size: 2rem;
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
}

.factory-logo img {
  width: 150px;
  height: 150px;
  object-fit: cover;
  border-radius: 50%;
  margin-bottom: 20px;
}

.factory-details h2 {
  font-size: 1.5rem;
  margin-bottom: 10px;
}

.location-info p {
  margin: 5px 0;
}

.factory-details p {
  margin: 5px 0;
}
</style>
