<template>
    <div class="add-chocolate">
      <h1>Add Chocolate</h1>
      <form @submit.prevent="submitForm">
        <div class="form-group">
          <label for="name">Name:</label>
          <input type="text" id="name" v-model="chocolate.name" required />
        </div>
        <div class="form-group">
          <label for="chocolateSort">Chocolate Sort:</label>
          <select id="chocolateSort" v-model="chocolate.chocolateSort" required>
            <option value="regular">Regular</option>
            <option value="cooking">Cooking</option>
          </select>
        </div>
        <div class="form-group">
          <label for="chocolateType">Chocolate Type:</label>
          <select id="chocolateType" v-model="chocolate.chocolateType" required>
            <option value="dark">Dark</option>
            <option value="white">White</option>
          </select>
        </div>
        <div class="form-group">
          <label for="gramsOfChocolate">Grams of Chocolate:</label>
          <input type="number" id="gramsOfChocolate" v-model="chocolate.gramsOfChocolate" required />
        </div>
        <div class="form-group">
          <label for="chocolateDescription">Description:</label>
          <textarea id="chocolateDescription" v-model="chocolate.chocolateDescription" required></textarea>
        </div>
        <div class="form-group">
          <label for="imagePath">Image Path:</label>
          <input type="text" id="imagePath" v-model="chocolate.imagePath" required />
        </div>
        <div class="form-group">
          <label for="isAvailable">Is Available:</label>
          <input type="checkbox" id="isAvailable" v-model="chocolate.isAvailable" />
        </div>
        <div class="form-group">
          <label for="amountOfChocolate">Amount of Chocolate:</label>
          <input type="number" id="amountOfChocolate" v-model="chocolate.amountOfChocolate" required />
        </div>
        <button type="submit">Add Chocolate</button>
      </form>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue';
  import { useRouter, useRoute } from 'vue-router';
  import axios from 'axios';
  
  const route = useRoute();
  const router = useRouter();
  const factoryId = route.query.factoryId;
  
  const chocolate = ref({
    name: '',
    chocolateSort: 'regular',
    chocolateType: 'dark',
    factoryId: factoryId,
    gramsOfChocolate: 0,
    chocolateDescription: '',
    imagePath: '',
    isAvailable: false,
    amountOfChocolate: 0,
  });
  
  function submitForm() {
    axios.post('http://localhost:8080/WebShopAppREST/rest/chocolates/save', chocolate.value)
      .then(response => {
        alert('Chocolate added successfully!');
        router.push('/');
      })
      .catch(error => {
        console.error('Error adding chocolate:', error);
      });
  }
  </script>
  
  <style>
  .add-chocolate {
    max-width: 600px;
    margin: 50px auto;
    padding: 20px;
    background-color: #fff;
    border-radius: 10px;
    box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
  }
  
  .add-chocolate h1 {
    text-align: center;
    margin-bottom: 20px;
  }
  
  .form-group {
    margin-bottom: 15px;
  }
  
  .form-group label {
    display: block;
    margin-bottom: 5px;
  }
  
  .form-group input, .form-group select, .form-group textarea {
    width: 100%;
    padding: 8px;
    box-sizing: border-box;
  }
  
  button {
    display: block;
    width: 100%;
    padding: 10px;
    background-color: #dd6755;
    color: #fff;
    border: none;
    border-radius: 5px;
    font-size: 16px;
    cursor: pointer;
  }
  
  button:hover {
    background-color: #bf5640;
  }
  </style>
  
  