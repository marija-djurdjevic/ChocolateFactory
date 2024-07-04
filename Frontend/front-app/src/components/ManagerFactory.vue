<template>
  <div>
    <h2>Factory Details</h2>
    <div v-if="factory">
      <h3>{{ factory.name }}</h3>
      
      <h4>Chocolates Produced:</h4>
      <ul>
        <li v-for="chocolate in chocolates" :key="chocolate.id">
          {{ chocolate.name }} 
        </li>
      </ul>
      <!-- Add sections for purchases and customers later -->
    </div>
    <div v-else>
      <p>Loading...</p>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue';
import axios from 'axios';

export default {
  setup() {
    const factory = ref(null);
    const chocolates = ref([]);
    const username = ref(localStorage.getItem("username") || '');

    onMounted(() => {
      // Retrieve factory data when component is mounted
      loadFactoryData();
    });

    const loadFactoryData = () => {
      if (!username.value) {
        console.error('Username is not defined in local storage');
        return;
      }

      axios.get(`http://localhost:8080/WebShopAppREST/rest/factories/find?username=${username.value}`)
        .then(response => {
          factory.value = response.data;
          // Call loadChocolates only if factory is defined
          if (factory.value) {
            loadChocolates();
          }
        })
        .catch(error => {
          console.error('Error fetching factory data', error);
        });
    };

    const loadChocolates = () => {
      if (!factory.value || !factory.value.id) {
        console.error('Factory data is not valid');
        return;
      }

      axios.get(`http://localhost:8080/WebShopAppREST/rest/chocolates/${factory.value.id}`)
        .then(response => {
          chocolates.value = response.data;
        })
        .catch(error => {
          console.error('Error fetching chocolates data', error);
        });
    };

    return {
      factory,
      chocolates,
      loadFactoryData,
      loadChocolates
    };
  }
};
</script>

<style scoped>
/* Add scoped styles here */
</style>
