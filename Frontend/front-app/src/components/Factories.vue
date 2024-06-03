<template>
  <div class="factory-display">
    <div v-for="factory in factories" :key="factory.id" class="factory-card">
      <div class="factory-info">
        <div class="factory-logo">
          <img :src="factory.image" alt="Factory Logo" />
        </div>
        <div class="factory-details">
          <h3>{{ factory.name }}</h3>
          <p v-if="factory.locationInfo">Address: {{ factory.locationInfo.address }}</p>
          <p v-else-if="factory.locationInfo === null">Loading...</p>
          <p v-else>No location information available</p>
          <p v-if="factory.status">Status: Open</p>
          <p v-else>Status: Closed</p>
          <p>Average Grade: {{ factory.grade }}</p>
          <p v-if="factory.locationInfo">Longitude: {{ factory.locationInfo.longitude }}</p>
          <p v-if="factory.locationInfo">Latitude: {{ factory.locationInfo.latitude }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import axios from 'axios';
import { onMounted, ref } from 'vue';

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
          // Ispisujemo informacije o lokaciji u konzoli
          console.log('Location:', location);
          // Kreiramo objekat sa informacijama o lokaciji
          let factoryLocation = {
            longitude: location.longitude,
            latitude: location.latitude,
            address: location.address
          };
          // Dodajemo informacije o lokaciji u objekat fabrike
          factory.locationInfo = factoryLocation;
          // Ispisujemo fabriku sa dodatim informacijama o lokaciji
          console.log('Factory with location info:', factory);
        } catch (error) {
          console.error('Error loading location:', error);
        }
      });
    })
    .catch(error => {
      console.error('Error loading factories:', error);
    });
}



</script>

<style scoped>
.factory-display {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
  background-color: darksalmon;
}

.factory-card {
  width: 300px;
  margin: 20px;
  border: 1px solid #ccc;
  border-radius: 5px;
  overflow: hidden;
}

.factory-info {
  display: flex;
  padding: 10px;
}

.factory-logo img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 50%;
  margin-right: 10px;
}

.factory-details {
  flex-grow: 1;
}

.factory-details h3 {
  margin: 0;
  margin-bottom: 5px;
}

.factory-details p {
  margin: 0;
  margin-bottom: 5px;
}
</style>
