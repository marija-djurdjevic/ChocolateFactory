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
        <div v-for="(chocolate, index) in filterChocolates()" :key="chocolate.id" class="chocolate-item" :style="{ backgroundColor: index % 2 === 0 ? '#ffe4b5' : '#FFC9AD' }">
          <div class="chocolate-details">
            <h3>{{ chocolate.name }}</h3>
            <p><strong>Sort:</strong> {{ chocolate.chocolateSort }}</p>
            <p><strong>Type:</strong> {{ chocolate.chocolateType }}</p>
            <p><strong>Weight:</strong> {{ chocolate.gramsOfChocolate }} grams</p>
            <p><strong>Description:</strong> {{ chocolate.chocolateDescription }}</p>
            <p><strong>Status:</strong> {{ chocolate.available ? 'Available' : 'Not Available' }}</p>
          </div>
          <div class="chocolate-image">
            <img :src="'data:image/jpeg;base64,' + chocolate.imageString" alt="Chocolate Image" />
          </div>
          <div v-if="isCustomer" class="user-container">
            <img class="user-button" src="/assets/minus-button.png" style="width: 40px; height: 40px;" @click="DecAmount(chocolate.id)" />
            <span class="amount">{{ chocolateAmounts[chocolate.id] }}</span>
            <img class="user-button" src="/assets/positive.png" style="width: 40px; height: 40px;" @click="IncAmount(chocolate.id, chocolate.amountOfChocolate)" />
          </div>
          <div class="button-group">
            <input v-if="isCustomer" v-on:click="AddToCart(chocolate.id, chocolate.amountOfChocolate)" type="submit" value="Add to cart">
            <input v-if="isManager" v-on:click="editChocolate(chocolate)" type="submit" value="Edit">
            <input v-if="isManager" v-on:click="deleteChocolate(chocolate.id)" type="submit" value="Delete">
            <input v-if="isWorker" v-on:click="openEditAmountDialog(chocolate)" type="submit" value="Edit Amount"> 
          </div>
        </div>
      </div>
    </div>
    <div v-if="errorMessage" class="error-message">
      <p>{{ errorMessage }}</p>
    </div>
    <div v-if="showButton" class="shopping-cart-button">
      <button class="shoppinggoto-button" @click="GoToShoppingCart">See shopping cart</button>
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
const existingchocolates = ref([]);
const chocolateAmounts = ref({});
const isManager = ref(false); 
const isWorker = ref(false);
const isCustomer = ref(false);
const isthisfactory = ref(false);
const role = localStorage.getItem("role");
const errorMessage = ref('');
const amount = ref(1);
const username = ref(localStorage.getItem("username") || '');
const isCartCreated = ref(false); 
const showButton = ref(false);

const user = ref({
    username: '',
    password: '',
    name: '',
    surname: '',
    gender: '',
    birthDate: ''
  });

  const existingCart = ref({
      customerId: -1,
      price: 0
  }); 

  const shoppingCart = ref({
      customerId: -1,
      price: 0
  });

  const newShoppingCart = ref({
    customerId: -1,
    price: 0
  });

const cartChocolate = ref({
  customerId: -1,
  chocolateId: -1,
  purchaseId: 'empty',
  amount: 0
  });

onMounted(() => {
  loadFactory();
  loadChocolates();
  isManager.value = role === 'Manager';
  isWorker.value = role === 'Worker';
  isCustomer.value = role === 'Customer';
  loadUser();
});

function loadUser() {
    axios.get(`http://localhost:8080/WebShopAppREST/rest/users/authenticateUser?username=${username.value}`)
    .then(response => {
      user.value = response.data;
      console.log(user.value.username);
      loadCartProbno();
  })
  .catch(error => {
    console.error('Login failed:', error);
    });
 }

 function IncAmount(chocolateId, amountOfChocolate) {
  if (!chocolateAmounts.value[chocolateId]) {
    chocolateAmounts.value[chocolateId] = 1;
  }
  if (amountOfChocolate > chocolateAmounts.value[chocolateId]) {
    errorMessage.value = '';
    chocolateAmounts.value[chocolateId] += 1;
  }
}

