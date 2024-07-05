<template>
  <div class="factory-details">
    <!-- Header -->
    <header>
      <h1>Factory Details</h1>
    </header>

    <!-- Factory Info Card -->
    <section v-if="factory" class="card factory-info">
      <h2>Factory Information</h2>
      <div class="info-item">
        <label>Name:</label>
        <p>{{ factory.name }}</p>
      </div>
      <div class="info-item">
        <label>Location:</label>
        <template v-if="location">
          <div>{{ location.address }}</div>
          <div>{{ location.city }}</div>
          <div>{{ location.country }}</div>
        </template>
        <template v-else>
          <p>Location data not available.</p>
        </template>
      </div>
      <div class="info-item">
        <label>Working Hours:</label>
        <p>{{ factory.worktime }}</p>
      </div>
      <div class="info-item">
        <label>Rating:</label>
        <p>{{ factory.grade }}</p>
      </div>
    </section>

    <!-- Chocolates Produced Card -->
    <section v-if="chocolates.length > 0" class="card chocolates-produced">
      <h2>Chocolates Produced</h2>
      <ul>
        <li v-for="chocolate in chocolates" :key="chocolate.id">
          {{ chocolate.name }}
        </li>
      </ul>
    </section>

    <!-- Purchases Card -->
    <section v-if="purchases.length > 0" class="card purchases">
      <h2>Purchases</h2>
      <ul>
        <li v-for="purchase in purchases" :key="purchase.id">
          <div class="purchase-details">
            <div>
              <strong>Date & Time:</strong> {{ purchase.dateAndTime }}
            </div>
            <div>
              <strong>Price:</strong> {{ purchase.price }}
            </div>
            <div>
              <strong>Status:</strong> {{ purchase.status }}
            </div>
            <div>
              <strong>Chocolates:</strong>
              <ul>
                <li v-for="chocolate in purchase.chocolates" :key="chocolate.id">
                  {{ chocolate.name }} (x{{ chocolate.amountOfChocolate }})
                </li>
              </ul>
            </div>
          </div>
        </li>
      </ul>
    </section>

    <!-- Customers Card -->
    <section v-if="customers.length > 0" class="card customers">
      <h2>Customers</h2>
      <ul>
        <li v-for="customer in customers" :key="customer.id">
          <div class="customer-details">
            <div>
              <strong>Username:</strong> {{ customer.username }}
            </div>
            <div>
              <strong>Name:</strong> {{ customer.name }} {{ customer.surname }}
            </div>
          </div>
        </li>
      </ul>
    </section>

    <!-- Loading Message -->
    <div v-else class="loading">
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
    const purchases = ref([]);
    const customers = ref([]);
    const location = ref(null); // New state for location
    const username = ref(localStorage.getItem('username') || '');

    onMounted(() => {
      // Retrieve factory data when component is mounted
      loadFactoryData();
    });

    const loadFactoryData = () => {
      if (!username.value) {
        console.error('Username is not defined in local storage');
        return;
      }

      axios
        .get(`http://localhost:8080/WebShopAppREST/rest/factories/find?username=${username.value}`)
        .then(response => {
          factory.value = response.data;
          
          // Call loadChocolates, loadPurchases, loadCustomers, and loadLocationDetails only if factory is defined
          if (factory.value) {
            loadChocolates();
            loadPurchases();
            loadCustomers(factory.value.id); // Pass factory ID to load customers
            loadLocationDetails(factory.value.locationId); // Pass locationId to load location details
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

      axios
        .get(`http://localhost:8080/WebShopAppREST/rest/chocolates/${factory.value.id}`)
        .then(response => {
          chocolates.value = response.data;
        })
        .catch(error => {
          console.error('Error fetching chocolates data', error);
        });
    };

    const loadPurchases = () => {
      if (!factory.value || !factory.value.id) {
        console.error('Factory data is not valid');
        return;
      }

      axios
        .get(`http://localhost:8080/WebShopAppREST/rest/purchases/factory/${factory.value.id}`)
        .then(response => {
          purchases.value = response.data;
        })
        .catch(error => {
          console.error('Error fetching purchases data', error);
        });
    };

    const loadCustomers = factoryId => {
      if (!factoryId) {
        console.error('Factory ID is not valid');
        return;
      }

      axios
        .get(`http://localhost:8080/WebShopAppREST/rest/purchases/factory/${factoryId}/customers`)
        .then(response => {
          customers.value = response.data;
        })
        .catch(error => {
          console.error('Error fetching customers data', error);
        });
    };

    const loadLocationDetails = locationId => {
      if (!locationId) {
        console.error('Location ID is not valid');
        return;
      }

      axios
        .get(`http://localhost:8080/WebShopAppREST/rest/locations/findLocation?id=${locationId}`)
        .then(response => {
          location.value = response.data;
        })
        .catch(error => {
          console.error('Error fetching location data', error);
        });
    };

    return {
      factory,
      chocolates,
      purchases,
      customers,
      location, // Return location as part of the output
      loadFactoryData,
    };
  },
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

.factory-details header {
  text-align: center;
  margin-bottom: 20px;
}

.factory-details header h1 {
  font-size: 28px;
  color: #dd6755; /* Dark pink */
}

.card {
  padding: 15px;
  margin-bottom: 20px;
  border: 1px solid #dd6755;
  border-radius: 5px;
  background-color: #ffffff; /* White background */
  box-shadow: 0px 2px 6px rgba(0, 0, 0, 0.1); /* Light shadow */
}

.card h2 {
  font-size: 22px;
  margin-bottom: 10px;
  color: #dd6755; /* Dark pink */
}

.card .info-item {
  margin-bottom: 15px;
}

.card label {
  font-weight: bold;
  color: #333; /* Dark grey */
}

.card p {
  margin: 0;
  color: #555; /* Grey */
}

.card ul {
  list-style-type: none;
  padding: 0;
}

.card ul li {
  margin-bottom: 5px;
  font-size: 16px;
  color: #333; /* Dark grey */
}

.loading {
  text-align: center;
  padding: 20px;
  font-size: 18px;
  color: #333; /* Grey */
}
</style>
