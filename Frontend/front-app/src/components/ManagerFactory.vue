<template>
  <div class="factory-details">
    <h2>Factory Details</h2>
    <div v-if="factory" class="factory-info">
      <h3>{{ factory.name }}</h3>
      <p><strong>Location:</strong> {{ factory.location }}</p>
      <p><strong>Working hours:</strong> {{ factory.worktime }}</p>
      <p><strong>Grade:</strong> {{ factory.grade }}</p>
      
      <div class="chocolates-section">
        <h4>Chocolates Produced:</h4>
        <ul>
          <li v-for="chocolate in chocolates" :key="chocolate.id">
            {{ chocolate.name }} 
          </li>
        </ul>
      </div>

      <div class="workers-section">
        <h4>Workers:</h4>
        <ul>
          <li v-for="worker in workers" :key="worker.id">
            {{ worker.username }} - {{ worker.name }} {{ worker.surname }}
          </li>
        </ul>
      </div>
      
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
    const workers = ref([]);
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
          // Call loadChocolates and loadWorkers only if factory is defined
          if (factory.value) {
            loadChocolates();
            loadWorkers();
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

    const loadWorkers = () => {
      if (!factory.value || !factory.value.id) {
        console.error('Factory data is not valid');
        return;
      }

      axios.get(`http://localhost:8080/WebShopAppREST/rest/workers/`)
        .then(response => {
          workers.value = response.data;
        })
        .catch(error => {
          console.error('Error fetching workers data', error);
        });
    };

    return {
      factory,
      chocolates,
      workers,
      loadFactoryData,
      loadChocolates,
      loadWorkers
    };
  }
};
</script>

<style scoped>
.factory-details {
  max-width: 800px;
  margin: auto;
  padding: 20px;
  border: 1px solid #dd6755;
  border-radius: 5px;
  background-color: #f8e0e3; /* Light pink background */
  box-shadow: 0px 0px 10px rgba(0, 0, 0, 0.1); /* Soft shadow */
}

.factory-details h2 {
  font-size: 24px;
  margin-bottom: 15px;
  color: #dd6755; /* Dark pink */
}

.factory-info {
  padding: 15px;
  border: 1px solid #dd6755;
  border-radius: 5px;
  margin-bottom: 20px;
  background-color: #ffffff; /* White background */
}

.factory-info h3 {
  font-size: 22px;
  margin-bottom: 10px;
  color: black; /* Dark grey */
}

.factory-info p {
  margin-bottom: 8px;
  font-size: 16px;
  color: black; /* Grey */
}

.chocolates-section {
  margin-bottom: 20px;
}

.chocolates-section h4,
.workers-section h4 {
  font-size: 20px;
  margin-bottom: 10px;
  color: #dd6755; /* Dark pink */
}

.workers-section ul {
  list-style-type: none;
  padding: 0;
}

.workers-section li {
  margin-bottom: 5px;
  font-size: 16px;
  color: black; /* Dark grey */
}

.loading {
  text-align: center;
  padding: 20px;
  font-size: 18px;
  color: #333; /* Grey */
}
</style>