function DecAmount(chocolateId) {
  if (!chocolateAmounts.value[chocolateId]) {
    chocolateAmounts.value[chocolateId] = 1;
  }
  if (chocolateAmounts.value[chocolateId] > 1) {
    errorMessage.value = '';
    chocolateAmounts.value[chocolateId] -= 1;
  }
}

function filterChocolates() {
  if (isCustomer.value) {
    return chocolates.value.filter(chocolate => chocolate.available);
  }
  return chocolates.value;
}

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
    chocolates.value.forEach(chocolate => {
      chocolateAmounts.value[chocolate.id] = 1;
    });
  } catch (error) {
    console.error('Error loading chocolates:', error);
  }
}

function AddToCart(chocolateId, amountOfChocolate) {
  loadCart(chocolateId, amountOfChocolate);
  isCartCreated.value = true; 
  showButton.value = true;
  alert('You added chocolate to cart!');
}

async function loadCartProbno() {
      try {
        const response = await axios.get(`http://localhost:8080/WebShopAppREST/rest/shoppingCarts/getCartDetailsByUserId?userId=${user.value.id}`);
        console.log(!response.data);
        if(!response.data){
          isthisfactory.value = true;
          isCartCreated.value = false;
          return;
        }
        existingCart.value = response.data.shoppingCart;
        if(existingCart.customerId === user.id){
          console.log(existingCart.customerId);
          console.log("existingCart" + existingCart);
          existingchocolates.value = existingCart.value.chocolates;
          console.log(existingchocolates.value[0].factoryId);
          console.log(factoryId);
          if(existingchocolates.value[0].factoryId == factoryId){
                  isthisfactory.value = true;
                  showButton.value = true;
        }
        }
        
        console.log("vrijednost isthisfactory" + isthisfactory.value);
       
      } catch (error) {
        console.error('Loading cart failed:', error);
      }
 }

function loadCart(chocolateId, amountOfChocolate) {
  isCartCreated.value = true;
  if(isthisfactory.value === false){
           alert('You already have one cart preparing. Finish your order and than continue.');
           showButton.value = true;
        return;
  }
    axios.get(`http://localhost:8080/WebShopAppREST/rest/shoppingCarts/getByUserId?userId=${user.value.id}`)
    .then(response => {
        shoppingCart.value = response.data;
        if(!shoppingCart.value || Object.keys(shoppingCart.value).length === 0){
            newShoppingCart.value.customerId = user.value.id;
            const token = localStorage.getItem('token');
            axios.post('http://localhost:8080/WebShopAppREST/rest/shoppingCarts/save', newShoppingCart.value, {
            headers: {
                'Authorization': `Bearer ${token}`
            }
            })
            .then(response => {
            if (response.status === 200) {
                saveCartChocolate(chocolateId, amountOfChocolate);
            } else {
                console.error('Error adding cart:', response.data);
            }
            })
            .catch(error => {
            console.error('Error adding cart:', error);
            });
        }  
        else{
          saveCartChocolate(chocolateId, amountOfChocolate);
        }
    })
    .catch(error => {
    console.error('Login failed:', error);
    });
 }

function saveCartChocolate(chocolateId, amountOfChocolate) {
  if(newShoppingCart.value.customerId !== -1){
    cartChocolate.value.shoppingCartId = newShoppingCart.value.id;
  }
  else{
    cartChocolate.value.shoppingCartId = shoppingCart.value.id;
  }

  cartChocolate.value.chocolateId = chocolateId;
  cartChocolate.value.amount = chocolateAmounts.value[chocolateId];
  const token = localStorage.getItem('token');
  axios.post('http://localhost:8080/WebShopAppREST/rest/cartChocolates/save', cartChocolate.value, {
    headers: {
      'Authorization': `Bearer ${token}`
    }
  })
  .then(response => {
    if (response.status === 200) {
      console.log(newShoppingCart.value.customerId);
      if(newShoppingCart.value.customerId === -1){
        editShoppingCart(shoppingCart);
      }
      else{
        editNewShoppingCart(newShoppingCart);
      }
      updateAmountOfChocolateNormal(chocolateId, amountOfChocolate);
    } else {
      console.error('Error adding CartChocolate:', response.data);
    }
  })
  .catch(error => {
    console.error('Error adding CartChocolate:', error);
  });
}

