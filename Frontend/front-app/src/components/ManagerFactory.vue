<template>
  <div class="factory-details">
    <header>
      <h1>Factory Details</h1>
    </header>

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

    <section v-if="chocolates.length > 0" class="card chocolates-produced">
      <h2>Chocolates Produced</h2>
      <ul>
        <li v-for="chocolate in chocolates" :key="chocolate.id">
          {{ chocolate.name }}
        </li>
      </ul>
    </section>

    <section v-if="purchases.length > 0" class="card purchases">
      <h2>Purchases</h2>
      <ul>
        <li v-for="purchase in purchases" :key="purchase.id" class="card purchase-card">
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
            <div v-if="purchase.status === 'Processing'" class="actions">
              <button @click="updatePurchaseStatus(purchase.id, 'Accepted')">Accept</button>
              <button @click="purchase.showDenyReasonDialog = true">Decline</button>
            </div>
            <div v-if="purchase.showDenyReasonDialog" class="deny-reason">
              <textarea v-model="purchase.denyReason" placeholder="Enter reason for denial"></textarea>
              <button @click="denyPurchase(purchase.id)">Submit</button>
            </div>
          </div>
        </li>
      </ul>
    </section>

    <section v-if="customers.length > 0" class="card customers">
      <h2>Customers</h2>
      <ul>
        <li v-for="customer in customers" :key="customer.id">
          <div class="customer-details">
            <div>
              <strong>Username:</strong> {{ customer.username }}
            </div>
            <div>
              <strong>Name:</strong> {{ customer.name }}
            </div>
            <div>
              <strong>Surname:</strong> {{ customer.surname }}
            </div>
            <div>
              <strong>Email:</strong> {{ customer.email }}
            </div>
          </div>
        </li>
      </ul>
    </section>

    <section class="card comments">
    <h2>Comments</h2>
    <ul>
      <li v-for="comment in comments" :key="comment.id" class="comment-card">
        <div class="comment-details">
          <div>
            <strong>Content:</strong> {{ comment.comment }}
          </div>
          <div>
            <strong>Grade:</strong> {{ comment.rating }}
          </div>
          <div>
            <strong>Customer:</strong> {{ getCustomerById(comment.customerId) }}
          </div>
          <div class="comment-status">
            <template v-if="comment.status === 'Processing'">
              <button @click="denyComment(comment.id)">Deny</button>
              <button @click="approveComment(comment.id)">Approve</button>
            </template>
            <template v-else>
              <span>{{ comment.status }}</span>
            </template>
          </div>
        </div>
      </li>
    </ul>
  </section>

    <section v-if="!factory" class="card no-factory">
      <p>No factory data available.</p>
    </section>

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
    const comments = ref([]);
    const location = ref(null); // New state for location
    const username = ref(localStorage.getItem('username') || '');
    const isLoading = ref(true); // New state for loading

    onMounted(() => {
      // Retrieve factory data when component is mounted
      loadFactoryData();
    });

    function getCustomerById(customerId)  {
      const customer = customers.value.find(customer => customer.id === customerId);
      return customer ? `${customer.name} ${customer.surname}` : 'Unknown';
    }

   
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
            loadComments();
            console.log("ucitani komentari" + comments)
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
          purchases.value = response.data.map(purchase => ({
            ...purchase,
            showDenyReasonDialog: false,
            denyReason: ''
          }));
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

    const updatePurchaseStatus = (purchaseId, newStatus) => {
      axios
        .put(`http://localhost:8080/WebShopAppREST/rest/purchases/updateStatus?id=${purchaseId}&status=${newStatus}`)
        .then(response => {
          const updatedPurchase = purchases.value.find(purchase => purchase.id === purchaseId);
          if (updatedPurchase) {
            updatedPurchase.status = newStatus;
          }
        })
        .catch(error => {
          console.error('Error updating purchase status', error);
        });
    };

    const denyPurchase = purchaseId => {
      const denyReason = purchases.value.find(purchase => purchase.id === purchaseId).denyReason;
      axios
        .put(`http://localhost:8080/WebShopAppREST/rest/purchases/updateStatus?id=${purchaseId}&status=Declined&reason=${denyReason}`)
        .then(response => {
          const updatedPurchase = purchases.value.find(purchase => purchase.id === purchaseId);
          if (updatedPurchase) {
            updatedPurchase.status = 'Declined';
            updatedPurchase.showDenyReasonDialog = false; // Hide deny reason dialog after submission
          }
        })
        .catch(error => {
          console.error('Error denying purchase', error);
        });
    };

    const denyComment = commentId => {
      axios
        .put(`http://localhost:8080/WebShopAppREST/rest/comments/updateStatus?id=${commentId}&status=Declined`)
        .then(response => {
          const updatedComment = comments.value.find(comment => comment.id === commentId);
          if (updatedComment) {
            updatedComment.status = 'Declined';
          }
        })
        .catch(error => {
          console.error('Error denying comment', error);
        });
    };

    const approveComment = commentId => {
      axios
        .put(`http://localhost:8080/WebShopAppREST/rest/comments/updateStatus?id=${commentId}&status=Accepted`)
        .then(response => {
          const updatedComment = comments.value.find(comment => comment.id === commentId);
          if (updatedComment) {
            updatedComment.status = 'Accepted';
          }
        })
        .catch(error => {
          console.error('Error denying comment', error);
        });
    };

    const loadComments = () => {
      console.log("DOBAVLJENI USERNAME MANAGERA "+ username.value);
      if (!username.value) {
        console.error('Username is not defined in local storage');
        return;
      }

      axios
        .get(`http://localhost:8080/WebShopAppREST/rest/comments/${username.value}`)
        .then(response => {
          comments.value = response.data;
          console.log(comments.value);
        })
        .catch(error => {
          console.error('Error fetching comments data', error);
        });
    };


    return {
      factory,
      chocolates,
      purchases,
      customers,
      location, // Return location as part of the output
      loadFactoryData,
      updatePurchaseStatus, // Return updatePurchaseStatus method
      denyPurchase, // Return denyPurchase method
      isLoading,
      comments,
      getCustomerById,
      denyComment,
      approveComment
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

/* Styles for purchase card */
.card.purchase-card {
  margin-bottom: 15px;
  padding: 10px;
  border: 1px solid #ccc;
  border-radius: 5px;
  background-color: #f9f9f9;
}

.card.purchase-card .actions {
  margin-top: 10px;
}

.card.purchase-card .actions {
  display: flex;
  justify-content: flex-end;
  margin-top: 10px;
}

.card.purchase-card button {
  background-color: #ff6347;
  color: white;
  border: none;
  border-radius: 5px;
  padding: 10px;
  cursor: pointer;
  transition: background-color 0.3s;
  font-size: 1rem;
  margin-left: 10px;
  height: 40px;
  width: 100px; /* Prilagodite širinu prema potrebi */
}

.card.purchase-card button:hover {
  background-color: tomato;
}

.card.purchase-card button:first-of-type {
  margin-left: 0;
}

.card.purchase-card button:last-of-type {
  margin-right: 0;
}

.card.purchase-card button {
  margin-right: 5px;
  padding: 5px 10px;
  border: none;
  border-radius: 3px;
  cursor: pointer;
}

.card.purchase-card button:first-of-type {
  background-color: #ff6347;
  color: white;
  border: none;
  border-radius: 5px;
  padding: 10px;
  cursor: pointer;
  transition: background-color 0.3s;
  font-size: 1rem;
  margin-left: 10px;
  height: 40px;
  width:140px;
}

.deny-reason {
  margin-top: 10px;
}

.deny-reason button {
  margin-top: 10px;
  padding: 8px 15px;
  background-color: #ffc0cb;
  color: #dd6755;
  border: none;
  border-radius: 5px;
  cursor: pointer;
  transition: background-color 0.3s, color 0.3s;
}

.deny-reason button:hover {
  background-color: tomato;
  color: #ffffff;
}

.deny-reason textarea {
  width: 100%;
  height: 60px;
  padding: 10px;
  border: 1px solid #e0e0e0;
  border-radius: 5px;
}

.card.purchase-card button:last-of-type {
  background-color: #ff6347;
  color: white;
  border: none;
  border-radius: 5px;
  padding: 10px;
  cursor: pointer;
  transition: background-color 0.3s;
  font-size: 1rem;
  margin-left: 10px;
  height: 40px;
  width:140px;
}

  .comment-card {
  position: relative;
  padding: 15px;
  border: 1px solid #ccc;
  border-radius: 5px;
  background-color: #f9f9f9;
  margin-bottom: 10px;
}

.comment-card .comment-status {
  display: flex; /* Postavite dugmad u red */
  justify-content: flex-end; /* Poravnanje na desno */
  margin-top: 10px;
}

.comment-card .comment-status button {
  padding: 5px 10px;
  border: none;
  border-radius: 3px;
  cursor: pointer;
  background-color: #ff6347;
  color: white;
  width: 100px;
  transition: background-color 0.3s;
  margin-left: 5px; /* Opciono: Dodajte razmak između dugmadi */
}

.comment-card .comment-status button:hover {
  background-color: tomato;
}

.comment-card .comment-status span {
  padding: 5px 10px;
  border-radius: 3px;
  background-color: #ccc;
  color: white;
}
</style>