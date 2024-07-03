<template>
    <div class="shopping-cart">
      <header class="status-bar">
        <h1>Your Shopping Cart</h1>
      </header>
      <div v-if="chocolates.length === 0" class="empty-cart">
        <p>Your cart is empty.</p>
      </div>
      <div v-else>
        <div class="cart-items">
          <div v-for="(chocolate, index) in chocolates" :key="index" class="cart-item">
            <img :src="'data:image/jpeg;base64,' + chocolate.imageString" alt="Chocolate Image" />
            <div class="chocolate-details">
              <h2>{{ chocolate.name }}</h2>
                <p><strong>Sort:</strong> {{ chocolate.chocolateSort }}</p>
                <p><strong>Type:</strong> {{ chocolate.chocolateType }}</p>
                <p><strong>Weight:</strong> {{ chocolate.gramsOfChocolate }} grams</p>
                <p><strong>Description:</strong> {{ chocolate.chocolateDescription }}</p>
                <p><strong>Price:</strong> {{ chocolate.price }}</p>
            </div>
            <div class="user-container">
            <img class="user-button" src="/assets/minus-button.png" style="width: 40px; height: 40px;" @click="decrementQuantity(chocolate)" />
            <span class="amount">{{ getChocolateAmount(chocolate.id) }}</span>
            <img class="user-button" src="/assets/positive.png" style="width: 40px; height: 40px;" @click="incrementQuantity(chocolate)" />
        </div>
            <button @click="removeChocolate(index)" class="remove-button">Remove</button>
          </div>
        </div>
        <div class="total-price">
        <h2>Total Price: {{ someShoppingCart.price }}</h2>
      </div>
        <button class="checkout-button" @click="checkout">Checkout</button>
      </div>
    </div>
  </template>
  
  <script setup>
  import axios from 'axios';
  import { onMounted, ref } from 'vue'; 
  import { useRoute, useRouter } from 'vue-router';
  import LocationMap from './LocationMap.vue';
  const router = useRouter();
  const chocolates = ref([]);
  const cartChocolates = ref([]);
  const chocolateAmounts = ref({}); 
  const role = localStorage.getItem("role");
  const username = ref(localStorage.getItem("username") || '');
  
  const user = ref({
    username: '',
    password: '',
    name: '',
    surname: '',
    gender: '',
    birthDate: ''
  });

  const someShoppingCart = ref({
    customerId: -1,
    price: 0
  });

  onMounted(() => {
    
    loadUser();
  });

  function initializeAmounts() {
    cartChocolates.value.forEach(item => {
        chocolateAmounts.value[item.chocolateId] = item.amount; 
    });
  }
  function loadUser() {
    axios.get(`http://localhost:8080/WebShopAppREST/rest/users/authenticateUser?username=${username.value}`)
    .then(response => {
      user.value = response.data;
      console.log("user value username " + user.value.username);
      console.log("user value id " + user.value.id);
      loadCartProbno();
  })
  .catch(error => {
    console.error('Login failed:', error);
    });
 }

 async function loadCartProbno() {
      try {
        const response = await axios.get(`http://localhost:8080/WebShopAppREST/rest/shoppingCarts/getCartDetailsByUserId?userId=${user.value.id}`);
        someShoppingCart.value = response.data.shoppingCart;
        chocolates.value = someShoppingCart.value.chocolates;
        cartChocolates.value = response.data.cartChocolates;
        console.log("Chocolates:", chocolates.value);
        console.log("Shopping Cart:", someShoppingCart.value);
        console.log("Cart chocolates:", cartChocolates.value);
        console.log("usao ovdjeeee");
        cartChocolates.value.forEach(item => {
        chocolateAmounts.value[item.chocolateId] = item.amount; 
        console.log(chocolateAmounts.value[item.chocolateId]);
        });
      } catch (error) {
        console.error('Loading cart failed:', error);
      }
    }

 function loadCart() {
    console.log(user.value.id);
    axios.get(`http://localhost:8080/WebShopAppREST/rest/shoppingCarts/getByUserId?userId=` + user.value.id)
    .then(response => {
        someShoppingCart.value = response.data;
        chocolates.value = response.data.chocolates;
        console.log("soping korpa vrijednost" + someShoppingCart.value);
        loadCartChocolates();  
    })
    .catch(error => {
    console.error('loading cart failed:', error);
    });
 }

 function loadCartChocolates() {
    console.log(user.value.id);
    axios.get(`http://localhost:8080/WebShopAppREST/rest/cartChocolates/getByCartId?cartId=` + someShoppingCart.value.id)
    .then(response => {
        cartChocolates.value = response.data;  
        console.log(cartChocolates.value);
        createChocolateAmountMap();
    })
    .catch(error => {
    console.error('loading cart failed:', error);
    });
 }


 function createChocolateAmountMap() {
    console.log("usao ovdjeeee");
  cartChocolates.value.forEach(item => {
    chocolateAmounts.value[item.chocolateId] = item.amount; 
    console.log(chocolateAmounts.value[item.chocolateId]);
  });
}

 function incrementQuantity(chocolate) {
  if (chocolateAmounts.value[chocolate.chocolateId]) {
    chocolateAmounts.value[chocolate.chocolateId]++;
    getChocolateAmount(chocolate.chocolateId);
  } else {
    chocolateAmounts.value[chocolate.chocolateId] = 1;
  }
}