function editShoppingCart(shoppingCart) {
      const token = localStorage.getItem('token');
      axios.post('http://localhost:8080/WebShopAppREST/rest/shoppingCarts/edit', shoppingCart.value, {
      headers: {
          'Authorization': `Bearer ${token}`
      }
      })
      .then(response => {
      if (response.status === 200) {
      } else {
          console.error('Error editing cart:', response.data);
      }
      })
      .catch(error => {
      console.error('Error editing cart:', error);
      });
}

function editNewShoppingCart(newShoppingCart) {
      const token = localStorage.getItem('token');
      axios.post('http://localhost:8080/WebShopAppREST/rest/shoppingCarts/edit', newShoppingCart.value, {
      headers: {
          'Authorization': `Bearer ${token}`
      }
      })
      .then(response => {
      if (response.status === 200) {
      } else {
          console.error('Error editing new cart:', response.data);
      }
      })
      .catch(error => {
      console.error('Error editing cart:', error);
      });
}

function GoToShoppingCart() {
  router.push('/shoppingCart');
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

async function updateAmountOfChocolateNormal(chocolateId, amountOfChocolate){
  amountOfChocolate = amountOfChocolate - chocolateAmounts.value[chocolateId];
  if(amountOfChocolate === 0){
    chocolates.value = chocolates.value.filter(chocolatee => chocolatee.id !== chocolateId);
  }
  const token = localStorage.getItem('token');
  const response = await axios.put(
      `http://localhost:8080/WebShopAppREST/rest/factories/${factoryId}/updateChocolateAmount/${chocolateId}?newAmount=${amountOfChocolate}`,
      {},
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    ).then(response => {
    if (response.status === 200) {
    } else {
      console.error('Error updating chocolate:', response.data);
    }
  })
  .catch(error => {
    console.error('Error updating chocolate:', error);
  });
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
      chocolates.value[index].available = updatedChocolate.amountOfChocolate > 0;
    }
    errorMessage.value = ''; 
  } catch (error) {
    console.error('Error updating chocolate amount:', error);
    if (error.response && error.response.status === 403) {
      errorMessage.value = 'You do not have permission to update the chocolate amount in this factory.';
    } else {
      errorMessage.value = 'An error occurred while updating the chocolate amount.';
    }
  }
}

</script>

<style>
.factory-chocolates {
  text-align: center;
  padding: 20px;
  min-height: 100vh;
}

 .shopping-cart-button {
  position: fixed;
  bottom: 20px;
  right: 20px;
  background-color: #FFC9AD;
  border: none;
  padding: 10px 20px;
  cursor: pointer;
  border-radius: 5px;
}

.status-bar {
  background-color: #FFC9AD;  
  color: #333;
  padding: 10px;
  margin-bottom: 20px;
}

.status-bar h1 {
  font-size: 2rem;
}

.user-container {
  display: flex;
  align-items: center;
  justify-content: center; 
}

.user-container img {
  cursor: pointer;
  border-radius: 50%;
}

.user-button {
  color: white;
  border: none;
  border-radius: 5px;
  padding: 10px;
  cursor: pointer;
  transition: background-color 0.3s;
  font-size: 1rem;
  margin: 0 10px;
  width: 120px; 
  height: 40px;  
}

.shoppinggoto-button {
  background-color: #ff6347;
  color: blanchedalmond;  
  font-weight: bold;

}

.user-button:hover {
  background-color: blanchedalmond; 
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
  text-align: center; 
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
  height: 550px; 
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
  max-height: 170px;
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