function decrementQuantity(chocolate) {
  if (chocolateAmounts.value[chocolate.chocolateId] && chocolateAmounts.value[chocolate.chocolateId] > 0) {
    chocolateAmounts.value[chocolate.chocolateId]--;
    getChocolateAmount(chocolate.chocolateId);
  }
}

function getChocolateAmount(chocolateId) {
  return chocolateAmounts.value[chocolateId] || 0;
}
  
  function removeChocolate(index) {
    chocolates.value.splice(index, 1);
  }
  
  async function checkout() {
  }
  </script>
  
  <style scoped>
    body {
    background-color: #dd6755;
    }

.shopping-cart {
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
  justify-content: space-between;
  align-items: center;
}

.status-bar h1 {
  font-size: 2rem;
  flex-grow: 1;
  text-align: center;
}

.user-container {
  display: flex;
  align-items: center;
}

.user-container img {
  cursor: pointer;
  border-radius: 50%;
}

.user-container .username {
  margin-left: 10px; 
}

.customer-button {
  background-color: #ff6347;
  color: white;
  border: none;
  border-radius: 5px;
  padding: 10px;
  cursor: pointer;
  transition: background-color 0.3s;
  font-size: 1rem; 
  width: 120px; 
  height: 40px;
  margin-left: auto; 
}

.customer-button:hover {
  background-color: #ff4500; 
}

.admin-button {
  background-color: #ff6347; 
  color: white;
  border: none;
  border-radius: 5px;
  padding: 10px;
  cursor: pointer;
  transition: background-color 0.3s;
  font-size: 1rem;
  width: 120px; 
  height: 40px; 
  margin-right: 10px; 
}

.admin-button:hover {
  background-color: #ff4500; 
}

.login-button {
  background-color: #ff6347; 
  color: white;
  border: none;
  border-radius: 5px;
  padding: 10px;
  cursor: pointer;
  transition: background-color 0.3s;
  font-size: 1rem; 
  width: 80px; 
  height: 40px;
  margin-left: auto; 
}

.login-button:hover {
  background-color: #ff4500; 
}

.cart-items {
  display: flex;
  flex-wrap: wrap;
  justify-content: center;
}

.cart-item {
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
  height: 550px; 
}

.cart-item:hover {
  border: 1px solid blanchedalmond;
}

.cart-item img {
  width: 100%;
  height: auto;
  max-height: 150px; 
  object-fit: cover;
}

.chocolate-details {
  padding: 20px;
  text-align: left;
  flex-grow: 1;
}

.chocolate-details h2 {
  font-size: 1.5rem;
  margin-bottom: 10px;
  text-align: center;
}

.chocolate-details p {
  margin-bottom: 5px;
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
  width: 40px; 
  height: 40px;  
}

.user-button:hover {
  background-color: blanchedalmond; 
}

.quantity-control {
  display: flex;
  align-items: center;
  justify-content: center;
}

.quantity-control button {
  background-color: #ff6347; 
  color: white;
  border: none;
  border-radius: 5px;
  padding: 10px;
  cursor: pointer;
  transition: background-color 0.3s;
}

.quantity-control button:hover {
  background-color: #ff4500; 
}

.quantity-control span {
  margin: 0 10px;
  font-size: 1.2rem;
  font-weight: bold;
}

.remove-button {
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

.remove-button:hover {
  background-color: #ff4500;
}

.total-price {
  margin-top: 20px;
  font-size: 1.5em;
}

.total-price h2 {
  margin: 0;
}

.checkout-button {
  background-color: #ff6347; 
  color: white;
  border: none;
  border-radius: 5px;
  padding: 10px;
  cursor: pointer;
  transition: background-color 0.3s;
  font-size: 1.2rem; 
  width: 200px;
  margin: 20px auto;
}

.checkout-button:hover {
  background-color: #ff4500; 
}

@media (max-width: 768px) {
  .status-bar {
    flex-direction: column;
    align-items: center;
  }
  
  .status-bar .user-container {
    margin-bottom: 10px;
  }
  
  .status-bar button {
    margin-top: 10px;
    width: 100%;
  }
  
  .quantity-control {
    flex-direction: column;
    align-items: flex-start;
  }
}
</style>